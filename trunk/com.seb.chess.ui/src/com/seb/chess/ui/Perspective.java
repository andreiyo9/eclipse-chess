package com.seb.chess.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);

		layout.addStandaloneView("com.seb.chess.ui.view", false,
				IPageLayout.LEFT, 1.0f, editorArea);

		layout.addStandaloneView(IConsoleConstants.ID_CONSOLE_VIEW, false, IPageLayout.RIGHT,
				.65f, "com.seb.chess.ui.view");
	}

}
