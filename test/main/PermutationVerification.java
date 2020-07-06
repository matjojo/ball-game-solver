package main;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * There are a lot of things you can do here, make a list of resultant boards from some naive implementation and ensure you hit every single one.
 * Make a list of MoveSets that are possible to hit and then collapse those down and check if you get the same amount of moves.
 *      This one would be harder since you don't know how you can verifiably collapse this set.
 * The easiest but not bulletproof fix is to just have a whole host of known solvable boards and try those.
 */
public class PermutationVerification {
	private static final int EVICTING_SIZE = 3;

	@Test
	public void testSolvedBoards() {
		Board solved = BoardOf(BucketOf());
		assertCanBeSolved(solved);
		solved = BoardOf(BucketOf(1,1,1,1));
		assertCanBeSolved(solved);
		solved = BoardOf(BucketOf(1,1,1,1), BucketOf(2,2,2,2));
		assertCanBeSolved(solved);
		solved = BoardOf(BucketOf(1,1,1,1), BucketOf(2,2,2,2), BucketOf(3,3,3,3));
		assertCanBeSolved(solved);
	}

	@Test
	public void testOneMoveBoards() {
		Board oneMoveAway = BoardOf(BucketOf(1,1,1), BucketOf(1));
		assertCanBeSolved(oneMoveAway);
		oneMoveAway = BoardOf(BucketOf(1,1,1,1), BucketOf(2,2,2), BucketOf(2));
		assertCanBeSolved(oneMoveAway);
		oneMoveAway = BoardOf(BucketOf(1,1,1), BucketOf(2,2,2,2), BucketOf(1));
		assertCanBeSolved(oneMoveAway);
	}

	@Test
	public void testTwoMoveBoards() {
		Board twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(1,1));
		assertCanBeSolved(twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(1), BucketOf(1));
		assertCanBeSolved(twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(2,2,2,2), BucketOf(1,1), BucketOf(1), BucketOf(1));
		assertCanBeSolved(twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(2,2,2,2), BucketOf(1), BucketOf(1));
		assertCanBeSolved(twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(1), BucketOf(2,2,2,2), BucketOf(1));
		assertCanBeSolved(twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(1), BucketOf(1), BucketOf(2,2,2,2));
		assertCanBeSolved(twoMovesAway);
	}

	@Test
	public void testManyMoveBoards() {
		Board manyAway = BoardOf(BucketOf(1,1,1,2), BucketOf(2,2,2,1));
		assertCanBeSolved(manyAway);
		manyAway = BoardOf(
				BucketOf(2, 1, 1, 1),
				BucketOf(2, 3, 3, 3),
				BucketOf(2, 4, 4, 4),
				BucketOf(2, 1, 3, 4));
		assertCanBeSolved(manyAway);
	}


	// *********** //
	// unsolvables //
	// *********** //
	@Test
	public void testSimpleUnsolvables() {
		Board unsolvable = BoardOf(BucketOf(1));
		assertCantBeSolved(unsolvable);
		unsolvable = BoardOf(BucketOf(1,1));
		assertCantBeSolved(unsolvable);
		unsolvable = BoardOf(BucketOf(1,1,1));
		assertCantBeSolved(unsolvable);
		unsolvable = BoardOf(BucketOf(1,2));
		assertCantBeSolved(unsolvable);
		unsolvable = BoardOf(BucketOf(1,2,3));
		assertCantBeSolved(unsolvable);
		unsolvable = BoardOf(BucketOf(1,2,3,4));
		assertCantBeSolved(unsolvable);
	}

	private static void assertCantBeSolved(Board shouldBeSolved) {
		Assertions.assertFalse(BallGameSolverMain.solve(shouldBeSolved, dummyList()));
	}

	private static void assertCanBeSolved(Board shouldBeSolved) {
		Assertions.assertTrue(BallGameSolverMain.solve(shouldBeSolved, dummyList()));
	}

	private static EvictingMoveList dummyList() {
		EvictingMoveList result = new EvictingMoveList(EVICTING_SIZE);
		for (int i = 0; i < EVICTING_SIZE; i++) {
			result.add(new Move(-1-i, -1-i));
		}
		return result;
	}

	private static BallBucket BucketOf(Integer... balls) {
		return new BallBucket(balls);
	}

	private static Board BoardOf(BallBucket... buckets) {
		ArrayList<BallBucket> bucketList = new ArrayList<>(Arrays.asList(buckets));
		bucketList.add(new BallBucket());
		bucketList.add(new BallBucket());
		return new Board(bucketList.toArray(new BallBucket[0]));
	}
}
