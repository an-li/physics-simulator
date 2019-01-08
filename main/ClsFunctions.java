package main;

import javafx.scene.control.*;
import static main.ClsStart.*;

public class ClsFunctions implements PhysicsConstants {

    public static void addButtons() {
        buttonBox.getChildren().addAll(start, done, pause, reset, help);
    }

    public static void disableButton(Button button, boolean b) {
        button.setDisable(b);
    }

    public static void disableMenus() {
        NYAMenu.setDisable(true);
        NYBMenu.setDisable(true);
        NYCMenu.setDisable(true);
    }

    public static void enableMenus() {
        NYAMenu.setDisable(false);
        NYBMenu.setDisable(false);
        NYCMenu.setDisable(false);
    }

    public static void doneButtonMethod() {
        removeEverything();
        showStartingElements();
    }
}