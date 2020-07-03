package main;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class EvictingMoveList {

	private final int size;
	private final Move[] backing;

	public EvictingMoveList(int size) {
		this.size = size;
		this.backing = new Move[size];
	}

	@Contract(pure = true)
	private EvictingMoveList(Move @NotNull [] backing) {
		this.backing = backing;
		this.size = backing.length;
	}

	public void add(@NotNull Move move) {
		// from the first to one from the last, so if len=3 we do 0 <= 1, 1<= 2, 2<= newMove
		for (int i = 0; i < this.size - 1; i++) { // <size-2 is the same as <=size - 2;
			backing[i] = backing[i+1];
		}
		backing[size-1] = move;
	}

	public EvictingMoveList copy() {
		return new EvictingMoveList(this.backing.clone());
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
