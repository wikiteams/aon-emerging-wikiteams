package internetz;

import java.text.DecimalFormat;

import logger.PjiitOutputter;
import cern.jet.random.ChiSquare;
import cern.jet.random.Zeta;

public class Experience {

	public LearningCurve lc = null;
	public double value; // aktualne "plain" doswiadczenie
	public int top; // hipotetyczne przeuczenie

	protected final static ExperienceSanityCheck esc = new ExperienceSanityCheck();

	public Experience() {
		new Experience(0d, 0);
	}

	public Experience(double value, int top) {
		lc = new LearningCurve();
		say("Creating Experience object with value: " + value + " and top: "
				+ top);
		this.value = value;
		this.top = top;
	}

	public double getDelta() {
		return lc.getDelta((value / top));
	}

	public void increment(double how_much) {
		this.value += how_much;
		DecimalFormat df = new DecimalFormat("#.######");
		sanity("Experience incremented by: " + df.format(how_much));
	}

	// public int getCardinal() {
	// return (int) (percentage * top);
	// }
	//
	// public void check1() {
	// if (lc != null)
	// lc.checkZeta();
	// }

	/**
	 * 
	 * To jest nasza funkcja delty! delta(E) Ta klasa nie ma nic wspolnego ze
	 * zmienna E (doswiadczenia) a sluzy jedyni otrzymaniu wartosci delta z E
	 * 
	 * @author Oskar
	 * @since 1.1
	 */
	class LearningCurve {

		cern.jet.random.ChiSquare chi = null;
		// cern.jet.random.ChiSquare zeta = null;

		int freedom = 15; // osi x
		int k = 6;

		LearningCurve() {
			// chi = RandomHelper.getChiSquare();
			chi = new ChiSquare(k,
					cern.jet.random.ChiSquare.makeDefaultGenerator());
		}

		private double getDelta(double k) {
			double x = chi.cdf(k * freedom);
			DecimalFormat df = new DecimalFormat("#.######");
			// NOTE: freedom (x axis of CDF) should be between 0 and 4
			say("getDelta for k: " + df.format(k) + "returned x:"
					+ df.format(x));
			return x;
		}

		// public void checkChi() {
		// say("chi.cdf(0): " + chi.cdf(0));
		// say("chi.cdf(0.5): " + chi.cdf(0.5));
		// say("chi.cdf(1): " + chi.cdf(1));
		// say("chi.pdf(0): " + chi.pdf(0));
		// say("chi.pdf(0.5): " + chi.pdf(0.5));
		// say("chi.pdf(1): " + chi.pdf(1));
		// say("chi.nextDouble(): " + chi.nextDouble());
		// }
		//
		// public void checkZeta() {
		// say("chi.cdf(0): " + zeta.cdf(0));
		// say("chi.cdf(0.5): " + zeta.cdf(0.5));
		// say("chi.cdf(1): " + zeta.cdf(1));
		// say("chi.pdf(0): " + zeta.pdf(0.01));
		// say("chi.pdf(0.5): " + zeta.pdf(0.5));
		// say("chi.pdf(1): " + zeta.pdf(1));
		// say("chi.nextDouble(): " + zeta.nextDouble());
		// }
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
	}
}

class ExperienceSanityCheck {
	
	ChiSquare chi;
	//Zeta zeta;
	int freedom;
	int k;
	
	public static double EpsilonCutValue;

	ExperienceSanityCheck() {
		freedom = 6; // osi x
		k = 15;

		chi = new ChiSquare(freedom, cern.jet.random.ChiSquare.makeDefaultGenerator());
		//zeta = new Zeta(freedom, k, cern.jet.random.Zeta.makeDefaultGenerator());
		
		checkChi();
		//checkZeta();
		
		EpsilonCutValue = checkEpsilonFromChi();
	}

	public void checkChi() {
		say("chi.cdf(0.1): " + chi.cdf(0.1 * k));
		say("chi.cdf(0.2): " + chi.cdf(0.2 * k));
		say("chi.cdf(0.3): " + chi.cdf(0.3 * k));
		say("chi.cdf(0.6): " + chi.cdf(0.6 * k));
		say("chi.cdf(0.8): " + chi.cdf(0.8 * k));
		say("chi.cdf(0.9): " + chi.cdf(0.9 * k));
		say("chi.cdf(0.95): " + chi.cdf(0.95 * k));
		say("chi.cdf(0.9): " + chi.cdf(0.999 * k));
		say("chi.nextDouble(): " + chi.nextDouble());
	}
	
	public double checkEpsilonFromChi(){
		double e = chi.cdf(1 * k);
		say("chi.cdf(1): " + e);
		return 1-e;
	}

//	public void checkZeta() {
//		say("zeta.nextInt(): " + zeta.nextInt());
//		say("zeta.nextDouble()): " + zeta.nextDouble());
//	}
	
	private void say(String s) {
		PjiitOutputter.say(s);
	}

}
