package NYC;

import javafx.event.*;
import WavesBean.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.image.*;
import javafx.scene.shape.*;
import main.*;
import static main.ClsFunctions.*;
import javafx.animation.*;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.*;
import javafx.util.*;
import static main.ClsStart.*;

public class SimpleHarmonicMotion implements PhysicsConstants {

    private static SHMClass shm=new SHMClass();

    private static TextField amplitudeField=new TextField();
    private static Label amplitudeLabel=new Label("Amplitude (m): ");
    private static TextField phaseconstantField=new TextField();
    private static Label phaseconstantLabel=new Label("Phase Constant: ");
    private static TextField frequencyField=new TextField();
    private static Label frequencyLabel=new Label("Frequency (Hz): ");
    private static TextField periodField=new TextField();
    private static Label periodLabel=new Label("Period (s): ");
    private static TextField angularvelocityField=new TextField();
    private static Label angularvelocityLabel=new Label("Angular Velocity (rad/s): ");

    private static NumberAxis timeAxis1=new NumberAxis();
    private static NumberAxis amplitudeAxis=new NumberAxis();
    private static LineChart<Number,Number> yDisVSTime=new LineChart<Number,Number>(timeAxis1,amplitudeAxis);
    private static NumberAxis timeAxis2=new NumberAxis();
    private static NumberAxis linVelAxis=new NumberAxis();
    private static LineChart<Number,Number> linVelVSTime=new LineChart<Number,Number>(timeAxis2,linVelAxis);

    private static Line wall=new Line();
    private static Rectangle block=new Rectangle();

    private static Image s=new Image("NYC/Spring.png");
    private static ImageView spring=new ImageView(s);

    private static SequentialTransition allTranslate=new SequentialTransition();
    private static SequentialTransition allScale=new SequentialTransition();
    private static SequentialTransition springTranslate=new SequentialTransition();
    private static ParallelTransition springAnimation=new ParallelTransition();

    private static boolean animationPaused;

    private static int blankCount, counterA;

