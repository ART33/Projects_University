import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Search {
	Problem problem;

	public Search(Problem problem) { this.problem = problem; }

	//Tree-search methods
	public String BreadthFirstTreeSearch() {
		return TreeSearch(new FrontierFIFO());
	}

	public String DepthFirstTreeSearch() {
		return TreeSearch(new FrontierLIFO());
	}

	public String UniformCostTreeSearch() {
		return TreeSearch(new FrontierPriorityQueue(new ComparatorG()));
	}

	public String GreedyBestFirstTreeSearch() {
		return TreeSearch(new FrontierPriorityQueue(new ComparatorH(problem)));
	}

	public String AstarTreeSearch() {
		return TreeSearch(new FrontierPriorityQueue(new ComparatorF(problem)));
	}

	//Graph-search methods
	public String BreadthFirstGraphSearch() {
		return GraphSearch(new FrontierFIFO());
	}

	public String DepthFirstGraphSearch() {
		return GraphSearch(new FrontierLIFO());
	}

	public String UniformCostGraphSearch() {
		return GraphSearch(new FrontierPriorityQueue(new ComparatorG()));
	}

	public String GreedyBestFirstGraphSearch() {
		return GraphSearch(new FrontierPriorityQueue(new ComparatorH(problem)));
	}

	public String AstarGraphSearch() {
		return GraphSearch(new FrontierPriorityQueue(new ComparatorF(problem)));
	}


	//Iterative deepening, tree-search and graph-search
	public String IterativeDeepeningTreeSearch() {
		int limit = 0;
		while(true) {
			FrontierLIFO FL = new FrontierLIFO();
			String result = TreeSearchDepthLimited(FL, limit);
			if(result != null) {
				return result;
			}
			limit = limit + 1;
		}
	}

	public String IterativeDeepeningGraphSearch() {
		int limit = 0;
		while(true) {
			FrontierLIFO FL = new FrontierLIFO();
			String result = GraphSearchDepthLimited(FL, limit);
			if(result != null) {
				return result;
			}
			limit = limit + 1;
		}
	}

	//For statistics purposes
	int cnt; //count expansions
	List<Node> node_list; //store all nodes ever generated
	Node initialNode; //initial node based on initial state
	//

	private String TreeSearch(Frontier frontier) {
		cnt = 0;
		node_list = new ArrayList<Node>();

		initialNode = MakeNode(problem.initialState);
		node_list.add( initialNode );

		frontier.insert( initialNode );
		while(true) {

			if(frontier.isEmpty())
				return null;

			Node node = frontier.remove();

			if( problem.goal_test(node.state) )
				return Solution(node);

			frontier.insertAll(Expand(node,problem));
			cnt++;
		}
	}

	private String GraphSearch(Frontier frontier) {
		cnt = 0;
		node_list = new ArrayList<Node>();

		initialNode = MakeNode(problem.initialState);
		node_list.add( initialNode );

		Set<Object> explored = new HashSet<Object>(); //empty set
		frontier.insert( initialNode );
		while(true) {

			if(frontier.isEmpty())
				return null;

			Node node = frontier.remove();

			if( problem.goal_test(node.state) )
				return Solution(node);

			if( !explored.contains(node.state) ) {
				explored.add(node.state);
				frontier.insertAll(Expand(node,problem));
				cnt++;
			}
		}
	}

	private String TreeSearchDepthLimited(Frontier frontier, int limit) {
		cnt = 0;
		node_list = new ArrayList<Node>();

		initialNode = MakeNode(problem.initialState);
		node_list.add( initialNode );
		frontier.insert( initialNode );

		while(true) {

			if(frontier.isEmpty())
				return null;

			Node node = frontier.remove();

			if(problem.goal_test(node.state))

				return Solution(node);

			if(node.depth <= limit - 1){
				frontier.insertAll(Expand(node, problem));
				cnt++;
			}
		}
	}

	private String GraphSearchDepthLimited(Frontier frontier, int limit) {
		cnt = 0;
		node_list = new ArrayList<Node>();

		initialNode = MakeNode(problem.initialState);
		node_list.add( initialNode );

		Set<Object> explored = new HashSet<Object>(); //empty set
		frontier.insert( initialNode );
		while(true) {

			if(frontier.isEmpty())
				return null;

			Node node = frontier.remove();

			if( problem.goal_test(node.state) )
				return Solution(node);

			if( !explored.contains(node.state) && (node.depth <= limit - 1)) {
				explored.add(node.state);
				frontier.insertAll(Expand(node,problem));
				cnt++;
			}
		}
	}

	private Node MakeNode(Object state) {
		Node node = new Node();
		node.state = state;
		node.parent_node = null;
		node.path_cost = 0;
		node.depth = 0;
		return node;
	}

	private Set<Node> Expand(Node node, Problem problem) {
		node.order = cnt;

		Set<Node> successors = new HashSet<Node>(); //empty set
		Set<Object> successor_states = problem.getSuccessors(node.state);

		for(Object result : successor_states) {
			Node s = new Node();
			s.state = result;
			s.parent_node = node;
			s.path_cost = node.path_cost + problem.step_cost(node.state, result);
			s.depth = node.depth + 1;
			successors.add(s);

			node_list.add( s );
		}

		return successors;
	}

	//Create a string to print solution.
	private String Solution(Node node) {

		String solution_str = "(cost=" + node.path_cost + ", expansions=" + cnt + ")\t";

		Deque<Object> solution = new ArrayDeque<Object>();
		do {
			solution.push(node.state);
			node = node.parent_node;
		} while(node != null);

		while(!solution.isEmpty())
			solution_str += solution.pop() + " ";

		//PrintTree(initialNode);
		return solution_str;
	}

	private void PrintTree(Node node) {
		double g = node.path_cost;
		double h = problem.h(node.state);
		double f = h + g;

		for(int j = 0; j < node.depth; j++){
			System.out.print("	");
		}
		System.out.print(node.state + " (g=" + g + ", h=" + h + ", f=" + f + ")");
		if(node.order!=-1){
			System.out.println(" order=" +node.order);
		}else{
			System.out.println();
		}

		for(Node n : node_list){
			if(n.parent_node != null) {
				if(n.parent_node== node){
					PrintTree(n);
				}
			}
		}
	}
}
