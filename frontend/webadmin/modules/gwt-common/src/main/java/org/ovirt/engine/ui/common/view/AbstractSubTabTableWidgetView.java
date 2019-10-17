package org.ovirt.engine.ui.common.view;

import org.ovirt.engine.ui.common.idhandler.WithElementId;
import org.ovirt.engine.ui.common.presenter.AbstractSubTabPresenter;
import org.ovirt.engine.ui.common.presenter.PlaceTransitionHandler;
import org.ovirt.engine.ui.common.widget.table.SimpleActionTable;
import org.ovirt.engine.ui.common.widget.uicommon.AbstractModelBoundTableWidget;
import org.ovirt.engine.ui.uicommonweb.models.ListWithDetailsModel;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;

import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * Base class for sub tab views that use {@linkplain AbstractModelBoundTableWidget model-bound table widgets}.
 *
 * @param <E>
 *            Main tab table row data type.
 * @param <T>
 *            Sub tab table row data type.
 * @param <M>
 *            Main model type.
 * @param <D>
 *            Detail model type.
 */
public class AbstractSubTabTableWidgetView<E, T, M extends ListWithDetailsModel<?, E>, D extends SearchableListModel<E, T>>
    extends AbstractView implements AbstractSubTabPresenter.ViewDef<E> {

    private static final String OBRAND_DETAIL_TAB = "obrand_detail_tab"; // $NON-NLS-1$

    private final AbstractModelBoundTableWidget<E, T, D> modelBoundTableWidget;

    private PlaceTransitionHandler placeTransitionHandler;

    @WithElementId
    public final SimpleActionTable<E, T, D> table;

    public AbstractSubTabTableWidgetView(AbstractModelBoundTableWidget<E, T, D> modelBoundTableWidget) {
        this.modelBoundTableWidget = modelBoundTableWidget;
        this.table = modelBoundTableWidget.getTable();
    }

    @Override
    public HandlerRegistration addWindowResizeHandler(ResizeHandler handler) {
        return Window.addResizeHandler(handler);
    }

    @Override
    public void resizeToFullHeight() {
        int tableTop = table.getTableAbsoluteTop();
        if (tableTop > 0) {
            table.setMaxGridHeight(Window.getClientHeight() - tableTop);
            table.updateGridSize();
        }
    }

    @Override
    protected void initWidget(IsWidget widget) {
        super.initWidget(widget);
        asWidget().addStyleName(OBRAND_DETAIL_TAB);
    }

    @Override
    public IsWidget getTableContainer() {
        return modelBoundTableWidget;
    }

    protected AbstractModelBoundTableWidget<E, T, D> getModelBoundTableWidget() {
        return modelBoundTableWidget;
    }

    @Override
    public SimpleActionTable<E, T, D> getTable() {
        return modelBoundTableWidget.getTable();
    }

    protected void initTable() {
        getModelBoundTableWidget().initTable();
    }

    @Override
    public void setMainSelectedItem(E selectedItem) {
        // No-op since table-based sub tab views don't handle main tab selection on their own
    }

    @Override
    public void setPlaceTransitionHandler(PlaceTransitionHandler handler) {
        placeTransitionHandler = handler;
        getModelBoundTableWidget().setPlaceTransitionHandler(handler);
    }

    protected PlaceTransitionHandler getPlaceTransitionHandler() {
        return placeTransitionHandler;
    }
}
