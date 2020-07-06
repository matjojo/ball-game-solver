package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

public class BallGameSolverMain {

	/**
	 * The stack is built up from the end of the solution down. So the last move is the deepest element.
	 */
	// TODO: implement a binary search for the smallest solution or cache the smallest solution and search on?
	private static final int EVICTING_MOVE_LIST_LENGTH = 3;

	// TODO: make this into threads that start with any one of the first possible moves already done.

	public static void main(String[] args) throws IOException {
		ArrayList<BallBucket> accumulatedBuckets = new ArrayList<>();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String res;
		Integer resNum;
		System.out.print("What should the max number of moves tried be? ");
		do {
			res = br.readLine();
			try {
				resNum = Integer.parseInt(res);
			} catch (NumberFormatException ignored) {
				System.out.print("Couldn't parse number, try again: ");
				resNum = null;
			}
		} while (resNum == null);
		System.out.println("Set max num to " + resNum);
		int maxMoves = resNum;

		System.out.println("Enter numbers with spaces in between (left is bottom): ");
		String s;
		while (!"".equals(s = br.readLine())) {
			String[] numbers = s.split(" ");
			if (numbers.length != 4) {
				System.out.println("Invalid amount of numbers, try again.");
				continue;
			}
			BallBucket ballBucket = new BallBucket(numbers);
			accumulatedBuckets.add(ballBucket);
		}
		System.out.println("Found " + accumulatedBuckets.size() + " nonempty ballbuckets.");
		accumulatedBuckets.add(new BallBucket());
		accumulatedBuckets.add(new BallBucket());

		Board toSolve = new Board(accumulatedBuckets.toArray(new BallBucket[0]));

		// -1, -1  is an impossible move. To make sure that none of the first moves are the opposite of their 'previous' move.
		EvictingMoveList previousMoves = new EvictingMoveList(EVICTING_MOVE_LIST_LENGTH);
		for (int i = 0; i < EVICTING_MOVE_LIST_LENGTH; i++) {
			previousMoves.add(new Move(-1-i, -1-i)); // these moves may not be consecutive
		}

		GameSolver solver = new GameSolver();

		if (!solver.solve(toSolve, previousMoves, maxMoves)) {
			System.out.println("Did not find a solution!");
			System.out.println("We attempted " + solver.getAmountTries() + " tries.");
			return;
		}
		System.out.println("The solution takes " + solver.getAmountMoves() + " steps.");
		System.out.println("We attempted " + solver.getAmountTries() + " tries.");
		printSolution(solver.getSolution());
	}

	public static void printSolution(Stack<Move> solution) {
		System.out.println("Remember, solutions are played top down.");
		try {
			Move current;
			while ((current = solution.pop()) != null) {
				System.out.println(current);
			}
		} catch (EmptyStackException ignored) {}
	}

	/**
	 * More often than not the more wide and faster isConsecutiveMove should be enough.
	 *
	 * @param first  some nonnull move
	 * @param second some nonnull move
	 * @return true if the second move will undo the first.
	 */
	static boolean isReverseMove(Move first, Move second) {
		return first.from == second.to && first.to == second.from;
	}

	/**
	 * Note that reverse moves are a subset of the consecutive moves.
	 *
	 * @param first  some nonnull move
	 * @param second some nonnull move
	 * @return true if the second move moves the same ball as the first did.
	 */
	static boolean isConsecutiveMove(Move first, Move second) {
		return first.to == second.from;
	}

}
