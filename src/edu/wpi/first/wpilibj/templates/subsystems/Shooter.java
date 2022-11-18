package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.templates.RobotTemplate;

/**
 *
 * @author Adam Hill
 */
public class Shooter {
    
    
    public PneumaticShooter pneumatics = new PneumaticShooter();
    
    private static final int SHOOT_BUTTON = 1;
    private static final int TOGGLE_BUTTON = 7;
    private static final double LOW_SPEED = 0.3;
    private static final double HIGH_SPEED = 0.5;
    private static final double FULL_SPEED = 1.0;
    private static final double STOP = 0.0;
    public static double currentSpeed = FULL_SPEED;
    private boolean highSpeed = false;
    private boolean lowSpeed = true;
    private boolean release = true;
    
    
    // Method to start and stop the shooter.  When the robot is first started
    // up and initialized, the parameter "speed" is set to LOW_SPEED.
    public void StartShooting(double speed) {
//        if (RobotTemplate.rightJoystick.getRawButton(SHOOT_BUTTON)) {
//            pneumatics.FirePiston();
//        }
    }
    
    
    // Method to start and stop the shooter.  When the robot is first started
    // up and initialized, the parameter "speed" is set to LOW_SPEED.
    public void XboxStartShooting(double speed) {
        if (RobotTemplate.assisstantController.getTriggerAxis() == RobotTemplate.RIGHT_TRIGGER)
        {
//            System.out.println("Recieved button push (Right Trigger)");
            RobotTemplate.timer.start();
            RobotTemplate.timer.reset();
            
        }
        if(RobotTemplate.timer.get() < 1.5 && RobotTemplate.timer.get() > 0.0)
            {
                RobotTemplate.shooterMotor.set(speed);
//                System.out.println("Motor should be turned on");
            }
//        System.out.println(RobotTemplate.timer.get());
        if (RobotTemplate.timer.get() > 1.5 && RobotTemplate.timer.get() < 2.15)
            {
                pneumatics.Fire();
//                System.out.println("pneumatics was called");
            }
        if (RobotTemplate.timer.get() > 3.0)
            {
                RobotTemplate.shooterMotor.set(STOP);
                RobotTemplate.timer.stop();
//                System.out.println("Motor and timer is stopped");
            }
            
          //  timer.reset();
    
        
//        if (timer != null)
//         {
//        pneumatics.RetractPiston();
//        }

    }
    

    
    // Method to toggle the speed of the shooter, using one button.  This
    // method was originally just for fun, but we may use it.  It is also for
    // instructional purposes.  Use this method for the joysticks.  Press
    // button "7" on the left joystick to toggle the two speeds.
    public void Toggle() {
        if ((lowSpeed == true) && (highSpeed == false) && (release == true) &&
                (RobotTemplate.leftJoystick.getRawButton(TOGGLE_BUTTON) == true))
        {
            currentSpeed = HIGH_SPEED;
            highSpeed = true;
            lowSpeed = false;
            release = false;
            System.out.println("High Speed");
        }
        
        if ((lowSpeed == false) && (highSpeed == true) && (release == true) &&
                (RobotTemplate.leftJoystick.getRawButton(TOGGLE_BUTTON) == true))
        {
            currentSpeed = LOW_SPEED;
            highSpeed = false;
            lowSpeed = true;
            release = false;
            System.out.println("Low Speed");
        }
        
         if (RobotTemplate.leftJoystick.getRawButton(TOGGLE_BUTTON) == false) {
            release = true;
        }
    }
    
    
    // Same as toggle method above, but for the Xbox controllers.  Use the
    // right bumper to toggle the two speeds.
    public void XboxToggle() {
        if ((lowSpeed == true) && (highSpeed == false) && (release == true) &&
                (RobotTemplate.controller.getRightBumper() == true))
        {
            currentSpeed = HIGH_SPEED;
            highSpeed = true;
            lowSpeed = false;
            release = false;
            System.out.println("High Speed");
        }
        
        if ((lowSpeed == false) && (highSpeed == true) && (release == true) &&
                (RobotTemplate.controller.getRightBumper() == true))
        {
            currentSpeed = LOW_SPEED;
            highSpeed = false;
            lowSpeed = true;
            release = false;
            System.out.println("Low Speed");
        }
        
        if (RobotTemplate.controller.getRightBumper() == false) 
        {
            release = true;
        }
    }
}
