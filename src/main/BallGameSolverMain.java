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
	private static int MAX_MOVES = 70;
	// TODO: implement a binary search for the smallest solution or cache the smallest solution and search on?
	private static int amountMoves = 0;

	private static final int EVICTING_MOVE_LIST_LENGTH = 2;

	private static long amountTries = 0;

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
		MAX_MOVES = resNum;

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
		if (!solve(toSolve, previousMoves)) {
			System.out.println("Did not find a solution!");
			System.out.println("We attempted " + amountTries + " tries.");
			return;
		}
		System.out.println("Solution needs to be played bottom up!");

		// TODO: make a printable for Move to make the moves actually matter.
		System.out.println("The solution takes " + amountMoves + " steps.");
		System.out.println("We attempted " + amountTries + " tries.");
	}

	static boolean solve(Board board, EvictingMoveList previousList) {
		amountMoves++;
		if (amountMoves > MAX_MOVES) {
			amountMoves--;
			return false;
		}
		EvictingMoveList currentPreviousList; // since we can't remove attempts from the top we make copies and edit those
		for (Move attempt : board.getAllMoves(previousList)) {
			amountTries++;
			currentPreviousList = previousList.copy();
			board.doMove(attempt);
			currentPreviousList.add(attempt);
			if (board.solved()) {
				// we built up the solution from the last move up.
				// So this will be the bottom most move and the highest in the console.
//				solution.push(attempt);
				System.out.println("Last step: " + attempt.from + " -> " + attempt.to);
				return true;
			}
			// try deeper
			if (solve(board, currentPreviousList)) {
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
