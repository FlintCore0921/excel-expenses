package org.flintcore.excel_expenses.excels_handler.services.receipts;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.junit.jupiter.api.Test;

class MainExpenseItemHandlerFileServiceTest {
    @Test
    void pruebaFilter() {
        ObservableList<Object> observabled = FXCollections.observableArrayList();
        FilteredList<Object> list = new FilteredList<>(observabled);

        list.addListener((ListChangeListener<? super Object>) changes -> {
            while (changes.next()) {
                if(!changes.getAddedSubList().isEmpty()) {
                    System.out.println(changes.getAddedSubList());
                }

                if(!changes.getRemoved().isEmpty()) {
                    System.out.println(changes.getRemoved());
                }
            }
        });

        observabled.add(20);
        observabled.add(30);
        observabled.add(40);
    }
}