 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.templates.RobotTemplate;

/**
 *
 * @author Adam Hill
 */
public class PneumaticShooter {
    
    public static DoubleSolenoid piston = new DoubleSolenoid(2, 1);
    boolean pneumaticMove = true;
    
    public void Fire() {
        StartTimer();
        if (RobotTemplate.timer.get() < 1.65 ){
            MoveForward();
        }
        if (RobotTemplate.timer.get() > 1.65 && RobotTemplate.timer.get() < 1.85){
            MoveDelay();
        }
        if (RobotTemplate.timer.get() > 1.85 && RobotTemplate.timer.get() < 2.15) {
            MoveReverse();
        }
        
        if (RobotTemplate.timer.get() >= 2.15){
            EndTimer();
        }
        System.out.println(RobotTemplate.timer.get());
    }
    
    private void StartTimer(){
        if (pneumaticMove == true){
            //Shooter.timer.start();
            pneumaticMove = false;
            //piston.set(DoubleSolenoid.Value.kOff);
            System.out.println("got to start");
            System.out.println("Start timer " + RobotTemplate.timer.get());
        }
    }
    
    public void EndTimer(){
        
            //Shooter.timer.stop();
            //Shooter.timer.reset();
            pneumaticMove = true;
            piston.set(DoubleSolenoid.Value.kOff);
        System.out.println("got to end");
    }
    
    private void MoveForward(){
         
            piston.set(DoubleSolenoid.Value.kForward);
        System.out.println("got to forward");

    }
    
    private void MoveDelay(){
         
           piston.set(DoubleSolenoid.Value.kOff);
         System.out.println("got to delay");
    }
    
    public void MoveReverse(){
        
             piston.set(DoubleSolenoid.Value.kReverse);
        System.out.println("got to reverse");
    }
//    public void TriggerReleased(){
//        if (Shooter.timer.get() > 2.0) {
//            MoveReverse();
//        }
//        
//        if (Shooter.timer.get() > 5.0){
//            EndTimer();
//        }
//    }
//    public void FirePiston()
//    {
//        piston.set(DoubleSolenoid.Value.kForward);
//    }
//    public void RetractPiston()
//    {
//        piston.set(DoubleSolenoid.Value.kOff);
//    }
}