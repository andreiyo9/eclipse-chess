package com.seb.chess.ui;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.seb.chess.core.ChessCoreLog;
import com.seb.chess.core.Game;
import com.seb.chess.core.Move;
import com.seb.chess.core.MoveGenerator;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.Position;
import com.seb.chess.core.Utils;
import com.seb.chess.core.Piece.Couleur;
import com.seb.chess.core.ai.IEvalProgressMonitor;
import com.seb.chess.core.players.IPlayer;
import com.seb.chess.core.players.IPlayerListener;

public class ChessView extends ViewPart {

    public static final String ID = "com.seb.chess.ui.view";

    private Game game = new Game();
    
    private static IPlayer whitePlayer, blackPlayer;

    private Label blancPlayerLabel, blancPlayerDesc, noirPlayerLabel, noirPlayerDesc;

    private final Plateau plateau = new Plateau();

    private PlateauViewer plateauViewer;

    private Long noirTime = new Long(0), blancTime = new Long(0);

    private Timer timer;

    private Text noirTimeField, blancTimeField;

    private ProgressBar noirProgressBar, blancProgressBar;

    private Button noirPlayButton, blancPlayButton;
    
    private boolean isPlaying;

    @Override
    public void createPartControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, false));

        Composite rightComposite = new Composite(composite, SWT.NONE);
        rightComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        rightComposite.setLayout(new GridLayout());

        Composite topComposite = new Composite(rightComposite, SWT.BORDER);
        topComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        topComposite.setLayout(new GridLayout(6, false));

        noirPlayerLabel = new Label(topComposite, SWT.NONE);
        noirPlayerLabel.setLayoutData(new GridData());
        noirPlayerLabel.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                if (plateauViewer.getPlateau() != null
                    && plateauViewer.getPlateau().getTurn() == Couleur.NOIR) {
                    Image cursor = SharedImages.getImage(SharedImages.CURSOR);
                    e.gc.drawImage(cursor, 0, 0, cursor.getBounds().width,
                                   cursor.getBounds().height, 0, 0,
                                   noirPlayerLabel.getBounds().width - 1,
                                   noirPlayerLabel.getBounds().height - 1);
                }
            }
        });

        noirPlayerDesc = new Label(topComposite, SWT.NONE);
        noirPlayerDesc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Label clockLabel = new Label(topComposite, SWT.NONE);
        clockLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        clockLabel.setImage(SharedImages.getImage(SharedImages.CLOCK));

        noirTimeField = new Text(topComposite, SWT.READ_ONLY | SWT.BORDER);
        noirTimeField.setLayoutData(new GridData());

        noirProgressBar = new ProgressBar(topComposite, SWT.NONE);
        noirProgressBar.setLayoutData(new GridData());

        noirPlayButton = new Button(topComposite, SWT.PUSH);
        noirPlayButton.setLayoutData(new GridData());
        noirPlayButton.setText("Play!");

        plateauViewer = new PlateauViewer(rightComposite, SWT.BORDER);
        plateauViewer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        plateauViewer.setPlateau(plateau);
        // plateauCanvas.setPlateau(plateau);

        Composite bottomComposite = new Composite(rightComposite, SWT.BORDER);
        bottomComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        bottomComposite.setLayout(new GridLayout(6, false));

        blancPlayerLabel = new Label(bottomComposite, SWT.NONE);
        blancPlayerLabel.setLayoutData(new GridData());
        blancPlayerLabel.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
                if (plateau != null && plateau.getTurn() == Couleur.BLANC) {
                    Image cursor = SharedImages.getImage(SharedImages.CURSOR);
                    e.gc.drawImage(cursor, 0, 0, cursor.getBounds().width,
                                   cursor.getBounds().height, 0, 0,
                                   blancPlayerLabel.getBounds().width - 1,
                                   blancPlayerLabel.getBounds().height - 1);
                }
            }
        });
        blancPlayerDesc = new Label(bottomComposite, SWT.NONE);
        blancPlayerDesc.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        clockLabel = new Label(bottomComposite, SWT.NONE);
        clockLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
        clockLabel.setImage(SharedImages.getImage(SharedImages.CLOCK));

        blancTimeField = new Text(bottomComposite, SWT.READ_ONLY | SWT.BORDER);
        blancTimeField.setLayoutData(new GridData());

        blancProgressBar = new ProgressBar(bottomComposite, SWT.NONE);
        blancProgressBar.setLayoutData(new GridData());

        blancPlayButton = new Button(bottomComposite, SWT.PUSH);
        blancPlayButton.setLayoutData(new GridData());
        blancPlayButton.setText("Play!");

        resetTimer();

        updatePlayerButtons();

        updatePlateau();

        fillToolBar();
    }

    public void newGame(IPlayer white, IPlayer black) {
        game = new Game();
        game.initPlateau(plateau);
        plateauViewer.redraw();
        resetTimer();
        updatePlayerButtons();
        updatePlateau();
        
        if(white instanceof HumanPlayer)
        	((HumanPlayer)white).setPlateauViewer(plateauViewer);
        if(black instanceof HumanPlayer)
        	((HumanPlayer)black).setPlateauViewer(plateauViewer);
        
        startNewGame(white,black);
    }

    private void startNewGame(IPlayer white, IPlayer black) {
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

    private IPlayer getCurrentPlayer(){
        return (plateau.getTurn() == Couleur.BLANC ? whitePlayer
                                                   : blackPlayer);
    }

    private void executeMove(final Move move) {
        ChessCoreLog.logDebug(Utils.not(plateau.getTurn()) + " : " + move);
        game.doMove(move);
        game.initPlateau(plateau);
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                plateauViewer.redraw();
                plateauViewer.setCurseur(move.getFinalPos());
                Collection<Position> list = new ArrayList<Position>();
                list.add(move.getInitialPos());
                plateauViewer.setPossibleDestinations(list);
            }
        });
        if (MoveGenerator.checkMate(plateau)) {
            ChessCoreLog.logDebug("### Echec et Mat ###");
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    MessageDialog.openInformation(
                                                  ChessView.this.getViewSite().getShell(),
                                                  "Echec et mat!",
                                                  "Les "
                                                          + (plateau.getTurn() == Couleur.BLANC
                                                                                               ? "noirs"
                                                                                               : "blancs")
                                                          + " gagnent.");
                }
            });
            return;
        }
        else {
            if (MoveGenerator.isEchec(plateau, plateau.getTurn()))
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        MessageDialog.openInformation(
                                                      ChessView.this.getViewSite().getShell(),
                                                      "Echec!",
                                                      "Le roi "
                                                              + (plateau.getTurn() == Couleur.BLANC
                                                                                                   ? "blanc"
                                                                                                   : "noir")
                                                              + " est en échec.");
                    }
                });
        }

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                updatePlateau();
            }
        });

        if (timer == null) {
            timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (plateau != null) {
                        if (plateau.getTurn() == Couleur.BLANC)
                            blancTime += 1000;
                        else
                            noirTime += 1000;
                        Display.getDefault().asyncExec(new Runnable() {
                            public void run() {
                                updateTimeLabels();
                            };
                        });
                    }
                }
            }, 0, 1000);
        }
        playNextMove();
    }

    private void resetTimer() {
        if (timer != null)
            timer.cancel();
        timer = null;
        noirTime = new Long(0);
        blancTime = new Long(0);
        updateTimeLabels();
    }

    private static final long timeOffset = 60 * 60 * 1000;

    private void updateTimeLabels() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                IFileEditorMapping[] mappings = PlatformUI.getWorkbench().getEditorRegistry().getFileEditorMappings();
                mappings.clone();
            }
        });
        if (!noirTimeField.isDisposed())
            noirTimeField.setText(MessageFormat.format("{0,time}", new Object[] { noirTime
                                                                                  - timeOffset }));
        if (!blancTimeField.isDisposed())
            blancTimeField.setText(MessageFormat.format("{0,time}", new Object[] { blancTime
                                                                                   - timeOffset }));
    }

    protected void fillToolBar() {
        // IToolBarManager manager =
        // getViewSite().getActionBars().getToolBarManager();
    }

    private class ThinkProgressMonitor implements IProgressMonitor {

        private boolean canceled = false;

        private int worked = 0;

        private final ProgressBar progressBar;

        private final Button playButton;

        private SelectionAdapter cancelListener = new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setCanceled(true);
            }
        };

        public ThinkProgressMonitor(Couleur couleur) {
            if (couleur == Couleur.NOIR) {
                progressBar = noirProgressBar;
                playButton = noirPlayButton;
            }
            else {
                progressBar = blancProgressBar;
                playButton = blancPlayButton;
            }
        }

        @Override
        public void beginTask(String name, final int totalWork) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    progressBar.setSelection(0);
                    progressBar.setMinimum(0);
                    progressBar.setMaximum(totalWork);
                    playButton.addSelectionListener(cancelListener);
                    canceled = false;
                    worked = 0;
                }
            });
        }

        @Override
        public void done() {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    progressBar.setSelection(0);
                    noirPlayButton.removeSelectionListener(cancelListener);
                    playButton.removeSelectionListener(cancelListener);
                }
            });
        }

        @Override
        public void internalWorked(double work) {
        }

        @Override
        public boolean isCanceled() {
            return canceled;
        }

        @Override
        public void setCanceled(boolean value) {
            canceled = true;
        }

        @Override
        public void setTaskName(String name) {
        }

        @Override
        public void subTask(String name) {
        }

        @Override
        public void worked(int work) {
            worked += work;
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    progressBar.setSelection(worked);
                }
            });
        }

    }

    protected void updatePlateau() {
        blancPlayerLabel.redraw();
        noirPlayerLabel.redraw();
    }

    protected void updatePlayerButtons() {
/*        blancPlayerLabel.setImage(whitePlayer.getImage());
        noirPlayerLabel.setImage(blackPlayer.getImage());
        blancPlayerDesc.setText(whitePlayer.getName());
        noirPlayerDesc.setText(blackPlayer.getName());*/
    }

    @Override
    public void setFocus() {
    }

    @Override
    public void dispose() {
        if (timer != null)
            timer.cancel();
        super.dispose();
    }

    private Map<Move, Move> bestMoveCache = new HashMap<Move, Move>();

