package main;

import java.util.Stack;

public class GameSolver {

	private Stack<Move> solution;
	private int maxMoves;
	private int amountMoves;
	private long amountTries;

	public Stack<Move> getSolution() {
		return solution;
	}

	public int getAmountMoves() {
		return amountMoves;
	}

	public long getAmountTries() {
		return amountTries;
	}

	// TODO: investigate how much of the memory and speed footprint is from this movelist and see if we may be better off using a stack with multiple peeks?
	public boolean solve(Board board, EvictingMoveList previousList, int maxMoves) {
		this.solution = new Stack<>(); // reset the values stored
		this.maxMoves = maxMoves;
		this.amountMoves = 0;
		this.amountTries = 0;
		return _solve(board, previousList);
	}

	private boolean _solve(Board board, EvictingMoveList previousList) {
		if (board.solved()) { // make sure we actually call solved boards solved
			return true;
		}
		amountMoves++;
		if (amountMoves > maxMoves) {
			amountMoves--;
			return false;
		}
		EvictingMoveList currentPreviousList; // since we can't remove attempts from the top we make copies and edit those
		for (Move attempt : board.getAllMoves(previousList)) {
			amountTries++;
			currentPreviousList = previousList.copy();

			board.doMove(attempt);
			currentPreviousList.add(attempt);
			// try deeper
			if (_solve(board, currentPreviousList)) {
				// we built the solution from the last move up.
				// this will be one of the intermediate or first move.
				// which will be on top of the stack and at the bottom of the console.
				solution.push(attempt);
				return true;
			}
			board.undoMove(attempt);
		}
		amountMoves--;
		return false;
	}

}
