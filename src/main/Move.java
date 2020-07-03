package main;

public class Move {
	public int from;
	public int to;

	public Move(int from, int to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Move)) return false;
		Move other = (Move) obj;
		return this.from == other.from && this.to == other.to;
	}
}
