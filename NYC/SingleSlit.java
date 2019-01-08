package NYC;

import javafx.event.*;
import WavesBean.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.image.*;
import javafx.scene.shape.*;
import main.*;
import static main.ClsStart.*;
import static main.ClsFunctions.*;
import javafx.animation.*;
import javafx.scene.paint.*;
import javafx.util.*;
import javafx.scene.chart.*;

public class SingleSlit implements PhysicsConstants {

    private static SingleSlitClass ss=new SingleSlitClass();

    private static TextField slitwidthField=new TextField();
    private static Label slitwidthLabel=new Label("Slit Width (m): ");
    private static TextField angleField=new TextField();
    private static Label angleLabel=new Label("Angle (˚): ");
    private static TextField wavelengthField=new TextField();
    private static Label wavelengthLabel=new Label("Wavelength (m): ");
    private static TextField distanceField=new TextField();
    private static Label distanceLabel=new Label("Distance (m): ");
    private static TextField yDisplacementField=new TextField();
    private static Label yDisplacementLabel=new Label("Y-displacement (m): ");
    private static TextField orderField=new TextField();
    private static Label orderLabel=new Label("Order: ");

    private static NumberAxis wavelengthAxis=new NumberAxis();
    private static NumberAxis angleAxis=new NumberAxis();
    private static LineChart<Number,Number> angleOfDiffraction1vsWavelength=new LineChart<Number,Number>(wavelengthAxis,angleAxis);

    private static Line distance=new Line();
    private static Line wall=new Line();
    private static Line slitTop=new Line();
    private static Line slitBot=new Line();
    private static Line laserBeam1=new Line();
    private static Line laserBeam2=new Line();

    private static Image lp=new Image("NYC/Laser pointer.png");
    private static ImageView laserPointer=new ImageView(lp);

    private static Image diffraction=new Image("NYC/Single slit pattern.png");
    private static ImageView diffPattern=new ImageView(diffraction);

    private static ParallelTransition slitAnimation=new ParallelTransition();
    private static ParallelTransition distanceAnimation=new ParallelTransition();
    private static ParallelTransition allAnimation=new ParallelTransition();

    private static boolean animationPaused;

    private static int counter1, counter2;

