package com.seb.chess.rcp;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.seb.chess.ui.NewGameDialog;

class NewGameAction extends Action {

    private final Shell shell;

    public NewGameAction(Shell shell) {
        super();
        this.shell = shell;
        setText("Nouvelle partie");
    }

    @Override
    public void run() {
	    new NewGameDialog(Display.getDefault().getActiveShell()).open();
    }
}
