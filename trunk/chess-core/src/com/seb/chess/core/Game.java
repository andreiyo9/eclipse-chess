package com.seb.chess.core;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final List<Move> moves;
    
    private String white, black, event, site, date;
    
    public Game() {
        this.moves = new ArrayList<Move>();
    }

    public int getNbMoves() {
        return moves.size();
    }

    public List<Move> getMoves(){
    	return moves;
    }
    
    public void initPlateau(Plateau plateau, int step){
    	plateau.reset();
        for(int i=0; i<=step; i++){
            if(i<moves.size()){
                plateau.executeMove(moves.get(i));
            }
        }
    }

    public void initPlateau(Plateau plateau){
        initPlateau(plateau, moves.size()-1);
    }
    
    public Move getMove(int i){
        if(i<moves.size())
            return moves.get(i);
        return null;
    }

    public void doMove(Move move){
        moves.add(move);
    }
    
    public void undoMove(){
        moves.remove(moves.size()-1);
    }

	public String getWhite() {
		return white;
	}

	public void setWhite(String white) {
		this.white = white;
	}

	public String getBlack() {
		return black;
	}

	public void setBlack(String black) {
		this.black = black;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
    
}
