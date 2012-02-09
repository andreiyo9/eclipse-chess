package com.seb.chess.core.ai;

public interface IEvalProgressMonitor {

    public void beginTask(String name, int size);
    
    public void worked(int worked);
    
    public void done();

    public void setTaskName(String nam);
    
}
