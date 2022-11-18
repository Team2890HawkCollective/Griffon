/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Jaguar;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.templates.subsystems.AutonomousShooter;
import edu.wpi.first.wpilibj.templates.subsystems.CameraGimble;
import edu.wpi.first.wpilibj.templates.subsystems.Shooter;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {
    
    // Initialize all objects and variables here
    
    public static Joystick leftJoystick;
    public static Joystick rightJoystick;
    public static Joystick assisstingJoystick;
    public static XboxControllers controller;
    public static XboxControllers assisstantController;
    
    public static Jaguar leftMotor;
    public static Jaguar rightMotor;
    public static Jaguar angleMotor;
    public static Jaguar shooterMotor;
    
    public static Timer timer;
    public static Timer autonomousTimer;
    private static Gyro gyro;
    private static DoubleSolenoid liftPiston;
    public static PressureSwitch pressureSwitch;
    
    public static double cameraHorizontalAngle = 85;
    public static double cameraVerticalAngle = 85;
    public static boolean centerCamera = true;
    public static Servo cameraHorizontalServo;
    public static Servo cameraVerticalServo;
    
    public static RobotDrive drive;
    public static Shooter shoot;
    public static AutonomousShooter autonomousShooter;
//    public static CameraGimble camera;
//    public static TargetingCode target;
    
    public static final int DIGITAL_SIDE_CAR = 1;
    public static final int LEFT_MOTOR_PORT = 2;
    public static final int RIGHT_MOTOR_PORT = 1;
    public static final int SHOOTER_MOTOR_PORT = 9;
    public static final int ANGLE_MOTOR_PORT = 8;
    public static final int CAMERA_HORIZONTAL_SERVO_PORT = 5;
    public static final int CAMERA_VERTICAL_SERVO_PORT = 7;
    public static final int LEFT_JOYSTICK_PORT = 1;
    public static final int RIGHT_JOYSTICK_PORT = 2;
    public static final int ASSISTANCE_JOYSTICK_PORT = 3;
//    public static final int CONTROLLER_PORT = 1;
//    public static final int ASSISSTANT_CONTROLLER_PORT = 2;
    
    public static final double LEFT_TRIGGER = 1.0;
    public static final double RIGHT_TRIGGER = -1.0;
    
    public static boolean isXCentered = true;
    //public static boolean isYCentered = true;
    public static boolean isInRange = true; //Optimal distance to target
    
    private static boolean timerStart = true;
    private static boolean gyroReset = true;
    private static boolean lift = true;
    
     /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        // All constructors go here
        
        leftJoystick = new Joystick(LEFT_JOYSTICK_PORT);
        rightJoystick = new Joystick(RIGHT_JOYSTICK_PORT);
        assisstingJoystick = new Joystick(ASSISTANCE_JOYSTICK_PORT);
//        controller = new XboxControllers(CONTROLLER_PORT);
//        assisstantController = new XboxControllers(ASSISSTANT_CONTROLLER_PORT);
        
        leftMotor = new Jaguar(DIGITAL_SIDE_CAR, LEFT_MOTOR_PORT);
        rightMotor = new Jaguar(DIGITAL_SIDE_CAR, RIGHT_MOTOR_PORT);
        angleMotor = new Jaguar(DIGITAL_SIDE_CAR, ANGLE_MOTOR_PORT);
        shooterMotor = new Jaguar(DIGITAL_SIDE_CAR, SHOOTER_MOTOR_PORT);
        
        timer = new Timer();
        autonomousTimer = new Timer();
        
        gyro = new Gyro(1);
        liftPiston = new DoubleSolenoid(3, 4);
        pressureSwitch = new PressureSwitch();
        
        cameraHorizontalServo = new Servo(DIGITAL_SIDE_CAR, CAMERA_HORIZONTAL_SERVO_PORT);
        cameraVerticalServo = new Servo(DIGITAL_SIDE_CAR, CAMERA_VERTICAL_SERVO_PORT);
        
        drive = new RobotDrive(leftMotor, rightMotor);
        shoot = new Shooter();
        autonomousShooter = new AutonomousShooter();
//        camera = new CameraGimble();
//        target = new TargetingCode();
        
        liftPiston.set(DoubleSolenoid.Value.kOff);
        
        
        
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        // If you would like to test something during autonomous mode,
        // call the methods in here
        
//        if (centerCamera = true)
//        {
//        camera.MoveCamera();
//        centerCamera = false;
//        }
        
        if (timerStart == true) {
            autonomousTimer.start();
            timerStart = false;
        }
        
        autonomousShooter.AutoShooting(Shooter.currentSpeed);
        
        lift = false;
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        
        autonomousTimer.stop();
        autonomousTimer.reset();
        timerStart = true;
        
        // Call all of the methods from other classes here
        
        // WARNING:  Do not use the joysticks and the Xbox controllers at
        //           the same time.  Usage of both may result in the
        //           destruction of the robot, the room,... and your life.
        
        drive.tankDrive(leftJoystick, rightJoystick);
//        drive.tankDrive(controller.getLeftStickY(), controller.getRightStickY()); //for xbox controller
        
        shoot.StartShooting(Shooter.currentSpeed);
//        shoot.XboxStartShooting(Shooter.currentSpeed);

        shoot.Toggle();
//        shoot.XboxToggle();
        
//        camera.MoveCamera();
//        camera.CenterCamera();
        
        if (assisstantController.getTriggerAxis() == LEFT_TRIGGER)
//        {
//            isXCentered = false;
//            //isYCentered = false;
//            isInRange = false; 
//        }
        
//        target.CenterBot();
//        target.HorizontalDistance();
        
        if ((assisstantController.getBack() == true) && (lift == false)) {
            liftPiston.set(DoubleSolenoid.Value.kReverse);
            System.out.println("lift activated");
            lift = true;
        }
        
        pressureSwitch.Switch();
        
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
