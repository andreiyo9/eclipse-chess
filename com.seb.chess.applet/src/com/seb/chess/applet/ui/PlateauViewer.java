package com.seb.chess.applet.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.seb.chess.core.Move;
import com.seb.chess.core.MoveGenerator;
import com.seb.chess.core.Piece;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.Position;
import com.seb.chess.core.players.IPlayer;
import com.seb.chess.core.players.IPlayerListener;

public class PlateauViewer extends Canvas implements IPlayer {

    /**
     * 
     */
    private static final long serialVersionUID = -5740646615482809539L;

    private final ImgRegistry imgRegistry;

    private final Plateau plateau;

    private Position curseur = null;

    private Collection<Position> possibleDestinations = new ArrayList<Position>();

    public PlateauViewer(Plateau plateau, ImgRegistry imgRegistry) {
        super();
        this.plateau = plateau;
        this.imgRegistry = imgRegistry;
        addMouseListener(mouseListener);
    }
    
    public Plateau getPlateau(){
        return plateau;
    }
    
    public void reset(){
        curseur = null;
        possibleDestinations.clear();        
    }

    private Set<IPlayerListener> listeners = new HashSet<IPlayerListener>();

    public void addListener(IPlayerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IPlayerListener listener) {
        listeners.remove(listener);
    }

    public Position getCurseur() {
		return curseur;
	}

	public void setCurseur(Position curseur) {
		this.curseur = curseur;
	}

	public Collection<Position> getPossibleDestinations() {
		return possibleDestinations;
	}

