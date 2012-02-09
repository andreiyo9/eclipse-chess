package com.seb.chess.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.seb.chess.core.ChessCoreLog;
import com.seb.chess.core.Piece;
import com.seb.chess.core.Piece.Couleur;
import com.seb.chess.core.Piece.Type;
import com.seb.chess.core.players.ComPlayer;
import com.seb.chess.core.players.IPlayer;

public class NewGameDialog extends Dialog {
    private Button blancHumanButton, blancComButton, noirHumanButton, noirComButton;

    private Text blancName, noirName;

    private Combo blancLevelCombo, noirLevelCombo;

    private static final int MIN_LEVEL = 0, MAX_LEVEL = 3;

    public NewGameDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText("Nouvelle partie");
        getShell().setImage(SharedImages.getImage(new Piece(Type.CAVALIER,Couleur.BLANC)));
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());

        Group blancGroup = new Group(composite, SWT.NONE);
        blancGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        blancGroup.setLayout(new GridLayout(2, false));
        blancGroup.setText("Blancs");

        blancHumanButton = new Button(blancGroup, SWT.RADIO);
        blancHumanButton.setLayoutData(new GridData());
        blancHumanButton.setImage(SharedImages.getImage(SharedImages.HUMAN));
        blancHumanButton.setText("Joueur humain");

        blancName = new Text(blancGroup, SWT.BORDER);
        blancName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        blancName.setText(HumanPlayer.DEFAULT_NAME + "_blancs");

        blancComButton = new Button(blancGroup, SWT.RADIO);
        blancComButton.setLayoutData(new GridData());
        blancComButton.setText("Ordinateur");
        blancComButton.setImage(SharedImages.getImage(SharedImages.COMPUTER));

        blancLevelCombo = new Combo(blancGroup, SWT.READ_ONLY);
        blancLevelCombo.setLayoutData(new GridData());
        for (int i = MIN_LEVEL; i <= MAX_LEVEL; i++) {
            String str = getLevelString(i);
            blancLevelCombo.add(str);
            blancLevelCombo.setData(str, i);
        }
        blancLevelCombo.select(2);

        blancHumanButton.setSelection(true);

        Group noirGroup = new Group(composite, SWT.NONE);
        noirGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        noirGroup.setLayout(new GridLayout(2, false));
        noirGroup.setText("Noirs");

        noirHumanButton = new Button(noirGroup, SWT.RADIO);
        noirHumanButton.setLayoutData(new GridData());
        noirHumanButton.setText("Joueur humain");
        noirHumanButton.setImage(SharedImages.getImage(SharedImages.HUMAN));

        noirName = new Text(noirGroup, SWT.BORDER);
        noirName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//        noirName.setText(HumanPlayer.DEFAULT_NAME + "_noirs");

        noirComButton = new Button(noirGroup, SWT.RADIO);
        noirComButton.setLayoutData(new GridData());
        noirComButton.setText("Ordinateur");
        noirComButton.setImage(SharedImages.getImage(SharedImages.COMPUTER));
        noirComButton.setSelection(true);

        noirLevelCombo = new Combo(noirGroup, SWT.READ_ONLY);
        noirLevelCombo.setLayoutData(new GridData());
        for (int i = MIN_LEVEL; i <= MAX_LEVEL; i++) {
            String str = getLevelString(i);
            noirLevelCombo.add(str);
            noirLevelCombo.setData(str, i);
        }
        noirLevelCombo.select(2);
        return composite;
    }

    private String getLevelString(int level) {
        return "Niveau " + level;
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
        	IPlayer white, black;
            if (blancComButton.getSelection())
                white = new ComPlayer(
                                                        (Integer)blancLevelCombo.getData(blancLevelCombo.getItem(blancLevelCombo.getSelectionIndex())));
            else
                white = new HumanPlayer(blancName.getText());
            if (noirComButton.getSelection())
                black = new ComPlayer(
                                                       (Integer)noirLevelCombo.getData(noirLevelCombo.getItem(noirLevelCombo.getSelectionIndex())));
            else
                black = new HumanPlayer(noirName.getText());
            try {
                ((ChessView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(ChessView.ID)).newGame(white,black);
            }
            catch (PartInitException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            ChessCoreLog.logDebug("**** Nouvelle partie ****");
        }
        super.buttonPressed(buttonId);
    }
}
