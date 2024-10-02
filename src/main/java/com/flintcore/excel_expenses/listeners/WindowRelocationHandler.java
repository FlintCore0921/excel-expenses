package com.flintcore.excel_expenses.listeners;

import com.flintcore.excel_expenses.models.RelocationParam;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import lombok.RequiredArgsConstructor;

import java.awt.*;

@RequiredArgsConstructor
public class WindowRelocationHandler implements EventHandler<MouseEvent> {

    private final RelocationParam params;
    private double xScreen = -1, yScreen = -1;

    @Override
    public void handle(MouseEvent event) {

        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            if(params.range().getHeight() <= event.getY()) return;

            // Capture the initial position of the mouse relative to the scene
            xScreen = event.getSceneX();
            yScreen = event.getSceneY();
        }

        if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (xScreen == -1 && yScreen == -1) return;

            // Move the stage based on the difference between the mouse's screen position and the offset
            params.window().setX(event.getScreenX() - xScreen);
            params.window().setY(event.getScreenY() - yScreen);

            System.out.println(params.window().getX());
            System.out.println(params.window().getY());
        }

        if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            xScreen = -1;
            yScreen = -1;
        }
    }
}
