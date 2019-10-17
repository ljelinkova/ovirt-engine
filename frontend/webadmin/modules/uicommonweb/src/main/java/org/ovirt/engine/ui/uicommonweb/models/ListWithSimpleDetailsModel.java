package org.ovirt.engine.ui.uicommonweb.models;

/**
 * Simple {@link org.ovirt.engine.ui.uicommonweb.models.ListWithDetailsModel}
 * that has the same detail entity as its list elements.
 *
 * @param <E> The type of the main entity.
 * @param <D> The type of the detail model entity
 * @param <T> The type of the detail entities
 */
public abstract class ListWithSimpleDetailsModel<E, T> extends ListWithDetailsModel<E, T> {
    @Override
    protected T provideDetailModelEntity(T selectedItem) {
        return selectedItem;
    }
}
