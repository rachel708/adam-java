package it.polimi.adam.engine.util;

import javax.inject.Named;

public class DumbClass {
	
	public @Named("returnMethod1") String method1() {
		return "method1 executed.";
	}
	
	public String method2(@Named("paramName") String param1, @Named("paramName2") String param2) {
		return "method1 executed.";
	}

}
