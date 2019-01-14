package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Sam Dodrill
 * 
 * http://bazaar.launchpad.net/~shadow-h511/robototes2412java/mecanum/view/head:/src/com/shadowh511/mayor/inputs/Xbox.java
 * 
 */

public class XboxControllers {

    public Joystick xboxController;

    /*
     * Makes a new joystick object on port
     */
    public XboxControllers (int port) {
        xboxController = new Joystick(port);
    }

    /*
     * Returns the xbox 360 controller trigger axis
     */
    public double getTriggerAxis() {
        return this.xboxController.getRawAxis(3);
    }
    
    /*
     * Returns the xBox 360 controller buffered left stick X-axis
     */
    public double getLeftStickX() {
        return this.calcBuffer(this.xboxController.getRawAxis(1));
    }

    /*
     * The xbox 360 controller has a bad axis readout.  This function takes n
     * (this was when I didn't make long variable names).  It makes a value of
     * up to 0.2 (IE: barely touching it/ leaving it neutral) equal to zero.
     */
    private double calcBuffer(double n) {
        double buffer = 0.2;
        if (Math.abs(n) < buffer) {
            n = 0.000000000000000;
        }
        return n;
    }

    /*
     * Returns the buffered left stick y-axis
     */
    public double getLeftStickY() {
        return this.calcBuffer(this.xboxController.getRawAxis(2));
    }

    /*
     * Returns the buffered right stick X axis
     */
    public double getRightStickX() {
        return this.calcBuffer(this.xboxController.getRawAxis(4));
    }

    /*
     * Returns the buffered right stick Y axis
     */
    public double getRightStickY() {
        return this.calcBuffer(this.xboxController.getRawAxis(5));
    }

    /*
     * Returns the state of the A button (button 1)
     */
    public boolean getA() {
        return this.xboxController.getRawButton(1);
    }

    /*
     * Returns the state of the b button
     */
    public boolean getB() {
        return this.xboxController.getRawButton(2);
    }

    /*
     * Returns the state of the y button
     */
    public boolean getY() {
        return this.xboxController.getRawButton(4);
    }

    /*
     * returns the state of the x button
     */
    public boolean getX() {
        return this.xboxController.getRawButton(3);
    }

    /*
     * Returns the state of the right bumper
     */
    public boolean getRightBumper() {
        return this.xboxController.getRawButton(6);
    }

    /*
     * Returns the state of the left bumper
     */
    public boolean getLeftBumper() {
        return this.xboxController.getRawButton(5);
    }

    /*
     * Returns the state of the Back button
     */
    public boolean getBack() {
        return this.xboxController.getRawButton(7);
    }

    /*
     * Returns the state of the Start button
     */
    public boolean getStart() {
        return this.xboxController.getRawButton(8);
    }
    
    public boolean getLeftStick() {
        return this.xboxController.getRawButton(9);
    }
    
    public boolean getRightStick() {
        return this.xboxController.getRawButton(10);
    }
}


// Axis numbers:  1 = left stick x-axis
//                2 = left stick y-axis
//                3 = triggers (both; left is positive, right is negative)
//                4 = right stick x-axis
//                5 = right stick y-axis
//                6 = D-pad (needs testing)
//                    (I think the x-axis is 6 and the y-axis is 7)


// Button numbers:  1 = A button
//                  2 = B button
//                  3 = X button
//                  4 = Y button
//                  5 = left bumper
//                  6 = right bumper
//                  7 = back button
//                  8 = start button
//                  9 = left stick
//                  10 = right stick
//
//                  I have not found a way to use the xbox guide button.
//                  It may be 0 or 11, but I do not know.