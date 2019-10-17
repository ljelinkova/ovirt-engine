package org.ovirt.engine.ui.common.uicommon.model;

import org.ovirt.engine.ui.common.widget.table.ActionTableDataProvider;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;

/**
 * Provider of {@link SearchableListModel} instances that is also an {@link ActionTableDataProvider}.
 *
 * @param <E>
 *            Main entity type.
 * @param <T>
 *            Detail entity type.
 * @param <M>
 *            List model type.
 */
public interface SearchableTableModelProvider<E, T, D extends SearchableListModel<E, T>> extends SearchableModelProvider<E, T, D>, ActionTableDataProvider<T> {
}
