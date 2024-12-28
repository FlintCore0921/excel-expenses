package org.flintcore.utilities.lists;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

import java.util.concurrent.CompletableFuture;

public final class FutureFXListUtils {
    private FutureFXListUtils() {
        // Unable to instance.
        throw new UnsupportedOperationException();
    }

    public static <T> CompletableFuture<ObservableList<T>> getListFrom(ObservableSet<T> collection) {
        return CompletableFuture.supplyAsync(() -> {
            ObservableList<T> result = FXCollections.observableArrayList();
            ObservableListUtils.listenList(collection, result);

            return result;
        });
    }
}