    public static void buttonMethods() {
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                disableButton(start,true);
                disableButton(pause,false);
                if (animationPaused) {
                    animationPaused=false;
                    springAnimation.play();
                } else {
                    calculateAllData();
                }
            }
        });
        done.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                reverseAnimations();
                doneButtonMethod();
            }
        });
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                pauseAnimation();
                disableButton(start,false);
                disableButton(pause,true);
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
                Alert helpDialog=new Alert(AlertType.INFORMATION);
                String h="\nFormula used in this topic: y = Asin(ωt + φ0)\n\n"
                        +"For the entry to be valid, user must fill in amplitude and one of the following: period, frequency or angular velocity.\n"
                        +"No variable can have a negative value and the amplitude must between 0 and 10.\n";
                helpDialog.setTitle("Help");
                helpDialog.setHeaderText("How to use this program");
                helpDialog.setContentText(HelpText+h);
                helpDialog.showAndWait();
            }
        });
    }

    public static void addTextFields() {
        textFieldBox.setConstraints(amplitudeLabel,0,0);
        textFieldBox.setConstraints(amplitudeField,1,0);
        textFieldBox.setConstraints(phaseconstantLabel,0,1);
        textFieldBox.setConstraints(phaseconstantField,1,1);
        textFieldBox.setConstraints(frequencyLabel,0,2);
        textFieldBox.setConstraints(frequencyField,1,2);
        textFieldBox.setConstraints(periodLabel,0,3);
        textFieldBox.setConstraints(periodField,1,3);
        textFieldBox.setConstraints(angularvelocityLabel,0,4);
        textFieldBox.setConstraints(angularvelocityField,1,4);
        textFieldBox.getChildren().addAll(amplitudeLabel,amplitudeField,phaseconstantLabel,phaseconstantField,frequencyLabel,frequencyField,periodLabel,periodField,angularvelocityLabel,angularvelocityField);
    }

    public static void addGraphics() {
        animationPaused=false;
        wall.setStartX(20);
        wall.setEndX(20);
        wall.setStartY(0);
        wall.setEndY(250);
        block.setX(420);
        block.setY(75);
        block.setWidth(100);
        block.setHeight(100);
        block.setFill(Color.BROWN);
        spring.setX(20);
        spring.setY(85);
        spring.setFitWidth(400);
        spring.setPreserveRatio(true);
        spring.setCache(true);
        animationBox.getChildren().addAll(wall,spring,block);

        timeAxis1.setLabel("Time (s)");
        amplitudeAxis.setLabel("Y-displacement (m)");
        yDisVSTime.setTitle("Y-displacement vs time");
        yDisVSTime.setPrefHeight(250);
        yDisVSTime.setPrefWidth(250);
        yDisVSTime.setCreateSymbols(false);
        graphBox1.getChildren().add(yDisVSTime);
        timeAxis2.setLabel("Time (s)");
        linVelAxis.setLabel("Linear velocity (m/s)");
        linVelVSTime.setTitle("Linear velocity vs time");
        linVelVSTime.setPrefHeight(250);
        linVelVSTime.setPrefWidth(250);
        linVelVSTime.setCreateSymbols(false);
        graphBox2.getChildren().add(linVelVSTime);
    }

    public static void setAllData() {
        double amplitude, phaseconstant, frequency, period, angularvelocity;
        blankCount=5;
        counterA=3;
        if (!amplitudeField.getText().equals(empty)) {
            amplitude=Double.parseDouble(amplitudeField.getText());
            shm.setAmplitude(amplitude);
            blankCount--;
        }
        if (!phaseconstantField.getText().equals(empty)) {
            phaseconstant=Double.parseDouble(phaseconstantField.getText());
            shm.setPhaseConstant(phaseconstant);
            blankCount--;
        }
        if (!frequencyField.getText().equals(empty)) {
            frequency=Double.parseDouble(frequencyField.getText());
            shm.setFrequency(frequency);
            blankCount--;
            counterA--;
        }
        if (!periodField.getText().equals(empty)) {
            period=Double.parseDouble(periodField.getText());
            shm.setPeriod(period);
            blankCount--;
            counterA--;
        }

        if (!angularvelocityField.getText().equals(empty)) {
            angularvelocity=Double.parseDouble(angularvelocityField.getText());
            shm.setAngularVelocity(angularvelocity);
            blankCount--;
            counterA--;
        }
    }

    public static boolean validate() {
        if (blankCount==Zero||blankCount>3) {
            return false;
        } else if (shm.getAmplitude()<0||shm.getAmplitude()>10||shm.getFrequency()<0||shm.getPeriod()<0) {
            return false;
        } else if (counterA!=2) {
            return false;
        } else {
            return true;
        }
    }

    public static void calculateAllData() {
        setAllData();
        if (!validate()) {
            showInvalidWarning();
            resetAll();
            setAllData();
        } else {
            if (phaseconstantField.getText().equals(empty)) {
                showPhaseConstantZeroWarning();
                shm.setPhaseConstant(Zero);
                phaseconstantField.setText(Double.toString(shm.getPhaseConstant()));
            }
            if (frequencyField.getText().equals(empty)) {
                shm.calculateFrequency();
                frequencyField.setText(Double.toString(shm.getFrequency()));
            }
            if (periodField.getText().equals(empty)) {
                shm.calculatePeriod();
                periodField.setText(Double.toString(shm.getPeriod()));
            }
            if (angularvelocityField.getText().equals(empty)) {
                shm.calculateAngularVelocity();
                angularvelocityField.setText(Double.toString(shm.getAngularVelocity()));
            }
            phaseconstantField.setEditable(false);
            frequencyField.setEditable(false);
            periodField.setEditable(false);
            amplitudeField.setEditable(false);
            angularvelocityField.setEditable(false);
            animate();
            graph();
        }
    }

    public static void animate() {
        disableMenus();
        double period=shm.getPeriod();
        double amplitude=shm.getAmplitude();

        TranslateTransition blockTranslation1=new TranslateTransition(Duration.seconds(period/4),block);
        TranslateTransition blockTranslation2=new TranslateTransition(Duration.seconds(period/2),block);
        TranslateTransition blockTranslation3=new TranslateTransition(Duration.seconds(period/4),block);

        ScaleTransition springTransition1=new ScaleTransition(Duration.seconds(period/4),spring);
        ScaleTransition springTransition2=new ScaleTransition(Duration.seconds(period/2),spring);
        ScaleTransition springTransition3=new ScaleTransition(Duration.seconds(period/4),spring);

        TranslateTransition springTranslation1=new TranslateTransition(Duration.seconds(period/4),spring);
        TranslateTransition springTranslation2=new TranslateTransition(Duration.seconds(period/2),spring);
        TranslateTransition springTranslation3=new TranslateTransition(Duration.seconds(period/4),spring);

        double toX1=0.09*shm.getAmplitude()+1, toX2=-.09*shm.getAmplitude()+1, toX3=1;
        springTransition1.setToX(toX1);
        springTransition2.setToX(toX2);
        springTransition3.setToX(toX3);

        springTranslation1.setToX(0.5*400*(toX1-1));
        springTranslation2.setToX(0.5*400*(toX2-1));
        springTranslation3.setToX(Zero);

        blockTranslation1.setToX(36*amplitude);
        blockTranslation2.setToX(-36*amplitude);
        blockTranslation3.setToX(Zero);

        springTransition1.setAutoReverse(false);
        springTransition2.setAutoReverse(false);
        springTransition3.setAutoReverse(false);

        springTranslation1.setAutoReverse(false);
        springTranslation2.setAutoReverse(false);
        springTranslation3.setAutoReverse(false);

        blockTranslation1.setAutoReverse(false);
        blockTranslation2.setAutoReverse(false);
        blockTranslation3.setAutoReverse(false);

        allTranslate.getChildren().addAll(blockTranslation1,blockTranslation2,blockTranslation3);

        allScale.getChildren().addAll(springTransition1,springTransition2,springTransition3);

        springTranslate.getChildren().addAll(springTranslation1,springTranslation2,springTranslation3);

        springAnimation.getChildren().addAll(allScale,springTranslate,allTranslate);
        springAnimation.setCycleCount((int) (10/shm.getPeriod()));
        springAnimation.playFromStart();
        springAnimation.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                showFinishDialog();
                disableButton(start,true);
                disableButton(pause,true);
                disableButton(help,true);
            }
        });
    }

    public static void pauseAnimation() {
        animationPaused=true;
        springAnimation.pause();
    }

    public static void reverseAnimations() {
        springAnimation.stop();
        allTranslate.getChildren().clear();
        allScale.getChildren().clear();
        springTranslate.getChildren().clear();
        springAnimation.getChildren().clear();

        TranslateTransition resetBlock=new TranslateTransition(Duration.seconds(ReverseDuration),block);
        resetBlock.setToX(Zero);
        resetBlock.setCycleCount(One);
        resetBlock.setAutoReverse(false);

        TranslateTransition translateSpringBack=new TranslateTransition(Duration.seconds(ReverseDuration),spring);
        translateSpringBack.setToX(Zero);
        translateSpringBack.setCycleCount(One);
        translateSpringBack.setAutoReverse(false);

        ScaleTransition resetSpring=new ScaleTransition(Duration.seconds(ReverseDuration),spring);
        resetSpring.setToX(One);
        resetSpring.setCycleCount(One);
        resetSpring.setAutoReverse(false);

        ParallelTransition reverseAll=new ParallelTransition();
        reverseAll.getChildren().addAll(resetBlock,translateSpringBack,resetSpring);
        reverseAll.playFromStart();
        reverseAll.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetAll();
            }
        });
    }

    public static void graph() {
        XYChart.Series series1=new XYChart.Series();
        series1.setName("Y-displacement vs. time");
        for (double t=Zero; t<=shm.getPeriod()*springAnimation.getCycleCount(); t+=.01) {
            series1.getData().add(new XYChart.Data(t,shm.calculateYDisplacement(t,shm.getAmplitude(),shm.getAngularVelocity(),shm.getPhaseConstant())));
        }
        yDisVSTime.getData().add(series1);

        XYChart.Series series2=new XYChart.Series();
        series2.setName("Linear velocity vs. time");
        for (double t=Zero; t<=shm.getPeriod()*springAnimation.getCycleCount(); t+=.01) {
            series2.getData().add(new XYChart.Data(t,shm.calculateLinearVelocity(t,shm.getAmplitude(),shm.getAngularVelocity(),shm.getPhaseConstant())));
        }
        linVelVSTime.getData().add(series2);
    }

    public static void resetAll() {
        yDisVSTime.getData().clear();
        linVelVSTime.getData().clear();
        phaseconstantField.setText(empty);
        phaseconstantField.setEditable(true);
        frequencyField.setText(empty);
        frequencyField.setEditable(true);
        periodField.setText(empty);
        periodField.setEditable(true);
        amplitudeField.setText(empty);
        amplitudeField.setEditable(true);
        angularvelocityField.setText(empty);
        angularvelocityField.setEditable(true);
        enableMenus();
        disableButton(start,false);
        disableButton(pause,true);
        disableButton(help,false);
    }

    public static void showFinishDialog() {
        Alert end=new Alert(AlertType.INFORMATION);
        end.setTitle("End");
        end.setHeaderText("Animation finished");
        end.setContentText("Click OK to close this dialog,\nthen click Done to choose another topic, or click Reset to remain in this topic and enter new values.");
        end.show();
    }

    public static void showInvalidWarning() {
        Alert invalid=new Alert(AlertType.WARNING);
        invalid.setTitle("Warning");
        invalid.setHeaderText("Invalid input");
        invalid.setContentText("One or more variables are out of range or empty. \nClick OK to try again. ");
        invalid.showAndWait();
    }

    public static void showPhaseConstantZeroWarning() {
        Alert pc0=new Alert(AlertType.WARNING);
        pc0.setTitle("Warning");
        pc0.setHeaderText("Phase constant field left blank");
        pc0.setContentText("Click OK to assume zero phase constant");
        pc0.showAndWait();
    }
}
