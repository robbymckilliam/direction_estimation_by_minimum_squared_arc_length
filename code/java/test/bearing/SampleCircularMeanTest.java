/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pubsim.bearing;

import pubsim.bearing.BearingEstimator;
import pubsim.bearing.SampleCircularMean;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import pubsim.distributions.RealRandomVariable;
import pubsim.distributions.circular.CircularRandomVariable;
import pubsim.distributions.processes.NoiseVector;
import pubsim.distributions.circular.WrappedGaussian;
import static org.junit.Assert.*;

/**
 *
 * @author robertm
 */
public class SampleCircularMeanTest {

    public SampleCircularMeanTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of estimateBearing method, of class LeastSquaresEstimator.
     */
    @Test
    public void testEstimateBearing() {
        System.out.println("estimateBearing");
        
        int n = 20;
        double mean = 0.2;
        
        RealRandomVariable noise = new WrappedGaussian(mean, 0.0001);
        NoiseVector sig = new NoiseVector(n);
        sig.setNoiseGenerator(noise);
        
        Double[] y = sig.generateReceivedSignal();
        
        BearingEstimator instance = new SampleCircularMean();

        double result = instance.estimateBearing(y);
        
        System.out.println(mean);
        System.out.println(result);
          
        assertTrue(Math.abs(result - mean)< 0.01);

    }

    /**
     * Test of estimateBearing method, of class LeastSquaresEstimator.
     */
    @Test
    public void testConfidenceInterval() {
        System.out.println("estimate confidence interval");

        int n = 5000;
        double mean = 0.0;

        CircularRandomVariable noise = new WrappedGaussian(mean, 0.01);
        NoiseVector sig = new NoiseVector(n);
        sig.setNoiseGenerator(noise);

        Double[] y = sig.generateReceivedSignal();

        BearingEstimator instance = new SampleCircularMean();

        double[] res = instance.confidenceInterval(y);

        //System.out.println(mean + ", " + res[0]);
        assertEquals(mean, res[0], 0.01 );

        //this assumes that the unwrapped mean is zero, this test will
        //fail if you change the mean, but can be made to work if you
        //change the behaviour of this function. However doing so makes
        //some computations very slow.
        double var = instance.asymptoticVariance(noise, n);

        System.out.println(var);
        System.out.println(res[1]);
        assertEquals(n*var, n*res[1], 0.01 );
        
    }

}