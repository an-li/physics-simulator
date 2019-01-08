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
import javafx.util.*;

public class CoulombLaw implements PhysicsConstants {

    private static CoulombClass cc=new CoulombClass();

    private static TextField q1Field=new TextField();
    private static Label q1Label=new Label("Charge of object 1 (C): ");
    private static TextField q2Field=new TextField();
    private static Label q2Label=new Label("Charge of object 2 (C): ");
    private static TextField distanceField=new TextField();
    private static Label distanceLabel=new Label("Distance between charged objects (m): ");
    private static TextField eForceField=new TextField();
    private static Label eForceLabel=new Label("Magnitude of electric force (N): ");

    private static Circle c1=new Circle();
    private static Circle c2=new Circle();

    private static Image ar=new Image("NYB/crightarrow.png");
    private static Image arLeft=new Image("NYB/cleftarrow.png");

    private static ImageView arrow=new ImageView(ar);
    private static ImageView arrowLeft=new ImageView(arLeft);

    private static NumberAxis chargeAxis=new NumberAxis();
    private static NumberAxis distAxis=new NumberAxis();
    private static LineChart<Number,Number> coulombLaw=new LineChart<Number,Number>(chargeAxis,distAxis);

    private static TranslateTransition translateC1=new TranslateTransition();
    private static TranslateTransition translateC2=new TranslateTransition();
    private static TranslateTransition translateAr=new TranslateTransition();
    private static TranslateTransition translateArLeft=new TranslateTransition();

    private static ScaleTransition scaleA1=new ScaleTransition();
    private static ScaleTransition scaleA2=new ScaleTransition();
    private static ScaleTransition scaleC1=new ScaleTransition();
    private static ScaleTransition scaleC2=new ScaleTransition();

    private static ParallelTransition par=new ParallelTransition();

    private static int blankCount;

    private static boolean animationPaused;

