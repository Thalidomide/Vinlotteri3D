package no.olj.joglLottery.primitives.drawable;

import no.olj.joglLottery.lottery.Participant;
import no.olj.joglLottery.primitives.Point3D;
import no.olj.joglLottery.primitives.Rotation;
import no.olj.joglLottery.primitives.LotteryColor;
import no.olj.joglLottery.util.Util;

import javax.media.opengl.GL;
import java.util.List;
import java.util.ArrayList;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class DrawableLotteryTickets implements Drawable {

    private List<Participant> participants;
    private int totalTickets;
    private Point3D centre;

    private List<Drawable> drawableTickets;

    private double radius;

    public DrawableLotteryTickets(List<Participant> participants, Point3D centre, double radius) {
        this.participants = participants;
        this.centre = centre;
        this.radius = radius;

        calculateTotalTickets();
        createArcs();
    }

    public void draw(GL gl) {
        gl.glEnable(GL.GL_COLOR_MATERIAL);
        for (Drawable ticket : drawableTickets) {
            ticket.draw(gl);
        }
        gl.glDisable(GL.GL_COLOR_MATERIAL);
    }

    public double getAngle(Participant participant) {
        int participantStartingTicket = 0;

        for (Participant tmpParticipant : participants) {
            if (tmpParticipant.equals(participant)) {
                break;
            } else {
                participantStartingTicket += tmpParticipant.getTickets();
            }
        }

        double meanTicket = (double) participant.getTickets() / 2.0 + (double) participantStartingTicket;
        double meanFactor = meanTicket / (double) totalTickets;
        return 360 - meanFactor * 360;
    }

    public void setRotation(Rotation rotation) {
        for (Drawable ticket : drawableTickets) {
            ticket.setRotation(new Rotation(rotation));
        }
    }

    public void setRotationAngle(double angle) {
        for (Drawable ticket : drawableTickets) {
            ticket.setRotationAngle(angle);
        }
    }

    private void createArcs() {
        drawableTickets = new ArrayList<Drawable>();
        double startAngle = 0;
        for (Participant participant : participants) {
            float angle = (float) participant.getTickets() / (float) totalTickets * 360;

            LotteryColor color = Util.convertColor(participant.getColor());
            DrawableArc arc = new DrawableArc(centre, startAngle, angle, radius, color);
            drawableTickets.add(arc);
            startAngle += angle;
        }
    }

    private void calculateTotalTickets() {
        totalTickets = 0;
        for (Participant participant : participants) {
            totalTickets += participant.getTickets();
        }
    }
}
