package org.ovirt.engine.ui.uicommonweb.models;

import java.util.ArrayList;
import java.util.List;

import org.ovirt.engine.core.common.businessentities.VDS;
import org.ovirt.engine.ui.uicommonweb.models.hosts.HostInterfaceListModel;
import org.ovirt.engine.ui.uicompat.PropertyChangedEventArgs;

/**
 *
 * List with main entity, detail entities and sub detail entities.
 *
 * @param <E> The type of the main entity.
 * @param <D> The type of the detail entity
 * @param <T> The type of the sub detail entities
 */
public abstract class ListWithDetailsModel<E, T> extends SearchableListModel<E, T> {

    private static final String ACTIVE_DETAIL_MODELS = "ActiveDetailModels"; //$NON-NLS-1$
    private static final String DETAIL_MODELS = "DetailModels"; // $NON-NLS-1$

    private List<HasEntity<T>> detailModels;

    public List<HasEntity<T>> getDetailModels() {
        return detailModels;
    }

    public void setDetailModels(List<HasEntity<T>> value) {
        if (detailModels != value) {
            detailModels = value;
            onPropertyChanged(new PropertyChangedEventArgs(DETAIL_MODELS));
        }
    }

    private List<HasEntity<T>> activeDetailModels = new ArrayList<>();

    public HasEntity<T> getActiveDetailModel() {
        return activeDetailModels.isEmpty() ? null : activeDetailModels.get(0);
    }

    public void ensureActiveDetailModel() {
        if (getActiveDetailModel() == null) {
            setActiveDetailModel(getDetailModels().get(0));
        }
    }

    public void setActiveDetailModel(HasEntity<T> value) {
        if (!activeDetailModels.contains(value)) {
            activeDetailModelChanging(value, true);
            activeDetailModels.clear();
            activeDetailModels.add(value);
            activeDetailModelChanged();
            onPropertyChanged(new PropertyChangedEventArgs(ACTIVE_DETAIL_MODELS));
        }
    }

    public void addActiveDetailModel(HasEntity<T> value) {
        if (!activeDetailModels.contains(value)) {
            activeDetailModelChanging(value, false);
            activeDetailModels.add(value);
            activeDetailModelChanged();
            onPropertyChanged(new PropertyChangedEventArgs(ACTIVE_DETAIL_MODELS));
        }
    }

    protected void updateDetailsAvailability() {
    }

    private void activeDetailModelChanging(HasEntity<T> newValue, boolean stopRefresh) {
        if (stopRefresh) {
            for (HasEntity<T> oldValue : activeDetailModels) {
                // Make sure we had set an entity property of details model.
                if (oldValue != null) {
                    oldValue.setEntity(null);

                    if (oldValue instanceof SearchableListModel) {
                        ((SearchableListModel) oldValue).stopRefresh();
                    }
                }
            }
        }

        if (newValue != null) {
            newValue.setEntity(provideDetailModelEntity(getSelectedItem()));
        }
    }

    protected abstract T provideDetailModelEntity(T selectedItem);

    @Override
    protected void onSelectedItemChanged() {
        super.onSelectedItemChanged();

        if (getSelectedItem() != null) {
            // Try to choose default (first) detail model.
            updateDetailsAvailability();
            if (getDetailModels() != null) {
                if ((getActiveDetailModel() != null && !getActiveDetailModel().getIsAvailable())
                        || getActiveDetailModel() == null) {
                    HasEntity<T> model = null;
                    for (HasEntity<T> item : getDetailModels()) {
                        if (item.getIsAvailable()) {
                            model = item;
                            break;
                        }
                    }
                    setActiveDetailModel(model);
                }
            }
        } else {
            // If selected item become null, make sure we stop all activity on an active detail model.
            if (getActiveDetailModel() != null && getActiveDetailModel() instanceof SearchableListModel) {
                ((SearchableListModel) getActiveDetailModel()).stopRefresh();
            }
        }

        // Synchronize selected item with the entity of an active details model.
        HasEntity<T> activeDetailModel = getActiveDetailModel();
        if (getSelectedItem() != null && activeDetailModel != null) {
            if (activeDetailModel instanceof HostInterfaceListModel) {
                ((HostInterfaceListModel) activeDetailModel).setEntity((VDS) provideDetailModelEntity(getSelectedItem()));
            } else {
                activeDetailModel.setEntity(provideDetailModelEntity(getSelectedItem()));
            }
        }
    }

    protected void activeDetailModelChanged() {
    }

    @Override
    public void stopRefresh() {
        super.stopRefresh();

        if (getDetailModels() != null) {
            // Stop search on all list models.
            for (HasEntity<T> model : getDetailModels()) {
                if (model instanceof SearchableListModel) {
                    SearchableListModel listModel = (SearchableListModel) model;
                    listModel.stopRefresh();
                }
            }
        }
    }

}
