package org.flintcore.excel_expenses.models.observables.collections;

/**
 * Listener interface to handle changes
 * and updates inside any observable collection listeners.
 */
public abstract class OnChangeListener<V> {
    public void onAdded(V value) {}

    public void onRemove(V value) {}

    public void onUpdated(V value) {}
}
