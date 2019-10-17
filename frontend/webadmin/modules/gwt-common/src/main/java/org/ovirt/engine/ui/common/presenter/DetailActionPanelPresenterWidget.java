package org.ovirt.engine.ui.common.presenter;

import javax.inject.Inject;

import org.ovirt.engine.ui.common.uicommon.model.SearchableDetailModelProvider;
import org.ovirt.engine.ui.common.uicommon.model.SearchableTabModelProvider;
import org.ovirt.engine.ui.uicommonweb.models.ListWithDetailsModel;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;

import com.google.web.bindery.event.shared.EventBus;

/**
 * Represents the detail action panel that contains action buttons.
 *
 * @param <E> The type of the main entity
 * @param <T> The type of the detail entity
 * @param <M> Main entity searchable list model
 * @param <D> Detail entity searchable list model
 */
public abstract class DetailActionPanelPresenterWidget<E, T, M extends ListWithDetailsModel<?, E>, D extends SearchableListModel<E, T>>
    extends ActionPanelPresenterWidget<E, T, D> {

    public interface ViewDef<T> extends ActionPanelPresenterWidget.ViewDef<T> {
    }

    @Inject
    public DetailActionPanelPresenterWidget(EventBus eventBus,
            DetailActionPanelPresenterWidget.ViewDef<T> view,
            SearchableDetailModelProvider<E, T, M, D> dataProvider) {
        super(eventBus, view, (SearchableTabModelProvider<E, T, D>) dataProvider);
    }

    protected D getDetailModel() {
        return (D) ((SearchableDetailModelProvider<E, T, ?, ?>)getDataProvider()).getModel();
    }

}
