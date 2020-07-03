package main;

import java.util.ArrayList;

public class Board {

	private final BallBucket[] buckets;

	public Board(BallBucket[] buckets) {
		this.buckets = buckets;
	}

	/**
	 * @param one     assumed to be nonnull
	 * @param twoFrom an int
	 * @return true if this selection of moves is in essence just one.from -> two.to
	 */
	private static boolean moveIsConsecutiveTo(Move one, int twoFrom) {
		return one.to == twoFrom;
	}

	@SuppressWarnings("DuplicatedCode")
	public Move[] getAllMoves() {
		// todo: see how the default size influences this?
		ArrayList<Move> result = new ArrayList<>();
		for (int i = 0; i < buckets.length; i++) {      // [i] = from
			if (buckets[i].empty()) continue;
			for (int j = 0; j < buckets.length; j++) {  // [j] = to
				if (i == j) {
					continue;
				}
				if (buckets[j].full()) continue;
				if (buckets[j].empty()) {
					result.add(new Move(i, j));
					continue;
				}
				if (buckets[i].top() == buckets[j].top()) {
					result.add(new Move(i, j));
					continue;
				}
			}
		}
		return result.toArray(new Move[0]);
	}

	/**
	 * @param prev The previous move, used to ensure more efficient move selection.
	 * @return a list of all the valid, nonconsecutive to the previous, moves
	 */
	@SuppressWarnings("DuplicatedCode")
	public Move[] getAllMoves(Move prev) {
		// todo: see how the default size influences this?
		ArrayList<Move> result = new ArrayList<>();
		for (int i = 0; i < buckets.length; i++) {      // [i] = from
			if (buckets[i].solved() ||
					moveIsConsecutiveTo(prev, i) ||
					buckets[i].hasOnlyThreeSameBalls()) continue;
			// TODO: check if a move is not a reverse of another earlier move and those buckets have not been changed
			// TODO: check if if the order of these checks influences anything?
			for (int j = 0; j < buckets.length; j++) {  // [j] = to
				if (i == j) {
					continue;
				}
				if (buckets[j].full()) continue;
				if (buckets[j].empty()) {
					result.add(new Move(i, j));
					continue;
				}
				if (buckets[i].top() == buckets[j].top()) {
					result.add(new Move(i, j));
					continue;
				}
			}
		}
		return result.toArray(new Move[0]);
	}

	/**
	 * @param move To must not be full, from must not be empty.
	 * @throws IllegalStateException if to or from detect an illegal state
	 */
	public void doMove(Move move) {
		buckets[move.to].addTop(buckets[move.from].removeTop());
	}

	/**
	 * @param move From must not be full, to must not be empty.
	 * @throws IllegalStateException if to or from detect an illegal state
	 */
	public void undoMove(Move move) {
		buckets[move.from].addTop(buckets[move.to].removeTop());
	}

	/**
	 * @return true when all the ballbuckets inside are solved.
	 */
	public boolean solved() {
		for (BallBucket bucket : buckets) {
			// if all buckets are solved the board is solves
			if (!bucket.solved()) return false;
		}
		return true;
	}

}
