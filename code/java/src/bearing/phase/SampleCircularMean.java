/*
 */

package pubsim.bearing.phase;

import pubsim.Complex;

/**
 * Standard least squares estimator of phase
 * @author Robby McKilliam
 */
public class SampleCircularMean implements PhaseEstimator{

    private int n;

    public void setSize(int n) {
        this.n = n;
    }

    public double estimatePhase(Complex[] y) {
        n = y.length;
        
        double real = 0.0, imag = 0.0;
        for(int i = 0; i < n; i++){
            real += y[i].re();
            imag += y[i].im();
        }

        return Math.atan2(imag, real)/(2*Math.PI);

    }

}
