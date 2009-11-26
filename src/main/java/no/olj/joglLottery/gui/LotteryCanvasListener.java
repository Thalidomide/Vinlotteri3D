package no.olj.joglLottery.gui;

import no.olj.joglLottery.lottery.Participant;

/**
 * <h1></h1>
 *
 * @author Olav Jensen
 * @since 14.okt.2008
 */
public interface LotteryCanvasListener {

    void gotWinner(Participant winner);
}
