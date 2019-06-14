package org.ovirt.engine.ui.uicommonweb.models.templates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ovirt.engine.core.common.action.ActionParametersBase;
import org.ovirt.engine.core.common.action.ActionType;
import org.ovirt.engine.core.common.action.ImportVmTemplateFromOvaParameters;
import org.ovirt.engine.core.common.businessentities.StoragePool;
import org.ovirt.engine.core.common.businessentities.VmTemplate;
import org.ovirt.engine.core.common.businessentities.comparators.LexoNumericNameableComparator;
import org.ovirt.engine.core.common.businessentities.profiles.CpuProfile;
import org.ovirt.engine.core.common.businessentities.storage.DiskImage;
import org.ovirt.engine.core.compat.Guid;
import org.ovirt.engine.ui.frontend.AsyncCallback;
import org.ovirt.engine.ui.frontend.Frontend;
import org.ovirt.engine.ui.uicommonweb.UICommand;
import org.ovirt.engine.ui.uicommonweb.dataprovider.AsyncDataProvider;
import org.ovirt.engine.ui.uicommonweb.models.EntityModel;
import org.ovirt.engine.ui.uicommonweb.models.clusters.ClusterListModel;
import org.ovirt.engine.ui.uicommonweb.models.quota.QuotaListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.ImportTemplateData;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmImportAppListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmImportDiskListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.VmImportInterfaceListModel;
import org.ovirt.engine.ui.uicompat.ConstantsManager;
import org.ovirt.engine.ui.uicompat.IFrontendMultipleActionAsyncCallback;
import org.ovirt.engine.ui.uicompat.UIConstants;

import com.google.inject.Inject;

public class ImportTemplateFromOvaModel extends ImportTemplateFromExportDomainModel {

    protected String ovaPath;
    protected Guid hostId;

    @Inject
    public ImportTemplateFromOvaModel(VmImportDiskListModel vmImportDiskListModel,
            ClusterListModel<Void> cluster,
            QuotaListModel clusterQuota,
            TemplateImportGeneralModel templateImportGeneralModel,
            VmImportInterfaceListModel vmImportInterfaceListModel,
            VmImportAppListModel vmImportAppListModel,
            TemplateImportDiskListModel templateImportDiskListModel,
            TemplateImportInterfaceListModel templateImportInterfaceListModel) {
        super(vmImportDiskListModel,
                cluster,
                clusterQuota,
                templateImportGeneralModel,
                vmImportInterfaceListModel,
                vmImportAppListModel,
                templateImportDiskListModel,
                templateImportInterfaceListModel);
    }

    @Override
    public void executeImport(IFrontendMultipleActionAsyncCallback callback) {
        Frontend.getInstance().runMultipleAction(
                ActionType.ImportVmTemplateFromOva,
                buildImportTemplateFromOvaParameters(),
                true,
                callback,
                null);
    }

    private List<ActionParametersBase> buildImportTemplateFromOvaParameters() {
        List<ActionParametersBase> prms = new ArrayList<>();
        for (Object item : getItems()) {
            ImportTemplateData importTemplateData = (ImportTemplateData) item;
            VmTemplate template = importTemplateData.getTemplate();

            ImportVmTemplateFromOvaParameters prm = new ImportVmTemplateFromOvaParameters(
                    template,
                    Guid.Empty,
                    getStoragePool().getId(),
                    getCluster().getSelectedItem().getId());
            prm.setOvaPath(ovaPath + "/" + importTemplateData.getUniqueID()); //$NON-NLS-1$
            prm.setProxyHostId(hostId);

            if (getClusterQuota().getSelectedItem() != null &&
                    getClusterQuota().getIsAvailable()) {
                prm.setQuotaId(getClusterQuota().getSelectedItem().getId());
            }

            CpuProfile cpuProfile = getCpuProfiles().getSelectedItem();
            if (cpuProfile != null) {
                prm.setCpuProfileId(cpuProfile.getId());
            }

            Map<Guid, Guid> map = new HashMap<>();
            for (DiskImage disk : template.getDiskList()) {
                map.put(disk.getId(), getDiskImportData(disk.getId()).getSelectedStorageDomain().getId());

                if (getDiskImportData(disk.getId()).getSelectedQuota() != null) {
                    disk.setQuotaId(getDiskImportData(disk.getId()).getSelectedQuota().getId());
                }
            }
            prm.setImageToDestinationDomainMap(map);

            if (importTemplateData.isExistsInSystem() || importTemplateData.getClone().getEntity()) {
                ImportTemplateData clonedTemplateData = (ImportTemplateData) cloneObjectMap.get(template.getId());
                if (clonedTemplateData == null) {
                    continue;
                }
                prm.setImportAsNewEntity(true);
                prm.getVmTemplate().setName(clonedTemplateData.getTemplate().getName());
            }

            prms.add(prm);
        }
        return prms;
    }

