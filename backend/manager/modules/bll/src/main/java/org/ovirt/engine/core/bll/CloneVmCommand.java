package org.ovirt.engine.core.bll;

import static org.ovirt.engine.core.bll.storage.disk.image.DisksFilter.ONLY_ACTIVE;
import static org.ovirt.engine.core.bll.storage.disk.image.DisksFilter.ONLY_PLUGGED;
import static org.ovirt.engine.core.bll.storage.disk.image.DisksFilter.ONLY_SNAPABLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.ovirt.engine.core.bll.context.CommandContext;
import org.ovirt.engine.core.bll.storage.disk.image.DisksFilter;
import org.ovirt.engine.core.bll.utils.PermissionSubject;
import org.ovirt.engine.core.bll.utils.VmDeviceUtils;
import org.ovirt.engine.core.common.VdcObjectType;
import org.ovirt.engine.core.common.action.ActionReturnValue;
import org.ovirt.engine.core.common.action.ActionType;
import org.ovirt.engine.core.common.action.AttachDetachVmDiskParameters;
import org.ovirt.engine.core.common.action.CloneVmParameters;
import org.ovirt.engine.core.common.action.LockProperties;
import org.ovirt.engine.core.common.action.LockProperties.Scope;
import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.common.businessentities.VMStatus;
import org.ovirt.engine.core.common.businessentities.VmDevice;
import org.ovirt.engine.core.common.businessentities.VmInit;
import org.ovirt.engine.core.common.businessentities.VmStatic;
import org.ovirt.engine.core.common.businessentities.storage.Disk;
import org.ovirt.engine.core.common.businessentities.storage.DiskImage;
import org.ovirt.engine.core.common.businessentities.storage.DiskVmElement;
import org.ovirt.engine.core.common.errors.EngineError;
import org.ovirt.engine.core.common.errors.EngineException;
import org.ovirt.engine.core.common.errors.EngineMessage;
import org.ovirt.engine.core.common.locks.LockingGroup;
import org.ovirt.engine.core.common.queries.IdQueryParameters;
import org.ovirt.engine.core.common.queries.QueryReturnValue;
import org.ovirt.engine.core.common.queries.QueryType;
import org.ovirt.engine.core.common.utils.Pair;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.core.dao.VmDao;
import org.ovirt.engine.core.dao.VmDeviceDao;
import org.ovirt.engine.core.dao.VmInitDao;

@DisableInPrepareMode
@NonTransactiveCommandAttribute(forceCompensation = true)
public class CloneVmCommand<T extends CloneVmParameters> extends AddVmAndCloneImageCommand<T> {

    @Inject
    private VmDeviceDao vmDeviceDao;
    @Inject
    protected VmDeviceUtils vmDeviceUtils;
    @Inject
    private VmDao vmDao;
    @Inject
    private VmInitDao vmInitDao;

    private Collection<DiskImage> diskImagesFromConfiguration;

    private Guid oldVmId;

    private VM vm;

    private VM sourceVm;

    private VM editedVM;

    protected CloneVmCommand(T params, CommandContext commandContext) {
        super(params, commandContext);
    }

    public CloneVmCommand(Guid commandId) {
        super(commandId);
    }


    @Override
    protected void init() {
        super.init();
        oldVmId = getParameters().getVmId();
        setVmName(getParameters().getNewName());

        // init the parameters only at first instantiation (not subsequent for end action)
        if (Guid.isNullOrEmpty(getParameters().getNewVmGuid())) {
            setupParameters();
        } else {
            // the VM id has to be the new VM id - same as the getVm is always the new VM
            setVmId(getParameters().getNewVmGuid());
        }
        getParameters().setUseCinderCommandCallback(!getAdjustedDiskImagesFromConfiguration().isEmpty());
    }

    @Override
    protected void executeVmCommand() {
        super.executeVmCommand();
        if (getParameters().isEdited()) {
            editedVM.setId(getVmId());
            editedVM.setStoragePoolId(getParameters().getStorageDomainId());
            editedVM.setVmCreationDate(getVm().getVmCreationDate());
            editedVM.setCreatedByUserId(getVm().getCreatedByUserId());
            getParameters().setVm(editedVM);
            getParameters().setVmId(editedVM.getId());

            ActionReturnValue returnValue = runInternalActionWithTasksContext(
                  ActionType.UpdateVm,
                  getParameters(),
                  getLock());

          if (!returnValue.getSucceeded()) {
              if (!returnValue.isValid()) {
                  throw new EngineException(EngineError.ENGINE, returnValue.getValidationMessages().stream().reduce("", String::concat));
              }
              throw new EngineException(returnValue.getFault().getError(), returnValue.getFault().getMessage());
          }
        }
    }

    @Override
    protected LockProperties applyLockProperties(LockProperties lockProperties) {
        return lockProperties.withScope(Scope.Command);
    }

    @Override
    protected void logErrorOneOrMoreActiveDomainsAreMissing() {
        log.error("Can not found any default active domain for one of the disks of snapshot with id '{}'",
                oldVmId);
    }

    @Override
    protected Guid getStoragePoolIdFromSourceImageContainer() {
        return getVm().getStoragePoolId();
    }

    @Override
    protected Map<String, Pair<String, String>> getSharedLocks() {
        Map<String, Pair<String, String>> locks = new HashMap<>();

        for (DiskImage image: getImagesToCheckDestinationStorageDomains()) {
            locks.put(image.getId().toString(),
                    LockMessagesMatchUtil.makeLockingPair(LockingGroup.DISK, getDiskSharedLockMessage()));
        }

        locks.put(getSourceVmFromDb().getId().toString(),
                LockMessagesMatchUtil.makeLockingPair(LockingGroup.VM, EngineMessage.ACTION_TYPE_FAILED_VM_IS_BEING_CLONED));

        return locks;
    }