/*    private Job preComputeJob = new Job("Precomputing best move") {
        protected IStatus run(IProgressMonitor monitor) {
            Collection<Move> possibleMoves = MoveGenerator.getPossibleMoves(plateau);
            monitor.beginTask("Precomputing best move...", possibleMoves.size());
            bestMoveCache.clear();
            int depth;
            if (plateau.getTurn() == Couleur.BLANC)
                depth = ((ComPlayer)ChessPlugin.noirPlayer).getLevel();
            else
                depth = ((ComPlayer)ChessPlugin.blancPlayer).getLevel();
            for (Move move : possibleMoves) {
                if (monitor.isCanceled())
                    return Status.CANCEL_STATUS;
                ChessCoreLog.logDebug("Pre-calcul pour le coup " + move);
                Plateau p = new Plateau(plateau);
                p.executeMove(move);
                Move bestMove = PlateauEvaluation.getBestMove(p, depth,
                                                              new EvalProgressMonitor(new SubProgressMonitor(monitor, 1)));
                if (!monitor.isCanceled())
                    bestMoveCache.put(move, bestMove);
            }
            return Status.OK_STATUS;
        };
    };*/

    private static class EvalProgressMonitor implements IEvalProgressMonitor{

        private IProgressMonitor monitor;
        
        public EvalProgressMonitor(IProgressMonitor monitor){
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
        
        @Override
        public void setTaskName(String name) {
            monitor.setTaskName(name);
        }
    }
}
