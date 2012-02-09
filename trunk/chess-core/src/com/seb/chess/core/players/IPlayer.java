package com.seb.chess.core.players;

import com.seb.chess.core.Plateau;

public interface IPlayer {

    public void startComputingMove(Plateau plateau);
    
    public void addListener(IPlayerListener listener);
    
    public void removeListener(IPlayerListener listener);
    
    public String getDescription();
}
