package no.olj.joglLottery.lottery;

import junit.framework.TestCase;

/**
 * @author Olav Jensen
 * @since 26.nov.2009
 */
public class DrawControllerTest extends TestCase implements DrawControllerListener {

	private boolean finished;

	public void testDraw() {
		DrawController controller = new DrawController(this);

		controller.setInitialSpinSpeed(10);
		controller.setSpinFadeDown(1);
		controller.setMinFadeDownRounds(1);
		controller.setMaxFadeDownRounds(1);

		controller.start();

		for (int i = 0; i < 100; i++) {
			controller.updateAngle();
			if (finished) {
				break;
			}
		}
		assertTrue(finished);
	}

	@Override
	public void drawCompleted() {
		finished = true;
	}
}
