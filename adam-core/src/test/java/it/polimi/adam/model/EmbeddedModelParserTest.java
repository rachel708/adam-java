package it.polimi.adam.model;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import it.polimi.adam.domain.Connection;
import it.polimi.adam.domain.EmbeddedModel;
import it.polimi.adam.domain.Interval;
import it.polimi.adam.domain.Metric;
import it.polimi.adam.domain.State;
import it.polimi.adam.domain.Metric.Limit;
import it.polimi.adam.model.EmbeddedModelParser;
import it.polimi.adam.model.Requirement;
import it.polimi.adam.model.Requirement.Policy;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
 

public class EmbeddedModelParserTest {

	private EmbeddedModel embeddedModel;
	
	@Before
	public void getEmbeddedModel(){
		InputStream modelStream = getClass().getResourceAsStream("/embedded.txt");		
		EmbeddedModelParser parser = new EmbeddedModelParser();
		embeddedModel = parser.parse(modelStream);
	}
	
	@Test
	public void testGetInitialState(){
		assertEquals("it.polimi.adam.app.ClassWithImplementations.one",embeddedModel.getInitialState());
	}
	
	@Test
	public void testGetMetrics(){
		
		
		Map<String, Metric> metrics = embeddedModel.getMetrics();
		
		assertEquals(3,metrics.size());
		
		Metric rt = metrics.get("rt");
		Metric e = metrics.get("e");		
		Metric u = metrics.get("u");
		
		assertEquals("rt",rt.getName());
		assertEquals("e", e.getName());
		assertEquals("u", u.getName());
		
		assertEquals(Limit.upper,rt.getLimit());
		assertEquals(Limit.upper,e.getLimit());
		assertEquals(Limit.lower,u.getLimit());

	}
	
	@Test
	public void testGetRequirements(){
		
		List<Requirement> requirements = embeddedModel.getRequirements();
		
		assertEquals(4,requirements.size());
		

		Requirement rq1 = requirements.get(0);
		Requirement rq2 = requirements.get(1);
		Requirement rq3 = requirements.get(2);
		Requirement rq4 = requirements.get(3);
		
		assertEquals("rq1",rq1.getName());
		assertEquals("e",rq1.getMetric().getName());
		assertEquals(5.0,rq1.getValue());
		assertTrue(rq1.isEssential());
		assertEquals(0.3,rq1.getWeight());
		assertNull(rq1.getPolicy());

		assertEquals("rq2",rq2.getName());
		assertEquals("e", rq2.getMetric().getName());
		assertNull(rq2.getValue());
		assertFalse(rq2.isEssential());
		assertEquals(0.1,rq2.getWeight());
		assertEquals(Policy.max, rq2.getPolicy());

		assertEquals("rq3",rq3.getName());
		assertEquals("rt",rq3.getMetric().getName());
		assertEquals(1200.0,rq3.getValue());
		assertTrue(rq3.isEssential());
		assertEquals(0.3,rq3.getWeight());
		assertNull(rq3.getPolicy());
		
		assertEquals("rq4",rq4.getName());
		assertEquals("u", rq4.getMetric().getName());
		assertEquals(3.0,rq4.getValue());
		assertTrue(rq4.isEssential());
		assertEquals(0.3,rq4.getWeight());
		assertNull(rq4.getPolicy());
	}
	
	
	@Test
	public void testGetStates(){
		
		Map<String, State> states = embeddedModel.getStates();
		
		assertNotNull(states);
		
		State stateOne = states.get("it.polimi.adam.app.ClassWithImplementations.one");
		assertNotNull(stateOne);
		assertEquals("it.polimi.adam.app.ClassWithImplementations.one", stateOne.getName());
		
		Map<String, Double> impacts = stateOne.getImpacts();
		assertNotNull(impacts);
		assertEquals(500.0, impacts.get("rt"));
		assertEquals(1.0, impacts.get("e"));
		assertEquals(1.0, impacts.get("u"));
		
		assertEquals("it.polimi.adam.app.ClassWithImplementations.hasAutoFocus", stateOne.getTransitionCondition());
		
		List<Connection> connections = stateOne.getConnections();
		assertEquals(2, connections.size());
		assertEquals("it.polimi.adam.app.ClassWithImplementations.two", connections.get(0).getState().getName());
		assertEquals("it.polimi.adam.app.ClassWithImplementations.three", connections.get(1).getState().getName());
		
		assertEquals("true", connections.get(0).getLabel());
		assertEquals("false", connections.get(1).getLabel());
		
		Map<String, Interval> intervals = stateOne.getIntervals();
		assertEquals(3, intervals.size());
		Interval rt = intervals.get("rt");
		assertEquals(500.0, rt.getMin());
		assertEquals(2000.0, rt.getMax());	
		
		Interval e = intervals.get("e");
		assertEquals(3.0, e.getMin());
		assertEquals(5.0, e.getMax());	
		
		Interval u = intervals.get("u");
		assertEquals(1.0, u.getMin());
		assertEquals(6.0, u.getMax());	
	}
	
	@Test
	public void testGetStates2(){
		
		Map<String, State> states = embeddedModel.getStates();
		
		assertNotNull(states);
		
		State stateTwo = states.get("it.polimi.adam.app.ClassWithImplementations.two");
		assertNotNull(stateTwo);
		assertEquals("it.polimi.adam.app.ClassWithImplementations.two", stateTwo.getName());
		
		Map<String, Double> impacts = stateTwo.getImpacts();
		assertNotNull(impacts);
		assertEquals(700.0, impacts.get("rt"));
		assertEquals(2.0, impacts.get("e"));
		assertEquals(3.0, impacts.get("u"));
		
		assertNull(stateTwo.getTransitionCondition());
		
		List<Connection> connections = stateTwo.getConnections();
		assertEquals(1, connections.size());
		assertEquals("it.polimi.adam.app.ClassWithImplementations.three", connections.get(0).getState().getName());
		
		assertNull(connections.get(0).getLabel());
		
		Map<String, Interval> intervals = stateTwo.getIntervals();
		assertEquals(3, intervals.size());
		Interval rt = intervals.get("rt");
		assertEquals(200.0, rt.getMin());
		assertEquals(1000.0, rt.getMax());
		
		Interval e = intervals.get("e");
		assertEquals(3.0, e.getMin());
		assertEquals(7.0, e.getMax());
		
		Interval u = intervals.get("u");
		assertEquals(2.0, u.getMin());
		assertEquals(5.0, u.getMax());	
	}
	

}
