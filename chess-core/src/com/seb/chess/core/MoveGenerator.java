package com.seb.chess.core;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.seb.chess.core.Piece.Couleur;
import com.seb.chess.core.Piece.Type;

public class MoveGenerator {

	public static boolean isMoveValid(Plateau plateau, Move move) {
		return getNonEchecMoves(plateau, move.getInitialPos()).contains(
				move.getFinalPos());
	}

	public static Collection<Position> getPossibleMoves(Plateau plateau,
			Position pos) {
		List<Position> moves = new ArrayList<Position>();

		Piece piece = plateau.getPiece(pos);
		if (piece != null) {
			switch (piece.getType()) {
			case PION:
				// Indice de la ligne suivant le pion en fonction de sa couleur
				int nextLine = pos.getY()
						+ (piece.getCouleur() == Couleur.BLANC ? (-1) : 1);
				// Case juste devant le pion
				Position frontPos = new Position(pos.getX(), nextLine);
				if (frontPos.isValid() && plateau.getPiece(frontPos) == null)
					moves.add(frontPos);

				// Cases en diagonales devant le pion
				Position diagPos = new Position(pos.getX() + 1, nextLine);
				if (diagPos.isValid()) {
					Piece pieceAPrendre = plateau.getPiece(diagPos);
					if (pieceAPrendre != null
							&& pieceAPrendre.getCouleur() != piece.getCouleur())
						moves.add(diagPos);
				}

				diagPos = new Position(pos.getX() - 1, nextLine);
				if (diagPos.isValid()) {
					Piece pieceAPrendre = plateau.getPiece(diagPos);
					if (pieceAPrendre != null
							&& pieceAPrendre.getCouleur() != piece.getCouleur())
						moves.add(diagPos);
				}

				// 2 cases devant le pion
				if ((pos.getY() == 1 && piece.getCouleur() == Couleur.NOIR)
						|| (pos.getY() == 6 && piece.getCouleur() == Couleur.BLANC)) {
					nextLine = nextLine
							+ (piece.getCouleur() == Couleur.BLANC ? (-1) : 1);
					Position twoFrontPos = new Position(pos.getX(), nextLine);
					if (twoFrontPos.isValid()
							&& plateau.getPiece(frontPos) == null && plateau.getPiece(twoFrontPos) == null)
						moves.add(twoFrontPos);
				}

				break;
			case CAVALIER:
				Position dest = new Position(pos.getX() + 1, pos.getY() + 2);
				if (dest.isValid()
						&& (plateau.getPiece(dest) == null
						|| plateau.getPiece(dest).getCouleur() != piece
								.getCouleur()))
					moves.add(dest);
				dest = new Position(pos.getX() - 1, pos.getY() + 2);
				if (dest.isValid()
						&& (plateau.getPiece(dest) == null
						|| plateau.getPiece(dest).getCouleur() != piece
								.getCouleur()))
					moves.add(dest);
				dest = new Position(pos.getX() + 1, pos.getY() - 2);
				if (dest.isValid()
						&& (plateau.getPiece(dest) == null
						|| plateau.getPiece(dest).getCouleur() != piece
								.getCouleur()))
					moves.add(dest);
				dest = new Position(pos.getX() - 1, pos.getY() - 2);
				if (dest.isValid()
						&& (plateau.getPiece(dest) == null
						|| plateau.getPiece(dest).getCouleur() != piece
								.getCouleur()))
					moves.add(dest);
				dest = new Position(pos.getX() + 2, pos.getY() + 1);
				if (dest.isValid()
						&& (plateau.getPiece(dest) == null
						|| plateau.getPiece(dest).getCouleur() != piece
								.getCouleur()))
					moves.add(dest);
				dest = new Position(pos.getX() - 2, pos.getY() + 1);
				if (dest.isValid()
						&& (plateau.getPiece(dest) == null
						|| plateau.getPiece(dest).getCouleur() != piece
								.getCouleur()))
					moves.add(dest);
				dest = new Position(pos.getX() + 2, pos.getY() - 1);
				if (dest.isValid()
						&& (plateau.getPiece(dest) == null
						|| plateau.getPiece(dest).getCouleur() != piece
								.getCouleur()))
					moves.add(dest);
				dest = new Position(pos.getX() - 2, pos.getY() - 1);
				if (dest.isValid()
						&& (plateau.getPiece(dest) == null
						|| plateau.getPiece(dest).getCouleur() != piece
								.getCouleur()))
					moves.add(dest);
				break;
			case FOU:
				addLineMoves(plateau,moves, pos, 1, 1,true);
				addLineMoves(plateau,moves, pos, 1, -1,true);
				addLineMoves(plateau,moves, pos, -1, -1,true);
				addLineMoves(plateau,moves, pos, -1, 1,true);
				break;
			case TOUR:
				addLineMoves(plateau,moves, pos, 1, 0,true);
				addLineMoves(plateau,moves, pos, 0, -1,true);
				addLineMoves(plateau,moves, pos, -1,0,true);
				addLineMoves(plateau,moves, pos, 0, 1,true);
				break;
			case DAME:
				addLineMoves(plateau,moves, pos, 1, 1,true);
				addLineMoves(plateau,moves, pos, 1, 0,true);
				addLineMoves(plateau,moves, pos, 1, -1,true);
				addLineMoves(plateau,moves, pos, 0, -1,true);
				addLineMoves(plateau,moves, pos, -1, -1,true);
				addLineMoves(plateau,moves, pos, -1,0,true);
				addLineMoves(plateau,moves, pos, -1, 1,true);
				addLineMoves(plateau,moves, pos, 0, 1,true);
				break;
			case ROI:
				addLineMoves(plateau,moves, pos, 1, 1,false);
				addLineMoves(plateau,moves, pos, 1, 0,false);
				addLineMoves(plateau,moves, pos, 1, -1,false);
				addLineMoves(plateau,moves, pos, 0, -1,false);
				addLineMoves(plateau,moves, pos, -1, -1,false);
				addLineMoves(plateau,moves, pos, -1,0,false);
				addLineMoves(plateau,moves, pos, -1, 1,false);
				addLineMoves(plateau,moves, pos, 0, 1,false);

				int roqueLine = piece.getCouleur() == Couleur.BLANC ? 7 : 0;
				if (pos.getX() == 4 && pos.getY() == roqueLine) {
					try {
						// Petit roque
						if ((plateau.getPiece(new Position(5, roqueLine)) == null)
								&& (plateau
										.getPiece(new Position(6, roqueLine)) == null)
								&& (plateau
										.getPiece(new Position(7, roqueLine)) != null)
								&& (plateau
										.getPiece(new Position(7, roqueLine))
										.getType() == Type.TOUR)) {
							moves.add(new Position(6, roqueLine));
						}
					} catch (IllegalArgumentException e) {
					}
					try {
						// Grand roque
						if ((plateau.getPiece(new Position(3, roqueLine)) == null)
								&& (plateau
										.getPiece(new Position(2, roqueLine)) == null)
								&& (plateau
										.getPiece(new Position(1, roqueLine)) == null)
								&& (plateau
										.getPiece(new Position(0, roqueLine)) != null)
								&& (plateau
										.getPiece(new Position(0, roqueLine))
										.getType() == Type.TOUR)) {
							moves.add(new Position(2, roqueLine));
						}
					} catch (IllegalArgumentException e) {
					}
				}
				break;
			}
		}
		return moves;
	}
	
