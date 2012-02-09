package com.seb.chess.applet;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import com.seb.chess.applet.ui.ImgRegistry;
import com.seb.chess.applet.ui.PlateauViewer;
import com.seb.chess.core.Game;
import com.seb.chess.core.Move;
import com.seb.chess.core.MoveGenerator;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.Piece.Couleur;
import com.seb.chess.core.players.ComPlayer;
import com.seb.chess.core.players.IPlayer;
import com.seb.chess.core.players.IPlayerListener;
import com.seb.chess.core.players.NetPlayer;

public class MyApplet extends JApplet {

    /**
     * 
     */
    private static final long serialVersionUID = -9124856928176241692L;

    private Game game;

    private final Plateau plateau = new Plateau();

    private PlateauViewer viewer;

    private JLabel title;

    private JButton newComButton, newServerButton, newClientButton;

    private JTextField serverIpField, serverPortField, listeningPortField;

    private SwingWorker<Void, Void> listeningTask;

    private JList moveList;

    private IPlayer whitePlayer, blackPlayer;

    private boolean isListening;

    private boolean isPlaying;
    
    private JComboBox colorCombo;

    @Override
    public void init() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGui();
            }
        });
    }
    
    private IPlayer getCurrentPlayer(){
        return (plateau.getTurn() == Couleur.BLANC ? whitePlayer
                                                   : blackPlayer);
    }

    private final MoveListModel moveListModel = new MoveListModel();

    private class MoveListModel implements ListModel {

        private Set<ListDataListener> listeners = new HashSet<ListDataListener>();

        @Override
        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        @Override
        public Object getElementAt(int index) {
            if (game != null)
                return game.getMove(index);
            else
                return null;
        }

        @Override
        public int getSize() {
            if (game != null)
                return game.getNbMoves();
            else
                return 0;
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }

        public void notifyChange(ListDataEvent e) {
            for (ListDataListener l : listeners) {
                l.contentsChanged(e);
            }
        }
    }

    private void createGui() {
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        title = new JLabel();
        container.add(title, BorderLayout.PAGE_START);

        viewer = new PlateauViewer(plateau, new ImgRegistry(this));
        container.add(viewer, BorderLayout.CENTER);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BorderLayout());
        JLabel listTitle = new JLabel("List des coups");
        listPanel.add(listTitle, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane();
        moveList = new JList(moveListModel);
        scrollPane.getViewport().add(moveList);
        listPanel.add(scrollPane, BorderLayout.CENTER);

//        container.add(listPanel, BorderLayout.EAST);

        JPanel newGamePanel = new JPanel();
        newGamePanel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel newGameLabel = new JLabel("Nouvelle partie :");
        c.gridx=0;
        c.gridy=0;
        newGamePanel.add(newGameLabel,c);
        
        // VS computer
        newComButton = new JButton("Contre l'ordinateur");
        newComButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(colorCombo.getSelectedItem().equals(Couleur.BLANC.toString()))
            		startNewGame(viewer,new ComPlayer(DEPTH));
            	else
            		startNewGame(new ComPlayer(DEPTH),viewer);
            }
        });
        c.weightx=0.0;
        c.gridx=0;
        c.gridy=1;
        newGamePanel.add(newComButton,c);
        colorCombo = new JComboBox(new String[]{Couleur.BLANC.toString(),Couleur.NOIR.toString()});
        colorCombo.setSelectedIndex(0);
        c.gridx=1;
        c.gridy=1;
	    newGamePanel.add(colorCombo,c);

        // Network (server)
        newServerButton = new JButton("Reseau (serveur)");
        newServerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	if(isListening)
            		listeningTask.cancel(true);
            	else{
	                listeningTask = new SwingWorker<Void, Void>() {
	                    private Socket listenConnection(ServerSocket service) throws Exception {
	                        Socket connexion = null;
	                        try {
	                            connexion = service.accept();
	                            return connexion;
	                        }
	                        catch (SocketTimeoutException e) {
	                            if (!isCancelled())
	                                return listenConnection(service);
	                        }
	                        catch (Exception e) {
	                            throw e;
	                        }
	                        return connexion;
	                    }
	
	                    @Override
	                    protected Void doInBackground() throws Exception {
	                        ServerSocket service = null;
	                        try {
	                            setProgress(0);
	                            Integer port = Integer.parseInt(listeningPortField.getText());
	                            service = new ServerSocket(port);
	                            service.setSoTimeout(1000);
	
	                            newServerButton.setText("Annuler ecoute serveur");
	                            newComButton.setEnabled(false);
	                            newClientButton.setEnabled(false);
	                            isListening = true;
	                            Socket connexion = listenConnection(service);
	                            if (connexion == null)
	                                service.close();
	                            else {
	                                System.out.println("Connexion acceptee");
	                                JOptionPane.showMessageDialog(getContentPane(),
	                                                              "Reception d'une connexion client ! Vous avez les blancs");
	                                startNewGame(viewer,new NetPlayer(connexion));
	                            }
	                        }
	                        catch (Exception ex) {
	                            JOptionPane.showMessageDialog(getContentPane(),
	                                                          "Impossible de recevoir une connexion client : "
	                                                                  + ex, "Erreur de connexion",
	                                                          JOptionPane.ERROR_MESSAGE);
	                            ex.printStackTrace();
	                            if (service != null)
	                                service.close();
	                        }
	                        isListening = false;
	                        newServerButton.setText("Reseau (serveur)");
                            newComButton.setEnabled(true);
                            newClientButton.setEnabled(true);
	                        return null;
	                    }
	                };
	                listeningTask.execute();
            	}
            }
        });
        c.weightx=0.0;
        c.gridx=0;
        c.gridy=2;
        newGamePanel.add(newServerButton,c);
        JLabel label = new JLabel("Port d'ecoute : ");
        c.weightx=0.0;
        c.gridx=1;
        c.gridy=2;
        newGamePanel.add(label,c);
        listeningPortField = new JTextField("3456");
        c.weightx=1.0;
        c.gridx=2;
        c.gridy=2;
        newGamePanel.add(listeningPortField,c);

        // Network (client)
        newClientButton = new JButton("Reseau (client)");
        newClientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Integer port = Integer.parseInt(serverPortField.getText());
                    Socket connexion = new Socket(serverIpField.getText(), port);
                    System.out.println("Connexion acceptee");
                    resetBoard();
                    JOptionPane.showMessageDialog(getContentPane(),
                                                  "Connexion au serveur reussie ! Vous avez les noirs");
                    startNewGame(new NetPlayer(connexion),viewer);
                }
                catch (Exception ex) {
                    JOptionPane.showMessageDialog(getContentPane(),
                                                  "Impossible de se connecter au serveur specifie : "
                                                          + ex, "Erreur de connexion",
                                                  JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                    return;
                }
            }
        });
        c.weightx=0.0;
        c.gridx=0;
        c.gridy=3;
        newGamePanel.add(newClientButton,c);
        label = new JLabel("Port : ");
        c.weightx=0.0;
        c.gridx=1;
        c.gridy=3;
        newGamePanel.add(label,c);
        serverPortField = new JTextField("3456");
        c.weightx=1.0;
        c.gridx=2;
        c.gridy=3;
        newGamePanel.add(serverPortField,c);
        label = new JLabel("Serveur IP : ");
        c.weightx=0.0;
        c.gridx=3;
        c.gridy=3;
        newGamePanel.add(label,c);
        serverIpField = new JTextField("fiddler38.dyndns.org");
        c.weightx=1.0;
        c.gridx=4;
        c.gridy=3;
        newGamePanel.add(serverIpField,c);

        container.add(newGamePanel, BorderLayout.PAGE_END);

        updateLabel();
    }

    private void startNewGame(IPlayer white, IPlayer black) {
    	if(getCurrentPlayer() != null)
    		getCurrentPlayer().removeListener(moveListener);
    	
        // Closes all connections with net players
        if (whitePlayer instanceof NetPlayer) {
            try {
                ((NetPlayer)whitePlayer).getConnection().close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (blackPlayer instanceof NetPlayer) {
            try {
                ((NetPlayer)blackPlayer).getConnection().close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Reset plateau
        game = new Game();
        game.initPlateau(plateau);
        viewer.reset();
        viewer.repaint();

    	whitePlayer = white;
    	blackPlayer = black;
        isPlaying = true;
        playNextMove();
    }

    private IPlayerListener moveListener = new IPlayerListener(){
    	public void moveComputed(Move move) {
    		getCurrentPlayer().removeListener(this);
            executeMove(move);
    	}
    };
    
    private void playNextMove(){
    	getCurrentPlayer().addListener(moveListener);
    	getCurrentPlayer().startComputingMove(plateau);
    }

    private void resetBoard() {
        game = new Game();
        game.initPlateau(plateau);
        moveListModel.notifyChange(new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, 0,
                                                     moveList.getComponents().length));
        viewer.repaint();
        updateLabel();
    }

    private void updateLabel() {
        String text = "Les " + (plateau.getTurn() == Couleur.BLANC ? "blancs" : "noirs")
                      + " jouent";
        title.setText(text);
    }

    private final static int DEPTH = 2;

    private void executeMove(Move move) {
		if(move == null){
			JOptionPane.showMessageDialog(getContentPane(), "Une erreur s'est produite pendant le coup");
			isPlaying = false;
			return;
		}

		System.out.println("Executing move : " + move.toString());
        
        game.doMove(move);
        game.initPlateau(plateau);
        moveListModel.notifyChange(new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED,
                                                     game.getNbMoves() - 1, game.getNbMoves()));
        viewer.setCurseur(move.getFinalPos());
        viewer.getPossibleDestinations().clear();
        viewer.repaint();
        if (MoveGenerator.checkMate(plateau)) {
            JOptionPane.showMessageDialog(this, "Echec et mat!");
            return;
        }
        updateLabel();
        
        if(getCurrentPlayer() instanceof NetPlayer){
            ((NetPlayer)getCurrentPlayer()).sendMove(move);
        }
        
        // Start computing the next move in a thread (if the game flag is on)
        if (isPlaying)
            playNextMove();
    }

}
