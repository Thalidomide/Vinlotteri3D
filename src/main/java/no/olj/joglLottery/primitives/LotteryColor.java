package no.olj.joglLottery.primitives;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class LotteryColor {

    private double red, green, blue;

	private boolean runAnimation = true;
	private boolean animating = true;
	private double animateMax = 1.5;
	private double animateMin = 0.9;
	private double animateStep = 0.015;
	private double animatePointer;
	private boolean animateUp;

	public LotteryColor(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

	public void updateColor() {
		if (!animating) {
			return;
		}

		if (!runAnimation) {
			moveToActualColor();
		} else if (animateUp) {
			doAnimate(animateMax);
		} else {
			doAnimate(animateMin);
		}
	}

	private void moveToActualColor() {
		if (animatePointer == 1.0) {
			return;
		}
		if (doAnimate(1)) {
			animating = false;
		}
	}

	private boolean doAnimate(double animateTarget) {
		boolean reachedTarget = false;

		if (animateUp) {
			animatePointer += animateStep;
			if (animatePointer >= animateTarget) {
				animatePointer = animateTarget;
				animateUp = false;
				reachedTarget = true;
			}
		} else {
			animatePointer -= animateStep;
			if (animatePointer <= animateTarget) {
				animatePointer = animateTarget;
				animateUp = true;
				reachedTarget = true;
			}
		}

		return reachedTarget;
	}

	private double getAnimatedColorValue(double value) {
		double result = value * animatePointer;
		if (result < 0) {
			result = 0;
		} else if (result > 255) {
			result = 255;
		}
		return result;
	}

	public double getRed() {
		return getAnimatedColorValue(red);
	}

	public void setRed(double red) {
        this.red = red;
    }

    public double getGreen() {
        return getAnimatedColorValue(green);
    }

    public void setGreen(double green) {
        this.green = green;
    }

    public double getBlue() {
        return getAnimatedColorValue(blue);
    }

    public void setBlue(double blue) {
        this.blue = blue;
    }

	public void setRunAnimation(boolean runAnimation) {
		this.runAnimation = runAnimation;
	}
}
