package com.seb.chess.ui;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.seb.chess.core.Game;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.pgn.PgnGenerator;
import com.seb.chess.core.pgn.PgnGenerator.Language;

public class ChessOutlinePage extends ContentOutlinePage implements IContentOutlinePage{
	
	private Game game;
	
	private PgnGenerator pgnGenerator;
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		getTreeViewer().setContentProvider(new ITreeContentProvider(){

			@Override
			public Object[] getChildren(Object parentElement) {
				if(parentElement instanceof Game){
					Game game = (Game)parentElement;
					Integer[] children = new Integer[game.getNbMoves()];
					for(int i=0; i<children.length;i++){
						children[i] = i;
					}
					return children;
				}
				return null;
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return getChildren(inputElement);
			}

			@Override
			public void dispose() {
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
			
		});
		getTreeViewer().setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if(element instanceof Integer){
					int i = (Integer)element;
					Plateau plateau = new Plateau();
					game.initPlateau(plateau, i-1);
					return i+") "+pgnGenerator.toPgnString(Language.FRENCH, game.getMoves().get(i), plateau);
				}
				return super.getText(element);
			}
		});
		if(game != null)
			getTreeViewer().setInput(game);
	}
	
	public void setGame(Game game){
		this.game = game;
		pgnGenerator = new PgnGenerator(game);
	}
	
}
