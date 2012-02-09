package com.seb.chess.core;


import com.seb.chess.core.Piece.Couleur;
import com.seb.chess.core.Piece.Type;

public class Plateau {

    public static final int SIZE = 8;

    private Piece[][] pieces;

    private Couleur turn = Couleur.BLANC;

    public Plateau(Plateau plateau) {
        this();
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                pieces[x][y] = plateau.pieces[x][y];
            }
        }
        this.turn = plateau.turn;
    }

    public Plateau() {
        pieces = new Piece[SIZE][SIZE];
//        reset();
    }

    public Piece getPiece(Position pos) {
        return pieces[pos.getX()][pos.getY()];
    }

    public void reset() {
        pieces = new Piece[SIZE][SIZE];
        turn = Couleur.BLANC;
        
        // Pions
        for (int i = 0; i < SIZE; i++) {
            addPiece(i, 1, Type.PION, Couleur.NOIR);
            addPiece(i, 6, Type.PION, Couleur.BLANC);
        }

        // Tours
        addPiece(0, 0, Type.TOUR, Couleur.NOIR);
        addPiece(0, 7, Type.TOUR, Couleur.BLANC);
        addPiece(7, 0, Type.TOUR, Couleur.NOIR);
        addPiece(7, 7, Type.TOUR, Couleur.BLANC);

        // Cavaliers
        addPiece(1, 0, Type.CAVALIER, Couleur.NOIR);
        addPiece(1, 7, Type.CAVALIER, Couleur.BLANC);
        addPiece(6, 0, Type.CAVALIER, Couleur.NOIR);
        addPiece(6, 7, Type.CAVALIER, Couleur.BLANC);

        // Fous
        addPiece(2, 0, Type.FOU, Couleur.NOIR);
        addPiece(2, 7, Type.FOU, Couleur.BLANC);
        addPiece(5, 0, Type.FOU, Couleur.NOIR);
        addPiece(5, 7, Type.FOU, Couleur.BLANC);

        // Dames
        addPiece(3, 0, Type.DAME, Couleur.NOIR);
        addPiece(3, 7, Type.DAME, Couleur.BLANC);

        // Rois
        addPiece(4, 0, Type.ROI, Couleur.NOIR);
        addPiece(4, 7, Type.ROI, Couleur.BLANC);

    }

    private void addPiece(int x, int y, Type type, Couleur couleur) {
        pieces[x][y] = new Piece(type, couleur);
    }

    public void executeMove(Move move) {
    	if(move == null)
    		return;
        Position initialPos = move.getInitialPos();
        Position finalPos = move.getFinalPos();
        pieces[finalPos.getX()][finalPos.getY()] = pieces[initialPos.getX()][initialPos.getY()];
        pieces[initialPos.getX()][initialPos.getY()] = null;
        
        // Cas du roque : il faut la tour
        Piece finalPiece = pieces[finalPos.getX()][finalPos.getY()]; 
        if (finalPiece.getType() == Type.ROI
            && initialPos.getX() == 4) {
            int roqueLine = initialPos.getY();
            if (finalPos.getX() == 6) {
                pieces[5][roqueLine] = pieces[7][roqueLine];
                pieces[7][roqueLine] = null;
            }
            else if (finalPos.getX() == 2) {
                pieces[3][roqueLine] = pieces[0][roqueLine];
                pieces[0][roqueLine] = null;
            }

        }
        
        // Cas d'un pion sur la dernire ligne
        if((finalPos.getY()==0 && finalPiece.getType()==Type.PION && finalPiece.getCouleur()==Couleur.BLANC)
        		|| (finalPos.getY()==7 && finalPiece.getType()==Type.PION && finalPiece.getCouleur()==Couleur.NOIR)){
        	pieces[finalPos.getX()][finalPos.getY()] = new Piece(Type.DAME,finalPiece.getCouleur());
        }
        
        turn = Utils.not(turn);
    }

    public Couleur getTurn() {
        return turn;
    }

    private static final String MOVE_SEP = "|", NO_PIECE = "--";
    
    @Override
    public String toString() {
        String str = "";
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Piece piece = pieces[x][y];
                str += (piece == null ? NO_PIECE : piece.toString());
                str += MOVE_SEP;
            }
            str += "\n";
        }
        return str;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Plateau))
            return false;
        if(obj==this)
            return true;
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece piece1 = pieces[x][y];
                Piece piece2 = ((Plateau)obj).pieces[x][y];
                if(piece1 == null){
                    if(piece2!=null)
                        return false;
                }
                else if(!piece1.equals(piece2))
                    return false;
            }
        }
        return ((Plateau)obj).turn == turn;
    }
    
    public void parse(String plateauStr){
    	String[] array = plateauStr.split(MOVE_SEP);
    	if(array.length != SIZE*SIZE)
    		throw new IllegalArgumentException("Illegal Plateau string.");
    	int i = 0;
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
            	if(array[i].equals(NO_PIECE))
            		pieces[x][y] = null;
            	else
            		pieces[x][y] = Piece.fromString(array[i++]); 
            }
        }
    }
}
