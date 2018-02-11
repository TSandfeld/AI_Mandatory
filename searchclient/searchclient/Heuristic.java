package searchclient;

import java.util.Comparator;
import java.util.HashMap;

class Tuple<X,Y> {
	public final X x;
	public final Y y;
	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
}

public abstract class Heuristic implements Comparator<Node> {
	HashMap<String, Tuple<Integer, Integer>> goalPositions = new HashMap<>();
	HashMap<String, Tuple<Integer, Integer>> boxPositions = new HashMap<>();

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
					this.goalPositions.put(goalChr + row + "" + col, new Tuple<>(row, col));
				}

				if ('A' <= boxChr && boxChr <= 'Z') {
					this.boxPositions.put(boxChr + row + "" + col, new Tuple<>(row, col));
				}
			}
		}
	}

	public int findDistances(Node n) {
		HashMap<String, Tuple<Integer, Integer>> goalToBoxesDistances = new HashMap<>();

		for (String goalKey : this.goalPositions.keySet()) {
			String currentMinName = "NaN";
			Tuple<Integer, Integer> currentMinDistanceBoxTuple = new Tuple<>(0,0);
			int currentMinDistance = (int) Math.pow(10,5);

			Tuple<Integer, Integer> goalPosition = this.goalPositions.get(goalKey);
			int a = goalPosition.x;
			int b = goalPosition.y;

			for (String boxKey : this.boxPositions.keySet()) {
				Tuple<Integer, Integer> boxPosition = this.boxPositions.get(boxKey);
				int c = boxPosition.x;
				int d = boxPosition.y;

				int manhattanDistance = Math.abs(a - c) + Math.abs(b - d);

				if (manhattanDistance < currentMinDistance) {
					currentMinName = goalKey + boxKey;
					currentMinDistance = manhattanDistance;
					currentMinDistanceBoxTuple = new Tuple<>(c,d);
				}
			}

			goalToBoxesDistances.put(currentMinName, currentMinDistanceBoxTuple);
		}

		int sum = 0;
		for (String distanceKey : goalToBoxesDistances.keySet()) {
			Tuple<Integer, Integer> boxPosition = goalToBoxesDistances.get(distanceKey);

			int agentToBoxManhattanDistance = Math.abs(n.agentRow - boxPosition.x) - Math.abs(n.agentCol - boxPosition.y);

			sum += agentToBoxManhattanDistance;
		}

		return sum;
	}

	public int h(Node n) {
		// Heuristics: Manhattan
		findPositions(n);

		return findDistances(n);
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
