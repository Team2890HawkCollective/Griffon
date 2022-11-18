/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Relay;

/**
 *
 * @author King Code Monkey
 */
public class PressureSwitch {
    
    private static Compressor compressor = new Compressor(1, 1, 1, 1);
    
    public void Switch() {
        
//        System.out.println(compressor.getPressureSwitchValue());
        
        if (compressor.getPressureSwitchValue() == false) {
            compressor.setRelayValue(Relay.Value.kOn);
            compressor.start();
        }
        if (compressor.getPressureSwitchValue() == true) {
            compressor.stop();
            compressor.setRelayValue(Relay.Value.kOff);
        }   
    }
}
