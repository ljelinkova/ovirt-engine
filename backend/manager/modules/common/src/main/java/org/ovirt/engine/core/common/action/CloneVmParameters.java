package org.ovirt.engine.core.common.action;

import org.ovirt.engine.core.common.businessentities.VM;
import org.ovirt.engine.core.compat.Guid;

public class CloneVmParameters extends AddVmParameters {

    private static final long serialVersionUID = 4851787064680308265L;

    private Guid newVmGuid;

    private String newName;

    private boolean isEdited;

    public CloneVmParameters() {

    }

    public CloneVmParameters(VM vm, String newName) {
        super(vm);
        this.newName = newName;
    }

    public CloneVmParameters(VM vm, String newName, boolean isEdited) {
        super(vm);
        this.newName = newName;
        this.isEdited = isEdited;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public Guid getNewVmGuid() {
        return newVmGuid;
    }

    public void setNewVmGuid(Guid newVmGuid) {
        this.newVmGuid = newVmGuid;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }
}
