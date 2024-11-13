package org.flintcore.excel_expenses.excels_handler.models.observables.collections;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FilteredMapTest {
    private FilteredMap<Integer, String> filteredMap;
    private ObservableMap<Integer, String> objectObservableMap;

    @BeforeEach
    void setUp() {
        objectObservableMap = FXCollections.observableHashMap();
        filteredMap = new FilteredMap<>(objectObservableMap);
    }

    @Test
    void createMapEmpty() {
        assertNotNull(this.filteredMap);
        assertNotNull(this.objectObservableMap);
    }

    @Test
    void createMapAppendData() {
        printUpdatesInFilter();

        final int counter = 5;

        appendNumericItems(counter);

        assertEquals(counter, this.filteredMap.size());
    }

    @Test
    void createMapAppendNFilter() {
        printUpdatesInFilter();

        final int counter = 10;

        appendNumericItems(counter);

        final int reducerKeyValue = 6;
        this.filteredMap.filterByKey(key -> key > reducerKeyValue);

        int actualSize = counter - reducerKeyValue - 1;

        assertNotEquals(counter, this.filteredMap.size());
        assertEquals(actualSize, this.filteredMap.size());
    }

    private void printUpdatesInFilter() {
        this.filteredMap.addListener((MapChangeListener<? super Integer, ? super String>) changes -> {
            String action = changes.wasAdded() ? "added" : "removed";
            System.out.printf("%s %s%n", action, changes.getKey());
        });
    }

    private void appendNumericItems(int counter) {
        for (int key = 0; key < counter; key++) {
            this.objectObservableMap.put(key, key + "__");
        }
    }
}