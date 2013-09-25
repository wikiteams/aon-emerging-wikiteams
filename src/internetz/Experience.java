package internetz;

import cern.jet.random.ChiSquare;
import repast.simphony.random.RandomHelper;

public class Experience {
	
	public LearningCurve lc = null;
	public double percentage;
	public int top;
	
	private void say(String s) {
		System.out.println(s);
	}
	
	public Experience(){
		new Experience(0d, 0);
	}
	
	public Experience(double percentage, int top){
		lc = new LearningCurve();
		this.percentage = percentage;
		this.top = top;
	}
	
	public void check1(){
		if (lc != null)
			lc.checkZeta();
	}
	
	class LearningCurve{
		
		cern.jet.random.ChiSquare chi = null;
		cern.jet.random.ChiSquare zeta = null;
		
		int freedom = 1;
		
		LearningCurve(){
			chi = RandomHelper.getChiSquare();
			zeta = new ChiSquare(freedom, cern.jet.random.ChiSquare.makeDefaultGenerator());
		}
		
		public void checkChi(){
			say("chi.cdf(0): " + chi.cdf(0));
			say("chi.cdf(0.5): " + chi.cdf(0.5));
			say("chi.cdf(1): " + chi.cdf(1));
			say("chi.pdf(0): " + chi.pdf(0));
			say("chi.pdf(0.5): " + chi.pdf(0.5));
			say("chi.pdf(1): " + chi.pdf(1));
			say("chi.nextDouble(): " + chi.nextDouble());
		}
		
		public void checkZeta(){
			say("chi.cdf(0): " + zeta.cdf(0));
			say("chi.cdf(0.5): " + zeta.cdf(0.5));
			say("chi.cdf(1): " + zeta.cdf(1));
			say("chi.pdf(0): " + zeta.pdf(0.01));
			say("chi.pdf(0.5): " + zeta.pdf(0.5));
			say("chi.pdf(1): " + zeta.pdf(1));
			say("chi.nextDouble(): " + zeta.nextDouble());
		}
	}

}
