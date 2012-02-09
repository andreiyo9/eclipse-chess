package com.seb.chess.core;


import com.seb.chess.core.Piece.Couleur;

public class Utils {

	public static Couleur not(Couleur couleur){
		if(couleur == Couleur.BLANC)
			return Couleur.NOIR;
		else
			return Couleur.BLANC;
	}
	
}
