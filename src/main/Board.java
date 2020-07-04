package main;

import org.jetbrains.annotations.NotNull;

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

	private static boolean moveIsConsecutiveTo(Move one, Move two) {
		return moveIsConsecutiveTo(one, two.from);
	}

	/**
	 * @param previousList The previous move, used to ensure more efficient move selection.
	 * @return a list of all the valid, nonconsecutive to the previous, moves
	 */
	@SuppressWarnings("DuplicatedCode")
	public Move[] getAllMoves(EvictingMoveList previousList) {
		// todo: see how the default size influences this?
		ArrayList<Move> result = new ArrayList<>();
		Move option;
		for (int i = 0; i < buckets.length; i++) {      // [i] = from
			if (buckets[i].solved() ||
					moveIsConsecutiveTo(previousList.mostRecent(), i) ||
					buckets[i].hasOnlyThreeSameBalls()) continue;
			// TODO: check if if the order of these checks influences anything?
			for (int j = 0; j < buckets.length; j++) {  // [j] = to
				if (i == j) {
					continue;
				}
				if (buckets[j].full()) continue;
				option = new Move(i, j);
				if (!isApprovedThreeMoveList(previousList, option)) continue;
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
	 * A list of three shapes can come in several flavours, one set we can filter:
	 *
	 * If two of the moves are consecutive and the other does not influence them this can come in three flavours:
	 * C = Consecutive
	 * N = Not influencing
	 * -1 0 current
	 *  N C C   // won't happen due to consecutive filter
	 *  C N C   // can be replace by C1 * C2 N. So we will filter it out here.
	 *  C C N   // won't happen due to consecutive filter
	 * In this case we will approve only one of these setups. Cutting the amount of accepted steps by two thirds.
	 *
	 *
	 * @return true if the list of three moves is approved.
	 */
	private static boolean isApprovedThreeMoveList(EvictingMoveList list, Move current) {
		// case C N C
		if (!moveInfluences(list.mostRecent(), list.secondMostRecent()) &&
			!moveInfluences(list.mostRecent(), current) &&
			moveIsConsecutiveTo(list.secondMostRecent(), current)) {
			return false;
		}
		return true;
	}

	/**
	 * @param first nonnull move
	 * @param second nonull move
	 * @return true if the moves influence each other, so if the moves have any pieces in common
	 */
	private static boolean moveInfluences(@NotNull Move first, @NotNull Move second) {
		return first.from == second.from ||
				first.from == second.to  ||
				first.to == second.from  ||
				first.to == second.to;
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
