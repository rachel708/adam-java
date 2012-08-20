package it.polimi.adam.domain;

public class Metric {
	
	public enum Limit{
		upper,lower
	}
	
	private String name;
	private Limit limit;
	private String aggregator;
	
	public String getName() {
		return name;
	}
	
	public Limit getLimit() {
		return limit;
	}

	public String getAggregator() {
		return aggregator;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLimit(Limit limit) {
		this.limit = limit;
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
		Metric other = (Metric) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
	

}
