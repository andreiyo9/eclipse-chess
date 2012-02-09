package com.seb.chess.core.pgn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.seb.chess.core.Game;
import com.seb.chess.core.Move;
import com.seb.chess.core.MoveGenerator;
import com.seb.chess.core.Piece;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.Position;
import com.seb.chess.core.Utils;
import com.seb.chess.core.Piece.Type;

public class PgnGenerator {

    public static final String PRISE = "x", ECHEC = "+", PETIT_ROQUE = "o-o", GRAND_ROQUE = "o-o-o", VICTOIRE_BLANCS = "1-0", VICTOIRE_NOIRS = "0-1", NUL1 = "1/2-1/2", NUL2 = "0,5-0,5";
    
    private Game game;
    
    public PgnGenerator(Game game){
        this.game = game;
    }
    
    public void write(File file, IPgnGeneratorMonitor monitor){
    	monitor.beginTask("Writing to PGN...", game.getNbMoves());
    	// Stream to write file
		FileOutputStream fout;		

		try
		{
		    // Open an output stream
		    fout = new FileOutputStream(file);

		    // Print a line of text
		    PrintStream stream = new PrintStream(fout);
		    stream.println (buildParamLine("White", game.getWhite()));
		    stream.println (buildParamLine("Black", game.getBlack()));
		    stream.println (buildParamLine("Site", game.getSite()));
		    stream.println (buildParamLine("Event", game.getEvent()));
		    stream.println (buildParamLine("Date", game.getDate()));
		    
		    String line = "";
		    Plateau plateau = new Plateau();
		    for(int i=0; i<game.getNbMoves()-1; i+=2){
		    	if(i==0)
		    		plateau.reset();
		    	else
		    		game.initPlateau(plateau, i-1);
		    	line += (i/2+1)+". ";
		    	line += toPgnString(Language.ENGLISH, game.getMove(i), plateau)+" ";
	    		game.initPlateau(plateau, i);
		    	line += toPgnString(Language.ENGLISH, game.getMove(i+1), plateau)+" ";
		    	monitor.worked(1);
		    }
		    stream.println (line);

		    // Close our output stream
		    fout.close();
		}
		// Catches any error conditions
		catch (IOException e){
			System.err.println ("Unable to write to file");
			System.exit(-1);
		}
	    monitor.done();
    }
    
    private static String buildParamLine(String param, String value){
    	return "["+param+" \""+value+"\"]";
    }
    
    public String toPgnString(Language language, Move move, Plateau plateau) {
        Piece piece = plateau.getPiece(move.getInitialPos());
        Type pieceType = piece.getType();
        boolean isPetitRoque = pieceType == Type.ROI && move.getInitialPos().getX() == 4 && move.getFinalPos().getX() == 6;
        if(isPetitRoque)
            return PETIT_ROQUE;
        boolean isGrandRoque = pieceType == Type.ROI && move.getInitialPos().getX() == 4 && move.getFinalPos().getX() == 2;
        if(isGrandRoque)
            return GRAND_ROQUE;
        Position finalPos = move.getFinalPos();
        boolean isEchec = MoveGenerator.isEchec(plateau, Utils.not(plateau.getTurn()));
        boolean isPrise = plateau.getPiece(finalPos) != null;
        String str = toString(language,pieceType);
        // Ambiguité ?
        boolean ambiguite = false;
        if(pieceType == Type.PION && isPrise)
        	ambiguite = true;
        else
	        for(Move m : MoveGenerator.getPossibleMoves(plateau)){
	        	if(m.getFinalPos().equals(move.getFinalPos()) && plateau.getPiece(m.getInitialPos()).getType()==pieceType && !m.getInitialPos().equals(move.getInitialPos()))
	        		ambiguite = true;
	        }
        
        if(ambiguite)
        	str += (char)('a'+ move.getInitialPos().getX());
        //
        if(isPrise)
            str += PRISE;
        str += finalPos.toString();
        if(isEchec)
            str += ECHEC;
        return str;
    }
    
    public enum Language {FRENCH,ENGLISH};
    
    public static String toString(Language language, Type type) {
        if(language == Language.FRENCH){
            switch (type) {
            case CAVALIER:
                return "C";
            case DAME:
                return "D";
            case FOU:
                return "F";
            case ROI:
                return "R";
            case TOUR:
                return "T";
            default:
                return "";
            }
        }
        else{
            switch (type) {
            case CAVALIER:
                return "N";
            case DAME:
                return "Q";
            case FOU:
                return "B";
            case ROI:
                return "K";
            case TOUR:
                return "R";
            default:
                return "";
            }
        }
    }
}
