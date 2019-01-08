package NYB;

import ENMBean.*;
import javafx.animation.*;
import javafx.event.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.image.*;
import main.*;
import static main.ClsStart.*;
import static main.ClsFunctions.*;
import javafx.scene.shape.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.*;

public class OhmLaw implements PhysicsConstants {

    private static OhmClass oc = new OhmClass();

    private static TextField voltageField = new TextField();
    private static Label voltageLabel = new Label("Potential difference (V): ");
    private static TextField currentField = new TextField();
    private static Label currentLabel = new Label("Current (A): ");
    private static TextField resistanceField = new TextField();
    private static Label resistanceLabel = new Label("Resistance (Ω): ");

    private static Image r = new Image("file:///E:/Programming/Vanier/420-204-RE/Final Project/src/NYB/resistor.png");
    private static Image b = new Image("file:///E:/Programming/Vanier/420-204-RE/Final Project/src/NYB/battery.png");

    private static ImageView resistor = new ImageView(r);
    private static ImageView battery = new ImageView(b);
    private static Circle electron = new Circle();

    private static Rectangle circuit = new Rectangle();

    private static Text bat = new Text("Battery");
    private static Text res = new Text("Resistor");

    private static SequentialTransition eTrans = new SequentialTransition();
    private static ParallelTransition scalePar = new ParallelTransition();

    private static TranslateTransition translateLeftFast = new TranslateTransition();

    private static ScaleTransition batScale = new ScaleTransition();
    private static ScaleTransition resScale = new ScaleTransition();
    private static ScaleTransition elecScale = new ScaleTransition();

    private static NumberAxis currentAxis = new NumberAxis();
    private static NumberAxis voltageAxis = new NumberAxis();
    private static LineChart<Number, Number> ohmLaw = new LineChart<Number, Number>(currentAxis, voltageAxis);

    private static int blankCount;

    private static boolean animationPaused;

