package no.olj.joglLottery.lottery;

import java.awt.Color;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class Participant {

    private String name;
    private int tickets;
    private Color color;

    public Participant(String name, int tickets, Color color) {
        this.name = name;
        this.tickets = tickets;
        this.color = color;
    }

    public void decreaseTickets() {
        tickets--;
    }

    public String getName() {
        return name;
    }

    public int getTickets() {
        return tickets;
    }

    public Color getColor() {
        return color;
    }
}
