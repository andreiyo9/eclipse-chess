package com.seb.chess.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

import com.seb.chess.core.Piece;

public class SharedImages {

	public static final String CURSOR = "CURSOR";
	public static final String CURSOR2 = "CURSOR2";
	
	public static final String HUMAN = "HUMAN";
	public static final String COMPUTER = "COMPUTER";
	
	public static final String CLOCK = "CLOCK";
	
	public static String key(Piece piece){
		return piece.getType().toString()+"_"+piece.getCouleur().toString();
	}
	
    public static Image getImage(final String id) {
        Image img = PlatformUI.getWorkbench().getSharedImages().getImage(id);
        if (img == null) {
            img = ChessPlugin.getImgRegistry().get(id);
        }
        return img;
    }

    public static Image getImage(Piece piece){
    	if(piece == null)
    		return null;
    	return getImage(key(piece));
    }
}
