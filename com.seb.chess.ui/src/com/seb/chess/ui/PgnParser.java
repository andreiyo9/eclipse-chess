package com.seb.chess.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.seb.chess.core.Game;
import com.seb.chess.core.Move;
import com.seb.chess.core.MoveGenerator;
import com.seb.chess.core.Piece;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.Position;
import com.seb.chess.core.Piece.Couleur;
import com.seb.chess.core.Piece.Type;
import com.seb.chess.core.pgn.PgnGenerator;
import com.seb.chess.core.pgn.PgnGenerator.Language;

public class PgnParser {

    private final Game game;
    
    private final Language language;

    public PgnParser(Language language) {
        this.game = new Game();
        this.language = language;
    }

    public Game getGame() {
        return game;
    }

    public void parsePgnFile(File file) throws PgnParsingException,
            FileNotFoundException, IOException {
        FileReader fileReader = new FileReader(file);
        BufferedReader bufRead = new BufferedReader(fileReader);
        String line;
        do {
            line = bufRead.readLine();
            if(line != null){
	            if(line.contains("White")){
	            	game.setWhite(getValue(line));
	            }
	            if(line.contains("Black")){
	            	game.setBlack(getValue(line));
	            }
	            if(line.contains("Event")){
	            	game.setEvent(getValue(line));
	            }
	            if(line.contains("Date")){
	            	String dateStr = getValue(line);
					game.setDate(dateStr);
	            }
	            if(line.contains("Site")){
					game.setSite(getValue(line));
	            }
            }
        } while (line != null && (line.startsWith("[") || line.trim().isEmpty()));
        
        String coups = "";
        while(line != null) {
        	coups += line + " ";
            line = bufRead.readLine();
        }
        parseCoups(coups);
    }
    
    private static String getValue(String line){
    	int firstIndex = line.indexOf("\"");
    	if(firstIndex == -1)
    		return null;
    	int lastIndex = line.indexOf("\"",firstIndex+1);
    	if(lastIndex == -1)
    		return null;
    	return line.substring(firstIndex+1, lastIndex);
    }

    private void parseCoups(String line) throws PgnParsingException {
    	// Remove multiple blanks
        line = line.replaceAll("[\\s]+", " ");
        // Remove comments
        line = line.replaceAll("\\{[^\\}]+\\}", "");
        // Replace '...' by '.'
        line = line.replaceAll("\\.\\.\\.", "\\. ");

        String newLine = "";
        for(int i=0; i<line.length();i++){
            char c = line.charAt(i);
            newLine += c;
            if(c == '.' && line.charAt(i+1) != ' ')
                newLine += ' ';
        }
        
        String[] chunks = newLine.split(" ");
        for (int i = 0; i < chunks.length; i++) {
            String chunk = chunks[i];
            if(chunk.equals(PgnGenerator.VICTOIRE_BLANCS) || chunk.equals(PgnGenerator.VICTOIRE_NOIRS) || chunk.equals(PgnGenerator.NUL1) || chunk.equals(PgnGenerator.NUL2))
            	return;
            if (i % 3 == 0) {
                // Num�ro
                // V�rification �ventuelle
            }
            else if(!chunk.trim().isEmpty()) {
                try{
                    Move move = parse(chunk);
                    game.doMove(move);
                }
                catch(PgnParsingException e){
                    throw new PgnParsingException("Erreur pour le coup "+(i/3)+1,e);
                }
            }
        }
    }