    public static void buttonMethods() {
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                disableButton(start, true);
                disableButton(pause, false);
                if (animationPaused) {
                    animationPaused = false;
                    scalePar.play();
                } else {
                    calculateAllData();
                }
            }
        });
        done.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                doneButtonMethod();
            }
        });
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                pauseAnimation();
                disableButton(start, false);
                disableButton(pause, true);
            }
        });
        reset.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                reverseAnimations();
            }
        });
        help.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                Alert helpDialog = new Alert(AlertType.INFORMATION);
                String h = "\nFormula used in this topic: V = RI\n\n"
                        + "For the entry to be valid, user can only leave one field blank and no values can be negative.\n";;
                helpDialog.setTitle("Help");
                helpDialog.setHeaderText("How to use this program");
                helpDialog.setContentText(HelpText + h);
                helpDialog.showAndWait();
            }
        });
    }

    public static void addTextFields() {
        animationPaused = false;
        textFieldBox.setConstraints(voltageLabel, 0, 0);
        textFieldBox.setConstraints(voltageField, 1, 0);
        textFieldBox.setConstraints(currentLabel, 0, 1);
        textFieldBox.setConstraints(currentField, 1, 1);
        textFieldBox.setConstraints(resistanceLabel, 0, 2);
        textFieldBox.setConstraints(resistanceField, 1, 2);
        textFieldBox.getChildren().addAll(voltageLabel, voltageField, currentLabel, currentField, resistanceLabel, resistanceField);
    }

    public static void setAllData() {
        double v, c, r;
        blankCount = 3;
        if (!voltageField.getText().equals(empty)) {
            v = Double.parseDouble(voltageField.getText());
            oc.setVoltage(v);
            blankCount--;
        }
        if (!currentField.getText().equals(empty)) {
            c = Double.parseDouble(currentField.getText());
            oc.setCurrent(c);
            blankCount--;
        }
        if (!resistanceField.getText().equals(empty)) {
            r = Double.parseDouble(resistanceField.getText());
            oc.setResistance(r);
            blankCount--;
        }
    }

    public static void calculateAllData() {
        setAllData();
        if (!validate()) {
            showInvalidWarning();
            resetAll();
            setAllData();
        } else {
            if (voltageField.getText().equals(empty)) {
                oc.calculateVoltage();
                voltageField.setText(Double.toString(oc.getVoltage()));
            } else if (currentField.getText().equals(empty)) {
                oc.calculateIntensity();
                currentField.setText(Double.toString(oc.getCurrent()));
            } else if (resistanceField.getText().equals(empty)) {
                oc.calculateResistance();
                resistanceField.setText(Double.toString(oc.getResistance()));
            }
            voltageField.setEditable(false);
            currentField.setEditable(false);
            resistanceField.setEditable(false);
            animate();
            graph();
        }
    }

    public static void addGraphics() {
        circuit.setX(200);
        circuit.setY(55);
        circuit.setHeight(250);
        circuit.setWidth(550);
        circuit.setFill(Color.TRANSPARENT);
        circuit.setStroke(Color.BLACK);
        circuit.setStrokeWidth(5);

        resistor.setFitWidth(100);
        resistor.setPreserveRatio(true);
        resistor.setCache(true);
        resistor.setX(425);
        resistor.setY(280);

        battery.setFitWidth(100);
        battery.setPreserveRatio(true);
        battery.setCache(true);
        battery.setX(425);
        battery.setY(29);

        bat.setX(450);
        bat.setY(20);
        res.setX(450);
        res.setY(270);

        animationBox.getChildren().addAll(circuit, resistor, battery, bat, res);

        currentAxis.setLabel("Current (A)");
        voltageAxis.setLabel("Voltage (V)");
        ohmLaw.setTitle("Voltage vs. Current");
        ohmLaw.setPrefHeight(250);
        ohmLaw.setPrefWidth(250);
        ohmLaw.setCreateSymbols(false);
        graphBox1.getChildren().add(ohmLaw);
    }

    public static void animate() {
        disableMenus();
        double scaleB;
        double scaleR;
        double eScale;
        double eSpeedR;

        if (oc.getVoltage() > 50) {
            scaleB = 2;
        } else if (oc.getVoltage() < 10) {
            scaleB = 0.5;
        } else {
            scaleB = oc.getVoltage() * .0375 + .125;
        }

        if (oc.getResistance() > 1000) {
            scaleR = 2;
            eSpeedR = 2000;
        } else if (oc.getResistance() < 100) {
            scaleR = 0.5;
            eSpeedR = 1600;
        } else {
            scaleR = oc.getResistance() / 600 + (1 / 3);
            eSpeedR = oc.getResistance() * (4 / 9) + (14000 / 9);
        }

        if (oc.getCurrent() > 5) {
            eScale = 1.5;
        } else if (oc.getCurrent() < .1) {
            eScale = 0.5;
        } else {
            eScale = oc.getCurrent() * .204082 + .479592;
        }

        electron.setRadius(10);
        electron.setFill(Color.BLUE);
        electron.setCache(true);
        electron.setCenterX(475);
        electron.setCenterY(55);

        batScale.setDuration(Duration.millis(1500));
        batScale.setNode(battery);
        batScale.setToX(scaleB);
        batScale.setCycleCount(1);

        resScale.setDuration(Duration.millis(1500));
        resScale.setNode(resistor);
        resScale.setToX(scaleR);
        resScale.setCycleCount(1);

        elecScale.setDuration(Duration.millis(500));
        elecScale.setNode(electron);
        elecScale.setToX(eScale);
        elecScale.setToY(eScale);
        elecScale.setCycleCount(1);

        double start1 = battery.getX() + battery.getFitWidth() + ((scaleB - 1) * battery.getFitWidth() / 2);
        double end1 = resistor.getX() + resistor.getFitWidth() + ((scaleR - 1) * resistor.getFitWidth() / 2);
        double start2 = resistor.getX() - ((scaleR - 1) * resistor.getFitWidth() / 2);
        double end2 = battery.getX() - ((scaleB - 1) * battery.getFitWidth() / 2);

        Polyline a1 = new Polyline();
        a1.getPoints().addAll(new Double[]{start1, 55.0, 750.0, 55.0, 750.0, 305.0, end1, 305.0});

        PathTransition pt1 = new PathTransition(Duration.seconds(4.5), a1, electron);

        translateLeftFast.setDuration(Duration.millis(eSpeedR));
        translateLeftFast.setNode(electron);
        translateLeftFast.setToX(-1 * scaleR * resistor.getFitWidth() / 2);
        translateLeftFast.setCycleCount(One);

        Polyline a2 = new Polyline();
        a2.getPoints().addAll(new Double[]{start2, 305.0, 200.0, 305.0, 200.0, 55.0, end2, 55.0});

        PathTransition pt2 = new PathTransition(Duration.seconds(4.5), a2, electron);

        eTrans.getChildren().addAll(pt1, translateLeftFast, pt2);

        scalePar.getChildren().addAll(elecScale, batScale, resScale, eTrans);
        scalePar.playFromStart();

        animationBox.getChildren().addAll(electron);

        scalePar.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showFinishDialog();
                animationBox.getChildren().remove(electron);
                disableButton(start, true);
                disableButton(pause, true);
                disableButton(help, true);
            }
        });
    }

    public static void reverseAnimations() {
        scalePar.stop();
        eTrans.getChildren().clear();
        scalePar.getChildren().clear();

        ParallelTransition revPar = new ParallelTransition();
        SequentialTransition revSeq = new SequentialTransition();

        ScaleTransition reverse1 = new ScaleTransition(Duration.seconds(ReverseDuration), battery);
        ScaleTransition reverse2 = new ScaleTransition(Duration.seconds(ReverseDuration), resistor);

        reverse1.setToX(One);
        reverse1.setCycleCount(One);
        reverse1.setAutoReverse(false);

        reverse2.setToX(One);
        reverse2.setCycleCount(One);
        reverse2.setAutoReverse(false);

        revPar.getChildren().addAll(reverse1, reverse2);
        animationBox.getChildren().remove(electron);
        revPar.playFromStart();

        revPar.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetAll();
            }
        });
    }

    public static void graph() {
        XYChart.Series series = new XYChart.Series();
        series.setName("Slope: " + oc.getResistance() + "Ω (Resistance)");
        for (double t = Zero; t <= 10; t++) {
            series.getData().add(new XYChart.Data(t * oc.getCurrent(), t * oc.getVoltage()));
        }
        ohmLaw.getData().add(series);
    }

    public static void pauseAnimation() {
        animationPaused = true;
        scalePar.pause();
    }

    public static void resetAll() {
        blankCount = 3;
        animationPaused = false;
        ohmLaw.getData().clear();
        voltageField.setText(empty);
        voltageField.setEditable(true);
        currentField.setText(empty);
        currentField.setEditable(true);
        resistanceField.setText(empty);
        resistanceField.setEditable(true);
        enableMenus();
        disableButton(start, false);
        disableButton(pause, true);
        disableButton(help, false);
    }

    public static boolean validate() {
        if (oc.getVoltage() < 0 || oc.getCurrent() < 0 || oc.getResistance() < 0) {
            return false;
        } else if (blankCount != 1) {
            return false;
        } else {
            return true;
        }
    }

    public static void showFinishDialog() {
        Alert end = new Alert(AlertType.INFORMATION);
        end.setTitle("End");
        end.setHeaderText("Animation finished");
        end.setContentText("Click OK to close this dialog,\nthen click Done to choose another topic, or click Reset to remain in this topic and enter new values.");
        end.show();
    }

    public static void showInvalidWarning() {
        Alert invalid = new Alert(AlertType.WARNING);
        invalid.setTitle("Warning");
        invalid.setHeaderText("Invalid input");
        invalid.setContentText("One or more variables are out of range or empty. \nClick OK to try again.");
        invalid.showAndWait();
    }
}