package com.seb.chess.ui;

import java.io.PrintStream;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.seb.chess.core.Piece;
import com.seb.chess.core.Piece.Couleur;
import com.seb.chess.core.Piece.Type;

/**
 * The activator class controls the plug-in life cycle
 */
public class ChessPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.seb.chess.ui";

	// The shared instance
	private static ChessPlugin plugin;
	
	/**
	 * The constructor
	 */
	public ChessPlugin() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	    myConsole = new MessageConsole("Chess Console", null);
	    msgStream = myConsole.newMessageStream();
	    System.setOut(new PrintStream(msgStream));
	    System.setErr(new PrintStream(msgStream));
	    ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{myConsole});
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ChessPlugin getDefault() {
		return plugin;
	}

	public static ImageRegistry getImgRegistry(){
		return plugin.getImageRegistry();
	}
	
    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        super.initializeImageRegistry(reg);
        addImage(SharedImages.CURSOR,"cursor.gif");
        addImage(SharedImages.CURSOR2,"cursor2.gif");
        addImage(SharedImages.HUMAN,"human.gif");
        addImage(SharedImages.COMPUTER,"computer.gif");
        addImage(SharedImages.CLOCK,"clock.gif");
        for(Type type : Type.values()){
        	for(Couleur couleur : Couleur.values()){
        		String key = SharedImages.key(new Piece(type,couleur));
        		String fileName = key.toLowerCase()+".gif";
        		addImage(key,fileName);
        	}
        }
    }

    private void addImage(String key, String fileName) {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        IPath path = new Path("icons/" + fileName);
        URL url = FileLocator.find(bundle, path, null);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        getImageRegistry().put(key, desc);
    }
    
    private MessageConsole myConsole;
    private MessageConsoleStream msgStream;


    public MessageConsoleStream getMessageConsoleStream() {
      return msgStream;
    }
    public MessageConsole getMessageConsole() {
      return myConsole;
    }
  	
}
