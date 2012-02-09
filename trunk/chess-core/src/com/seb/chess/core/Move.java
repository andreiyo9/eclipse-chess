package com.seb.chess.core;


public class Move {

	private final Position initialPos, finalPos;
	
	public Move(String initialPos, String finalPos){
	    this(new Position(initialPos),new Position(finalPos));
	}
	
	public Move(Position initialPos, Position finalPos){
		this.initialPos= initialPos;
		this.finalPos = finalPos;
	}
	
	public Position getInitialPos(){
		return initialPos;
	}
	
	public Position getFinalPos(){
		return finalPos;
	}
	
	@Override
	public String toString() {
		return "(" + initialPos.toString() + "-" + finalPos.toString() + ")";
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Move))
			return false;
		if(obj==this)
			return true;
		return initialPos.equals(((Move)obj).initialPos) && finalPos.equals(((Move)obj).finalPos);
	}
	
	@Override
	public int hashCode() {
		return initialPos.hashCode();
	}
	
	public static Move parseMove(String moveStr){
		moveStr = moveStr.substring(1,moveStr.length()-1);
		String[] positions = moveStr.split("-");
		if(positions.length != 2)
			return null;
		return new Move(new Position(positions[0]),new Position(positions[1]));
	}
}
