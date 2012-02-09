package com.seb.chess.ui;

import com.seb.chess.core.Move;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.players.AbstractPlayer;
import com.seb.chess.core.players.IPlayer;
import com.seb.chess.core.players.IPlayerListener;

public class HumanPlayer extends AbstractPlayer implements IPlayer, IPlayerListener {

	protected String name;
	protected PlateauViewer viewer;
	
	public HumanPlayer(String name){
		this.name = name;
	}
	
	public void setPlateauViewer(PlateauViewer viewer){
		this.viewer = viewer;
	}
	
	@Override
	public String getDescription() {
		return name;
	}

	@Override
	public void startComputingMove(Plateau plateau) {
		viewer.addListener(this);
	}

	@Override
	public void moveComputed(Move move) {
		viewer.removeListener(this);
		notifyMove(move);
	}
}
