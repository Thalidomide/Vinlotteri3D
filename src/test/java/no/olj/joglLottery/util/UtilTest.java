package no.olj.joglLottery.util;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Olav Jensen
 * @since 26.nov.2009
 */
public class UtilTest extends TestCase {

	public void testGetRandomInt() {
		assertRandomIntBetween(1, 1);
		assertRandomIntBetween(1, 5);
	}

	public void testRandomizeList() {
		List<Integer> list = Arrays.asList(1, 2, 3, 4);
		List<Integer> randomList = Util.randomizeList(list);

//		for (Integer integer : randomList) {
//			System.out.println(integer);
//		}

		assertEquals(list.size(), randomList.size());
	}

	private void assertRandomIntBetween(int min, int max) {
		for (int i = 0; i < 20; i++) {
			int result = Util.getRandom(min, max);
			assertTrue(result >= min);
			assertTrue(result <= max);
		}
	}
}
