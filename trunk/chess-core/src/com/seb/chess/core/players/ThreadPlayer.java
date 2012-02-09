package com.seb.chess.core.players;

import com.seb.chess.core.Move;
import com.seb.chess.core.Plateau;

public abstract class ThreadPlayer extends AbstractPlayer {

    protected abstract Move computeMove(Plateau plateau) throws Exception;

    public void startComputingMove(final Plateau plateau) {
        new Thread() {
            @Override
            public void run() {
                try {
                    notifyMove(computeMove(plateau));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    notifyMove(null);
                }
            }
        }.start();
    }

}
