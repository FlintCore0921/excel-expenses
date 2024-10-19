package org.flintcore.utilities.lists;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public final class ObservableListUtils {
    private ObservableListUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    /**
     * Concat and listen changes in the provided lists.
     *
     * @param lists lists to be added and updated
     */
    @SafeVarargs
    public static <T> ObservableList<T> concat(ObservableList<? extends T>... lists) {
        ObservableList<T> observable = FXCollections.observableArrayList();

        for (ObservableList<? extends T> list : lists) {
            list.addListener((ListChangeListener<? super T>) change -> {
                while (change.next()) {
                    if (change.wasAdded()) {
                        observable.addAll(change.getAddedSubList());
                    }
                    if (change.wasRemoved()) {
                        observable.removeAll(change.getRemoved());
                    }
                }
            });
        }

        return observable;
    }

    public static <T> FilteredList<T> wrapInto(ObservableList<T> list) {
        return list instanceof FilteredList<T> filtered ?
                filtered : list.filtered(null);
    }
}
