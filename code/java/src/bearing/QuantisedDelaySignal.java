/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pubsim.bearing;

import pubsim.distributions.circular.WrappedUniform;
import pubsim.distributions.processes.NoiseVector;
import pubsim.SignalGenerator;
import static pubsim.Util.fracpart;

/**
 * Simulates a quantised timer for delay estimation with incomplete data.
 * The assumes that the period of the signal T = 1.
 * @author Robby McKilliam
 */
public class QuantisedDelaySignal extends NoiseVector{

    protected double delay = 0.0;
    private double P;

    public QuantisedDelaySignal(int length){ super(length); }

    public void setDelay(double delay){
        this.delay = delay;
    }

    public double getDelay(){
        return delay;
    }

    public void setClockPeriod(double P){
        this.P = P;
    }

    public double getClockPeriod(){
        return P;
    }

    /**
     * Generate the iid noise of length n.
     * Here, the noise generator is expected to be a discrete distribution
     * that generates the sparse u
     */
    @Override
    public Double[] generateReceivedSignal(){
        if( iidsignal.length != n )
            iidsignal = new Double[n];

        //WrappedUniform.Mod1 cnoise = new WrappedUniform.Mod1();
        //cnoise.setRange(P);

        double usum = noise.getNoise();
        //double usum = 0;
        for(int i = 0; i < n; i++){
            double yn = Math.round((usum + delay)/P) * P;
            iidsignal[i] = fracpart(yn);
            //iidsignal[i] = fracpart(delay + cnoise.getNoise());
            usum += noise.getNoise();
            //System.out.println(usum);
        }
        return iidsignal;
    }
}
