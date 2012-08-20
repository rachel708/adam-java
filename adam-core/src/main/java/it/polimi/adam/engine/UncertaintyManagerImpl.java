package it.polimi.adam.engine;

import it.polimi.adam.domain.EmbeddedModel;
import it.polimi.adam.domain.Metric;
import it.polimi.adam.domain.State;
import it.polimi.adam.model.Requirement;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UncertaintyManagerImpl implements UncertaintyManager {

	private List<Requirement> requirements;
	private Map<String, Metric> metrics;
	private Map<String, Double> currentExecutionValues;
	
	
	protected UncertaintyManagerImpl() {
		currentExecutionValues = new HashMap<String, Double>();
	}

	public UncertaintyManagerImpl(EmbeddedModel model) {
		this();
		this.requirements = model.getRequirements();
		this.metrics = model.getMetrics();
	}

	public List<State> getNextState(List<State> availableNextStates) {

		if(availableNextStates == null || availableNextStates.isEmpty()){
			return availableNextStates;
		}
		
		for (State state : availableNextStates) {
			state.resetProbability();
		}
		
		for (State state : availableNextStates) {
			state.calculateProbabilities(requirements, availableNextStates);
		}
		
		Collections.sort(availableNextStates, new StateComparator());
		return availableNextStates;
	}

	public void updateExecutionValuesForMetric(String metricName, double value) {
		
		Metric metric = metrics.get(metricName);
		if(metric == null){
			return;
		}
		Double currentValue = currentExecutionValues.get(metricName);
		if("+".equals(metric.getAggregator())){
			if(currentValue == null){
				currentValue = 0d;
			}
			currentValue = currentValue+value;
		}
		else if("*".equals(metric.getAggregator())){
			if(currentValue == null){
				currentValue = 1d;
			}
			currentValue = currentValue*value;
		}
		
		currentExecutionValues.put(metricName, currentValue);
		
	}

	class StateComparator implements Comparator<State> {
		
		public int compare(State s1, State s2) {

			Double probabilityS1 = s1.getProbabilityForEssentialRequirements();
			Double probabilityS2 = s2.getProbabilityForEssentialRequirements();

			int compareResult = compare(probabilityS1, probabilityS2);
			if(compareResult != 0){//their have different probabilities for essential metrics
				return compareResult;
			}
			
			//If their probabilities for essential metrics are the same, consider all metrics
			probabilityS1 = s1.getProbabilityForRequirements();
			probabilityS2 = s2.getProbabilityForRequirements();

			return compare(probabilityS1, probabilityS2);
		}
		
		private int compare(double v1, double v2){
			// As I want the list to be ordered in reverse order, a greater
			// probability is "less"
			if (v1 > v2) {
				return -1;
			}
			if (v1 < v2) {
				return 1;
			}
			return 0;
		}

	}

	public Double getExecutionValue(String metric) {
		return currentExecutionValues.get(metric);
	}

}
