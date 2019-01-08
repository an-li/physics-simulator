package main;

public interface PhysicsConstants {

    public final double Zero=0;
    public final int One=1;
    public final double CoulombConstant=9E9;
    public final double ProjectileMotionYAcceleration=-9.80665;
    public final double RadToDeg=180/Math.PI;
    public final double DegToRad=Math.PI/180;
    public final double ReverseDuration=0.2;
    public final String HelpText="Use the top menu to select a course and a topic.\n"
            +"The \"Start\" button will calculate the remaining variable(s), start the animation and plot the graph(s).\n"
            +"The \"Done\" button will return to the main screen to select another topic.\n"
            +"The \"Pause\" button will pause the animation.\n"
            +"The \"Reset\" button will clear all text fields and graphs and return the animation to the starting state.\n"
            +"The \"Help\" button will display the Help dialog (this dialog).\n\n"
            +"To use the program, input the known variables and leave the variable(s) to be calculated blank, then click \"Start\".\n"
            +"The remaining variable(s) will be calculated and the relevant animations and graphs will be displayed.\n\n"
            +"To enter a number in scientific notation, use E followed by the exponent of 10. Ex.: Input 2*10^6 as 2E6 or 4*10^-3 as 4E-3.\n"
            +"Some engineering prefixes with their respective values:\n"
            +"k=E3; M=E6; G=E9; T=E12; m=E-3; μ=E-6; n=E-9.\n";
    public final String empty="";
}