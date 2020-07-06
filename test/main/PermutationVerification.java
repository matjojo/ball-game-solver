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
		assertCanBeSolved(5, solved);
		solved = BoardOf(BucketOf(1,1,1,1));
		assertCanBeSolved(5, solved);
		solved = BoardOf(BucketOf(1,1,1,1), BucketOf(2,2,2,2));
		assertCanBeSolved(5, solved);
		solved = BoardOf(BucketOf(1,1,1,1), BucketOf(2,2,2,2), BucketOf(3,3,3,3));
		assertCanBeSolved(5, solved);
	}

	@Test
	public void testOneMoveBoards() {
		GameSolver solver = new GameSolver();
		Board oneMoveAway = BoardOf(BucketOf(1,1,1), BucketOf(1));
		assertCanBeSolved(1, oneMoveAway);
		oneMoveAway = BoardOf(BucketOf(1,1,1,1), BucketOf(2,2,2), BucketOf(2));
		assertCanBeSolved(1, oneMoveAway);
		oneMoveAway = BoardOf(BucketOf(1,1,1), BucketOf(2,2,2,2), BucketOf(1));
		assertCanBeSolved(1, oneMoveAway);
	}

	@Test
	public void testTwoMoveBoards() {
		Board twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(1,1));
		assertCanBeSolved(2, twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(1), BucketOf(1));
		assertCanBeSolved(2, twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(2,2,2,2), BucketOf(1,1), BucketOf(1), BucketOf(1));
		assertCanBeSolved(2, twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(2,2,2,2), BucketOf(1), BucketOf(1));
		assertCanBeSolved(2, twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(1), BucketOf(2,2,2,2), BucketOf(1));
		assertCanBeSolved(2, twoMovesAway);
		twoMovesAway = BoardOf(BucketOf(1,1), BucketOf(1), BucketOf(1), BucketOf(2,2,2,2));
		assertCanBeSolved(2, twoMovesAway);
	}

	@Test
	public void testManyMoveBoards() {
		Board manyAway = BoardOf(BucketOf(1,1,1,2), BucketOf(2,2,2,1));
		assertCanBeSolved(20, manyAway);

		manyAway = BoardOf(
				BucketOf(2, 1, 1, 1),
				BucketOf(2, 3, 3, 3),
				BucketOf(2, 4, 4, 4),
				BucketOf(2, 1, 3, 4));
		assertCanBeSolved(20, manyAway);

		manyAway = BoardOf(
				BucketOf(7, 4, 5, 2),
				BucketOf(1, 2, 10, 6),
				BucketOf(10, 10, 2, 8),
				BucketOf(2, 4, 3, 7),
				BucketOf(6, 6, 4, 5),
				BucketOf(3, 6, 1, 7),
				BucketOf(5, 5, 8, 3),
				BucketOf(4, 1, 8, 10),
				BucketOf(3, 1, 7, 8)
		);
		assertCanBeSolved(50, manyAway);
	}

	// *********** //
	// unsolvables //
	// *********** //
	@Test
	public void testSimpleUnsolvables() {
		Board unsolvable = BoardOf(BucketOf(1));
		assertCantBeSolved(10, unsolvable);
		unsolvable = BoardOf(BucketOf(1,1));
		assertCantBeSolved(10, unsolvable);
		unsolvable = BoardOf(BucketOf(1,1,1));
		assertCantBeSolved(10, unsolvable);
		unsolvable = BoardOf(BucketOf(1,2));
		assertCantBeSolved(10, unsolvable);
		unsolvable = BoardOf(BucketOf(1,2,3));
		assertCantBeSolved(10, unsolvable);
		unsolvable = BoardOf(BucketOf(1,2,3,4));
		assertCantBeSolved(10 , unsolvable);
	}

	private static void assertCantBeSolved(int limit, Board shouldBeSolved) {
		Assertions.assertFalse(new GameSolver().solve(shouldBeSolved, dummyList(), limit));
	}

	private static void assertCanBeSolved(int limit, Board shouldBeSolved) {
		Assertions.assertTrue(new GameSolver().solve(shouldBeSolved, dummyList(), limit));
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