    public void setIsoName(String ovaPath) {
        this.ovaPath = ovaPath;
    }

    public void setHostId(Guid hostId) {
        this.hostId = hostId;
    }

    public void init(final Collection<VmTemplate> externalTemplates, final Guid dataCenterId) {

    }

    public void initTemplates(final Map<String, VmTemplate> externalTemplates, final Guid dataCenterId) {
        setCloseCommand(new UICommand(null, this)
                .setTitle(ConstantsManager.getInstance().getConstants().close())
                .setIsDefault(true)
                .setIsCancel(true));

        Map<String, List<DiskImage>> templateToDisks = new HashMap<String, List<DiskImage>>();

        for (Entry<String, VmTemplate> entry : externalTemplates.entrySet()) {
            List<DiskImage> disks = entry.getValue().getDiskList();
            disks.sort(new LexoNumericNameableComparator<>());
            templateToDisks.put(entry.getKey(), disks);
        }

        ((TemplateImportDiskListModel) getImportDiskListModel()).setExtendedItems(templateToDisks);

        AsyncDataProvider.getInstance().getTemplateList(
                createSearchPattern(externalTemplates.values()),
                new AsyncQuery<>(returnValue -> {
                        UIConstants constants = ConstantsManager.getInstance().getConstants();
                        List<ImportTemplateData> templateDataList = new ArrayList<>();
                        List<VmTemplate> vmtList = returnValue.getReturnValue();
                        for (Entry<String, VmTemplate> entry : externalTemplates.entrySet()) {
                            ImportTemplateData templateData = new ImportTemplateData(entry.getValue());
                            templateData.setUniqueID(entry.getKey());
                            boolean templateExistsInSystem = vmtList.contains(entry.getValue());
                            templateData.setExistsInSystem(templateExistsInSystem);
                            if (templateExistsInSystem) {
                                templateData.enforceClone(constants.importTemplateThatExistsInSystemMustClone());
                            } else if (!entry.getValue().isBaseTemplate() &&
                                    vmtList.stream().anyMatch(t -> t.getId().equals(entry.getValue().getBaseTemplateId()))) {
                                templateData.enforceClone(constants.importTemplateWithoutBaseMustClone());
                            }
                            templateDataList.add(templateData);
                        }
                        setItems(templateDataList);
                        withDataCenterLoaded(r -> doInit(), dataCenterId);
                }));
    }

    protected void withDataCenterLoaded(final AsyncCallback<StoragePool> callback, Guid storagePoolId) {
        // get Storage pool
        AsyncDataProvider.getInstance().getDataCenterById(new AsyncQuery<>(dataCenter -> {
            setStoragePool(dataCenter);
            callback.onSuccess(dataCenter);
        }), storagePoolId);
    }

    @Override
    protected void initDisksStorageDomainsList() {
        for (Object item : getItems()) {
            VmTemplate template = ((ImportTemplateData) item).getTemplate();
            for (DiskImage diskImage : template.getDiskTemplateMap().values()) {
                addDiskImportData(diskImage.getId(),
                        filteredStorageDomains, diskImage.getVolumeType(), new EntityModel(true));
            }
        }
        postInitDisks();
    }
}
