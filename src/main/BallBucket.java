package main;

public class BallBucket {

	private final Integer[] balls;

	public BallBucket() {
		this.balls = new Integer[4];
	}

	public BallBucket(String[] balls) {
		this();
		for (int i = 0; i < balls.length; i++) {
			this.balls[i] = Integer.parseInt(balls[i]);
		}
	}

	public boolean full() {
		return !(balls[3] == null);
	}

	public boolean empty() {
		return balls[0] == null;
	}

	public int top() {
		if (this.empty()) return -1;
		// TODO: would caching this make it faster?
		for (int i = balls.length - 1; i >= 0; i--) {
			if (balls[i] != null) return balls[i];
		}
		throw new IllegalStateException("Not empty but also no ball?");
	}

	/**
	 * Assumed to have space free, will throw exception when not.
	 *
	 * @param newBall inserted at the topmost empty slot
	 * @throws IllegalStateException when this bucket is full.
	 */
	public void addTop(int newBall) {
		for (int i = 0; i < balls.length; i++) {
			if (balls[i] == null) {
				balls[i] = newBall;
				return;
			}
		}
		throw new IllegalStateException("Tried to add to full ballBucket!");
	}

	/**
	 * Assumes that this bucket has balls in it. Will throw exception when not.
	 *
	 * @return the value of the ball that was at the top.
	 * @throws IllegalStateException When there is no top to remove.
	 */
	public int removeTop() {
		for (int i = balls.length - 1; i >= 0; i--) {
			if (balls[i] != null) {
				int ret = balls[i];
				balls[i] = null;
				return ret;
			}
		}
		throw new IllegalStateException("Removed top from emtpty ballBucket!");
	}

	/**
	 * @return true if this bucket is empty or has 4 the same balls
	 */
	public boolean solved() {
		if (this.empty()) return true; // TODO: test if this is really faster?
		for (int i = 1; i < balls.length; i++) {
			// we know there is a first ball due to not empty, so we can just check the last balls to this one.
			if (!balls[0].equals(balls[i])) return false;
		}
		return true;
	}

	/**
	 * to check if the first three balls are the same and there is no topmost ball
	 *
	 * @return true if three the same balls
	 */
	public boolean hasOnlyThreeSameBalls() {
		if (this.full()) return false;
		if (this.empty()) return false;
		// due to empty we know for sure that balls[0] is nonnull
		return balls[0].equals(balls[1]) && balls[0].equals(balls[2]);
	}

}
