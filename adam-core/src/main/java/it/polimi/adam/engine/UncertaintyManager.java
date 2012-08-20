package it.polimi.adam.engine;

import it.polimi.adam.domain.State;

import java.util.List;

public interface UncertaintyManager {

	/**
	 * Return the list of states ordered by the preference
	 * in which they should be tried
	 * @param availableNextStates
	 * @return
	 */
	public List<State> getNextState(List<State> availableNextStates);
	
	public void updateExecutionValuesForMetric(String metric, double value);
	
	public Double getExecutionValue(String metric);
	
}
