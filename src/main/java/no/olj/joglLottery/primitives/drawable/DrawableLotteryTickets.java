package no.olj.joglLottery.primitives.drawable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.media.opengl.GL;

import no.olj.joglLottery.lottery.Participant;
import no.olj.joglLottery.primitives.LotteryColor;
import no.olj.joglLottery.primitives.Point3D;
import no.olj.joglLottery.primitives.Rotation;
import no.olj.joglLottery.util.Util;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class DrawableLotteryTickets implements Drawable {

    private Map<Participant, DrawableArc> participantMap = new LinkedHashMap<Participant, DrawableArc>();
    private int totalTickets;
    private Point3D centre;

//    private List<Drawable> drawableTickets;

    private final double radius;
	private final double angleOffset;

	public DrawableLotteryTickets(List<Participant> participants, Point3D centre, double radius, double angleOffset) {
        this.centre = centre;
        this.radius = radius;
		this.angleOffset = angleOffset;

		calculateTotalTickets(participants);
		createArcs(participants);
    }

    @Override
	public void draw(GL gl) {
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        for (Drawable ticket : participantMap.values()) {
            ticket.draw(gl);
        }
        gl.glDisable(GL.GL_COLOR_MATERIAL);
    }

	public Participant getWinnerAndAnimate(double angle) {
		Participant winner = getParticipant(angle);
		participantMap.get(winner).color.setRunAnimation(true);
		return winner;
	}

	Participant getParticipant(double angle) {
		Participant result = null;
		int participantFirstTicket = 0;

		for (Participant participant : participantMap.keySet()) {
			int participantLastTicket = participantFirstTicket + participant.getTickets();

			double startAngle = participantFirstTicket / (double) totalTickets * 360;
			double endAngle = participantLastTicket / (double) totalTickets * 360;

			if (angle >= startAngle && angle < endAngle) {
				result = participant;
				break;
			}

			participantFirstTicket = participantLastTicket;
		}

		if (result == null) {
			throw new RuntimeException("Did not find participant for angle: " + angle);
		}

		return result;
	}

	@Override
	public void setRotation(Rotation rotation) {
        for (Drawable ticket : participantMap.values()) {
            ticket.setRotation(new Rotation(rotation));
        }
    }

	@Override
	public void setRotationAngle(double angle) {
        for (Drawable ticket : participantMap.values()) {
            ticket.setRotationAngle(angle);
        }
    }

	private void createArcs(List<Participant> participants) {
        double startAngle = angleOffset;
        for (Participant participant : participants) {
            float angle = (float) participant.getTickets() / (float) totalTickets * 360;
//            float angle = 45;

            LotteryColor color = Util.convertColor(participant.getColor());
            DrawableArc arc = new DrawableArc(centre, startAngle, angle, radius, color);
			participantMap.put(participant, arc);
            startAngle += angle;
//			break;
        }
    }

	private void calculateTotalTickets(List<Participant> participants) {
        totalTickets = 0;
        for (Participant participant : participants) {
            totalTickets += participant.getTickets();
        }
    }
}
