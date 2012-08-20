package it.polimi.lemon.app;

import javax.inject.Named;

public class ClassWithImplementations {

	public static Boolean HAS_AUTO_FOCUS = true;

	public @Named("a_object") String one(){
		return "one";
	}
	

	public @Named("a_object") String two(@Named("a_object") String param1){
		return "Executing method two "+param1;
	}
	
	
	public @Named("finalResult") String three(@Named("a_object") String param1){
		return param1 + " at three!";
	}
	
	public Boolean hasAutoFocus(){
		return HAS_AUTO_FOCUS;
	}
	
	
}
