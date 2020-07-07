package main;

import java.util.ArrayList;
import java.util.Arrays;

public class SpeedTest {

	private static final int EVICTING_SIZE = 5;

	public static void main(String[] args) {
		System.out.println("Running speedtest version of ballgamesolver.");

		for (int i = 0; i < 10; i++) {
			int maxMoves = 66;
			Board solvable = BoardOf(
					BucketOf(3, 3, 2, 1),
					BucketOf(7, 6, 5, 4),
					BucketOf(10, 9, 4, 8),
					BucketOf(8, 9, 5, 8),
					BucketOf(1, 11, 9, 2),
					BucketOf(2, 10, 2, 11),
					BucketOf(12, 9, 4, 10),
					BucketOf(12, 5, 4, 12),
					BucketOf(1, 3, 7, 6),
					BucketOf(6, 7, 11, 10),
					BucketOf(1, 12, 11, 7),
					BucketOf(8, 5, 3, 6)
			);

			GameSolver solver = new GameSolver();

			long timeBefore = System.nanoTime();
			solver.solve(solvable, dummyList(), maxMoves);
			long timeAfter = System.nanoTime();

			System.out.println("Solving took: " + (timeAfter - timeBefore) + " nanos! ");
		}


	}
	private static EvictingMoveList dummyList() {
		EvictingMoveList result = new EvictingMoveList(EVICTING_SIZE);
		for (int i = 0; i < EVICTING_SIZE; i++) {
			result.add(new Move(-1-i, -1-i));
		}
		return result;
	}

	private static BallBucket BucketOf(int... balls) {
		return new BallBucket(balls);
	}

	private static Board BoardOf(BallBucket... buckets) {
		ArrayList<BallBucket> bucketList = new ArrayList<>(Arrays.asList(buckets));
		bucketList.add(new BallBucket());
		bucketList.add(new BallBucket());
		return new Board(bucketList.toArray(new BallBucket[0]));
	}
}