    public static void buttonMethods() {

        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                disableButton(start,true);
                disableButton(pause,false);
                if (animationPaused) {
                    animationPaused=false;
                    par.play();
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
                String h="\nFormula used in this topic: F = (kq1q2)/(r²)\n\n"
                        +"For the entry to be valid, user can only leave electric force blank.\n"
                        +"Electric force cannot be inputted and is only calculated.\n"
                        +"Distance cannot be negative.";
                helpDialog.setTitle("Help");
                helpDialog.setHeaderText("How to use this program");
                helpDialog.setContentText(HelpText+h);
                helpDialog.showAndWait();
            }
        });
    }

    public static void addTextFields() {
        animationPaused=false;
        textFieldBox.setConstraints(q1Label,0,0);
        textFieldBox.setConstraints(q1Field,1,0);
        textFieldBox.setConstraints(q2Label,0,1);
        textFieldBox.setConstraints(q2Field,1,1);
        textFieldBox.setConstraints(distanceLabel,0,2);
        textFieldBox.setConstraints(distanceField,1,2);
        textFieldBox.setConstraints(eForceLabel,0,3);
        textFieldBox.setConstraints(eForceField,1,3);
        textFieldBox.getChildren().addAll(q1Label,q1Field,q2Label,q2Field,distanceLabel,distanceField,eForceLabel,eForceField);
        eForceField.setEditable(false);
    }

    public static void setAllData() {
        double q1, q2, distance, eForce;
        blankCount=3;
        if (!q1Field.getText().equals(empty)) {
            q1=Double.parseDouble(q1Field.getText());
            cc.setQ1(q1);
            blankCount--;
        }
        if (!q2Field.getText().equals(empty)) {
            q2=Double.parseDouble(q2Field.getText());
            cc.setQ2(q2);
            blankCount--;
        }
        if (!distanceField.getText().equals(empty)) {
            distance=Double.parseDouble(distanceField.getText());
            cc.setDistance(distance);
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
            cc.calculateEForce();
            eForceField.setText(Double.toString(cc.getEForce()));
            q1Field.setEditable(false);
            q2Field.setEditable(false);
            distanceField.setEditable(false);
            animate();
            graph();
        }
    }

    public static void addGraphics() {
        c1.setCenterX(350);
        c1.setCenterY(250);
        c1.setRadius(50);
        c2.setCenterX(590);
        c2.setCenterY(250);
        c2.setRadius(50);

        c1.setFill(Color.BLACK);
        c2.setFill(Color.RED);

        animationBox.getChildren().addAll(c1,c2);

        chargeAxis.setLabel("Product of charges (μC^2)");
        distAxis.setLabel("Force by distance^2 (Nm^2)");
        coulombLaw.setTitle("Proving Coulomb's constant");
        coulombLaw.setPrefHeight(250);
        coulombLaw.setPrefWidth(250);
        coulombLaw.setCreateSymbols(false);
        graphBox1.getChildren().add(coulombLaw);
    }

    public static void animate() {
        disableMenus();
        double Dist;

        if (cc.getQ1()<0) {
            c1.setFill(Color.RED);
        } else {
            c1.setFill(Color.BLACK);
        }

        if (cc.getQ2()<0) {
            c2.setFill(Color.RED);
        } else {
            c2.setFill(Color.BLACK);
        }

        arrow.setFitWidth(50);
        arrow.setPreserveRatio(true);
        arrow.setCache(true);
        arrowLeft.setFitWidth(50);
        arrowLeft.setPreserveRatio(true);
        arrowLeft.setCache(true);

        if (cc.getDistance()>10) {
            Dist=10;
        } else {
            Dist=cc.getDistance();
        }

        translateC1.setDuration(Duration.millis(1500));
        translateC1.setNode(c1);
        translateC1.setToX(-Dist*160/10);
        translateC1.setCycleCount(1);

        translateC2.setDuration(Duration.millis(1500));
        translateC2.setNode(c2);
        translateC2.setToX(Dist*160/10);
        translateC2.setCycleCount(1);

        double eForce=cc.getEForce()*200;
        if (eForce>1.5) {
            eForce=1.5;
        } else if (eForce<0.5) {
            eForce=0.5;
        }

        double charge=55555.55556*Math.abs(cc.getQ1())+0.6944444444;
        if (charge>1.25) {
            charge=1.25;
        } else if (charge<0.75) {
            charge=0.75;
        }

        double charge2=55555.55556*Math.abs(cc.getQ2())+0.6944444444;
        if (charge2>1.25) {
            charge2=1.25;
        } else if (charge2<0.75) {
            charge2=0.75;
        }

        if (cc.getQ1()*cc.getQ2()>0) {
            arrow.setX(c2.getCenterX()+c2.getRadius());
            arrow.setY(c2.getCenterY()-4);

            arrowLeft.setX(c1.getCenterX()-c1.getRadius()-arrowLeft.getFitWidth());
            arrowLeft.setY(c1.getCenterY()-4);

            translateAr.setDuration(Duration.millis(1500));
            translateAr.setNode(arrow);
            translateAr.setToX(translateC2.getToX()-0.5*arrow.getFitWidth()*(1-eForce));
            translateAr.setCycleCount(One);

            translateArLeft.setDuration(Duration.millis(1500));
            translateArLeft.setNode(arrowLeft);
            translateArLeft.setToX(translateC1.getToX()+0.5*arrowLeft.getFitWidth()*(1-eForce));
            translateArLeft.setCycleCount(One);
        } else {
            arrowLeft.setX(c2.getCenterX()-c2.getRadius()-arrowLeft.getFitWidth());
            arrowLeft.setY(c2.getCenterY()-4);

            arrow.setX(c1.getCenterX()+c1.getRadius());
            arrow.setY(c1.getCenterY()-4);

            translateAr.setDuration(Duration.millis(1500));
            translateAr.setNode(arrow);
            translateAr.setToX(translateC1.getToX()-0.5*arrow.getFitWidth()*(1-eForce));
            translateAr.setCycleCount(One);

            translateArLeft.setDuration(Duration.millis(1500));
            translateArLeft.setNode(arrowLeft);
            translateArLeft.setToX(translateC2.getToX()+0.5*arrow.getFitWidth()*(1-eForce));
            translateArLeft.setCycleCount(One);
        }
        scaleA1.setDuration(Duration.millis(1500));
        scaleA1.setNode(arrow);
        scaleA1.setToX(eForce);
        scaleA1.setCycleCount(One);

        scaleA2.setDuration(Duration.millis(1500));
        scaleA2.setNode(arrowLeft);
        scaleA2.setToX(eForce);
        scaleA2.setCycleCount(One);

        scaleC1.setDuration(Duration.millis(1500));
        scaleC1.setNode(c1);
        scaleC1.setToX(charge);
        scaleC1.setToY(charge);
        scaleC1.setCycleCount(One);

        scaleC2.setDuration(Duration.millis(1500));
        scaleC2.setNode(c2);
        scaleC2.setToX(charge2);
        scaleC2.setToY(charge2);
        scaleC2.setCycleCount(One);

        par.getChildren().addAll(translateC1,translateC2,translateAr,translateArLeft,scaleA1,scaleA2,scaleC1,scaleC2);
        par.playFromStart();

        animationBox.getChildren().addAll(arrow,arrowLeft);

        par.setOnFinished(new EventHandler<ActionEvent>() {
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
        par.pause();
    }

    public static void resetAll() {
        blankCount=4;
        animationPaused=false;
        coulombLaw.getData().clear();
        q1Field.setText(empty);
        q1Field.setEditable(true);
        q2Field.setText(empty);
        q2Field.setEditable(true);
        distanceField.setText(empty);
        distanceField.setEditable(true);
        eForceField.setText(empty);
        eForceField.setEditable(false);
        enableMenus();
        disableButton(start,false);
        disableButton(pause,true);
        disableButton(help,false);
    }

    public static void graph() {
        XYChart.Series series=new XYChart.Series();
        series.setName("Slope: 9E-9 (Coulomb's constant)");
        for (double t=Zero; t<=10; t++) {
            series.getData().add(new XYChart.Data(t*1E12*cc.getProductOfCharges(cc.getQ1(),cc.getQ2()),t*cc.getForceByDistance2(cc.getEForce(),cc.getDistance())));
        }
        coulombLaw.getData().add(series);
    }

    public static void reverseAnimations() {
        par.stop();
        par.getChildren().clear();

        ParallelTransition revPar=new ParallelTransition();

        TranslateTransition reverseC1=new TranslateTransition(Duration.seconds(ReverseDuration),c1);
        TranslateTransition reverseC2=new TranslateTransition(Duration.seconds(ReverseDuration),c2);
        TranslateTransition reverseAr=new TranslateTransition(Duration.seconds(ReverseDuration),arrow);
        TranslateTransition reverseArLeft=new TranslateTransition(Duration.seconds(ReverseDuration),arrowLeft);

        ScaleTransition reverse1=new ScaleTransition(Duration.seconds(ReverseDuration),arrow);
        ScaleTransition reverse2=new ScaleTransition(Duration.seconds(ReverseDuration),arrowLeft);
        ScaleTransition reverse3=new ScaleTransition(Duration.seconds(ReverseDuration),c1);
        ScaleTransition reverse4=new ScaleTransition(Duration.seconds(ReverseDuration),c2);

        reverseC1.setToX(Zero);
        reverseC1.setCycleCount(One);
        reverseC1.setAutoReverse(false);

        reverseC2.setToX(Zero);
        reverseC2.setCycleCount(One);
        reverseC2.setAutoReverse(false);

        reverseAr.setToX(Zero);
        reverseAr.setCycleCount(One);
        reverseAr.setAutoReverse(false);

        reverseArLeft.setToX(Zero);
        reverseArLeft.setCycleCount(One);
        reverseArLeft.setAutoReverse(false);

        reverse1.setToX(One);
        reverse1.setCycleCount(One);
        reverse1.setAutoReverse(false);

        reverse2.setToX(One);
        reverse2.setCycleCount(One);
        reverse2.setAutoReverse(false);

        reverse3.setToY(One);
        reverse3.setToX(One);
        reverse3.setCycleCount(One);
        reverse3.setAutoReverse(false);

        reverse4.setToY(One);
        reverse4.setToX(One);
        reverse4.setCycleCount(One);
        reverse4.setAutoReverse(false);

        revPar.getChildren().addAll(reverseC1,reverseC2,reverseAr,reverseArLeft,reverse1,reverse2,reverse3,reverse4);
        animationBox.getChildren().removeAll(arrow,arrowLeft);
        revPar.playFromStart();

        revPar.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetAll();
            }
        });
    }

    public static boolean validate() {
        if (cc.getDistance()<0) {
            return false;
        } else if (blankCount!=0) {
            return false;
        } else {
            return true;
        }
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
        invalid.setContentText("One or more variables are out of range or empty.\nClick OK to try again.");
        invalid.showAndWait();
    }
}