    public static void buttonMethods() {
        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent Event) {
                disableButton(start,true);
                disableButton(pause,false);
                if (animationPaused) {
                    animationPaused=false;
                    allAnimation.play();
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
                String h="\nFormulas used in this topic: \n"
                        +"1. asinθ = mλ\n"
                        +"2. y = dtanθ\n\n"
                        +"For the entry to be valid, user must input one of slit width, wavelength and order AND one of angle, distance and Y-displacement.\n"
                        +"No variable can have a negative value.";
                helpDialog.setTitle("Help");
                helpDialog.setHeaderText("How to use this program");
                helpDialog.setContentText(HelpText+h);
                helpDialog.showAndWait();
            }
        });
    }

    public static void addTextFields() {
        textFieldBox.setConstraints(slitwidthLabel,0,0);
        textFieldBox.setConstraints(slitwidthField,1,0);
        textFieldBox.setConstraints(angleLabel,0,1);
        textFieldBox.setConstraints(angleField,1,1);
        textFieldBox.setConstraints(wavelengthLabel,0,2);
        textFieldBox.setConstraints(wavelengthField,1,2);
        textFieldBox.setConstraints(distanceLabel,0,3);
        textFieldBox.setConstraints(distanceField,1,3);
        textFieldBox.setConstraints(yDisplacementLabel,0,4);
        textFieldBox.setConstraints(yDisplacementField,1,4);
        textFieldBox.setConstraints(orderLabel,0,5);
        textFieldBox.setConstraints(orderField,1,5);

        textFieldBox.getChildren().addAll(slitwidthLabel,slitwidthField,angleLabel,angleField,wavelengthLabel,wavelengthField,distanceLabel,distanceField,yDisplacementLabel,yDisplacementField,orderLabel,orderField);

    }

    public static void addGraphics() {
        animationPaused=false;
        wall.setStartX(600);
        wall.setEndX(600);
        wall.setStartY(0);
        wall.setEndY(250);
        slitTop.setStartX(100);
        slitTop.setEndX(100);
        slitTop.setStartY(50);
        slitTop.setEndY(110);
        slitBot.setStartX(100);
        slitBot.setEndX(100);
        slitBot.setStartY(140);
        slitBot.setEndY(200);
        laserPointer.setX(25);
        laserPointer.setY(117);
        laserPointer.setFitWidth(50);
        laserPointer.setPreserveRatio(true);
        laserPointer.setCache(true);
        distance.setStartX(110);
        distance.setEndX(600);
        distance.setStartY(125);
        distance.setEndY(125);

        animationBox.getChildren().addAll(wall,slitTop,slitBot,laserPointer,distance);
        wavelengthAxis.setLabel("Slit width (μm)");
        angleAxis.setLabel("Angle (˚)");
        angleOfDiffraction1vsWavelength.setTitle("First order diffraction angle vs. slit width");
        angleOfDiffraction1vsWavelength.setPrefHeight(250);
        angleOfDiffraction1vsWavelength.setPrefWidth(250);
        angleOfDiffraction1vsWavelength.setCreateSymbols(false);
        graphBox1.getChildren().add(angleOfDiffraction1vsWavelength);
    }

    public static void setAllData() {
        double slitwidth, angle, wavelength, dist, yDisplacement;
        int order;
        counter1=3;
        counter2=3;
        if (!slitwidthField.getText().equals(empty)) {
            slitwidth=Double.parseDouble(slitwidthField.getText());
            ss.setSlitWidth(slitwidth);
            counter1--;
        }
        if (!angleField.getText().equals(empty)) {
            angle=Double.parseDouble(angleField.getText());
            ss.setAngle(angle);
            counter2--;
        }
        if (!wavelengthField.getText().equals(empty)) {
            wavelength=Double.parseDouble(wavelengthField.getText());
            ss.setWavelength(wavelength);
            counter1--;
        }
        if (!distanceField.getText().equals(empty)) {
            dist=Double.parseDouble(distanceField.getText());
            ss.setDistance(dist);
            counter2--;
        }
        if (!yDisplacementField.getText().equals(empty)) {
            yDisplacement=Double.parseDouble(yDisplacementField.getText());
            ss.setYDisplacement(yDisplacement);
            counter2--;
        }
        if (!orderField.getText().equals(empty)) {
            order=Integer.parseInt(orderField.getText());
            ss.setOrder(order);
            counter1--;
        }
    }

    public static boolean validate() {
        if (ss.getAngle()<0||ss.getSlitWidth()<0||ss.getWavelength()<0||ss.getDistance()<0||ss.getOrder()<0||ss.getYDisplacement()<0) {
            return false;
        } else if (ss.getAngle()>45) {
            return false;
        } else if (counter1!=1) {
            return false;
        } else if (counter2!=1) {
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
            if (slitwidthField.getText().equals(empty)) {
                ss.calculateSlitWidth();
                slitwidthField.setText(Double.toString(ss.getSlitWidth()));
            }
            if (angleField.getText().equals(empty)) {
                ss.calculateAngle();
                angleField.setText(Double.toString(ss.getAngle()));
            }
            if (wavelengthField.getText().equals(empty)) {
                ss.calculateWavelength();
                wavelengthField.setText(Double.toString(ss.getWavelength()));
            }
            if (distanceField.getText().equals(empty)) {
                ss.calculateDistance();
                distanceField.setText(Double.toString(ss.getDistance()));
            }
            if (yDisplacementField.getText().equals(empty)) {
                ss.calculateYDisplacement();
                yDisplacementField.setText(Double.toString(ss.getYDisplacement()));
            }
            if (orderField.getText().equals(empty)) {
                ss.calculateOrder();
                orderField.setText(Integer.toString(ss.getOrder()));
            }
            slitwidthField.setEditable(false);
            angleField.setEditable(false);
            wavelengthField.setEditable(false);
            distanceField.setEditable(false);
            yDisplacementField.setEditable(false);
            orderField.setEditable(false);
            animate();
            graph();
        }
    }

    public static void animate() {
        disableMenus();
        diffPattern.setX(500);
        diffPattern.setY(0);
        diffPattern.setFitHeight(250);
        diffPattern.setPreserveRatio(true);
        diffPattern.setCache(true);
        laserBeam1.setStartX(25);
        laserBeam1.setEndX(100);
        laserBeam1.setStartY(125);
        laserBeam1.setEndY(125);
        laserBeam1.setStroke(Color.RED);
        laserBeam2.setStartX(100);
        laserBeam2.setStartY(125);
        laserBeam2.setStroke(Color.RED);
        
        animationBox.getChildren().addAll(diffPattern,laserBeam1,laserBeam2);

        TranslateTransition slitTranslate1=new TranslateTransition(Duration.seconds(One),slitTop);
        TranslateTransition slitTranslate2=new TranslateTransition(Duration.seconds(One),slitBot);

        double translateSlitFactor=1E6*ss.getSlitWidth()-15;
        if (translateSlitFactor>14) {
            translateSlitFactor=14;
        }
        if (translateSlitFactor<-14) {
            translateSlitFactor=-14;
        }
        slitTranslate1.setToY(-1*translateSlitFactor);
        slitTranslate2.setToY(translateSlitFactor);
        slitAnimation.getChildren().addAll(slitTranslate1,slitTranslate2);

        TranslateTransition wallTranslate=new TranslateTransition(Duration.seconds(One),wall);
        TranslateTransition distanceTranslate=new TranslateTransition(Duration.seconds(One),distance);
        ScaleTransition distanceScale=new ScaleTransition(Duration.seconds(One),distance);
        TranslateTransition diffPatternTranslate1=new TranslateTransition(Duration.seconds(One),diffPattern);
        ScaleTransition scaleDiffPattern=new ScaleTransition(Duration.seconds(One),diffPattern);
        FadeTransition fadeDiffPattern=new FadeTransition(Duration.seconds(2),diffPattern);
        FadeTransition fadeLaser1=new FadeTransition(Duration.seconds(2),laserBeam1);
        FadeTransition fadeLaser2=new FadeTransition(Duration.seconds(2),laserBeam2);

        double toX=490*ss.getDistance()-490;
        if (toX>0) {
            toX=0;
        } else if (toX<-440) {
            toX=-440;
        }
        double dsFactor=ss.getDistance();
        if (dsFactor<(0.1020408163265306)) {
            dsFactor=0.1020408163265306;
        } else if (dsFactor>1) {
            dsFactor=1;
        }
        diffPatternTranslate1.setToX(toX);
        wallTranslate.setToX(toX);
        distanceTranslate.setToX(-.5*490*(1-dsFactor));
        distanceScale.setToX(dsFactor);

        distanceAnimation.getChildren().addAll(distanceTranslate,distanceScale,wallTranslate,diffPatternTranslate1);
        laserBeam2.setEndX(wall.getStartX()+toX);
        double endY=(125-(490*ss.getYDisplacement()/ss.getOrder()));
        if (endY<0) {
            endY=0;
        }
        laserBeam2.setEndY(endY);

        double patternScaleY=(ss.getYDisplacement()/(ss.getOrder()*0.051020408));
        if (patternScaleY>5.08333333333333) {
            patternScaleY=5.08333333333333;
        }
        scaleDiffPattern.setToY(patternScaleY);
        scaleDiffPattern.setCycleCount(One);
        scaleDiffPattern.setAutoReverse(false);

        fadeDiffPattern.setFromValue(Zero);
        fadeDiffPattern.setToValue(One);
        fadeDiffPattern.setCycleCount(One);
        fadeDiffPattern.setAutoReverse(false);

        fadeLaser1.setFromValue(Zero);
        fadeLaser1.setToValue(One);
        fadeLaser1.setCycleCount(One);
        fadeLaser1.setAutoReverse(false);

        fadeLaser2.setFromValue(Zero);
        fadeLaser2.setToValue(One);
        fadeLaser2.setCycleCount(One);
        fadeLaser2.setAutoReverse(false);

        ParallelTransition fadeAll=new ParallelTransition();
        fadeAll.getChildren().addAll(fadeDiffPattern,fadeLaser1,fadeLaser2);

        allAnimation.getChildren().addAll(slitAnimation,distanceAnimation,scaleDiffPattern,fadeAll);
        allAnimation.setCycleCount(One);
        allAnimation.playFromStart();
        allAnimation.setOnFinished(new EventHandler<ActionEvent>() {
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
        allAnimation.pause();
    }

    public static void reverseAnimations() {
        allAnimation.stop();
        slitAnimation.getChildren().clear();
        distanceAnimation.getChildren().clear();
        allAnimation.getChildren().clear();

        TranslateTransition resetWall=new TranslateTransition(Duration.seconds(ReverseDuration),wall);
        resetWall.setToX(Zero);
        resetWall.setCycleCount(One);
        resetWall.setAutoReverse(false);

        TranslateTransition translateDistanceBack=new TranslateTransition(Duration.seconds(ReverseDuration),distance);
        translateDistanceBack.setToX(Zero);
        translateDistanceBack.setCycleCount(One);
        translateDistanceBack.setAutoReverse(false);

        TranslateTransition translatePatternBack=new TranslateTransition(Duration.seconds(ReverseDuration),diffPattern);
        translatePatternBack.setToX(Zero);
        translatePatternBack.setCycleCount(One);
        translatePatternBack.setAutoReverse(false);

        TranslateTransition translateTopSlitBack=new TranslateTransition(Duration.seconds(ReverseDuration),slitTop);
        translateTopSlitBack.setToY(Zero);
        translateTopSlitBack.setCycleCount(One);
        translateTopSlitBack.setAutoReverse(true);

        TranslateTransition translateBottomSlitBack=new TranslateTransition(Duration.seconds(ReverseDuration),slitBot);
        translateBottomSlitBack.setToY(Zero);
        translateBottomSlitBack.setCycleCount(One);
        translateBottomSlitBack.setAutoReverse(true);

        ScaleTransition resetPatternScale=new ScaleTransition(Duration.seconds(ReverseDuration),diffPattern);
        resetPatternScale.setToX(One);
        resetPatternScale.setCycleCount(One);
        resetPatternScale.setAutoReverse(false);

        ScaleTransition resetDistance=new ScaleTransition(Duration.seconds(ReverseDuration),distance);
        resetDistance.setToX(One);
        resetDistance.setCycleCount(One);
        resetDistance.setAutoReverse(false);

        ParallelTransition reverseAll=new ParallelTransition();
        reverseAll.getChildren().addAll(resetWall,translateDistanceBack,translatePatternBack,translateTopSlitBack,translateBottomSlitBack,resetPatternScale,resetDistance);
        animationBox.getChildren().removeAll(diffPattern,laserBeam1,laserBeam2);
        reverseAll.playFromStart();
        reverseAll.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetAll();
            }
        });

    }

    public static void graph() {
        XYChart.Series series=new XYChart.Series();
        series.setName("Wavelength: "+ss.getWavelength()+"m");
        for (double a=1E-6; a<=10E-6; a+=.1E-6) {
            series.getData().add(new XYChart.Data(a*1E6,ss.getAngleFromSlitWidth(ss.getWavelength(),a)));
        }
        angleOfDiffraction1vsWavelength.getData().add(series);
    }

    public static void resetAll() {
        angleOfDiffraction1vsWavelength.getData().clear();
        slitwidthField.setText(empty);
        slitwidthField.setEditable(true);
        angleField.setText(empty);
        angleField.setEditable(true);
        wavelengthField.setText(empty);
        wavelengthField.setEditable(true);
        distanceField.setText(empty);
        distanceField.setEditable(true);
        yDisplacementField.setText(empty);
        yDisplacementField.setEditable(true);
        orderField.setText(empty);
        orderField.setEditable(true);
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
}