	private static void addLineMoves(Plateau plateau, List<Position> moves,Position initialPos, int hStep, int vStep,boolean iterate){
		Position dest = initialPos;
		Piece initPiece = plateau.getPiece(initialPos);
		int i=0;
		while(dest.isValid()){
			if(i>0){
				Piece piece = plateau.getPiece(dest);
				if(piece == null){
					moves.add(dest);
				}
				else{
					if(piece.getCouleur() != initPiece.getCouleur())
						moves.add(dest);
					return;
				}
			}
			dest = new Position(dest.getX()+hStep,dest.getY()+vStep);
			i++;
			if(i==2 && !iterate)
				return;
		}
	}

	public static boolean isEchec(Plateau plateau, Couleur couleur) {
		// Cherche la position des rois
		Position roiPos = null;
		for (int x = 0; x < Plateau.SIZE; x++) {
			for (int y = 0; y < Plateau.SIZE; y++) {
				Position pos = new Position(x, y);
				Piece piece = plateau.getPiece(pos);
				if (piece != null && piece.getType() == Type.ROI
						&& piece.getCouleur() == couleur) {
					roiPos = pos;
					break;
				}
			}
		}
		if (roiPos != null) {
			// Cherche les pièces qui menacent le roi
			for (int x = 0; x < Plateau.SIZE; x++) {
				for (int y = 0; y < Plateau.SIZE; y++) {
					Position pos = new Position(x, y);
					if (getPossibleMoves(plateau, pos).contains(roiPos))
						return true;
				}
			}
		}

		return false;
	}

	public static Collection<Position> getNonEchecMoves(Plateau plateau,
			Position pos) {
		Collection<Position> moves = new ArrayList<Position>();
		Piece piece = plateau.getPiece(pos);
		if (piece != null) {
			// Enlève tous les coups qui mettent le joueur qui joue en échec
			Collection<Position> possibleMoves = getPossibleMoves(plateau, pos);
			for (Position dest : possibleMoves) {
				Plateau p = new Plateau(plateau);
				p.executeMove(new Move(pos, dest));
				if (!isEchec(p, piece.getCouleur()))
					moves.add(dest);
			}
		}
		return moves;
	}

	public static Collection<Move> getPossibleMoves(Plateau plateau) {
		List<Move> moves = new ArrayList<Move>();
		for (int x = 0; x < Plateau.SIZE; x++) {
			for (int y = 0; y < Plateau.SIZE; y++) {
				Position pos = new Position(x, y);
				Piece piece = plateau.getPiece(pos);
				if (piece != null && piece.getCouleur() == plateau.getTurn()) {
					for (Position dest : getNonEchecMoves(plateau, pos)) {
						moves.add(new Move(pos, dest));
					}
				}
			}
		}
		return moves;
	}

	public static boolean checkMate(Plateau plateau) {
		return getPossibleMoves(plateau).size() == 0;
	}
}
