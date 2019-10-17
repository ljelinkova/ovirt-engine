package org.ovirt.engine.ui.common.widget.action;

import org.ovirt.engine.ui.common.uicommon.model.SearchableModelProvider;
import org.ovirt.engine.ui.uicommonweb.models.SearchableListModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.Widget;

public class SimpleActionPanel<E, T, M extends SearchableListModel<E, T>> extends AbstractActionPanel<E, T, M> {

    interface WidgetUiBinder extends UiBinder<Widget, SimpleActionPanel<?, ?, ?>> {
        WidgetUiBinder uiBinder = GWT.create(WidgetUiBinder.class);
    }

    @UiField
    ButtonBase refreshButton;

    public SimpleActionPanel(SearchableModelProvider<E, T, M> dataProvider) {
        super(dataProvider);
        initWidget(WidgetUiBinder.uiBinder.createAndBindUi(this));
    }

    @UiHandler("refreshButton")
    void handleRefreshButtonClick(ClickEvent event) {
        getDataProvider().getModel().getForceRefreshCommand().execute();
    }
}
