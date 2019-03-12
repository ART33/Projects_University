import java.util.HashSet;
import java.util.Set;

public class ProblemWaterJugs extends Problem {

  static final int L = 0;
  static final int M = 1;
  static final int S = 2;
  static final int LL = 12;
  static final int ML = 8;
  static final int SL = 3;

  boolean goal_test(Object state) {
    StateWaterJugs stateJugs = (StateWaterJugs) state;
        if (stateJugs.jugArray[L] == 1 || stateJugs.jugArray[M] == 1 || stateJugs.jugArray[S] == 1) {
            return true;
        }
        return false;
  }

  Set<Object> getSuccessors(Object state) {
    Set<Object> set = new HashSet<Object>();
    StateWaterJugs stateJugs = (StateWaterJugs) state;

    int diff;
    StateWaterJugs stateSuccessor;
    //adding water to large
    stateSuccessor = new StateWaterJugs(stateJugs);
		if (stateSuccessor.jugArray[L] != LL){
			stateSuccessor.jugArray[L] = LL;
			set.add(stateSuccessor);
		}

		//adding water to medium
    stateSuccessor = new StateWaterJugs(stateJugs);
		if (stateSuccessor.jugArray[M] != ML){
			stateSuccessor.jugArray[M] = ML;
			set.add(stateSuccessor);
		}

		//adding water to small
    stateSuccessor = new StateWaterJugs(stateJugs);
		if (stateSuccessor.jugArray[S] != SL){
			stateSuccessor.jugArray[S] = SL;
			set.add(stateSuccessor);
		}

		//removing water from large
		stateSuccessor = new StateWaterJugs(stateJugs);
		if (stateSuccessor.jugArray[L] != 0){
			stateSuccessor.jugArray[L] = 0;
			set.add(stateSuccessor);
		}

		//removing water from medium
    stateSuccessor = new StateWaterJugs(stateJugs);
		if (stateSuccessor.jugArray[M] != 0){
			stateSuccessor.jugArray[M] = 0;
			set.add(stateSuccessor);
		}

		//removing water from small
    stateSuccessor = new StateWaterJugs(stateJugs);
		if (stateSuccessor.jugArray[S] != 0){
			stateSuccessor.jugArray[S] = 0;
			set.add(stateSuccessor);
		}


		//water from large into medium
		stateSuccessor = new StateWaterJugs(stateJugs);
		diff = pourAmount(stateSuccessor.jugArray[L], ML, stateSuccessor.jugArray[M]);
		if (diff != 0){
			stateSuccessor.jugArray[L] -= diff;
			stateSuccessor.jugArray[M] += diff;
			set.add(stateSuccessor);
		}

		//water from large into small
		stateSuccessor = new StateWaterJugs(stateJugs);
		diff = pourAmount(stateSuccessor.jugArray[L], SL, stateSuccessor.jugArray[S]);
		if (diff != 0){
			stateSuccessor.jugArray[L] -= diff;
			stateSuccessor.jugArray[S] += diff;
			set.add(stateSuccessor);
		}

		//water from medium into large
		stateSuccessor = new StateWaterJugs(stateJugs);
		diff = pourAmount(stateSuccessor.jugArray[M], LL, stateSuccessor.jugArray[L]);
		if (diff != 0){
			stateSuccessor.jugArray[M] -= diff;
			stateSuccessor.jugArray[L] += diff;
			set.add(stateSuccessor);
		}

		//water from medium into small
		stateSuccessor = new StateWaterJugs(stateJugs);
		diff = pourAmount(stateSuccessor.jugArray[M], SL, stateSuccessor.jugArray[S]);
		if (diff != 0){
			stateSuccessor.jugArray[M] -= diff;
			stateSuccessor.jugArray[S] += diff;
			set.add(stateSuccessor);
		}

		//water from small into large
		stateSuccessor = new StateWaterJugs(stateJugs);
		diff = pourAmount(stateSuccessor.jugArray[S], LL, stateSuccessor.jugArray[L]);
		if (diff != 0){
			stateSuccessor.jugArray[S] -= diff;
			stateSuccessor.jugArray[L] += diff;
			set.add(stateSuccessor);
		}

		//water from small into medium
		stateSuccessor = new StateWaterJugs(stateJugs);
		diff = pourAmount(stateSuccessor.jugArray[S], ML, stateSuccessor.jugArray[M]);
		if (diff != 0){
			stateSuccessor.jugArray[S] -= diff;
			stateSuccessor.jugArray[M] += diff;
			set.add(stateSuccessor);
		}

    return set;

  }

  private int pourAmount(int from, int limit, int to){
		if (from + to < limit){
			return from;
		} else {
			return limit - to;
		}
	}

  double step_cost(Object fromState, Object toState) {
    StateWaterJugs from = (StateWaterJugs) fromState;
    StateWaterJugs to = (StateWaterJugs) toState;

    for (int i = 0; i < 3; i++){
          if (from.jugArray[i] != to.jugArray[i]) {
            int diff = Math.abs(from.jugArray[i] - to.jugArray[i]);
            return diff;
          }
    }
    return 0;
  }

  public double h(Object state) {
    return 0;
  }

  public static void main(String[] args) throws Exception {
    ProblemWaterJugs problem = new ProblemWaterJugs();
    int[] jugArray = {0,0,0};
    problem.initialState = new StateWaterJugs(jugArray);

    Search search  = new Search(problem);

    System.out.println("BreadthFirstTreeSearch:\t" + search.BreadthFirstTreeSearch());

    System.out.println("BreadthFirstGraphSearch:\t" + search.BreadthFirstGraphSearch() + "\n");

    System.out.println("DepthFirstTreeSearch:\t" + search.DepthFirstTreeSearch());

    System.out.println("DepthFirstGraphSearch:\t" + search.DepthFirstGraphSearch() + "\n");

    System.out.println("UniformCostTreeSearch:\t" + search.UniformCostTreeSearch());

    System.out.println("UniformCostGraphSearch:\t" + search.UniformCostGraphSearch() + "\n");

    System.out.println("IterativeDeepeningTreeSearch:\t" + search.IterativeDeepeningTreeSearch());

    System.out.println("IterativeDeepeningGraphSearch:\t" + search.IterativeDeepeningGraphSearch() + "\n");

  }

}
