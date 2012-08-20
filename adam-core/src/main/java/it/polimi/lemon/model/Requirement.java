package it.polimi.lemon.model;

import it.polimi.lemon.domain.Metric;

public class Requirement {
	
	public enum Policy{
		min,max
	}

	private String name;
	private Metric metric;
	private Double value;
	private Policy policy;	
	private boolean essential;
	private Double weight;
	
	
	
	public String getName() {
		return name;
	}
	
	public Metric getMetric() {
		return metric;
	}

	public Double getValue() {
		return value;
	}
	
	public Policy getPolicy(){
		return policy;
	}
	
	public boolean isEssential() {
		return essential;
	}
	
	public Double getWeight() {
		return weight;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Requirement other = (Requirement) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	
	

}
