package com.seb.chess.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.seb.chess.core.Move;
import com.seb.chess.core.MoveGenerator;
import com.seb.chess.core.Piece;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.Position;
import com.seb.chess.core.players.IPlayer;
import com.seb.chess.core.players.IPlayerListener;

public class PlateauViewer extends Canvas implements IPlayer {

    private Plateau plateau;

    private Position curseur;

    private Collection<Position> possibleDestinations = new ArrayList<Position>();
    
    private static int BORDER_WIDTH = 20;

    private Rectangle getExternalPlateauBounds(){
        int canvasWidth = getBounds().width;
        int canvasHeight = getBounds().height;
        int plateauSize = Math.min(canvasWidth,canvasHeight);
        return new Rectangle((canvasWidth-plateauSize)/2,(canvasHeight-plateauSize)/2,plateauSize,plateauSize);
    }

    private Rectangle getInternalPlateauBounds(){
        Rectangle extBounds = getExternalPlateauBounds();
        return new Rectangle(extBounds.x+BORDER_WIDTH,extBounds.y+BORDER_WIDTH,extBounds.width-2*BORDER_WIDTH,extBounds.height-2*BORDER_WIDTH);
    }
    
    public PlateauViewer(Composite parent, int style) {
        super(parent, style);
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                paintBackground(e.gc);
                paintLetters(e.gc);
                Position startPosition = new Position(0,0);
                Position lastPosition = new Position(Plateau.SIZE-1,Plateau.SIZE-1);
                e.gc.drawRectangle(getExternalPlateauBounds());
                e.gc.drawRectangle(getInternalPlateauBounds());
                for (int x = startPosition.getX(); x <= lastPosition.getX(); x++) {
                    for (int y = startPosition.getY(); y <= lastPosition.getY(); y++) {
                        paintPiece(e.gc, new Position(x, y));
                    }
                }
            }
        });
        addListener(SWT.Resize,new Listener(){
            @Override
            public void handleEvent(Event event) {
                int min = Math.min(event.width, event.height);
                event.width = min;
                event.height = min;
            }
        });
        addMouseListener(mouseListener);
    }
    
    protected void notifyMove(Move move) {
        for (IPlayerListener listener : new HashSet<IPlayerListener>(listeners)) {
            listener.moveComputed(move);
        }
    }

    private MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mouseDown(MouseEvent e) {
            Position pos = getPosition(new Point(e.x,e.y));
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

    private Set<IPlayerListener> listeners = new HashSet<IPlayerListener>();

    public void addListener(IPlayerListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IPlayerListener listener) {
        listeners.remove(listener);
    }

    private void redraw(Position pos) {
        Rectangle rect = getRectangle(pos);
        redraw(rect.x, rect.y, rect.width, rect.height, false);
    }

    public void setPlateau(Plateau plateau) {
        this.plateau = plateau;
        redraw();
    }
    
    public Plateau getPlateau(){
        return plateau;
    }

    protected Rectangle getRectangle(Position pos) {
        Rectangle plateauBounds = getInternalPlateauBounds();
        int unitWidth = plateauBounds.width / Plateau.SIZE;
        int unitHeight = plateauBounds.height / Plateau.SIZE;
        return new Rectangle(plateauBounds.x + unitWidth * pos.getX(), plateauBounds.y + unitHeight * pos.getY(), unitWidth, unitHeight);
    }

    protected Position getPosition(Point point) {
        Rectangle plateauBounds = getInternalPlateauBounds();
        if(plateauBounds.contains(point)){
            return new Position(Math.min((point.x-plateauBounds.x) / (plateauBounds.width / Plateau.SIZE), Plateau.SIZE - 1),
                                Math.min((point.y-plateauBounds.y) / (plateauBounds.height / Plateau.SIZE), Plateau.SIZE - 1));
        }
        else
            return null;
    }

    protected void paintBackground(GC backgroundGc) {
        backgroundGc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        backgroundGc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
        for (int x = 0; x < Plateau.SIZE; x++) {
            for (int y = 0; y < Plateau.SIZE; y++) {
                if ((x + y) % 2 == 0)
                    backgroundGc.fillRectangle(getRectangle(new Position(x, y)));
            }
        }
    }
    
    protected void paintLetters(GC gc){
        for (int x = 0; x < Plateau.SIZE; x++) {
            Position position = new Position(x,0);
            Rectangle rect = getRectangle(position);
            gc.drawText(position.toString().substring(0,1), rect.x+rect.width/2, rect.y-(BORDER_WIDTH*3)/4,true);
            position = new Position(x,Plateau.SIZE-1);
            rect = getRectangle(position);
            gc.drawText(position.toString().substring(0,1), rect.x+rect.width/2, rect.y+rect.height+BORDER_WIDTH/4,true);
        }
        for (int y = 0; y < Plateau.SIZE; y++) {
            Position position = new Position(0,y);
            Rectangle rect = getRectangle(position);
            gc.drawText(position.toString().substring(1,2), rect.x-BORDER_WIDTH/2, rect.y+rect.height/2,true);
            position = new Position(Plateau.SIZE-1,y);
            rect = getRectangle(position);
            gc.drawText(position.toString().substring(1,2),  rect.x+rect.width+BORDER_WIDTH/2, rect.y+rect.height/2,true);
        }
    }

    protected void paintPiece(GC piecesGc, Position pos) {
        Rectangle rect = getRectangle(pos);

        if (pos.equals(curseur)) {
            Image cursorImg = SharedImages.getImage(SharedImages.CURSOR);
            piecesGc.drawImage(cursorImg, 0, 0, cursorImg.getBounds().width,
                               cursorImg.getBounds().height, rect.x, rect.y, rect.width,
                               rect.height);
        }
        else if (possibleDestinations.contains(pos)) {
            Image cursorImg = SharedImages.getImage(SharedImages.CURSOR2);
            piecesGc.drawImage(cursorImg, 0, 0, cursorImg.getBounds().width,
                               cursorImg.getBounds().height, rect.x, rect.y, rect.width,
                               rect.height);
        }

        if (plateau == null)
            return;

        piecesGc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
        piecesGc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));

        Piece piece = plateau.getPiece(pos);
        if (piece != null) {
            Image pieceImg = SharedImages.getImage(piece);
            piecesGc.drawImage(pieceImg, 0, 0, pieceImg.getBounds().width,
                               pieceImg.getBounds().height, rect.x, rect.y, rect.width, rect.height);
        }
    }

    public Position getCurseur(){
        return curseur;
    }
    
    public void setCurseur(Position pos){
        this.curseur = pos;
        redraw(pos);
    }
 
    public Collection<Position> getPossibleDestinations(){
        return possibleDestinations;
    }
    
    public void setPossibleDestinations(Collection<Position> dests){
        this.possibleDestinations = dests;
        redraw();
    }

    @Override
    public String getDescription() {
        return "HUMAIN";
    }

    @Override
    public void startComputingMove(Plateau plateau) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
            	setEnabled(true);
            }
        });
    }
}
