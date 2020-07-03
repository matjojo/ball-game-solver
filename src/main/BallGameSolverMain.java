package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Stack;

public class BallGameSolverMain {

	/**
	 * The stack is built up from the end of the solution down. So the last move is the deepest element.
	 */
	private static final Stack<Move> solution = new Stack<>();
	private static final int MAX_MOVES = 42;
	// TODO: implement a binary search for the smallest solution or cache the smallest solution and search on?
	private static int amountMoves = 0;

	private static long amountTries = 0;

	public static void main(String[] args) throws IOException {
		ArrayList<BallBucket> accumulatedBuckets = new ArrayList<>();

		System.out.println("MAX_MOVES: " + MAX_MOVES);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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
		if (!solve(toSolve, new Move(-1, -1))) {
			System.out.println("Did not find a solution!");
			return;
		}
		System.out.println("Solution needs to be played bottom up!");

		// TODO: make a printable for Move to make the moves actually matter.
		System.out.println("The solution takes " + amountMoves + " steps.");
		System.out.println("We attempted " + amountTries + " tries.");
	}

	static boolean solve(Board board, Move previous) {
		amountMoves++;
		if (amountMoves > MAX_MOVES) {
			amountMoves--;
			return false;
		}
		for (Move attempt : board.getAllMoves(previous)) {
			amountTries++;
			board.doMove(attempt);
			if (board.solved()) {
				// we built up the solution from the last move up.
				// So this will be the bottom most move and the highest in the console.
//				solution.push(attempt);
				System.out.println("Last step: " + attempt.from + " -> " + attempt.to);
				return true;
			}
			// try deeper
			if (solve(board, previous)) {
				// we built the solution from the last move up.
				// this will be one of the intermediate or first move.
				// which will be on top of the stack and at the bottom of the console.
				System.out.println("Step: " + attempt.from + " -> " + attempt.to);
//				solution.push(attempt);
				return true;
			}
			board.undoMove(attempt);
		}
		amountMoves--;
		return false;
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
