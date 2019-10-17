package org.ovirt.engine.ui.common.uicommon.model;

import org.ovirt.engine.ui.common.presenter.popup.DefaultConfirmationPopupPresenterWidget;
import org.ovirt.engine.ui.uicommonweb.models.ListWithDetailsModel;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;

import com.google.gwt.event.shared.EventBus;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Default {@link DetailModelProvider} implementation for use with tab controls.
 *
 * @param <M> Main model type (extends ListWithDetailsModel)
 * @param <D> Detail model type (extends EntityModel)
 */
public class DetailTabModelProvider<E, T, M extends ListWithDetailsModel<?, E>, D extends SearchableListModel<E, T>> extends TabModelProvider<E, D> implements DetailModelProvider<E, M, D> {

    private Provider<M> mainModelProvider;

    @Inject
    public DetailTabModelProvider(EventBus eventBus,
            Provider<DefaultConfirmationPopupPresenterWidget> defaultConfirmPopupProvider) {
        super(eventBus, defaultConfirmPopupProvider);
    }

    @Override
    public M getMainModel() {
        return mainModelProvider.get();
    }

    @Override
    public void onSubTabSelected() {
        getMainModel().setActiveDetailModel(getModel());
    }

    @Override
    public void activateDetailModel() {
        getMainModel().addActiveDetailModel(getModel());
    }

    @Override
    public void onSubTabDeselected() {
        getMainModel().setActiveDetailModel(null);
    }

    @Inject
    public void setMainModelProvider(Provider<M> mainModelProvider) {
        this.mainModelProvider = mainModelProvider;
    }
}
