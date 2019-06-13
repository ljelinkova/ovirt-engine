package org.ovirt.engine.ui.uicommonweb.models.templates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.ovirt.engine.core.common.businessentities.VmTemplate;
import org.ovirt.engine.core.common.businessentities.comparators.DiskByDiskAliasComparator;
import org.ovirt.engine.core.common.businessentities.storage.DiskImage;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;
import org.ovirt.engine.ui.uicommonweb.models.vms.ImportTemplateData;

@SuppressWarnings("unused")
public class TemplateImportDiskListModel extends SearchableListModel {
    private List<Map.Entry<VmTemplate, List<DiskImage>>> extendedItems;
    private Map<String, List<DiskImage>> extendedItemsMap;
    private ImportTemplateData editedTemplateData;

    public TemplateImportDiskListModel() {
        setIsTimerDisabled(true);
    }

    @Override
    protected void onEntityChanged() {
        super.onEntityChanged();

        if (getEntity() != null) {
            ArrayList<DiskImage> list = new ArrayList<>();
            if(extendedItems != null) {
                for (Map.Entry<VmTemplate, List<DiskImage>> item : extendedItems) {
                    VmTemplate template = (VmTemplate) getEntity();
                    if (item.getKey().getQueryableId().equals(template.getQueryableId())) {
                        list.addAll(item.getValue());
                        Collections.sort(list, new DiskByDiskAliasComparator());
                        setItems(list);
                        return;
                    }
                }
            }
            if (extendedItemsMap != null) {
                setItems(extendedItemsMap.get(editedTemplateData.getUniqueID()));
            }
        } else {
            setItems(null);
        }
    }

    @Override
    public void setEntity(Object value) {
        if (value != null && value instanceof ImportTemplateData) {
            this.editedTemplateData = (ImportTemplateData) value;
            super.setEntity(editedTemplateData.getTemplate(), true);
        } else {
            super.setEntity(null);
        }
    }

    public void setExtendedItems(List<Map.Entry<VmTemplate, List<DiskImage>>> arrayList) {
        this.extendedItems = arrayList;
        this.extendedItemsMap = null;
    }

    public void setExtendedItems(Map<String, List<DiskImage>> map) {
        this.extendedItems = null;
        this.extendedItemsMap = map;
    }

    @Override
    protected void syncSearch() {
    }

    @Override
    protected String getListName() {
        return "TemplateImportDiskListModel"; //$NON-NLS-1$
    }
}
