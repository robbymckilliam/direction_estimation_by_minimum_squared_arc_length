package bearing;

import pubsim.distributions.circular.CircularProcess;
import pubsim.distributions.circular.CircularRandomVariable;

/**
 * Interface for a Bearing estimator
 * @author Robby McKilliam
 */
public interface BearingEstimator {
    
    double estimateBearing(Double[] y);
    
    /**
     * Compute and estimate and the confidence interval.
     * Returns an double array of length 2. The first element is
     * the estimate and the second element is the confidence interval
     */
    double[] confidenceInterval(Double[] y);

    /**
     * Return the asymptotic variance of this estimator for the random
     * variable noise and N observations.
     */
    double asymptoticVariance(CircularRandomVariable noise, int N);
    
    /**
     * Return the asymptotic variance of this when the noise is generated by 
     * a random circular process.
     */
    double asymptoticVariance(CircularProcess noise, int N);
    
}
