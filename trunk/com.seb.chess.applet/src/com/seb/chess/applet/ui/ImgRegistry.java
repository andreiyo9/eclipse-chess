package com.seb.chess.applet.ui;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JApplet;

import com.seb.chess.core.Piece;

public class ImgRegistry {

    private Image curseur = null, curseur2 = null;

    private Map<Piece, Image> map = new HashMap<Piece, Image>();

    private final JApplet applet;
    
    public ImgRegistry(JApplet applet){
        this.applet = applet;
    }
    
    public Image getCurseur() {
        if (curseur == null)
            curseur = Toolkit.getDefaultToolkit().getImage("images/cursor.gif");
        return curseur;
    }

    public Image getCurseur2() {
        if (curseur2 == null)
            curseur2 = Toolkit.getDefaultToolkit().getImage("images/cursor2.gif");
        return curseur2;
    }

    public Image getImage(Piece piece) {
        Image image = map.get(piece);
        if (image == null) {
            String fileName = "images/"
                              + (piece.getType().toString() + "_" + piece.getCouleur().toString()).toLowerCase()
                              + ".gif";
            image = applet.getImage(applet.getCodeBase(),fileName);
            map.put(piece, image);
        }
        return image;
    }

}
