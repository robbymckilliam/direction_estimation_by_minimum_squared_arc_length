package bearing.phase;

import pubsim.Complex;

/**
 *
 * @author Robby McKilliam
 */
public interface PhaseEstimator {

    void setSize(int n);

    double estimatePhase(Complex[] y);

}
