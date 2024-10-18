package org.flintcore.utilities.lists;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public final class ObservableListUtils {
    private ObservableListUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    @SafeVarargs
    public static <T> ObservableList<T> concat(ObservableList<? extends T>... lists) {
        ObservableList<T> observabled = FXCollections.observableArrayList();

        for (ObservableList<? extends T> list : lists) {
            list.addListener((ListChangeListener<? super T>) change -> {
                    while(change.next()){
                        if (change.wasAdded()) {
                            observabled.addAll(change.getAddedSubList());
                        }
                        if (change.wasRemoved()) {
                            observabled.removeAll(change.getRemoved());
                        }
                    }
            });

            observabled.addAll(list);
        }

        return observabled;
    }
}
