package internetz;

import logger.PjiitOutputter;
import cern.jet.random.ChiSquare;
import repast.simphony.random.RandomHelper;

public class Experience {

	public LearningCurve lc = null;
	public double value;
	public int top; // hipotetyczne przeuczenie

	public Experience() {
		new Experience(0d, 0);
	}

	public Experience(double value, int top) {
		lc = new LearningCurve();
		say("Creating Experience object with value: " + value
				+ " and top: " + top);
		this.value = value;
		this.top = top;
	}
	
	public double getDelta(){
		return lc.getDelta((top / value));
	}
	
	public void increment(double how_much){
		this.value += how_much;
		sanity("Experience incremented by: " + how_much);
	}

//	public int getCardinal() {
//		return (int) (percentage * top);
//	}
//
//	public void check1() {
//		if (lc != null)
//			lc.checkZeta();
//	}

	/**
	 * 
	 * To jest nasza funkcja delty! delta(E)
	 * Ta klasa nie ma nic wspolnego ze zmienna E (doswiadczenia)
	 * a sluzy jedyni otrzymaniu wartosci delta z E
	 * 
	 * @author Oskar
	 * @since 1.1
	 */
	class LearningCurve {

		cern.jet.random.ChiSquare chi = null;
		//cern.jet.random.ChiSquare zeta = null;

		int freedom = 4;
		int k = 5;

		LearningCurve() {
			//chi = RandomHelper.getChiSquare();
			chi = new ChiSquare(k,
					cern.jet.random.ChiSquare.makeDefaultGenerator());
		}
		
		private double getDelta(double k){
			//NOTE: freedom (x axis of CDF) should be between 0 and 4
			return chi.cdf(k * freedom);
		}

//		public void checkChi() {
//			say("chi.cdf(0): " + chi.cdf(0));
//			say("chi.cdf(0.5): " + chi.cdf(0.5));
//			say("chi.cdf(1): " + chi.cdf(1));
//			say("chi.pdf(0): " + chi.pdf(0));
//			say("chi.pdf(0.5): " + chi.pdf(0.5));
//			say("chi.pdf(1): " + chi.pdf(1));
//			say("chi.nextDouble(): " + chi.nextDouble());
//		}
//
//		public void checkZeta() {
//			say("chi.cdf(0): " + zeta.cdf(0));
//			say("chi.cdf(0.5): " + zeta.cdf(0.5));
//			say("chi.cdf(1): " + zeta.cdf(1));
//			say("chi.pdf(0): " + zeta.pdf(0.01));
//			say("chi.pdf(0.5): " + zeta.pdf(0.5));
//			say("chi.pdf(1): " + zeta.pdf(1));
//			say("chi.nextDouble(): " + zeta.nextDouble());
//		}
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}
	
	private void sanity(String s){
		PjiitOutputter.sanity(s);
	}
}
