package bearing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import pubsim.distributions.GaussianNoise;

/**
 *
 * @author Robby McKilliam
 */
public class QuantisedDelaySignalTest {

    public QuantisedDelaySignalTest() {
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
     * Print out a signal.
     */
    @Test
    public void testSetDelay() {

        int n = 200000;
        double P = 1/(4*Math.PI*Math.PI);

        QuantisedDelaySignal siggen = new QuantisedDelaySignal(n);
        siggen.setDelay(0);
        siggen.setClockPeriod(P);

        siggen.setNoiseGenerator(new GaussianNoise(0,0));

        Double[] signal = siggen.generateReceivedSignal();

        double var = 0;

        try{
            File file = new File("quantisedsig");
            BufferedWriter writer =  new BufferedWriter(new FileWriter(file));
          
            for(int i = 0; i < n; i++){
                var += signal[i]*signal[i];
                writer.write(new Double(signal[i]).toString());
                writer.newLine();
            }

            writer.close();
        } catch(IOException e) {
            System.out.println(e.toString());
        }

        var /= n;
        
        double expvar = Math.pow(P/2.0 , 2)/3.0;

        System.out.println(var + ", " + expvar);
        assertEquals(expvar, var, 0.0001);

    }


}