package it.polimi.adam.domain;

import it.polimi.adam.engine.UncertaintyManager;
import it.polimi.adam.model.Requirement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.annotations.SerializedName;

public class EmbeddedModel {

	@SerializedName("initial_state")
	private String initialState;
	private Map<String, Metric> metrics;
	private List<Requirement> requirements;
	private Map<String, State> states;	
	
	public String getInitialState() {
		return initialState;
	}
	
	public Map<String, Metric> getMetrics() {
		return metrics;
	}

	public List<Requirement> getRequirements() {
		return requirements;
	}

	public Map<String, State> getStates() {
		return states;
	}
	
	public Map<String, Object> run(UncertaintyManager uncertaintyManager) throws Exception{
		return run(new HashMap<String, Object>(),uncertaintyManager);
	}
	
	public Map<String, Object> run(Map<String, Object> executionState, UncertaintyManager uncertaintyManager) throws Exception{
		State initialState = states.get(this.initialState);
		execState(initialState, uncertaintyManager, executionState);
		return executionState;
	}
	
	private void execState(State state, UncertaintyManager uncertaintyManager, Map<String, Object> executionState) throws Exception{
		state.run(executionState);
		if(state.isFinalState()){
			return;
		}
		for(State nextState:state.getAvailableNextStates(executionState, uncertaintyManager)){
			execState(nextState, uncertaintyManager, executionState);
			updateExecutionState(uncertaintyManager, nextState);
			break;
		}
	}

	private void updateExecutionState(UncertaintyManager uncertaintyManager, State nextState) {
		uncertaintyManager.updateExecutionValuesForMetric("rt", nextState.getExecutionTime());
		Map<String, Double> impacts = nextState.getImpacts();
		for(Entry<String, Double> impact:impacts.entrySet()){
			if(!impact.getKey().equals("rt")){
				uncertaintyManager.updateExecutionValuesForMetric(impact.getKey(), impact.getValue());
			}
		}
	}
	
	
}
