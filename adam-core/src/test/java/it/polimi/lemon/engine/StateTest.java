package it.polimi.lemon.engine;

import it.polimi.lemon.domain.EmbeddedModel;
import it.polimi.lemon.domain.Interval;
import it.polimi.lemon.domain.Metric;
import it.polimi.lemon.domain.Metric.Limit;
import it.polimi.lemon.domain.State;
import it.polimi.lemon.engine.model.EmbeddedModelParser;
import it.polimi.lemon.model.Requirement;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class StateTest {

	/**
	 * 
	 * Situations used in the name of the methods
	 * 
	 * Situation 1:
	 * 
	 * ---- Requirement between min and max. Ex: Requirement is 8, while min is
	 * 5 and max is 11
	 * 
	 * requirement | v | - - - - - | 5 8 11
	 * 
	 * 
	 * Situation 2:
	 * 
	 * ---- Requirement is greater than max: Ex.: Requirement is 14, while min
	 * is 5 and max is 11
	 * 
	 * requirement | v | - - - - - | - - - 5 11 14
	 * 
	 * 
	 * Situation 3:
	 * 
	 * ---- Requirement is smaller than min: Ex.: Requirement is 3, while min is
	 * 5 and max is 11
	 * 
	 * requirement | v - - | - - - - - | 3 4 5 11
	 * 
	 * 
	 * 
	 */

	@Test
	public void testGetProbabilityUpperLimitSituation1() {

		State state = new State();

		Map<String, Interval> intervals = new HashMap<String, Interval>();
		intervals.put("m1", new Interval(5.0, 11.0));
		state.setIntervals(intervals);

		Requirement requirement = new Requirement();
		Metric metric = new Metric();
		metric.setName("m1");
		metric.setLimit(Limit.upper);
		requirement.setMetric(metric);
		requirement.setValue(8.0);

		Double probability = state.getProbability(requirement);
		Assert.assertEquals(0.5, probability);

		requirement.setValue(9.0);
		probability = state.getProbability(requirement);
		Assert.assertEquals(2.0 / 3, probability);

		requirement.setValue(11.0);
		probability = state.getProbability(requirement);
		Assert.assertEquals(1.0, probability);

	}

	@Test
	public void testGetProbabilityUpperLimitSituation2() {
		State state = new State();

		Map<String, Interval> intervals = new HashMap<String, Interval>();
		intervals.put("m1", new Interval(5.0, 11.0));
		state.setIntervals(intervals);

		Requirement requirement = new Requirement();
		Metric metric = new Metric();
		metric.setName("m1");
		metric.setLimit(Limit.upper);
		requirement.setMetric(metric);
		requirement.setValue(12.0);

		Double probability = state.getProbability(requirement);
		Assert.assertEquals(1.0, probability);
	}

	@Test
	public void testGetProbabilityUpperLimitSituation3() {
		State state = new State();

		Map<String, Interval> intervals = new HashMap<String, Interval>();
		intervals.put("m1", new Interval(5.0, 11.0));
		state.setIntervals(intervals);

		Requirement requirement = new Requirement();
		Metric metric = new Metric();
		metric.setName("m1");
		metric.setLimit(Limit.upper);
		requirement.setMetric(metric);
		requirement.setValue(4.0);

		Double probability = state.getProbability(requirement);
		Assert.assertEquals(0.0, probability);
	}

	@Test
	public void testGetProbabilityLowerLimitSituation1() {

		State state = new State();
		
		Map<String, Interval> intervals = new HashMap<String, Interval>();
		intervals.put("m1", new Interval(5.0, 11.0));
		state.setIntervals(intervals);

		Requirement requirement = new Requirement();
		Metric metric = new Metric();
		metric.setName("m1");
		metric.setLimit(Limit.lower);
		requirement.setMetric(metric);
		requirement.setValue(8.0);
		
		Double probability = state.getProbability(requirement);
		Assert.assertEquals(0.5, probability);

		requirement.setValue(9.0);
		probability = state.getProbability(requirement);
		Assert.assertEquals(1.0 / 3, probability);
	}

	@Test
	public void testGetProbabilityLowerLimitSituation2() {
		State state = new State();
		
		Map<String, Interval> intervals = new HashMap<String, Interval>();
		intervals.put("m1", new Interval(5.0, 11.0));
		state.setIntervals(intervals);

		Requirement requirement = new Requirement();
		Metric metric = new Metric();
		metric.setName("m1");
		metric.setLimit(Limit.lower);
		requirement.setMetric(metric);
		requirement.setValue(12.0);
		
		Double probability = state.getProbability(requirement);
		Assert.assertEquals(0.0, probability);
	}

	@Test
	public void testGetProbabilityLowerLimitSituation3() {
		State state = new State();
		
		Map<String, Interval> intervals = new HashMap<String, Interval>();
		intervals.put("m1", new Interval(5.0, 11.0));
		state.setIntervals(intervals);

		Requirement requirement = new Requirement();
		Metric metric = new Metric();
		metric.setName("m1");
		metric.setLimit(Limit.lower);
		requirement.setMetric(metric);
		requirement.setValue(4.0);
		
		Double probability = state.getProbability(requirement);
		Assert.assertEquals(1.0, probability);
	}

	@Test
	public void getNextStatesOneMetric() {

		InputStream modelStream = getClass().getResourceAsStream(
				"/embedded.txt");
		EmbeddedModelParser parser = new EmbeddedModelParser();
		EmbeddedModel embeddedModel = parser.parse(modelStream);

		State one = embeddedModel.getStates().get(
				"it.polimi.lemon.app.ClassWithImplementations.one");
		State two = embeddedModel.getStates().get(
				"it.polimi.lemon.app.ClassWithImplementations.two");
		State three = embeddedModel.getStates().get(
				"it.polimi.lemon.app.ClassWithImplementations.three");

		UncertaintyManagerImpl uncertaintyManager = new UncertaintyManagerImpl(
				embeddedModel);

		List<State> states = Arrays.asList(one, two, three);
		states = uncertaintyManager.getNextState(states);

		Assert.assertEquals(two, states.get(0));
		Assert.assertEquals(one, states.get(1));
		Assert.assertEquals(three, states.get(2));

	}
	
	@Test
	public void getProbabilityForPolicy(){
		
		
		Interval interval 	= new Interval(5.0, 10.0);
		Interval interval2 	= new Interval(8.0, 12.0);
		
		State state = new State();
		Assert.assertEquals(0.8, state.getProbabilityForPolicyRequirement(interval, interval2), 0.01);

		interval 	= new Interval(6.0, 10.0);
		interval2 	= new Interval(5.0, 8.0);
		
		Assert.assertEquals(0.15, state.getProbabilityForPolicyRequirement(interval, interval2), 0.01);
		
		interval 	= new Interval(7.0, 10.0);
		interval2 	= new Interval(5.0, 12.0);
		
		Assert.assertEquals(0.4375, state.getProbabilityForPolicyRequirement(interval, interval2));

		
	}
}
