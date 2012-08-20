package it.polimi.adam.domain;

public class Connection {
	
	private State state;
	private String label;

	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
	public String getLabel() {
		return label;
	}

	public boolean isPossible(String resultAsString) {
		return resultAsString.equals(label);
	}
	

}
