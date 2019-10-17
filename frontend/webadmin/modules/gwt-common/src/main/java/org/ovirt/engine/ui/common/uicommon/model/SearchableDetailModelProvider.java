package org.ovirt.engine.ui.common.uicommon.model;

import org.ovirt.engine.ui.uicommonweb.models.ListWithDetailsModel;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;

/**
 * Provider of searchable detail model instances.
 * <p>
 * Contains main model type information to distinguish detail models of the same type for different main models.
 *
 * @param <E> Main entity item type.
 * @param <T> Detail item type.
 * @param <M> Main model type (extends ListWithDetailsModel).
 * @param <D> Detail model type (extends SearchableListModel).
 */
public interface SearchableDetailModelProvider<E, T, M extends ListWithDetailsModel<?, E>, D extends SearchableListModel<E, T>> extends DetailModelProvider<E, M, D>, SearchableTableModelProvider<E, T, D> {
}
