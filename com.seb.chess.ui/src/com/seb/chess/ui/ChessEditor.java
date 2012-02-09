package com.seb.chess.ui;

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.seb.chess.core.Game;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.pgn.IPgnGeneratorMonitor;
import com.seb.chess.core.pgn.PgnGenerator;
import com.seb.chess.core.pgn.PgnGenerator.Language;

public class ChessEditor extends EditorPart implements ISelectionChangedListener{

    private Game game;

    private final Plateau plateau;

    private PlateauViewer viewer;
    
    private Text eventField, siteField, whiteField, blackField, dateField;
    
    private boolean dirty = false;

    public ChessEditor() {
        super();
        plateau = new Plateau();
    }
    
    private static class PgnProgressMonitor implements IPgnGeneratorMonitor{

        private IProgressMonitor monitor;
        
        public PgnProgressMonitor(IProgressMonitor monitor){
            this.monitor = monitor;
        }
        
        @Override
        public void beginTask(String name, int size) {
            monitor.beginTask(name, size);
        }

        @Override
        public void done() {
            monitor.done();
        }

        @Override
        public void worked(int arg0) {
            monitor.worked(arg0);
        }
        
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
    	// Save values
    	game.setBlack(blackField.getText());
    	game.setWhite(whiteField.getText());
    	game.setEvent(eventField.getText());
    	game.setSite(siteField.getText());
    	game.setDate(dateField.getText());
    	//
    	
    	// Write file
    	IFile file = ((FileEditorInput)getEditorInput()).getFile();
    	new PgnGenerator(game).write(file.getLocation().toFile(), new PgnProgressMonitor(monitor){
    	    
    	});
    	
    	dirty = false;
    	firePropertyChange(IEditorPart.PROP_DIRTY);
    }

    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setInput(input);
        setSite(site);
        if (input instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput)input).getFile();
            setPartName(file.getName());
            try {
                PgnParser parser = new PgnParser(Language.ENGLISH);
                parser.parsePgnFile(file.getLocation().toFile());
                game = parser.getGame();
                game.initPlateau(plateau);
            }
            catch (Exception e){
                e.printStackTrace();
//                ErrorDialog.openError(getEditorSite().getShell(), "Erreur de lecture du fichier", "Une erreur s'est produite lors de la lecture du fichier", null);
            }
        }
        else if(input instanceof FileStoreEditorInput) {
        	FileStoreEditorInput fseInput = (FileStoreEditorInput)input;
        	URI uri = fseInput.getURI();
            try {
                PgnParser parser = new PgnParser(Language.ENGLISH);
                parser.parsePgnFile(new File(uri));
                game = parser.getGame();
                game.initPlateau(plateau);
            }
            catch (Exception e){
                e.printStackTrace();
//                ErrorDialog.openError(getEditorSite().getShell(), "Erreur de lecture du fichier", "Une erreur s'est produite lors de la lecture du fichier", null);
            }
        }
        else {
        	throw new IllegalArgumentException("input parameter is a " + input.getClass().getCanonicalName() + " : it should be a IFileEditorInput or FileStoreEditorInput");
        }
    }
    
    private void updateFields(){
    	if(game.getEvent()!=null)
    		eventField.setText(game.getEvent());
    	if(game.getSite()!=null)
    		siteField.setText(game.getSite());
    	if(game.getWhite()!=null)
    		whiteField.setText(game.getWhite());
    	if(game.getBlack()!=null)
    		blackField.setText(game.getBlack());
    	if(game.getDate()!=null){
	    	dateField.setText(game.getDate());
    	}
        dirty = false;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    public void createPartControl(Composite parent) {
    	Composite composite = new Composite(parent,SWT.NONE);
    	composite.setLayoutData(new GridData(GridData.FILL_BOTH));
    	composite.setLayout(new GridLayout(2,false));
    	
    	Composite detailsComposite = new Composite(composite,SWT.NONE);
    	detailsComposite.setLayoutData(new GridData());
    	detailsComposite.setLayout(new GridLayout(2,false));

    	ModifyListener listener = new ModifyListener(){
    		@Override
    		public void modifyText(ModifyEvent e) {
    			dirty = true;
    			firePropertyChange(IEditorPart.PROP_DIRTY);
    		}
    	};
    	
    	Label whiteLabel = new Label(detailsComposite,SWT.NONE);
    	whiteLabel.setLayoutData(new GridData());
    	whiteLabel.setText("Blancs :");
    	whiteField = new Text(detailsComposite,SWT.BORDER);
    	whiteField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	whiteField.addModifyListener(listener);

    	Label blackLabel = new Label(detailsComposite,SWT.NONE);
    	blackLabel.setLayoutData(new GridData());
    	blackLabel.setText("Noirs :");
    	blackField = new Text(detailsComposite,SWT.BORDER);
    	blackField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	blackField.addModifyListener(listener);

    	Label siteLabel = new Label(detailsComposite,SWT.NONE);
    	siteLabel.setLayoutData(new GridData());
    	siteLabel.setText("Site :");
    	siteField = new Text(detailsComposite,SWT.BORDER);
    	siteField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	siteField.addModifyListener(listener);

    	Label eventLabel = new Label(detailsComposite,SWT.NONE);
    	eventLabel.setLayoutData(new GridData());
    	eventLabel.setText("Evenement :");
    	eventField = new Text(detailsComposite,SWT.BORDER);
    	eventField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    	eventField.addModifyListener(listener);
    	
    	Label dateLabel = new Label(detailsComposite,SWT.NONE);
    	dateLabel.setLayoutData(new GridData());
    	dateLabel.setText("Date :");
    	dateField = new Text(detailsComposite,SWT.BORDER);
    	dateField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    	viewer = new PlateauViewer(composite, SWT.NONE);
        viewer.setLayoutData(new GridData(GridData.FILL_BOTH));
        viewer.setPlateau(plateau);

        updateFields();        
    }

    @Override
    public void setFocus() {
    }

    @Override
    protected void setInput(IEditorInput input) {
        super.setInput(input);
    }
    
    private ChessOutlinePage outlinePage;
    
    public Object getAdapter(Class required) {
        if (IContentOutlinePage.class.equals(required)) {
           if (outlinePage == null) {
        	   outlinePage = new ChessOutlinePage();
        	   outlinePage.setGame(game);
        	   outlinePage.addSelectionChangedListener(this);
           }
           return outlinePage;
        }
        return super.getAdapter(required);
     }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
    	IStructuredSelection sel = (IStructuredSelection)event.getSelection();
    	if(!sel.isEmpty()){
    		int i = (Integer)sel.getFirstElement();
    		game.initPlateau(plateau, i);
    		viewer.redraw();
    	}
    }
}
