package no.olj.joglLottery.gui;

import no.olj.joglLottery.lottery.Participant;

/**
 * <h1>Interface for listeners to {@link LotteryFrame}</h1>
 *
 * @author Olav Jensen
 * @since 15.okt.2008
 */
public interface LotteryFrameListener {

    /**
     * Stopped on a {@link Participant} winner.
     *
     * @param winner the winner {@link Participant}.
     */
    void stoppedOnWinner(Participant winner);

    /**
     * Called when the lottery has ended.
     */
    void lotteryEnded();
}
