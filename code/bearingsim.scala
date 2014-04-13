/**
* Script for mean direction estimation simulations.
*/
import bearing.AngularLeastSquaresEstimator
import bearing.BearingEstimator
import bearing.SampleCircularMean
import bearing.ConstantAngleSignal
import pubsim.distributions.GaussianNoise
import pubsim.distributions.RealRandomVariable
import pubsim.distributions.circular.CircularMeanVariance
import pubsim.distributions.circular.CircularRandomVariable
import pubsim.distributions.circular.ProjectedNormalDistribution
import pubsim.distributions.circular.VonMises
import pubsim.distributions.circular.WrappedGaussian
import pubsim.distributions.circular.WrappedUniform
import pubsim.distributions.circular.SumOfCircularDistributions
import pubsim.distributions.circular.CircularUniform
import pubsim.distributions.circular.InstrinsicMeanAndVariance
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
  Range.Double(-41.0, 1, 2).map( db => scala.math.pow(10, db/30.0) ).map( p => heavytaildist(p) ),
  Range.Double(-30.0, -7, 1).map( db => scala.math.pow(10, db/10.0) ).map( v => new WrappedGaussian(0,v) ),
  Range.Double(-30.0, -10, 1).map( db => scala.math.pow(10.0, db/10.0) ).map( v => new WrappedUniform(0,v) ),
  Range.Double(15.0, -10.0, -1.0).map( db => scala.math.pow(10, db/10.0) ).map( v => new VonMises(0,v) )
)

//function returns a random angle
val randanglegener = new scala.util.Random
def randangle = randanglegener.nextDouble() - 0.5

//for all the random variables and all the N
for( noises <- randomvars.par ; N <- Ns ) {

  val siggen =  new ConstantAngleSignal(N) //noisy circular data

  //List of estimators we will test for this N and noise distribution
  val estlist = List( new AngularLeastSquaresEstimator(N), new SampleCircularMean )
  
  for( est <- estlist ){
  
    val Tfname = (est.getClass().getSimpleName() + "_" + noises(0).getClass().getSimpleName() + "_" + N).replace('$','_')
    val Tfile = new java.io.FileWriter("data/" + Tfname)

    val starttime = (new java.util.Date).getTime
    
    for(noise <- noises ){
      siggen.setNoiseGenerator(noise)
      
      //compute the mses
      val msetotal = (1 to iters).map{ i => 
	  val a = randangle
	  siggen.setAngle(a)
	  val ahat = est.estimateBearing(siggen.generateReceivedSignal)
	  val d = fracpart(ahat - a)
	  d*d
	}.foldLeft(0.0)( _ + _)	
      val mse = msetotal/iters
      
      //compute the wrapped variance of this circular random variable
      val wrpvar = noise.intrinsicVariance(0.0)
      Tfile.write(wrpvar.toString.replace('E', 'e') + "\t" + mse.toString.replace('E', 'e') + "\n")		
    }
    val runtime = (new java.util.Date).getTime - starttime
    println(Tfname + " finished in " + (runtime/1000.0) + " seconds.") 
    Tfile.close;
  }

}
