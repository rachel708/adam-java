package it.polimi.lemon.domain;

public class Interval {

	private Double min;
	private Double max;
	
	public Interval(Double min, Double max) {
		super();
		this.min = min;
		this.max = max;
	}

	public Double getMin() {
		return min;
	}
	
	public Double getMax() {
		return max;
	}
	
}
