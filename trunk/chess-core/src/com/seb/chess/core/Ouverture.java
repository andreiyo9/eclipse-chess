package com.seb.chess.core;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ouverture {

    private final String name;

    private final Move[] moves;

    public Ouverture(String name, Move[] moves) {
        this.name = name;
        this.moves = moves;
    }

    public String getName() {
        return name;
    }

    public Move[] getMoves() {
        return moves;
    }

    // Jeux ouverts
    public static final Ouverture GAMBIT_ROI = new Ouverture("Gambit Roi",
                                                             new Move[] { new Move("e2", "e4"),
                                                                         new Move("e7", "e5"),
                                                                         new Move("f2", "f4") });

    public static final Ouverture GAMBIT_DANOIS = new Ouverture("Gambit danois",
                                                                new Move[] { new Move("e2", "e4"),
                                                                            new Move("e7", "e5"),
                                                                            new Move("d2", "d4"),
                                                                            new Move("e5", "d4"),
                                                                            new Move("c2", "c3") });

    public static final Ouverture OUVERTURE_ALAPINE = new Ouverture(
                                                                    "Ouverture Alapine",
                                                                    new Move[] {
                                                                                new Move("e2", "e4"),
                                                                                new Move("e7", "e5"),
                                                                                new Move("g1", "e2") });

    public static final Ouverture PARTIE_VIENNOISE = new Ouverture(
                                                                   "Partie viennoise",
                                                                   new Move[] {
                                                                               new Move("e2", "e4"),
                                                                               new Move("e7", "e5"),
                                                                               new Move("b1", "c3") });

    public static final Ouverture PORTUGAISE = new Ouverture("Ouverture portugaise",
                                                             new Move[] { new Move("e2", "e4"),
                                                                         new Move("e7", "e5"),
                                                                         new Move("f1", "b5") });

    public static final Ouverture DEBUT_DU_FOU = new Ouverture("Debut du fou",
                                                               new Move[] { new Move("e2", "e4"),
                                                                           new Move("e7", "e5"),
                                                                           new Move("f1", "c4") });

    public static final Ouverture GAMBIT_LETTON = new Ouverture("Gambit letton",
                                                                new Move[] { new Move("e2", "e4"),
                                                                            new Move("e7", "e5"),
                                                                            new Move("g1", "f3"),
                                                                            new Move("f7", "f5") });

    public static final Ouverture DEFENSE_RUSSE = new Ouverture("Defense russe",
                                                                new Move[] { new Move("e2", "e4"),
                                                                            new Move("e7", "e5"),
                                                                            new Move("g1", "f3"),
                                                                            new Move("g8", "f6") });

    public static final Ouverture DEUX_CAVALIERS = new Ouverture("Defense des deux cavaliers",
                                                                 new Move[] { new Move("e2", "e4"),
                                                                             new Move("e7", "e5"),
                                                                             new Move("g1", "f3"),
                                                                             new Move("b8", "c6"),
                                                                             new Move("f1", "c4"),
                                                                             new Move("g8", "f6") });

    public static final Ouverture QUATRE_CAVALIERS = new Ouverture(
                                                                   "Quatre cavaliers",
                                                                   new Move[] {
                                                                               new Move("e2", "e4"),
                                                                               new Move("e7", "e5"),
                                                                               new Move("g1", "f3"),
                                                                               new Move("b8", "c6"),
                                                                               new Move("b1", "c3"),
                                                                               new Move("g8", "f6") });

    public static final Ouverture PARTIE_ECOSSAISE = new Ouverture(
                                                                   "Partie ecossaise",
                                                                   new Move[] {
                                                                               new Move("e2", "e4"),
                                                                               new Move("e7", "e5"),
                                                                               new Move("g1", "f3"),
                                                                               new Move("b8", "c6"),
                                                                               new Move("d2", "d4"),
                                                                               new Move("e5", "d4") });

    public static final Ouverture PARTIE_ESPAGNOLE = new Ouverture(
                                                                   "Partie espagnole",
                                                                   new Move[] {
                                                                               new Move("e2", "e4"),
                                                                               new Move("e7", "e5"),
                                                                               new Move("g1", "f3"),
                                                                               new Move("b8", "c6"),
                                                                               new Move("f1", "b5") });

    public static final Ouverture PARTIE_ITALIENNE = new Ouverture(
                                                                   "Partie italienne",
                                                                   new Move[] {
                                                                               new Move("e2", "e4"),
                                                                               new Move("e7", "e5"),
                                                                               new Move("g1", "f3"),
                                                                               new Move("b8", "c6"),
                                                                               new Move("f1", "c4"),
                                                                               new Move("f8", "c5") });

    public static final Ouverture DEFENSE_PHILIDOR = new Ouverture(
                                                                   "Defense philidor",
                                                                   new Move[] {
                                                                               new Move("e2", "e4"),
                                                                               new Move("e7", "e5"),
                                                                               new Move("g1", "f3"),
                                                                               new Move("d7", "d6") });

    // Jeux semi_ouverts
    public static final Ouverture DEFENSE_FRANCAISE = new Ouverture(
                                                                    "Defense francaise",
                                                                    new Move[] {
                                                                                new Move("e2", "e4"),
                                                                                new Move("e7", "e6"),
                                                                                new Move("d2", "d4"),
                                                                                new Move("d7", "d5") });

    // Jeux ferms
    public static final Ouverture DEFENSE_SLAVE = new Ouverture("Defense slave",
                                                                new Move[] { new Move("d2", "d4"),
                                                                            new Move("d7", "d5"),
                                                                            new Move("c2", "c4"),
                                                                            new Move("c7", "c6") });

    public static final Ouverture[] ALL_OUVERTURES = new Ouverture[] { GAMBIT_ROI, GAMBIT_DANOIS,
                                                                      OUVERTURE_ALAPINE,
                                                                      PARTIE_VIENNOISE, PORTUGAISE,
                                                                      DEBUT_DU_FOU, GAMBIT_LETTON,
                                                                      DEFENSE_RUSSE,
                                                                      DEUX_CAVALIERS,
                                                                      QUATRE_CAVALIERS,
                                                                      PARTIE_ECOSSAISE,
                                                                      PARTIE_ESPAGNOLE,
                                                                      PARTIE_ITALIENNE,
                                                                      DEFENSE_PHILIDOR,
                                                                      DEFENSE_FRANCAISE,
                                                                      DEFENSE_SLAVE };

    private static final Random rnd = new Random();

    public static Move getMove(Plateau plateau) {
        List<Move> possibleMoves = new ArrayList<Move>();
        for (Ouverture ouverture : ALL_OUVERTURES) {
            Plateau tmpPlateau = new Plateau();
            tmpPlateau.reset();
            int i = 0;
            Move[] moves = ouverture.getMoves();
            if (tmpPlateau.equals(plateau)) {
//                ChessCoreLog.logDebug("(Ouverture possible : " + ouverture.getName() + ")");
                possibleMoves.add(moves[0]);
                continue;
            }
            for (Move move : moves) {
                tmpPlateau.executeMove(move);
                i++;
                if (tmpPlateau.equals(plateau) && i < moves.length) {
//                    ChessCoreLog.logDebug("(Ouverture possible : " + ouverture.getName() + ")");
                    possibleMoves.add(moves[i]);
                    continue;
                }
            }
        }
        if (possibleMoves.size() == 0)
            return null;
        else {
            int n = rnd.nextInt(possibleMoves.size());
            return possibleMoves.get(n);
        }
    }
}
