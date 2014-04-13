/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pubsim.bearing;

import pubsim.distributions.processes.NoiseVector;
import pubsim.SignalGenerator;
import static pubsim.Util.fracpart;

/**
 * Generate some angular data.
 * Assumes that angles are measure in interval [-1/2, 1/2).
 * @author Robby McKilliam
 */
public class ConstantAngleSignal extends NoiseVector {

    protected double angle = 0.0;

    public ConstantAngleSignal(int length){ super(length); }

    public void setAngle(double angle){
        this.angle = angle;
    }

    public double getAngle(){
        return angle;
    }
    
    /** 
     * Generate the noise of length n.
     */
    @Override
    public Double[] generateReceivedSignal(){
        if( iidsignal.length != n )
            iidsignal = new Double[n];
        for(int i = 0; i < n; i++)
            iidsignal[i] = fracpart(noise.getNoise() + angle);
        return iidsignal;
    }

    

}
