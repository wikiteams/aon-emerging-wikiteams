package internetz;

import internetz.Experience.SigmoidCurve;

import java.text.DecimalFormat;

import logger.PjiitOutputter;
import cern.jet.random.ChiSquare;

/**
 * Class describing the learning process of a human for simulation purpose.
 * 
 * @author Oskar Jarczyk
 * @version 1.3
 * 
 */
public class Experience {

	private LearningCurve lc = null;
	private SigmoidCurve sc = null;

	private enum ApproximationMethod {
		SIGMOID, CHI_SQUARE
	};

	private double value; // plain experience
	private int top; // hipothetical overlearning

	private static final double expStub = 0.03;

	protected final static ExperienceSanityCheck esc = new ExperienceSanityCheck();

	public Experience() {
		this(0d, 0, false);
	}

	public Experience(boolean passionStub) {
		this(0d, 0, passionStub);
	}

	public Experience(double value, int top) {
		this(value, top, false);
	}

	public Experience(double value, int top, boolean passionStub) {
		if (passionStub) {
			int maxx = SimulationParameters.agentSkillsMaximumExperience;
			this.value = maxx * expStub;
			this.top = maxx;
		} else {
			this.value = value;
			this.top = top;
		}
		createMathematicalCurves();
		say("Creating Experience object with value: " + this.value
				+ " and top: " + this.top);
	}
	
	private void createMathematicalCurves(){
		this.lc = new LearningCurve();
		this.sc = new SigmoidCurve();
	}

	public double getDelta() {
		return getDelta(ApproximationMethod.SIGMOID);
	}

	public double getDelta(ApproximationMethod method) {
		switch (method) {
			case SIGMOID:
				return sc.getDelta((value / top));
			case CHI_SQUARE:
				return lc.getDelta((value / top));
			default:
				break;
		}
		return lc.getDelta((value / top));
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public void increment(double how_much) {
		this.value += how_much;
		DecimalFormat df = new DecimalFormat("#.######");
		sanity("Experience incremented by: " + df.format(how_much));
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

	private void sanity(String s) {
		PjiitOutputter.sanity(s);
	}

	/**
	 * Learning Process represented by Sigmoid function
	 * 
	 * @author Oskar Jarczyk
	 * @since 1.0
	 * 
	 */
	class SigmoidCurve {

		SigmoidCurve() {
			say("Object SigmoidCurve created, ref: " + this);
		}

		protected double getDelta(double k) {
			return 1d / (1d + Math.pow(Math.E, -k));
		}
	}

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

		double xLearningAxis = 15; // osi x
		int freedom = 6;

		LearningCurve() {
			say("Object LearningCurve created, with ref: " + this);
			chi = new ChiSquare(freedom,
					cern.jet.random.ChiSquare.makeDefaultGenerator());
		}

		private double getDelta(double k) {
			double x = chi.cdf(k * xLearningAxis);
			DecimalFormat df = new DecimalFormat("#.######");
			// NOTE: freedom (x axis of CDF) should be between 0 and 4
			say("getDelta for k: " + df.format(k) + " returned x:"
					+ df.format(x));
			return x;
		}

	}
}

class ExperienceSanityCheck {

	private ChiSquare chi;
	private int freedom;
	private int k;
	//private SigmoidCurve sigmoidCurve;
	public static double EpsilonCutValue;

	ExperienceSanityCheck() {
		freedom = 6; // osi x
		k = 15;

		chi = new ChiSquare(freedom,
				cern.jet.random.ChiSquare.makeDefaultGenerator());
		//sigmoidCurve = new SigmoidCurve();
		
		checkChi();
		checkSigmoid();

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
	
	public void checkSigmoid() {
		say("sigmoid(-10.000): " + sigmoidGetDelta(-10d));
		say("sigmoid(-8.000): " + sigmoidGetDelta(-8d));
		say("sigmoid(-6.000): " + sigmoidGetDelta(-6d));
		say("sigmoid(-3.000): " + sigmoidGetDelta(-3d));
		say("sigmoid(0.000): " + sigmoidGetDelta(0d));
		say("sigmoid(0.005): " + sigmoidGetDelta(0.005d));
		say("sigmoid(0.505): " + sigmoidGetDelta(0.505d));
		say("sigmoid(0.995): " + sigmoidGetDelta(0.995d));
		say("sigmoid(1.000): " + sigmoidGetDelta(1d));
		say("sigmoid(3.000): " + sigmoidGetDelta(3d));
		say("sigmoid(6.000): " + sigmoidGetDelta(6d));
		say("sigmoid(8.000): " + sigmoidGetDelta(8d));
		say("sigmoid(10.000): " + sigmoidGetDelta(10d));
	}

	public double checkEpsilonFromChi() {
		double e = chi.cdf(1 * k);
		say("chi.cdf(1): " + e);
		return 1 - e;
	}
	
	private double sigmoidGetDelta(double k) {
		return 1d / (1d + Math.pow(Math.E, -k));
	}

	private void say(String s) {
		PjiitOutputter.say(s);
	}

}
