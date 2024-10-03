package com.flintcore.excel_expenses.listeners;

import com.flintcore.excel_expenses.models.RelocationParam;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WindowRelocationHandler implements EventHandler<MouseEvent> {

    private final RelocationParam params;
    private double xScreen = -1, yScreen = -1;

    @Override
    public void handle(MouseEvent event) {
        EventType<? extends MouseEvent> eventType = event.getEventType();
        if (eventType.equals(MouseEvent.MOUSE_PRESSED)) {
            handleOnPressed(event);
        } else if (eventType.equals(MouseEvent.MOUSE_DRAGGED)) {
            handleOnDragMouse(event);
        } else if (eventType.equals(MouseEvent.MOUSE_RELEASED)) {
            handleOnRelease();
        }
    }

    private void handleOnRelease() {
        xScreen = -1;
        yScreen = -1;
    }

    private void handleOnDragMouse(MouseEvent event) {
        if (xScreen == -1 && yScreen == -1) return;

        // Move the stage based on the difference between the mouse's screen position and the offset
        params.window().setX(event.getScreenX() - xScreen);
        params.window().setY(event.getScreenY() - yScreen);
    }

    private void handleOnPressed(MouseEvent event) {
        if(params.range().getHeight() <= event.getY()) return;

        xScreen = event.getSceneX();
        yScreen = event.getSceneY();
    }
}
