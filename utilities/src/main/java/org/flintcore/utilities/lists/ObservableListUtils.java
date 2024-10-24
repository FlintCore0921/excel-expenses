package org.flintcore.utilities.lists;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.util.Subscription;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

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
    public static <T> Pair<ObservableList<T>, List<Subscription>> concat(
            ObservableList<? extends T>... lists
    ) {
        ObservableList<T> observable = FXCollections.observableArrayList();
        List<Subscription> subscriptions = Arrays.stream(lists)
                .map(l -> listenList(l, observable))
                .toList();

        return Pair.ofNonNull(observable, subscriptions);
    }

    public static <T> Subscription listenList(
            ObservableList<? extends T> list,
            ObservableList<? super T> observable
    ) {
        ListChangeListener<? super T> changeListener = change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    observable.addAll(change.getAddedSubList());
                }
                if (change.wasRemoved()) {
                    observable.removeAll(change.getRemoved());
                }
            }
        };

        observable.addAll(list);
        list.addListener(changeListener);

        return () -> list.removeListener(changeListener);
    }

    public static <T> FilteredList<T> wrapInto(ObservableList<T> list) {
        return list instanceof FilteredList<T> filtered ?
                filtered : list.filtered(null);
    }
}
