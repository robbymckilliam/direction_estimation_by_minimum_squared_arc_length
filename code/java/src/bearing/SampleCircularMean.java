/*
 * @author Robby McKilliam
 */

package pubsim.bearing;

import pubsim.distributions.circular.CircularProcess;
import pubsim.distributions.circular.CircularRandomVariable;
import flanagan.integration.IntegralFunction;
import flanagan.integration.Integration;

/**
 * Assumes that angles are measured in the interval [-1/2, 1/2).
 * @author Robby McKilliam
 */
public class SampleCircularMean implements BearingEstimator {

    public double estimateBearing(Double[] y) {
        
        double csum = 0.0, ssum = 0.0;
        double twopi = 2.0 * Math.PI;
        for(int i = 0; i < y.length; i++){
            csum += Math.cos(twopi*y[i]);
            ssum += Math.sin(twopi*y[i]);
        }
        
        return Math.atan2(ssum, csum)/twopi;
                
    }

    public double asymptoticVariance(final CircularRandomVariable noise, int N){

        final double mu = noise.circularMean();
        final int INTEGRAL_STEPS = 5000;
        double Esin2 = (new Integration(new IntegralFunction() {
            public double function(double x) {
                double cosx = Math.cos(4*Math.PI*(x-mu));
                return 0.5*(1 - cosx)*noise.pdf(x);
            }
        }, -0.5, 0.5)).gaussQuad(INTEGRAL_STEPS);
        double sigma2 = 1 - noise.circularVariance();

        return Esin2/(N*sigma2*sigma2*4*Math.PI*Math.PI);
    }

    public double[] confidenceInterval(Double[] y) {
        int N = y.length;
        double twopi = 2.0 * Math.PI;
        double mu = estimateBearing(y);

        double cos4sum = 0.0, sinsum = 0.0, cossum = 0.0;
        for(int n = 0; n < N; n++){
            double sinymu = Math.sin(twopi*(y[n] - mu));
            cos4sum += Math.cos(2*twopi*(y[n] - mu));
            sinsum += Math.sin(twopi*(y[n] - mu));
            cossum += Math.cos(twopi*(y[n] - mu));
        }
        cos4sum/=N; sinsum/=N; cossum/=N;

        double sin2e = 0.5*(1 - cos4sum);
        double r2 = sinsum*sinsum + cossum*cossum;

        double varest = sin2e/(r2*4*Math.PI*Math.PI)/N;
        double[] ret = new double[2]; ret[0] = mu; ret[1] = varest;
        return ret;
    }

    @Override
    public double asymptoticVariance(CircularProcess noise, int N) {
        double[] ac = noise.sinusoidalAutocorrelation();
        double v = noise.circularMarginal().circularVariance();
        double h = ac[0];
        for(int k = 1; k < ac.length; k++) h += 2*ac[k];
        return h/(1-v)/(1-v)/4/Math.PI/Math.PI/N;
    }

}
