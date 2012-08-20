package it.polimi.adam.model;

import it.polimi.adam.domain.Connection;
import it.polimi.adam.domain.EmbeddedModel;
import it.polimi.adam.domain.Metric;
import it.polimi.adam.domain.State;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import com.google.gson.Gson;

public class EmbeddedModelParser {
	
	public EmbeddedModelParser() {
	}

	public EmbeddedModel parse(InputStream modelStream) {
		Gson gson = new Gson();
		EmbeddedModel embeddedModel = gson.fromJson(new InputStreamReader(modelStream), EmbeddedModel.class);
		for(String stateName:embeddedModel.getStates().keySet()){
			State state = embeddedModel.getStates().get(stateName);
			if(state.getConnections() != null){
				for(Connection connection:state.getConnections()){
					connection.setState(embeddedModel.getStates().get(connection.getState().getName()));
				}	
			}
		}
		
		Map<String, Metric> metrics = embeddedModel.getMetrics();
		for(Requirement requirement:embeddedModel.getRequirements()){
			requirement.setMetric(metrics.get(requirement.getMetric().getName()));
		}
		
		return embeddedModel;
	}

}