	protected void notifyMove(Move move) {
        for (IPlayerListener listener : new HashSet<IPlayerListener>(listeners)) {
            listener.moveComputed(move);
        }
    }
    
    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            Position pos = getPosition(e.getPoint());
            if (possibleDestinations.contains(pos)) {
                Move move = new Move(curseur, pos);
                setEnabled(false);
                notifyMove(move);
            }
            else if (plateau.getPiece(pos) != null
                     && plateau.getPiece(pos).getCouleur() == plateau.getTurn()) {
                // Efface le curseur courant
                if (curseur != null) {
                    redraw(curseur);
                }
                for (Position dest : possibleDestinations) {
                    redraw(dest);
                }
                curseur = pos;
                possibleDestinations.clear();
                possibleDestinations.addAll(MoveGenerator.getNonEchecMoves(plateau,
                                                                              curseur));
                // Affiche le nouveau curseur
                redraw(curseur);
                for (Position dest : possibleDestinations) {
                    redraw(dest);
                }
            }
        }
    };

    public void redraw(Position pos) {
        Rectangle rect = getRectangle(pos);
        repaint(rect.x, rect.y, rect.width, rect.height);
    }

    @Override
    public void paint(Graphics g) {
        paintBackground(g);
        paintLetters(g);
        // Position startPosition = getPosition(new Point(e.x, e.y));
        // Position lastPosition = getPosition(new Point(e.x + e.width - 1, e.y
        // + e.height - 1));
        Position startPosition = new Position(0, 0);
        Position lastPosition = new Position(Plateau.SIZE - 1, Plateau.SIZE - 1);
        Rectangle ext = getExternalPlateauBounds();
        g.drawRect(ext.x, ext.y, ext.width, ext.height);
        Rectangle inter = getInternalPlateauBounds();
        g.drawRect(inter.x, inter.y, inter.width, inter.height);
        for (int x = startPosition.getX(); x <= lastPosition.getX(); x++) {
            for (int y = startPosition.getY(); y <= lastPosition.getY(); y++) {
                paintPiece(g, new Position(x, y));
            }
        }
    }

    private static int BORDER_WIDTH = 20;

    private Rectangle getExternalPlateauBounds() {
        int canvasWidth = getBounds().width;
        int canvasHeight = getBounds().height;
        int plateauSize = Math.min(canvasWidth, canvasHeight);
        return new Rectangle((canvasWidth - plateauSize) / 2, (canvasHeight - plateauSize) / 2,
                             plateauSize, plateauSize);
    }

    private Rectangle getInternalPlateauBounds() {
        Rectangle extBounds = getExternalPlateauBounds();
        return new Rectangle(extBounds.x + BORDER_WIDTH, extBounds.y + BORDER_WIDTH,
                             extBounds.width - 2 * BORDER_WIDTH, extBounds.height - 2
                                                                 * BORDER_WIDTH);
    }

    protected Rectangle getRectangle(Position pos) {
        Rectangle plateauBounds = getInternalPlateauBounds();
        int unitWidth = plateauBounds.width / Plateau.SIZE;
        int unitHeight = plateauBounds.height / Plateau.SIZE;
        return new Rectangle(plateauBounds.x + unitWidth * pos.getX(), plateauBounds.y + unitHeight
                                                                       * pos.getY(), unitWidth,
                             unitHeight);
    }

    protected Position getPosition(Point point) {
        Rectangle plateauBounds = getInternalPlateauBounds();
        if (plateauBounds.contains(point)) {
            return new Position(Math.min((point.x - plateauBounds.x)
                                         / (plateauBounds.width / Plateau.SIZE), Plateau.SIZE - 1),
                                Math.min((point.y - plateauBounds.y)
                                         / (plateauBounds.height / Plateau.SIZE), Plateau.SIZE - 1));
        }
        else
            return null;
    }

    protected void paintBackground(Graphics backgroundGc) {
        backgroundGc.setColor(Color.GRAY);
        for (int x = 0; x < Plateau.SIZE; x++) {
            for (int y = 0; y < Plateau.SIZE; y++) {
                Position pos = new Position(x, y);
                Rectangle rect = getRectangle(pos);
                if ((x + y) % 2 == 1)
                    backgroundGc.fillRect(rect.x, rect.y, rect.width, rect.height);
            }
        }
    }

    protected void paintLetters(Graphics gc) {
        for (int x = 0; x < Plateau.SIZE; x++) {
            Position position = new Position(x, 0);
            Rectangle rect = getRectangle(position);
            gc.drawString(position.toString().substring(0, 1), rect.x + rect.width / 2,
                          rect.y - (BORDER_WIDTH / 4));
            position = new Position(x, Plateau.SIZE - 1);
            rect = getRectangle(position);
            gc.drawString(position.toString().substring(0, 1), rect.x + rect.width / 2,
                          rect.y + rect.height + BORDER_WIDTH * 3 / 4);
        }
        for (int y = 0; y < Plateau.SIZE; y++) {
            Position position = new Position(0, y);
            Rectangle rect = getRectangle(position);
            gc.drawString(position.toString().substring(1, 2), rect.x - BORDER_WIDTH / 2,
                          rect.y + rect.height / 2);
            position = new Position(Plateau.SIZE - 1, y);
            rect = getRectangle(position);
            gc.drawString(position.toString().substring(1, 2), rect.x + rect.width + BORDER_WIDTH
                                                               / 2, rect.y + rect.height / 2);
        }
    }

    protected void paintPiece(Graphics piecesGc, Position pos) {
        Rectangle rect = getRectangle(pos);
        if (pos.equals(curseur)) {
            /*
             * Image cursorImg = imgRegistry.getCurseur(); piecesGc.drawImage(cursorImg, rect.x, rect.y, rect.width,
             * rect.height, this);
             */
            piecesGc.setColor(Color.RED);
            piecesGc.fillRect(rect.x, rect.y, rect.width, rect.height);
            piecesGc.setColor(Color.BLACK);
            piecesGc.drawRect(rect.x, rect.y, rect.width, rect.height);
            piecesGc.drawRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
        }
        else if (possibleDestinations.contains(pos)) {
            /*
             * Image cursorImg = imgRegistry.getCurseur2(); piecesGc.drawImage(cursorImg, rect.x, rect.y, rect.width,
             * rect.height, this);
             */
            piecesGc.setColor(Color.YELLOW);
            piecesGc.fillRect(rect.x, rect.y, rect.width, rect.height);
            piecesGc.setColor(Color.BLACK);
            piecesGc.drawRect(rect.x, rect.y, rect.width, rect.height);
            piecesGc.drawRect(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2);
        }

        if (plateau == null)
            return;

        Piece piece = plateau.getPiece(pos);
        if (piece != null) {
            Image pieceImg = imgRegistry.getImage(piece);
            piecesGc.drawImage(pieceImg, rect.x, rect.y, rect.width, rect.height, this);
        }
    }

    @Override
    public String getDescription() {
        return "HUMAIN";
    }

    @Override
    public void startComputingMove(Plateau plateau) {
        setEnabled(true);
    }

}
