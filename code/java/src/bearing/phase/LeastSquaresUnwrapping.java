/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pubsim.bearing.phase;

import pubsim.Complex;
import pubsim.bearing.AngularLeastSquaresEstimator;

/**
 * Phase estiamtor based on the nearest lattice point in An*.
 * Minimises the sum of square errror in the phase parameter.
 * @author Robby McKilliam
 */
public class LeastSquaresUnwrapping implements PhaseEstimator {

    private AngularLeastSquaresEstimator ls;
    private int n;
    Double[] a;

    public void setSize(int n) {
        this.n = n;
        ls = new AngularLeastSquaresEstimator(n);
        a = new Double[n];
    }

    public double estimatePhase(Complex[] y) {
        if(n != y.length)
            setSize(y.length);

        for(int i = 0; i < n; i++)
            a[i] = y[i].phase()/(2*Math.PI);

        return ls.estimateBearing(a);
    }

}
