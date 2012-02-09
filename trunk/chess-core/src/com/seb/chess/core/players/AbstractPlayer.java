package com.seb.chess.core.players;

import java.util.HashSet;
import java.util.Set;

import com.seb.chess.core.Move;

public abstract class AbstractPlayer implements IPlayer {

    private Set<IPlayerListener> listeners = new HashSet<IPlayerListener>();

    public void addListener(IPlayerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IPlayerListener listener) {
        listeners.remove(listener);
    }

    protected void notifyMove(Move move) {
        for (IPlayerListener listener : new HashSet<IPlayerListener>(listeners)) {
            listener.moveComputed(move);
        }
    }
}
