package bearing.phase;

import bearing.AngularLeastSquaresEstimator;
import pubsim.distributions.GaussianNoise;
import pubsim.distributions.RealRandomVariable;
import pubsim.distributions.circular.CircularRandomVariable;
import pubsim.distributions.circular.ProjectedNormalDistribution;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import pubsim.Complex;
import static pubsim.Util.fracpart;

/**
 *
 * @author Robby McKilliam
 */
public class RunSimulation {

    public static void main(String[] args) throws Exception {

        int n = 64;
        int seed = 26;
        int iterations = 1000;

        String nameetx = "_" + Integer.toString(n);

        ConstantPhaseSignal signal_gen = new ConstantPhaseSignal();
        signal_gen.setLength(n);

        double from_var_db = 10;
        double to_var_db = -25; //this goes to 25 for smaller n
        //double from_var_db = -8;
        //double to_var_db = -40.0;
        double step_var_db = -1;

        Vector<RealRandomVariable> noise_array = new Vector<RealRandomVariable>();
        Vector<Double> var_db_array = new Vector<Double>();
        for(double vardb = from_var_db; vardb >= to_var_db; vardb += step_var_db){
            var_db_array.add(new Double(vardb));
            noise_array.add(new GaussianNoise(0,Math.pow(10.0, ((vardb)/10.0))));
        }

        Vector<PhaseEstimator> estimators = new Vector<PhaseEstimator>();

        //add the estimators you want to run
        estimators.add(new LeastSquaresUnwrapping());
        //estimators.add(new ArgumentOfComplexMean());
        //estimators.add(new SamplingLatticeEstimator(12*n));
        //estimators.add(new KaysEstimator());
        //estimators.add(new PSCFDEstimator());
        //estimators.add(new VectorMeanEstimator());

        Iterator<PhaseEstimator> eitr = estimators.iterator();
        while(eitr.hasNext()){

            PhaseEstimator est = eitr.next();

            Vector<Double> mse_array = new Vector<Double>(noise_array.size());
            java.util.Date start_time = new java.util.Date();
            for(int i = 0; i < noise_array.size(); i++){

                RealRandomVariable noise = noise_array.get(i);
                signal_gen.setNoiseGenerator(noise);

                double mse = runIterations(est, signal_gen, iterations);

                mse_array.add(mse/iterations);

                System.out.println(
                        new ProjectedNormalDistribution(0.0, noise.getVariance()).intrinsicVariance()
                        + "\t" + mse/iterations);


            }
            java.util.Date end_time = new java.util.Date();
            System.out.println(est.getClass().getName() +
                    " completed in " +
                    (end_time.getTime() - start_time.getTime())/1000.0
                    + "seconds");

            try{
                String fname = est.getClass().getName() + "_" + noise_array.get(0).getClass().getName();
                File file = new File(fname.concat(nameetx).replace('$', '.'));
                BufferedWriter writer =  new BufferedWriter(new FileWriter(file));
                for(int i = 0; i < noise_array.size(); i++){
                    writer.write(
                            noise_array.get(i).toString().replace('E', 'e')
                            + "\t" + mse_array.get(i).toString().replace('E', 'e'));
                    writer.newLine();
                }
                writer.close();
            } catch(IOException e) {
                System.out.println(e.toString());
            }

        }

        Vector<Double> mse_array = new Vector<Double>(noise_array.size());
        //finally print out the asymptotic circularVariance
        for(int i = 0; i < noise_array.size(); i++){
                RealRandomVariable noise = noise_array.get(i);
                CircularRandomVariable circn = new ProjectedNormalDistribution(0.0, noise.getVariance());
                double mse = new AngularLeastSquaresEstimator(0).asymptoticVariance(circn, n);
                //double mse = new VectorMeanEstimator().asymptoticVariance(circn, n);
                mse_array.add(mse);
                System.out.println(circn.intrinsicVariance() + "\t" + mse);
        }
        try{
            String fname = "asymp_arg_" + noise_array.get(0).getClass().getName();
            //String fname = "asmyp_" + noise.getClass().getName();
            File file = new File(fname.concat(nameetx).replace('$', '.'));
            BufferedWriter writer =  new BufferedWriter(new FileWriter(file));
            for(int i = 0; i < noise_array.size(); i++){
                writer.write(
                        noise_array.get(i).toString().replace('E', 'e')
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
    public static double runIterations(PhaseEstimator rec, ConstantPhaseSignal siggen, int iterations){

        double mse = 0.0;
        Random r = new Random();
        for(int i = 0; i < iterations; i++){

            double angle = r.nextDouble() - 0.5;
            Complex mean = new Complex(Math.cos(angle*2*Math.PI), Math.sin(angle*2*Math.PI));
            siggen.setMean(mean);

            siggen.generateReceivedSignal();

            double anglehat = rec.estimatePhase(siggen.generateReceivedSignal());

            double err = fracpart(angle - anglehat);

            mse += err*err;

        }

        return mse;
    }

}
