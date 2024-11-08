package org.flintcore.utilities.lists;

import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.util.Subscription;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collection;
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
                .map(l -> listenSet(observable, l))
                .toList();

        return Pair.ofNonNull(observable, subscriptions);
    }

    /**
     * Reflects changes of a list inside another list.
     *
     * @param receptorObservable List that will be listened and will provide data to {@code observableList}.
     * @param observableList     List that receipts the data from {@code receptorObservable}.
     */
    public static <T> Subscription listenSet(
            final ObservableList<? super T> receptorObservable,
            ObservableList<? extends T> observableList
    ) throws IllegalArgumentException {
        ListChangeListener<? super T> changeListener = prepareListChangeListener(receptorObservable);

        receptorObservable.addAll(observableList);
        observableList.addListener(changeListener);

        return () -> observableList.removeListener(changeListener);
    }

    /**
     * Reflects changes of a set inside another list.
     *
     * @param receptorObservable Set that will be listened and will provide data to {@code observableSet}.
     * @param observableSet      Set that receipts the data from {@code receptorObservable}.
     */
    public static <T> Subscription listenSet(
            final ObservableSet<? super T> receptorObservable,
            ObservableSet<? extends T> observableSet
    ) {
        SetChangeListener<? super T> changeListener = prepareSetChangeListener(receptorObservable);

        receptorObservable.addAll(observableSet);
        observableSet.addListener(changeListener);

        return () -> observableSet.removeListener(changeListener);
    }

    /**
     * Reflects changes of a set inside another list.
     *
     * @param receptorObservable Set that will be listened and will provide data to {@code observableList}.
     * @param observableList     List that receipts the data from {@code receptorObservable}.
     */
    public static <T> Subscription listenList(
            final ObservableSet<? super T> receptorObservable,
            ObservableList<? extends T> observableList
    ) {
        ListChangeListener<? super T> changeListener = prepareListChangeListener(receptorObservable);

        receptorObservable.addAll(observableList);
        observableList.addListener(changeListener);

        return () -> observableList.removeListener(changeListener);
    }

    /**
     * Reflects changes of a set inside another list.
     *
     * @param receptorObservable Set that will be listened and will provide data to {@code observableSet}.
     * @param observableSet      List that receipts the data from {@code receptorObservable}.
     */
    public static <T> Subscription listenSet(
            final ObservableList<? super T> receptorObservable,
            ObservableSet<? extends T> observableSet
    ) {
        SetChangeListener<? super T> changeListener = prepareSetChangeListener(receptorObservable);

        receptorObservable.addAll(observableSet);
        observableSet.addListener(changeListener);

        return () -> observableSet.removeListener(changeListener);
    }

    public static <T, R> Subscription listenMap(
            ObservableSet<? super T> receptorObservable,
            ObservableMap<T, R> observableMap
    ) {
        MapChangeListener<? super T, ? super R> listener = change -> {
            if (change.wasAdded()) {
                receptorObservable.add(change.getKey());
            }

            if (change.wasRemoved()) {
                receptorObservable.remove(change.getKey());
            }
        };

        receptorObservable.addAll(observableMap.keySet());
        observableMap.addListener(listener);

        return () -> observableMap.removeListener(listener);
    }

    public static <T> FilteredList<T> wrapInto(ObservableList<T> list) {
        return list instanceof FilteredList<T> filtered ?
                filtered : list.filtered(null);
    }

    // Utilities

    public static <T> SetChangeListener<T> prepareSetChangeListener(Collection<? super T> receptorObservable) {
        return change -> {
            if (change.wasAdded()) {
                receptorObservable.add(change.getElementAdded());
            }

            if (change.wasRemoved()) {
                receptorObservable.remove(change.getElementRemoved());
            }
        };
    }

    public static <T> ListChangeListener<T> prepareListChangeListener(Collection<? super T> receptorObservable) {
        return change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    receptorObservable.addAll(change.getAddedSubList());
                }

                if (change.wasRemoved()) {
                    change.getRemoved().forEach(receptorObservable::remove);
                }
            }
        };
    }
}
