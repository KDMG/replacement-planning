package instancegraph;

import java.util.ArrayList;

public class Parallelism {

	private String sourceState;
	private String transition;
	private ArrayList<String> parallelTransitions;

	
	public Parallelism(String transition, String sourceState,
			ArrayList<String> parallelTransitions) {
		super();
		this.sourceState = sourceState;
		this.transition = transition;
		this.parallelTransitions = parallelTransitions;
	}
	
	public String getSourceState() {
		return sourceState;
	}
	public void setSourceState(String sourceState) {
		this.sourceState = sourceState;
	}
	public String getTransition() {
		return transition;
	}
	public void setTransition(String transition) {
		this.transition = transition;
	}
	public ArrayList<String> getParallelTransitions() {
		return parallelTransitions;
	}
	public void setParallelTransitions(ArrayList<String> parallelTransitions) {
		this.parallelTransitions = parallelTransitions;
	}
}
