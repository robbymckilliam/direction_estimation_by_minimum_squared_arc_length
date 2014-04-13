package bearing;

import bearing.AngularLeastSquaresEstimator;
import bearing.BearingEstimator;
import bearing.SampleCircularMean;
import bearing.ConstantAngleSignal;
import pubsim.distributions.circular.CircularMeanVariance;
import pubsim.distributions.circular.CircularRandomVariable;
import pubsim.distributions.circular.WrappedGaussian;
import pubsim.distributions.circular.WrappedUniform;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import static pubsim.Util.fracpart;

/**
 *
 * @author Robert McKilliam
 */
public class RunNoisyDelaySimulation {

    public static void main(String[] args) throws Exception {

        int n = 1024;
        double angle = 0.1;
        int seed = 26;
        int iterations = 1000;

        String nameetx = "_" + Integer.toString(n);

        ConstantAngleSignal signal_gen = new ConstantAngleSignal(n);
        
        //using reflection here is a bit of a hack, but oh well.
        //it keeps the remaining code neat.
        //Class<WrappedUniform> noiseclass = WrappedUniform.class;
        Class<WrappedGaussian> noiseclass = WrappedGaussian.class;
        final Class[] cona = {double.class, double.class};

        double from_var_db = -8;
        double to_var_db = -30;
        //double from_var_db = -8;
        //double to_var_db = -40.0;
        double step_var_db = -1;

        Vector<Double> var_array = new Vector<Double>();
        Vector<Double> var_db_array = new Vector<Double>();
        for(double vardb = from_var_db; vardb >= to_var_db; vardb += step_var_db){
            var_db_array.add(new Double(vardb));
            var_array.add(new Double(Math.pow(10.0, ((vardb)/10.0))));
        }

        Vector<BearingEstimator> estimators = new Vector<BearingEstimator>();

        //add the estimators you want to run
        estimators.add(new AngularLeastSquaresEstimator(n));
        estimators.add(new SampleCircularMean());

        Iterator<BearingEstimator> eitr = estimators.iterator();
        while(eitr.hasNext()){

            BearingEstimator est = eitr.next();

            Vector<Double> mse_array = new Vector<Double>(var_array.size());
            java.util.Date start_time = new java.util.Date();
            for(int i = 0; i < var_array.size(); i++){

                CircularRandomVariable noise = noiseclass.getConstructor(cona).newInstance(0, var_array.get(i));
                signal_gen.setNoiseGenerator(noise);

                double mse = runIterations(est, signal_gen, iterations);

                mse_array.add(mse/iterations);

                System.out.println(var_array.get(i) + "\t" + mse/iterations);


            }
            java.util.Date end_time = new java.util.Date();
            System.out.println(est.getClass().getName() +
                    " completed in " +
                    (end_time.getTime() - start_time.getTime())/1000.0
                    + "seconds");

            try{
                String fname = est.getClass().getName() + "_" + noiseclass.getName();
                File file = new File(fname.concat(nameetx).replace('$', '.'));
                BufferedWriter writer =  new BufferedWriter(new FileWriter(file));
                for(int i = 0; i < var_array.size(); i++){
                    writer.write(
                            var_array.get(i).toString().replace('E', 'e')
                            + "\t" + mse_array.get(i).toString().replace('E', 'e'));
                    writer.newLine();
                }
                writer.close();
            } catch(IOException e) {
                System.out.println(e.toString());
            }

        }

        Vector<Double> mse_array = new Vector<Double>(var_array.size());
        //finally print out the asymptotic circularVariance
        for(int i = 0; i < var_array.size(); i++){
                CircularRandomVariable noise = noiseclass.getConstructor(cona).newInstance(0, var_array.get(i));
                //double mse = AngularLeastSquaresEstimator.asymptoticVariance(noise, n);
                 double mse = (new CircularMeanVariance(noise)).circularVariance()/n;
                //double wrappedvar = noise.getWrappedVariance();
                //double mse = var_array.get(i)/n;
                mse_array.add(mse);
                System.out.println(var_array.get(i) + "\t" + mse);
        }
        try{
            String fname = "asmyp_arg_" + noiseclass.getName();
            //String fname = "crb_" + noise.getClass().getName();
            File file = new File(fname.concat(nameetx).replace('$', '.'));
            BufferedWriter writer =  new BufferedWriter(new FileWriter(file));
            for(int i = 0; i < var_array.size(); i++){
                    writer.write(
                            var_array.get(i).toString().replace('E', 'e')
                            + "\t" + mse_array.get(i).toString().replace('E', 'e'));
                    writer.newLine();
            }
            writer.close();
        } catch(IOException e) {
            System.out.println(e.toString());
        }

    }

    /**
     * Runs \param iterations number of iterations of the QAM receiver and
     * returns the codeword error rate (CER)
     */
    public static double runIterations(BearingEstimator rec, ConstantAngleSignal siggen, int iterations){

        double mse = 0.0;
        Random r = new Random();
        for(int i = 0; i < iterations; i++){

            double angle = r.nextDouble() - 0.5;
            siggen.setAngle(angle);

            siggen.generateReceivedSignal();

            double anglehat = rec.estimateBearing(siggen.generateReceivedSignal());

            double err = fracpart(siggen.getAngle() - anglehat);

            mse += err*err;

        }

        return mse;
    }

}
