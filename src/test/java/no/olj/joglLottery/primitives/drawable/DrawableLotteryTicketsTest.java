package no.olj.joglLottery.primitives.drawable;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import no.olj.joglLottery.lottery.Participant;
import no.olj.joglLottery.primitives.Point3D;
import no.olj.joglLottery.primitives.drawable.DrawableLotteryTickets;

/**
 * @author Olav Jensen
 * @since 26.nov.2009
 */
public class DrawableLotteryTicketsTest extends TestCase {

	public void testGetParticipantFromAngle() {
		List<Participant> participants = new ArrayList<Participant>();
		participants.add(new Participant("1", 5, Color.BLACK));
		participants.add(new Participant("2", 5, Color.BLACK));
		participants.add(new Participant("3", 5, Color.BLACK));
		DrawableLotteryTickets tickets = new DrawableLotteryTickets(participants, new Point3D(0, 0, 0), 1, 0);

		assertEquals(participants.get(0), tickets.getParticipant(100));
		assertEquals(participants.get(1), tickets.getParticipant(200));
		assertEquals(participants.get(2), tickets.getParticipant(300));
	}
}
