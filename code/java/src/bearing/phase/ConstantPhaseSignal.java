package bearing.phase;

import pubsim.SignalGenerator;
import pubsim.Complex;
import pubsim.distributions.NoiseGenerator;

/**
 * Returns a noisy constant phase complex signal.
 * @author Robby McKilliam
 */
public class ConstantPhaseSignal implements SignalGenerator<Complex>{

    protected Complex mean;
    protected Complex[] signal;
    protected int n;
    protected NoiseGenerator<Double> noise;

    public ConstantPhaseSignal(){
        this.mean = new Complex();
    }

    public ConstantPhaseSignal(Complex mean){
        this.mean = mean;
    }

    public void setMean(Complex mean){
        this.mean = mean;
    }

    @Override
    public Complex[] generateReceivedSignal() {
        for(int i = 0; i<n; i++){
            Complex nc = new Complex(noise.getNoise(), noise.getNoise());
            signal[i] = mean.add(nc);
        }
        return signal;
    }

    @Override
    public void setNoiseGenerator(NoiseGenerator noise) {
        this.noise = noise;
    }

    @Override
    public NoiseGenerator getNoiseGenerator() {
        return noise;
    }

    public void setLength(int n) {
        this.n = n;
        signal = new Complex[n];
    }

    @Override
    public int getLength() {
        return n;
    }

}