    @Override
    protected Collection<DiskImage> getAdjustedDiskImagesFromConfiguration() {
        if (diskImagesFromConfiguration == null) {
            QueryReturnValue vdcReturnValue = runInternalQuery(
                    QueryType.GetAllDisksByVmId,
                    new IdQueryParameters(oldVmId));

            List<Disk> loadedImages = vdcReturnValue.getReturnValue() != null ? (List<Disk>) vdcReturnValue.getReturnValue() : new ArrayList<>();

            diskImagesFromConfiguration = DisksFilter.filterImageDisks(loadedImages, ONLY_SNAPABLE, ONLY_ACTIVE);
            diskImagesFromConfiguration.addAll(DisksFilter.filterCinderDisks(loadedImages, ONLY_PLUGGED));
            diskImagesFromConfiguration.addAll(DisksFilter.filterManagedBlockStorageDisks(loadedImages, ONLY_PLUGGED));
        }
        return diskImagesFromConfiguration;
    }

    @Override
    public Map<String, String> getJobMessageProperties() {
        if (jobProperties == null) {
            jobProperties = super.getJobMessageProperties();
            jobProperties.put(VdcObjectType.VM.name().toLowerCase(),
                    StringUtils.defaultString(getParameters().getNewName()));
            jobProperties.put("sourcevm",
                    StringUtils.defaultString(getSourceVmFromDb().getName()));
        }
        return jobProperties;
    }

    @Override
    protected Guid getSourceVmId() {
        return oldVmId;
    }

    @Override
    protected VM getVmFromConfiguration() {
        return getVm();
    }

    @Override
    protected VM getSourceVmFromDb() {
        if (sourceVm == null) {
            sourceVm = vmDao.get(oldVmId);
        }

        return sourceVm;
    }

    @Override
    public VM getVm() {
        if (vm == null) {
            vm = vmDao.get(oldVmId);
            getVmDeviceUtils().setVmDevices(vm.getStaticData());
            vmHandler.updateDisksFromDb(vm);
            vmHandler.updateVmGuestAgentVersion(vm);
            vmHandler.updateNetworkInterfacesFromDb(vm);
            vmHandler.updateVmInitFromDB(vm.getStaticData(), true);

            vm.setName(getParameters().getNewName());
            vm.setId(getVmId());
        }

        return vm;
    }

    @Override
    protected VmInit loadOriginalVmInitWithRootPassword() {
        return vmInitDao.get(oldVmId);
    }

    private void fillDisksToParameters() {

        for (Disk image : getAdjustedDiskImagesFromConfiguration()) {
                diskInfoDestinationMap.put(image.getId(), (DiskImage) image);
        }

        getParameters().setDiskInfoDestinationMap(diskInfoDestinationMap);
    }

    @Override
    protected void removeVmRelatedEntitiesFromDb() {
        detachDisks();
        super.removeVmRelatedEntitiesFromDb();
    }

    @Override
    protected boolean addVmImages() {
        if (super.addVmImages()) {
            attachDisks();
            return true;
        }

        return false;
    }

    private void detachDisks() {
        attachDetachDisks(ActionType.DetachDiskFromVm);
    }

    private void attachDisks() {
        attachDetachDisks(ActionType.AttachDiskToVm);
    }

    private void attachDetachDisks(ActionType actionType) {
        QueryReturnValue vdcReturnValue = runInternalQuery(
                QueryType.GetAllDisksByVmId,
                new IdQueryParameters(oldVmId));

        List<Disk> loadedImages = vdcReturnValue.getReturnValue() != null ? (List<Disk>) vdcReturnValue.getReturnValue() : new ArrayList<>();

        for (Disk disk : loadedImages) {
            if (disk.isShareable()) {
                attachDetachDisk(disk, actionType);
            }
        }
    }

    private void attachDetachDisk(Disk disk, ActionType actionType) {
        DiskVmElement oldDve = disk.getDiskVmElementForVm(oldVmId);
        DiskVmElement newDve = new DiskVmElement(disk.getId(), getParameters().getNewVmGuid());
        newDve.setDiskInterface(oldDve.getDiskInterface());
        runInternalAction(
                actionType,
                new AttachDetachVmDiskParameters(newDve, oldDve.isPlugged())
        );
    }

    private void setupParameters() {
        if (getParameters().isEdited()) {
            editedVM = getParameters().getVm();
        }

        setVmId(Guid.newGuid());
        getParameters().setNewVmGuid(getVmId());
        VM vmToClone = getVm();
        getParameters().setVm(vmToClone);

        if (!getParameters().isEdited()) {
            List<VmDevice> devices = vmDeviceDao.getVmDeviceByVmId(oldVmId);
            vmDeviceUtils.updateVmDevicesInParameters(getParameters(), devices);
            fillDisksToParameters();
        }
    }

    @Override
    public List<PermissionSubject> getPermissionCheckSubjects() {
        List<PermissionSubject> permissionList = new ArrayList<>();
        permissionList.add(new PermissionSubject(getParameters().getVmId(),
                VdcObjectType.VM,
                getActionType().getActionGroup()));

        return permissionList;
    }

    @Override
    protected boolean validate() {
        if (!(getSourceVmFromDb().getStatus() == VMStatus.Suspended || getSourceVmFromDb().isDown())) {
            return failValidation(EngineMessage.ACTION_TYPE_FAILED_VM_IS_NOT_DOWN);
        }

        return super.validate();
    }

    @Override
    protected void updateOriginalTemplate(VmStatic vmStatic) {
        vmStatic.setOriginalTemplateGuid(getVm().getOriginalTemplateGuid());
        vmStatic.setOriginalTemplateName(getVm().getOriginalTemplateName());
    }
}
