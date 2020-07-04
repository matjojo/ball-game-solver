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
				if (!isApprovedFourMoveList(previousList, option)) continue;
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
	 * The influencing cases work out like this:
	 * I C C  // consecutive filter does this
	 * C I C  // cannot be replaced by C1 * C2 I so is not filtered out.
	 * C C I  // consecutive filter does this
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
	 *
	 * A list of 4 moves can be of some amount of types.
	 * C = consecutive
	 * N = not influencing
	 * I = influencing
	 *-2-1 0 current
	 * C C C C // by doubles filter
	 * C C C N // by doubles filter
	 * C C N C // by doubles filter
	 * C C N N // by doubles filter
	 * C N C C // by doubles filter
	 * C N C N // by triples filter
	 * C N N C              // can be simplified to C1 * C2 N N
	 * C N N N // can't be simplified
	 * N C C C // by doubles filter
	 * N C C N // by doubles filter
	 * N C N C // by triples filter
	 * N C N N // can't be simplified
	 * N N C C // by doubles filter
	 * N N C N // can't be simplified
	 * N N N C // can't be simplified
	 * N N N N // can't be simplified
	 *
	 * // any other combination with influencing cannot be simplified until we make directional influencer methods
	 *
	 * @return true if this list of moves may be attempted.
	 */
	private static boolean isApprovedFourMoveList(@NotNull EvictingMoveList list, @NotNull Move current) {
		// case C N N C
		if (moveIsConsecutiveTo(list.thirdMostRecent(), current) &&
			!moveInfluences(list.secondMostRecent(), list.thirdMostRecent()) &&
			!moveInfluences(list.secondMostRecent(), current) &&
			!moveInfluences(list.mostRecent(), list.thirdMostRecent()) &&
			!moveInfluences(list.mostRecent(), current)) return false;
		return true;
	}

	/**
	 * NOTE: this is an unordered check. No fancy stuff here.
	 *
	 * @param one nonnull move
	 * @param two nonull move
	 * @return true if the moves influence each other, so if the moves have any pieces in common
	 */
	private static boolean moveInfluences(@NotNull Move one, @NotNull Move two) {
		return one.from == two.from ||
				one.from == two.to  ||
				one.to == two.from  ||
				one.to == two.to;
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
