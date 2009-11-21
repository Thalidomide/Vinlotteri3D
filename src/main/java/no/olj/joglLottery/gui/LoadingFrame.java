package no.olj.joglLottery.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 20.okt.2008
 */
public class LoadingFrame extends JFrame {

    private final static Color backgroundColor = Color.BLACK;
    private final static Color textColor = new Color(255, 150, 20);
    private final static Font font = new Font("Verdana", Font.PLAIN, 24);

    public LoadingFrame(String message) {
        JLabel infoLabel = new JLabel(message);
        infoLabel.setFont(font);
        infoLabel.setForeground(textColor);
        JPanel panel = new JPanel();
        panel.setBackground(backgroundColor);
        panel.add(infoLabel);
        getContentPane().add(panel);

        int width = (int) panel.getPreferredSize().getWidth();
        int height = (int) panel.getPreferredSize().getHeight();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = screenSize.width / 2 - width / 2;
        int y = screenSize.height / 2 - height / 2;

        setResizable(false);
        setUndecorated(true);
        setSize(width, height);
        setAlwaysOnTop(true);
        setLocation(x, y);
        setVisible(true);
    }
}
