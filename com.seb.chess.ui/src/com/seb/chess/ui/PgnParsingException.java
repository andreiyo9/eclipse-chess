package com.seb.chess.ui;

public class PgnParsingException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3732086695539275567L;

	public PgnParsingException(String message){
        super(message);
    }
    
    public PgnParsingException(Exception e){
        super(e);
    }

    public PgnParsingException(String message, Exception e){
        super(message,e);
    }
    
}
