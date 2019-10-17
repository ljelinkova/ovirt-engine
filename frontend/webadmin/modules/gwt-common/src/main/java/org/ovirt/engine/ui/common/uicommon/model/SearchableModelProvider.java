package org.ovirt.engine.ui.common.uicommon.model;

import java.util.List;

import org.ovirt.engine.ui.uicommonweb.models.HasEntity;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;

/**
 * Provider of {@link SearchableListModel} instances.
 *
 * @param <T>
 *            List model item type.
 * @param <M>
 *            List model type.
 */
public interface SearchableModelProvider<E, T, D extends SearchableListModel<E, T>> extends ModelProvider<E, D> {

    /**
     * Updates the item selection of the model.
     * @param items The list of items to select.
     */
    void setSelectedItems(List<T> items);

    /**
     * Implement this method if you wish to do anything additional when manually refreshing.
     */
    void onManualRefresh();
}
