package no.olj.joglLottery;

import no.olj.joglLottery.gui.LotteryFrame;
import no.olj.joglLottery.gui.LotteryFrameListener;
import no.olj.joglLottery.lottery.Participant;
import no.olj.joglLottery.util.Util;

import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class MainClass {

    String[] people = new String[]{
            "Raymond"
            , "Alf"
            , "Jens"
            , "Olav"
            , "Haakon"
            , "Frode"
    };
        int[] tickets = new int[]{20, 10, 15, 3, 11, 8};
//    int[] tickets = new int[]{3, 3, 3, 3};
    Color[] colors = new Color[]{
            new Color(0, 0, 200),
            new Color(200, 0, 0),
            new Color(0, 200, 0),
            new Color(255, 220, 0),
            new Color(200, 0, 200),
            new Color(0, 200, 200),
    };

    public void start() {
        List<Participant> participants = getParticipants();
        LotteryFrame mainFrame = new LotteryFrame(getListener(participants), participants, 10);
        mainFrame.setVisible(true);
    }

    private LotteryFrameListener getListener(final List<Participant> participants) {
        return new LotteryFrameListener() {
            public void stoppedOnWinner(Participant winner) {
                winner.decreaseTickets();
            }

            public Participant getNextWinner() {
                return participants.get((int) (Math.random() * participants.size()));
            }

            public void lotteryEnded() {
                //TODO Implement
            }
        };
    }

    private List<Participant> getParticipants() {
        List<Participant> participants = new ArrayList<Participant>();

        for (int i = 0; i < people.length; i++) {
            String name = people[i];
            Color color = i < colors.length ? colors[i] : getRandomColor();
            int nrOfTickets = i < tickets.length ? tickets[i] : getRandomTicket();
            participants.add(new Participant(name, nrOfTickets, color));
        }

        return participants;
    }

    public static void main(String[] args) {
        MainClass mainClass = new MainClass();
        mainClass.start();
    }

    private Color getRandomColor() {
        int red = (int) Util.getRandom(10, 200);
        int green = (int) Util.getRandom(10, 200);
        int blue = (int) Util.getRandom(10, 200);
        return new Color(red, green, blue);
    }

    private int getRandomTicket() {
        return (int) (Math.random() * 20 + 1);
    }
}
