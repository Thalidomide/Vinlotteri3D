package no.olj.joglLottery.gui;

import no.olj.joglLottery.lottery.Participant;
import no.olj.joglLottery.gui.LotteryPainter;
import no.olj.joglLottery.gui.LotteryCanvasListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 11.okt.2008
 */
public class LotteryFrame extends JFrame {

    private final Font InfoTitleFont = new Font("Verdana", Font.BOLD, 20);
    private Font infoFont = new Font("Verdana", Font.BOLD, 15);
    private Font statusFont = new Font("Verdana", Font.BOLD, 18);

    private Color panelBackgroundColor = Color.white;
    private Color startBackgroundColor = new Color(80, 120, 200);
    private Color statusLabelColor = new Color(255, 150, 0);

    private LotteryPainter canvas;
    private JPanel infoPanel;
    private List<Participant> participants;
    private Participant winner;
    private JLabel statusLabel;

    private List<ParticipantLabel> participantLabels;
    private LotteryFrameListener listener;
    private JButton startButton;

    private int numberOfPrizes;
    private int numberOfPrizesWon = 0;
    private JLabel prizesTotal;
    private JLabel prizesWon;

    private class ParticipantLabel extends JLabel {

        private Participant participant;

        private ParticipantLabel(Participant participant) {
            super();
            this.participant = participant;
            setFont(infoFont);
            updateTextAndColor();
        }

        public void updateTextAndColor() {
            Color color = participant.getTickets() > 0 ? participant.getColor() : Color.GRAY;
            this.setForeground(color);
            setText(participant.getName() + " - " + participant.getTickets() + " lodd");
        }
    }


    public LotteryFrame(LotteryFrameListener listener, List<Participant> participants, int numberOfPrizes) {
        this.listener = listener;
        this.participants = participants;
        this.numberOfPrizes = numberOfPrizes;

        setSize(1200, 700);
        setLocation(0, 0);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("3D lottery");

        setupContent();
    }

    private void startLottery() {
        if (canvas.isDrawing()) {
            return;
        }
        startButton.setEnabled(false);

        this.winner = listener.getNextWinner();
        canvas.initializeGui();
        updateParticipantColors();
        setInfoText("Klart for ny trekning!");
        Thread startThread = new Thread() {
            @Override
			public void run() {
                try {
                    sleep(1000);
                    canvas.startLottery(LotteryFrame.this.winner);
                    setInfoText("Trekning pågår..");
                } catch (InterruptedException e) {/**/}
            }
        };
        startThread.start();
    }

    public void setInfoText(String text) {
        statusLabel.setText(text);
    }

    private void setupContent() {
        canvas = new LotteryPainter(getLotteryCanvasListener(), participants);

        BevelBorder border = new BevelBorder(1);

        infoPanel = new JPanel();
        infoPanel.setBorder(border);
        infoPanel.setPreferredSize(new Dimension(250, 1));
        infoPanel.setLayout(new GridLayout(20, 1));
        infoPanel.setBackground(panelBackgroundColor);

        statusLabel = new JLabel("Vinner ikke trukket");
        statusLabel.setForeground(statusLabelColor);
        statusLabel.setFont(statusFont);
        JPanel statusPanel = new JPanel();
        statusPanel.setBorder(border);
        statusPanel.setBackground(panelBackgroundColor);
        statusPanel.add(statusLabel);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(canvas.getCanvas(), BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.EAST);
        mainPanel.add(statusPanel, BorderLayout.NORTH);

        addInfoToStatusPanel();

        getContentPane().add(mainPanel);
    }

    private void lotteryStoppedOnWinner() {
        boolean wasLastTicket = ++numberOfPrizesWon >= numberOfPrizes;
        String infoText = "Vinneren er: " + winner.getName() + "! " + getRandomCheer();
        listener.stoppedOnWinner(winner);
        setInfoText(infoText);
        updatePrizesInfo();
        if (!wasLastTicket) {
            startButton.setEnabled(true);
        } else {
            listener.lotteryEnded();
        }
    }

    private void addInfoToStatusPanel() {
        JLabel title = new JLabel("Deltagere:");
        title.setFont(InfoTitleFont);
        infoPanel.add(title);
        participantLabels = new ArrayList<ParticipantLabel>();
        for (Participant participant : participants) {
            ParticipantLabel participantLabel = new ParticipantLabel(participant);
            infoPanel.add(participantLabel);
            participantLabels.add(participantLabel);
        }

        infoPanel.add(new JLabel());

        JLabel prizesTitle = new JLabel("Premiestatus:");
        prizesTotal = new JLabel();
        prizesWon = new JLabel();
        prizesTitle.setFont(InfoTitleFont);
        prizesTotal.setFont(statusFont);
        prizesWon.setFont(statusFont);
        infoPanel.add(prizesTitle);
        infoPanel.add(prizesTotal);
        infoPanel.add(prizesWon);
        updatePrizesInfo();

        Font font = new Font("Verdana", Font.BOLD, 14);
        startButton = new JButton("Start trekning");
        startButton.setBackground(startBackgroundColor);
        startButton.setForeground(statusLabelColor);
        startButton.setFont(font);
        startButton.setFocusPainted(false);
        this.startButton.addMouseListener(getStartButtonListener());

        JPanel startButtonPanel = new JPanel();
        startButtonPanel.setBackground(panelBackgroundColor);
        startButtonPanel.add(startButton);

        infoPanel.add(new JLabel());
        infoPanel.add(new JLabel());
        infoPanel.add(startButtonPanel);
    }

    private void updatePrizesInfo() {
        prizesWon.setText("Vunnet: " + numberOfPrizesWon);
        prizesTotal.setText("Totalt: " + numberOfPrizes);
    }

    private void updateParticipantColors() {
        for (ParticipantLabel participantLabel : participantLabels) {
            participantLabel.updateTextAndColor();
        }
    }

    private String getRandomCheer() {
        String[] randomCheer = new String[]{
                "Hipp hipp.."
                , "WOOOHOOOO!"
                , "YEAH!"
                , ":-D"
                , "Grattis!"
                , "V�rs�god - finn deg en flaske."
        };
        int rand = (int) (Math.random() * randomCheer.length);
        return randomCheer[rand];
    }


    private LotteryCanvasListener getLotteryCanvasListener() {
        return new LotteryCanvasListener() {
            @Override
			public void stoppedOnLotteryWinner() {
                lotteryStoppedOnWinner();
            }
        };
    }

    private MouseListener getStartButtonListener() {
        return new MouseListener() {
            @Override
			public void mouseReleased(MouseEvent e) {
                if (startButton.isEnabled()) {
                    startLottery();
                }
            }

            @Override
			public void mouseClicked(MouseEvent e) {}
            @Override
			public void mousePressed(MouseEvent e) {}
            @Override
			public void mouseEntered(MouseEvent e) {}
            @Override
			public void mouseExited(MouseEvent e) {}
        };
    }
}
