package main;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class EvictingMoveList {

	private final int length;
	private final Move[] backing;

	public EvictingMoveList(int size) {
		this.length = size;
		this.backing = new Move[size];
	}

	@Contract(pure = true)
	private EvictingMoveList(Move @NotNull [] backing) {
		this.backing = backing;
		this.length = backing.length;
	}

	/**
	 * @return the topmost element in this list. So the one most recently added.
	 */
	public Move mostRecent() {
		return getOffsetFromFront(0);
	}

	/**
	 * @return the second most recent Move, so the one before the most recently added.
	 */
	public Move secondMostRecent() {
		return getOffsetFromFront(1);
	}

	/**
	 * @return the third most recent move, so offset -2.
	 */
	public Move thirdMostRecent() {
		return getOffsetFromFront(2);
	}

	public void add(@NotNull Move move) {
		// from the first to one from the last, so if len=3 we do 0 <= 1, 1<= 2, 2<= newMove
		for (int i = 0; i < this.length - 1; i++) { // <size-2 is the same as <=size - 2;
			backing[i] = backing[i+1];
		}
		backing[length -1] = move;
	}

	public EvictingMoveList copy() {
		return new EvictingMoveList(this.backing.clone());
	}

	public Move getOffsetFromFront(int offset) {
		int index = (length - offset) - 1;
		if (index < 0) throw new IllegalArgumentException("Offset too large! MoveList is of length: " + length + ", offset was: " + offset);
		return backing[index];
	}

	public void printContent() {
		for (Move move : backing) {
			if (move == null) {
				System.out.println("null");
				continue;
			}
			System.out.println(move.from + " -> " + move.to);
		}
	}

}
