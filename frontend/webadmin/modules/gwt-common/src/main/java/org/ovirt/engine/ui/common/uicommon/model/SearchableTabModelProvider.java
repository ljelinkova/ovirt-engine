package org.ovirt.engine.ui.common.uicommon.model;

import org.ovirt.engine.ui.common.presenter.popup.DefaultConfirmationPopupPresenterWidget;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Provider;

/**
 * Default {@link SearchableTableModelProvider} implementation for use with tab controls.
 *
 * @param <E>
 *            Main entity type.
 * @param <T>
 *            Detail entity type.
 * @param <M>
 *            List model type.
 */
public abstract class SearchableTabModelProvider<E, T, M extends SearchableListModel<E, T>> extends DataBoundTabModelProvider<E, T, M> implements SearchableTableModelProvider<E, T, M> {

    public SearchableTabModelProvider(EventBus eventBus,
            Provider<DefaultConfirmationPopupPresenterWidget> defaultConfirmPopupProvider) {
        super(eventBus, defaultConfirmPopupProvider);
    }

    @Override
    protected void clearData() {
        // Remove locally cached row data and enforce "loading" state
        getDataProvider().updateRowCount(0, false);
    }
}
