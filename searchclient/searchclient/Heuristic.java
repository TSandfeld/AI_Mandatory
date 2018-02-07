package searchclient;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;

import searchclient.NotImplementedException;

class Tuple<X,Y> {
	public final X x;
	public final Y y;
	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
}

public abstract class Heuristic implements Comparator<Node> {
	HashMap<Character, Tuple<Integer, Integer>> goalPositions = new HashMap<>();
	HashMap<Character, Tuple<Integer, Integer>> boxPositions = new HashMap<>();

	public Heuristic(Node initialState) {
		// Here's a chance to pre-process the static parts of the level.
		findPositions(initialState);
	}

	public void findPositions(Node n) {
		if (!this.goalPositions.isEmpty()) {
			this.goalPositions = new HashMap<>();
			this.boxPositions = new HashMap<>();
		}

		char[][] goals = n.goals;
		char[][] boxes = n.boxes;

		for (int row = 0; row < n.MAX_ROW; row++) {
			for (int col = 0; col < n.MAX_COL; col++) {
				char goalChr = goals[row][col];
				char boxChr = boxes[row][col];

				if ('a' <= goalChr && goalChr <= 'z') { // goal
					this.goalPositions.put(goalChr, new Tuple<>(row, col));
				}

				if ('A' <= boxChr && boxChr <= 'Z') {
					this.boxPositions.put(boxChr, new Tuple<>(row, col));
				}
			}
		}
	}

	public int h(Node n) {
		// Heurstics: Straight-line
		findPositions(n);

		int agentRow = n.agentRow;
		int agentCol = n.agentCol;

		

		throw new NotImplementedException();
	}

	public abstract int f(Node n);

	@Override
	public int compare(Node n1, Node n2) {
		return this.f(n1) - this.f(n2);
	}

	public static class AStar extends Heuristic {
		public AStar(Node initialState) {
			super(initialState);
		}

		@Override
		public int f(Node n) {
			return n.g() + this.h(n);
		}

		@Override
		public String toString() {
			return "A* evaluation";
		}
	}

	public static class WeightedAStar extends Heuristic {
		private int W;

		public WeightedAStar(Node initialState, int W) {
			super(initialState);
			this.W = W;
		}

		@Override
		public int f(Node n) {
			return n.g() + this.W * this.h(n);
		}

		@Override
		public String toString() {
			return String.format("WA*(%d) evaluation", this.W);
		}
	}

	public static class Greedy extends Heuristic {
		public Greedy(Node initialState) {
			super(initialState);
		}

		@Override
		public int f(Node n) {
			return this.h(n);
		}

		@Override
		public String toString() {
			return "Greedy evaluation";
		}
	}
}