    public Move parse(String pgnMove) throws PgnParsingException {
        Plateau plateau = new Plateau();
        game.initPlateau(plateau);
        Position initPos = null, finalPos = null;
        Couleur turn = plateau.getTurn();
        if (PgnGenerator.PETIT_ROQUE.equalsIgnoreCase(pgnMove)) {
            initPos = new Position(4, turn == Couleur.BLANC ? 7 : 0);
            finalPos = new Position(6, turn == Couleur.BLANC ? 7 : 0);
        }
        else if (PgnGenerator.GRAND_ROQUE.equalsIgnoreCase(pgnMove)) {
            initPos = new Position(4, turn == Couleur.BLANC ? 7 : 0);
            finalPos = new Position(2, turn == Couleur.BLANC ? 7 : 0);
        }
        else {
            String possibleTypes = "";
            for(Type type : Type.values()){
                possibleTypes += PgnGenerator.toString(language, type);
            }
            String typeChar = null;
            boolean prise = false;
            boolean echec = false;
            String finalPosStr = null;
            Character ambigChar = null;
            if(pgnMove.endsWith("+")){
                echec = true;
                pgnMove = pgnMove.substring(0, pgnMove.length()-1);
            }
            if(pgnMove.matches("["+possibleTypes+"][a-h][1-8]")){
                typeChar = pgnMove.substring(0, 1);      
                finalPosStr = pgnMove.substring(1,3);
            }
            else if(pgnMove.matches("["+possibleTypes+"]x[a-h][1-8]")){
                typeChar = pgnMove.substring(0, 1);      
                prise = true;
                finalPosStr = pgnMove.substring(2,4);
            }
            else if(pgnMove.matches("["+possibleTypes+"][a-h1-8][a-h][1-8]")){
                typeChar = pgnMove.substring(0, 1);      
                finalPosStr = pgnMove.substring(2,4);
                ambigChar = pgnMove.charAt(1);
            }
            else if(pgnMove.matches("["+possibleTypes+"][a-h1-8]x[a-h][1-8]")){
                typeChar = pgnMove.substring(0, 1);      
                finalPosStr = pgnMove.substring(3,5);
                ambigChar = pgnMove.charAt(1);
            }
            else if(pgnMove.matches("[a-h][1-8]")){
                finalPosStr = pgnMove.substring(0,2);
            }
            else if(pgnMove.matches("x[a-h][1-8]")){
                prise = true;
                finalPosStr = pgnMove.substring(1,3);
            }
            else if(pgnMove.matches("[a-h1-8][a-h][1-8]")){
                finalPosStr = pgnMove.substring(1,3);
                ambigChar = pgnMove.charAt(1);
            }
            else if(pgnMove.matches("[a-h1-8]x[a-h][1-8]")){
                finalPosStr = pgnMove.substring(2,4);
                ambigChar = pgnMove.charAt(0);
            }
            else
                throw new PgnParsingException("Pattern invalide : '"+pgnMove+"'");

            Type pieceType = null;
            if(typeChar != null)
                for (Type type : Type.values()) {
                    if (PgnGenerator.toString(language,type).equals(typeChar)) {
                        pieceType = type;
                        break;
                    }
                }
            if (pieceType == null)
                pieceType = Type.PION;


            try {
                finalPos = new Position(finalPosStr);
            }
            catch (IllegalArgumentException e) {
                throw new PgnParsingException("Position invalide : '" + finalPosStr + "'");
            }

            for (int x = 0; x < Plateau.SIZE; x++) {
                for (int y = 0; y < Plateau.SIZE; y++) {
                    Position pos = new Position(x, y);
                    Piece piece = plateau.getPiece(pos);
                    if (piece != null && piece.getType() == pieceType && piece.getCouleur() == turn
                        && MoveGenerator.getPossibleMoves(plateau, pos).contains(finalPos)) {
                        if(ambigChar!=null){
                            // On utilise le caract�re de d�sambiguisation
                            if(pos.toString().contains(ambigChar.toString()))
                                initPos = pos;
                        }
                        else
                            initPos = pos;                            
                        break;
                    }
                }
            }
            if (initPos == null) {
                throw new PgnParsingException("Le coup n'est pas possible : type='" + pieceType
                                              + "' , finalPos='" + finalPos + "' , turn='"
                                              + plateau.getTurn() + "' , ambigChar='" +ambigChar+"'");
            }
        }
        return new Move(initPos, finalPos);
    }

}
