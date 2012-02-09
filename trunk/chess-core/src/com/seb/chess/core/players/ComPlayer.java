package com.seb.chess.core.players;

import com.seb.chess.core.Move;
import com.seb.chess.core.Ouverture;
import com.seb.chess.core.Plateau;
import com.seb.chess.core.ai.NullEvalProgressMonitor;
import com.seb.chess.core.ai.PlateauEvaluation;

public class ComPlayer extends ThreadPlayer {

	private final int depth;

	private final PlateauEvaluation plateauEval;

	public ComPlayer(int depth) {
		this.depth = depth;
		this.plateauEval = new PlateauEvaluation();
	}

	@Override
	protected Move computeMove(Plateau plateau) {
		Move move = Ouverture.getMove(plateau);
		if (move == null)
			move = plateauEval.getBestMove(plateau, depth,
					new NullEvalProgressMonitor());
		return move;
	}

	@Override
	public String getDescription() {
		return "COMPUTER [Niveau " + depth + "]";
	}
}
