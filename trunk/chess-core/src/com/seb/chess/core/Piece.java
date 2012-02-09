package com.seb.chess.core;


public class Piece {

    public static enum Type {
        PION, CAVALIER, FOU, TOUR, DAME, ROI
    };

    public static enum Couleur {
        BLANC, NOIR
    };

    private final Type type;

    private final Couleur couleur;

    public Piece(Type type, Couleur couleur) {
        this.type = type;
        this.couleur = couleur;
    }

    public Type getType() {
        return type;
    }

    public Couleur getCouleur() {
        return couleur;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj==null || !(obj instanceof Piece))
            return false;
        if(obj==this)
            return true;
        return ((Piece)obj).type==type && ((Piece)obj).couleur==couleur;
    }
    
    @Override
    public String toString() {
    	return type.toString().substring(0, 1)+couleur.toString().substring(0, 1);
    }
    
    public static Piece fromString(String str){
    	Type type = null;
    	Couleur coul = null;
    	for(Type t : Type.values()){
    		if(str.startsWith(t.toString())){
    			type = t;
    			break;
    		}
    	}
    	for(Couleur c : Couleur.values()){
    		if(str.endsWith(c.toString())){
    			coul = c;
    			break;
    		}
    	}
    	if(type != null && coul != null)
    		return new Piece(type,coul);
    	return null;
    }
}
