package com.seb.chess.core;


public class Position {

	private final int x, y;

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isValid(){
		return check(x) && check(y);
	}
	
	private static final boolean check(int coord) {
		return coord >= 0 && coord < Plateau.SIZE;
	}

	public Position(String posString) {
		if (posString.length() != 2)
			throw new IllegalArgumentException("The string length must be 2");

		char c = posString.charAt(0);
		int x = c - 'a';
		int y = Plateau.SIZE - Integer.parseInt(posString.substring(1, 2));

		if (check(x) && check(y)) {
			this.x = x;
			this.y = y;
		} else
			throw new IllegalArgumentException();
	}

	@Override
	public String toString() {
		return new Character((char) ('a' + x)).toString()
				+ String.valueOf(Plateau.SIZE - y);
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 == null || !(arg0 instanceof Position))
			return false;
		if (arg0 == this)
			return true;
		return ((Position) arg0).x == this.x && ((Position) arg0).y == this.y;
	}

	@Override
	public int hashCode() {
		return x + y;
	}
}
