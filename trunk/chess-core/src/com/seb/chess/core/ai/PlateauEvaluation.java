package com.seb.chess.core.ai;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.seb.chess.core.ChessCoreLog;
import com.seb.chess.core.Move;
import com.seb.chess.core.MoveGenerator;
import com.seb.chess.core.Piece;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.Position;
import com.seb.chess.core.Utils;
import com.seb.chess.core.Piece.Couleur;

public class PlateauEvaluation {

    private final int EMPTY_PRISE, SELF_PRISE_WHEN_PLAYING, ADV_PRISE_WHEN_PLAYING, SELF_PRISE_WHEN_NOT_PLAYING, ADV_PRISE_WHEN_NOT_PLAYING;
    
    public PlateauEvaluation(){
        EMPTY_PRISE = 3;
        SELF_PRISE_WHEN_PLAYING = 5;
        ADV_PRISE_WHEN_PLAYING = 8;
        SELF_PRISE_WHEN_NOT_PLAYING = 8;
        ADV_PRISE_WHEN_NOT_PLAYING = 5;
    }
    
    public PlateauEvaluation(int EMPTY_PRISE, int SELF_PRISE_WHEN_PLAYING, int ADV_PRISE_WHEN_PLAYING, int SELF_PRISE_WHEN_NOT_PLAYING, int ADV_PRISE_WHEN_NOT_PLAYING){
        this.EMPTY_PRISE = EMPTY_PRISE;
        this.SELF_PRISE_WHEN_PLAYING = SELF_PRISE_WHEN_PLAYING;
        this.ADV_PRISE_WHEN_PLAYING = ADV_PRISE_WHEN_PLAYING;
        this.SELF_PRISE_WHEN_NOT_PLAYING = SELF_PRISE_WHEN_NOT_PLAYING;
        this.ADV_PRISE_WHEN_NOT_PLAYING = ADV_PRISE_WHEN_NOT_PLAYING;
    }
    
    public int value(Piece piece) {
        switch (piece.getType()) {
        case PION:
            return 10;
        case CAVALIER:
            return 30;
        case FOU:
            return 30;
        case TOUR:
            return 50;
        case DAME:
            return 100;
        }
        return 0;
    }

    private int evaluateCouleur(Plateau plateau, Couleur couleur) {
        int total = 0;
        boolean isPlaying = (plateau.getTurn() == couleur);
        for (int x = 0; x < Plateau.SIZE; x++) {
            for (int y = 0; y < Plateau.SIZE; y++) {
                Position pos = new Position(x, y);
                Piece piece = plateau.getPiece(pos);
                if (piece != null && piece.getCouleur() == couleur){
                    total += value(piece);
                    for(Position destPos : MoveGenerator.getPossibleMoves(plateau, pos)){
                    	Piece destPiece = plateau.getPiece(destPos);
                    	if(destPiece == null)
                    		total += EMPTY_PRISE;
                    	else if(isPlaying){ 
                    		if(destPiece.getCouleur()==couleur)
                    			total += SELF_PRISE_WHEN_PLAYING + value(destPiece)/10;
                    		else
                    			total += ADV_PRISE_WHEN_PLAYING+ value(destPiece)/10;
                    	}
                    	else {
                    		if(destPiece.getCouleur()==couleur)
                    			total += SELF_PRISE_WHEN_NOT_PLAYING + value(destPiece)/10;
                    		else
                    			total += ADV_PRISE_WHEN_NOT_PLAYING + value(destPiece)/10;                    		
                    	}
                    }
                }
            }
        }
        return total;
    }

    public int evaluatePlateau(Plateau plateau, Couleur couleur, int depth,
                                      List<Move> moveList) {
        return alphaBeta(plateau, -INF, INF, depth, couleur, moveList);
    }

    private static final int INF = 10000;

    private int alphaBeta(final Plateau plateau, Integer a, Integer b, int depth,
                                 Couleur couleur, List<Move> moveList) {
        if (depth == 0) {
            nbPositions++;
            int blancValue = evaluateCouleur(plateau, Couleur.BLANC);
            int noirValue = evaluateCouleur(plateau, Couleur.NOIR);
            if (couleur == Couleur.BLANC)
                return blancValue - noirValue;
            else
                return noirValue - blancValue;
        }

        int best = -INF;
        for (Move move : MoveGenerator.getPossibleMoves(plateau)) {
//            if (monitor.isCanceled())
                // Si l'annulation est requise, on revoit l'valuation directe du plateau (profondeur 0)
//                return alphaBeta(plateau,a,b,0,couleur,moveList,monitor);
            Plateau p = new Plateau(plateau);
            p.executeMove(move);
            List<Move> nextMoves = new ArrayList<Move>();
            int val = alphaBeta(p, -b, -a, depth - 1, Utils.not(couleur), nextMoves);
            String tab = "";
            for(int i=0;i<depth;i++){
            	tab+="   ";
            }
            ChessCoreLog.logDebug(tab+move+" : "+-val+" pour les "+couleur.toString());
            if (val > best) {
                moveList.clear();
                moveList.add(move);
                moveList.addAll(nextMoves);
                best = val;
                {
                    if (best > a)
                        a = new Integer(best);
                    if (a > b) {
                        ChessCoreLog.logDebug("Coupure alpha-beta!");
                        return -best;
                    }
                }
            }
        }
        return -best;
    }
    
    private static String movesToString(List<Move> moves){
        String moveString = "";
        boolean first = true;
        for (Move move : moves) {
        	if(!first){
        		moveString += ",";
        	}
            moveString += move.toString();
            first = false;
        }
        return moveString;
    }
    
    private int nbPositions;

    public Move getBestMove(Plateau plateau, int depth, IEvalProgressMonitor monitor) {
        nbPositions = 0;
        long initTime = System.currentTimeMillis();
        List<Move> bestMoveList = new ArrayList<Move>();
        int max = -INF-1;
        Collection<Move> possibleMoves = MoveGenerator.getPossibleMoves(plateau);
        monitor.setTaskName("Calcul du meilleur coup...");
        ChessCoreLog.logDebug("Comparaison des coups possibles :");
        for (Move move : possibleMoves) {
            Plateau p = new Plateau(plateau);
            p.executeMove(move);
            List<Move> moveList = new ArrayList<Move>();
            moveList.add(move);
            int val = evaluatePlateau(p, plateau.getTurn(), depth, moveList);
            ChessCoreLog.logDebug("* "+move+" -> "+movesToString(moveList)+" => "+val);
            if (val > max) {
                max = val;
                bestMoveList.clear();
                bestMoveList.add(move);
                bestMoveList.addAll(moveList);
            }
            monitor.worked(1);
        }
        ChessCoreLog.logDebug("Meilleur coup : " + (bestMoveList.size()>0 ? bestMoveList.get(0) : "NONE"));
        monitor.done();
        System.out.println(nbPositions+" positions �valu�es en "+(System.currentTimeMillis()-initTime)+" ms => "+(nbPositions/(System.currentTimeMillis()-initTime))+ " pos/ms");
        if(bestMoveList.size() > 0)
        	return bestMoveList.get(0);
        else
        	return null;
    }

    @Override
    public String toString() {
        return EMPTY_PRISE+","+SELF_PRISE_WHEN_PLAYING+","+ADV_PRISE_WHEN_PLAYING+","+SELF_PRISE_WHEN_NOT_PLAYING+","+ADV_PRISE_WHEN_NOT_PLAYING;
    }
}
