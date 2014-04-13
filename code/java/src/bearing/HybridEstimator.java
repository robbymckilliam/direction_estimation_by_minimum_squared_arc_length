/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pubsim.bearing;

import pubsim.distributions.circular.CircularProcess;
import pubsim.distributions.circular.CircularRandomVariable;

/**
 * Hybrid estimator that computes both the sample circular mean and the
 * angular least squares estimators and there confidence intervals.
 * The estimate returned is the one that has the tightest confidence interval.
 * @author Robby McKilliam
 */
public class HybridEstimator implements BearingEstimator{

    protected final AngularLeastSquaresEstimator als;
    protected final SampleCircularMean scm;

    public HybridEstimator(int N){
        als = new AngularLeastSquaresEstimator(N);
        scm = new SampleCircularMean();
    }

    public double estimateBearing(Double[] y) {
        double[] scmr = scm.confidenceInterval(y);
        double[] alsr = als.confidenceInterval(y);
        if(scmr[1] < alsr[1]) return scmr[0];
        else return alsr[0];
    }

    public double[] confidenceInterval(Double[] y) {
        double[] scmr = scm.confidenceInterval(y);
        double[] alsr = als.confidenceInterval(y);
        if(scmr[1] < alsr[1]) return scmr;
        else return alsr;
    }

    public double asymptoticVariance(CircularRandomVariable noise, int N) {
        double scmv = scm.asymptoticVariance(noise, N);
        double alsv = als.asymptoticVariance(noise, N);
        return Math.min(scmv, alsv);
    }

    @Override
    public double asymptoticVariance(CircularProcess noise, int N) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
