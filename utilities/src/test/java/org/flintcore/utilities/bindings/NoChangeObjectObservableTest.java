package org.flintcore.utilities.bindings;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoChangeObjectObservableTest {

    private NoChangeObjectObservable<Number> noChangeObjectObservable;

    @BeforeAll
    static void beforeAll() {
        System.setProperty("testfx.headless", "true");
    }

    @Test
    void shouldUpdateAndRegisterChange() {
        IntegerProperty toListen = new SimpleIntegerProperty(10);

        noChangeObjectObservable = NoChangeObjectObservable.bind(toListen);

        assertTrue(noChangeObjectObservable.getValue());
        toListen.set(90);

        Boolean afterChange = noChangeObjectObservable.getValue();
        System.out.println("HasNoChange = " + afterChange);
        assertFalse(afterChange);

        assertTrue(noChangeObjectObservable.getValue());
    }
}