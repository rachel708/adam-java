package it.polimi.adam.domain;

import it.polimi.adam.domain.Metric.Limit;
import it.polimi.adam.engine.UncertaintyManager;
import it.polimi.adam.engine.util.Reflection;
import it.polimi.adam.model.Requirement;
import it.polimi.adam.model.Requirement.Policy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.annotations.SerializedName;

public class State {
	
	private String name;
	private Map<String, Double> impacts;
	@SerializedName("transition_condition")
	private String transitionCondition;
	private List<Connection> connections;
	private Map<String, Interval> intervals;
	
	//Used when choosing between states
	private Map<Requirement,Double> probabilities;
	private long executionTime;
	
	public State(){
		 probabilities = new HashMap<Requirement, Double>();		
	}
	
	public State(Map<String, Interval> intervals){
		this.intervals = intervals;
	}
	
	
	public String getName() {
		return name;
	}

	public Map<String, Double> getImpacts() {
		return impacts;
	}

	public String getTransitionCondition() {
		return transitionCondition;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public Map<String, Interval> getIntervals() {
		return intervals;
	}
	
	public long getExecutionTime() {
		return executionTime;
	}
	
	public void setIntervals(Map<String, Interval> intervals) {
		this.intervals = intervals;
	}

	public boolean hasTransitionCondition(){
		return transitionCondition != null && !transitionCondition.isEmpty();
	}
	
	public void run(Map<String, Object> executionState) throws Exception{
		long begin = System.nanoTime();
		Reflection.execMethod(name, executionState);
		long end = System.nanoTime();
		this.executionTime = (end - begin);
	}
	
	public List<State> getAvailableNextStates(Map<String, Object> executionState, UncertaintyManager uncertaintyManager) throws Exception{
		if(isFinalState()){
			return Collections.emptyList();
		}
		List<State> possibleNextStates = new ArrayList<State>();
		if(hasTransitionCondition()){
			Object result = Reflection.execMethod(transitionCondition, executionState);			
			String resultAsString = null;
			if(result != null){
				resultAsString = result.toString();
				for(Connection connection:connections){
					if(connection.isPossible(resultAsString)){
						possibleNextStates.add(connection.getState());	
					}
				}
			}				
		}
		else{
			for(Connection connection:connections){
				possibleNextStates.add(connection.getState());
			}			
		}
		if(possibleNextStates.isEmpty()){
			return Collections.emptyList();
		}
		
		List<State> nextStatesOrdered = uncertaintyManager.getNextState(possibleNextStates);
		return nextStatesOrdered;		
	}

	
	public void resetProbability(){
		probabilities.clear();
	}
	
	public boolean isFinalState() {
		return connections == null || connections.isEmpty();
	}
	
	@Override
	public String toString() {
		return name;
	}

	public Double getProbabilityForEssentialRequirements() {
		return getProbability(true);
	}
	
	public Double getProbabilityForRequirements() {
		return getProbability(false);
	}
	
	public Double getProbability(boolean considerOnlyEssentials){
		double probability = 0;
		for(Entry<Requirement, Double> probabilityForRequirement:probabilities.entrySet()){
			Requirement requirement = probabilityForRequirement.getKey();
			Double value = probabilityForRequirement.getValue();
			//If a requirement is essential, its probability must be always greater than 0
			if(requirement.isEssential() && value == 0){
				return 0.0;
			}
			if(!considerOnlyEssentials || requirement.isEssential()){
				double weight = requirement.getWeight() == null?1:requirement.getWeight();
				probability += value * weight;
			}	
		}
		return probability;	
	}

	public void calculateProbabilities(List<Requirement> requirements,
			List<State> availableNextStates) {

		for(Requirement requirement:requirements){
			double requirementProbability = 0;
			if(requirement.getValue() != null){
				requirementProbability = getProbability(requirement);
			}
			else{
				Policy policy = requirement.getPolicy();
				String metricName = requirement.getMetric().getName();
				Interval intervalForMetric = this.intervals.get(metricName);
				requirementProbability = 1;

				boolean findMin = Policy.min.equals(policy);				
				for(State alternativeToThisState:availableNextStates){
					if(alternativeToThisState != this){
						double aux = getProbabilityForPolicyRequirement(intervalForMetric, alternativeToThisState.intervals.get(metricName));
						if(findMin){
							requirementProbability *= aux;
						}
						else{
							requirementProbability *=(1-aux);
						}

					}
				}
			}
			probabilities.put(requirement, requirementProbability);
		}
	}
	
	public Double getProbability(Requirement requirement) {
		Interval interval = getIntervals().get(requirement.getMetric().getName());
		double requirementValue = requirement.getValue();
		Limit metricLimit = requirement.getMetric().getLimit();
		if (metricLimit.equals(Limit.upper)) {
			return getProbabilityUpperLimit(interval, requirementValue);
		} else if (metricLimit.equals(Limit.lower)) {
			return getProbabilityLowerLimit(interval, requirementValue);
		}
		return null;
	}

	private Double getProbabilityUpperLimit(Interval interval,
			double requirement) {

		if (requirement > interval.getMin() && requirement <= interval.getMax()) {
			return (requirement - interval.getMin())
					/ (interval.getMax() - interval.getMin());
		}

		if (requirement > interval.getMax()) {
			return 1.0;
		}

		return 0.0;
	}

	private Double getProbabilityLowerLimit(Interval interval,
			double requirement) {

		if (requirement > interval.getMin() && requirement <= interval.getMax()) {
			return (interval.getMax() - requirement)
					/ (interval.getMax() - interval.getMin());
		}

		if (requirement > interval.getMax()) {
			return 0.0;
		}

		return 1.0;
	}
	
	/**
	 * Returns the probability in which the values in interval1 are smaller than interval2
	 * @param interval1
	 * @param interval2
	 * @return
	 */
	public double getProbabilityForPolicyRequirement(Interval interval1, Interval interval2){
		
		if(interval2.getMin() > interval1.getMax()){
			return 1.0;
		}
		
		if(interval1.getMin() > interval2.getMax()){
			return 0.0;
		}
		
		double interval1Size = (interval1.getMax() - interval1.getMin()) + 1;
		double interval2Size = (interval2.getMax() - interval2.getMin()) + 1;
		
		double prob1 = 1/interval1Size;
		double prob2 = 1/interval2Size;
		
		/**
		 *                   5   6   7   8   9   10
		 *    interval 1 = |---|---|---|---|---|---| 11  12
		 *    interval 2 =             |---|---|---|---|---|
		 *                 |-----------|-----------|-------|
		 *                      L            L_2     L_3
		 */
		//use the comment above to understand the variables name
		double L = interval2.getMin() - interval1.getMin();
		double Lprobability = 0;
		if(L > 0){
			Lprobability = L*prob1;
		}
		double L_2 = (interval1.getMax()>interval2.getMax()?interval2.getMax():interval1.getMax()) - 
					 (interval1.getMin() > interval2.getMin()?interval1.getMin():interval2.getMin()) + 1;
		double L_2_probability = L_2 *prob1;
		double L_3 = interval2.getMax() - interval1.getMax();
		double L_3_probability = 0;
		if(L_3 > 0){
			L_3_probability = L_3*prob2;
		}
		
		int sum = 0;
		int total = (int)L_2 - 1;
		while(total >0){
			sum += total;
			total--;
		}
		
		return Lprobability + L_2_probability*L_3_probability + sum*prob1*prob2;
	}
}
