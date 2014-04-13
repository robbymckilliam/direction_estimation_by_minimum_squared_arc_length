/**
* Script for mean direction estimation simulations.
*/
import bearing.AngularLeastSquaresEstimator
import bearing.BearingEstimator
import bearing.SampleCircularMean
import bearing.ConstantAngleSignal
import pubsim.distributions.GaussianNoise
import pubsim.distributions.RandomVariable
import pubsim.distributions.circular.CircularMeanVariance
import pubsim.distributions.circular.CircularRandomVariable
import pubsim.distributions.circular.ProjectedNormalDistribution
import pubsim.distributions.circular.VonMises
import pubsim.distributions.circular.WrappedGaussian
import pubsim.distributions.circular.WrappedUniform
import pubsim.distributions.circular.SumOfCircularDistributions
import pubsim.distributions.circular.CircularUniform
import pubsim.distributions.circular.UnwrappedMeanAndVariance
import pubsim.Util._

val Ns = List(4,64,1024) //number of observations
val iters = 10000 //number of iterations to run for each variance

def heavytaildist(p : Double) = {
  val dist = new SumOfCircularDistributions
  dist.addDistribution( new VonMises(0.0, 1.0/p), 1-p )
  dist.addDistribution( new CircularUniform, p )
  dist
}

//construct an list of lists noise distributions with a logarithmic scale
val randomvars = List( 
  Range.Double(-41.0, -1, 0.05).map( db => scala.math.pow(10, db/30.0) ).map( p => heavytaildist(p) ),
  Range.Double(-30.0, -8, 0.02).map( db => scala.math.pow(10, db/10.0) ).map( v => new WrappedGaussian(0,v) ),
  Range.Double(-30.0, -11, 0.02).map( db => scala.math.pow(10.0, db/10.0) ).map( v => new WrappedUniform(0,v) ),
  Range.Double(15.0, -9.0, -0.02).map( db => scala.math.pow(10, db/10.0) ).map( v => new VonMises(0,v) )
)

//function returns a random angle
val randanglegener = new scala.util.Random
def randangle = randanglegener.nextDouble() - 0.5

//for all the random variables and all the N
for( noises <- randomvars ; N <- Ns ) {

  //List of estimators we will test for this N and noise distribution
  val estlist = List( new AngularLeastSquaresEstimator(N), new SampleCircularMean )
  
  for( est <- estlist ){
  
    val Tfname = (est.getClass().getSimpleName() + "_" + noises(0).getClass().getSimpleName() + "_" + N).replace('$','_')
    val cltfile = new java.io.FileWriter("clt_" + Tfname)

    println("var \t clt")
    val starttime = (new java.util.Date).getTime
    
    for(noise <- noises ){
      
      //compute the wrapped variance of this circular random variable
      val wrpvar = noise.unwrappedVariance(0.0)
      //compute the varaince given by the central limit theorem
      val cltmse = est.asymptoticVariance(noise, N);
	
      println(wrpvar.toString.replace('E', 'e')  + "\t" + cltmse.toString.replace('E', 'e'))
      cltfile.write(wrpvar.toString.replace('E', 'e') + "\t" + cltmse.toString.replace('E', 'e') + "\n")
		
    }
    val runtime = (new java.util.Date).getTime - starttime
    println(Tfname + " finished in " + (runtime/1000.0) + " seconds.\n") 
    cltfile.close
  }

}
