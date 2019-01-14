/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.templates.RobotTemplate;

/**
 *
 * @author Team 2890
 */
public class CameraGimble {
    
//    public static double cameraHorizontalAngle = 0;
//    public static double cameraVerticalAngle = 0;
    public static final double servoRatio = 1;
    public static final double servoBuffer = 0.5;
    
    public void MoveCamera() {
        
        if ((RobotTemplate.assisstantController.getRightStickY() > servoBuffer) && (RobotTemplate.cameraVerticalAngle > 0))
        {
            RobotTemplate.cameraVerticalAngle -= servoRatio;
        }
        
        if ((RobotTemplate.assisstantController.getRightStickY() < -servoBuffer) && (RobotTemplate.cameraVerticalAngle < 170))
        {
            RobotTemplate.cameraVerticalAngle += servoRatio;
        }
        
        if ((RobotTemplate.assisstantController.getRightStickX() > servoBuffer) && (RobotTemplate.cameraHorizontalAngle < 170))
        {
            RobotTemplate.cameraHorizontalAngle += servoRatio;
        }
        
        if ((RobotTemplate.assisstantController.getRightStickX() < -servoBuffer) && (RobotTemplate.cameraHorizontalAngle > 0))
        {
            RobotTemplate.cameraHorizontalAngle -= servoRatio;
        }
        
        RobotTemplate.cameraHorizontalServo.setAngle(RobotTemplate.cameraHorizontalAngle);
        RobotTemplate.cameraVerticalServo.setAngle(RobotTemplate.cameraVerticalAngle);
        
//        System.out.println("Servo Vertical Position: " + RobotTemplate.cameraVerticalAngle);
//        
//        RobotTemplate.cameraHorizontalAngle = ((RobotTemplate.assisstantJoystick.getX() + 1.0) / 2);
//        RobotTemplate.cameraVerticalAngle = ((RobotTemplate.assisstantJoystick.getY() + 1.0) / 2);
//        
//        RobotTemplate.cameraHorizontalServo.set(RobotTemplate.cameraHorizontalAngle);
//        RobotTemplate.cameraVerticalServo.set(RobotTemplate.cameraVerticalAngle);
        
        
    }
    
    public void CenterCamera() {
        
        if (RobotTemplate.assisstantController.getLeftBumper() == true)
        {
            RobotTemplate.cameraHorizontalAngle = 85;
            RobotTemplate.cameraVerticalAngle = 85;
        }
    }
}
