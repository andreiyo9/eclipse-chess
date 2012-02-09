package com.seb.chess.core.pgn;

public interface IPgnGeneratorMonitor {

    public void beginTask(String name, int size);
    
    public void worked(int worked);
    
    public void done();
    
}
