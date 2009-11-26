package no.olj.joglLottery.lottery;

import no.olj.joglLottery.util.Util;

/**
 * @author Olav Jensen
 * @since 26.nov.2009
 */
public class DrawController {

	private final DrawControllerListener listener;

	private boolean drawing;

	private double spinFadeDown = 0.006;		// verdien farten minker med
	private double initialSpinSpeed = 4;		// hastigheten under trekning
	private int minFadeDownRounds = 1;			// Minimum runder før bremsing starter
	private int maxFadeDownRounds = 3;			// Maks runder før bremsing
	private int fadeDownRound;					// Hvilken runde man starter med å bremse
	private double fadeDownAngle;				// Ved hvilken vinkel bremsingen starter

	private boolean fadingDown;
	private double currentSpinSpeed = 0;
	private double angle = 0;
	private int rounds;

	public DrawController(DrawControllerListener listener) {
		this.listener = listener;
	}

	public void start() {
		if (drawing) {
			System.out.println(this + " - is already drawing!");
			return;
		}
		drawing = true;

		setInitialDrawValues();
	}

	private void setInitialDrawValues() {
		currentSpinSpeed = initialSpinSpeed;
		rounds = 0;
		fadeDownRound = Util.getRandom(minFadeDownRounds, maxFadeDownRounds);
		fadeDownAngle = Util.getRandom(0d, 360d);
		fadingDown = false;
	}

	public void updateAngle() {
		if (!drawing) {
			return;
		}
		double oldAngle = angle;
		angle = Util.getTransformedAngle(angle, currentSpinSpeed);

		checkNewRound(oldAngle);
		boolean stopped = checkFadeDown();

		if (stopped) {
			drawing = false;
			listener.drawCompleted();
		}
	}

	private void checkNewRound(double oldAngle) {
		if (angle < oldAngle) {
			rounds ++;
		}
	}

	/**
	 * Check if speed should be slowed down and if so, update the speed.
	 *
	 * @return true if stopped, else false.
	 */
	private boolean checkFadeDown() {
		if (!fadingDown) {
			if ((rounds == fadeDownRound && angle >= fadeDownAngle) || rounds > fadeDownRound) {
				fadingDown = true;
			}
		}

		if (fadingDown) {
			currentSpinSpeed -= spinFadeDown;
			if (currentSpinSpeed <= 0) {
				currentSpinSpeed = 0;
				return true;
			}
		}
		return false;
	}

	public double getAngle() {
		return angle;
	}

	public boolean isDrawing() {
		return drawing;
	}

	void setSpinFadeDown(double spinFadeDown) {
		this.spinFadeDown = spinFadeDown;
	}

	void setInitialSpinSpeed(double initialSpinSpeed) {
		this.initialSpinSpeed = initialSpinSpeed;
	}

	void setMinFadeDownRounds(int minFadeDownRounds) {
		this.minFadeDownRounds = minFadeDownRounds;
	}

	void setMaxFadeDownRounds(int maxFadeDownRounds) {
		this.maxFadeDownRounds = maxFadeDownRounds;
	}
}
