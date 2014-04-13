package bearing;

import pubsim.distributions.GaussianNoise;
import pubsim.distributions.circular.CircularProcess;
import pubsim.distributions.circular.CircularRandomVariable;
import pubsim.distributions.circular.DensityEstimator;
import pubsim.lattices.Anstar.Anstar;
import static pubsim.Util.fracpart;
import pubsim.lattices.Anstar.AnstarLinear;

/**
 * Least squares phase unwrapping estimator based on the lattice
 * An*.  Assumes that angles are measure in interval [-1/2, 1/2).
 * @author Robert McKilliam
 */
public class AngularLeastSquaresEstimator implements BearingEstimator{

    final int n;
    protected final Anstar anstar;
    protected double[] u;
    
    public AngularLeastSquaresEstimator(int length){
        this.n = length;
        anstar = new AnstarLinear(n-1);
    }    

    @Override
    final public double estimateBearing(Double[] y) {
        if(n != y.length) throw new ArrayIndexOutOfBoundsException("y is the wrong length");
        
        anstar.nearestPoint(y);
        u = anstar.getIndex();
        
        double sum = 0.0;
        for(int i = 0; i < y.length; i++)
            sum += y[i] - u[i];
        
        return fracpart(sum/n);
        
    }

    /**
     * Compute the asymptotic variance of the angular least squares
     * estimator. This makes the assumption that the distribution has
     * zero unwrapped mean. It will be incorrect if this is not the case.
     */
    public double asymptoticVariance(CircularRandomVariable noise, int N){
        double sigma2 = noise.intrinsicVariance(0.0);
        double d = 1 - noise.pdf(-0.5);
        return sigma2/(N*d*d);
    }

    public double[] confidenceInterval(Double[] y) {
        int N = y.length;
        double mu = estimateBearing(y);

        //it may be that other kernels than rectangular will be better.
        //double h = new DensityEstimator(y, new UniformNoise(0, 1.5/N, 0)).pdf(mu - 0.5);
        double h = new DensityEstimator(y, new GaussianNoise(0, 70.0/N/N )).pdf(mu - 0.5);

        double wrpv = 0.0;
        for(int i = 0; i < N; i++) wrpv += fracpart(y[i] - mu)*fracpart(y[i] - mu);
        double var = wrpv/(1-h)/(1-h)/N/N;
        
        double[] ret = new double[2]; ret[0] = mu; ret[1] = var;
        return ret;
        
    }

    @Override
    public double asymptoticVariance(CircularProcess noise, int N) {
        double[] ac = noise.autocorrelation();
        double d = 1 - noise.circularMarginal().pdf(-0.5);
        double h = ac[0];
        for(int k = 1; k < ac.length; k++) h += 2*ac[k];
        return h/d/d/N;
    }
    

}
