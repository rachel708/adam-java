package it.polimi.lemon.engine.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

public class Reflection {

	/*
	 * TODO: 
	 *  -- create object only once
	 */
	public static Object execMethod(String completeMethodName, 
									Map<String, Object> executionState) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException{
		
		Class clazz = loadClass(completeMethodName);
		Method method = findMethod(getMethodName(completeMethodName), clazz);
		Object[] params = getParams(method, executionState);
		Object returnValue = null;
		if(params== null || params.length == 0){
			returnValue = method.invoke(clazz.getConstructor().newInstance());	
		}
		else{
			returnValue = method.invoke(clazz.getConstructor().newInstance(), params);	
		}
		
		if(!method.getReturnType().equals(Void.TYPE)){
			String returnValueName = getReturnValueName(method);
			if(returnValueName != null){
				executionState.put(returnValueName, returnValue);	
			}
		}
		return returnValue;
	}

	private static Class loadClass(String completeMethodName) throws ClassNotFoundException{
		return Class.forName(getClassName(completeMethodName));
	}
	
	private static String getClassName(String completeMethodName){
		return completeMethodName.substring(0,completeMethodName.lastIndexOf('.'));
	}
	
	private static String getMethodName(String completeMethodName){
		return completeMethodName.substring(completeMethodName.lastIndexOf('.') + 1);
	}
	
	private static Method findMethod(String methodName, Class clazz){
		for(Method method:clazz.getMethods()){
			if(method.getName().equals(methodName)){
				return method;
			}
		}
		return null;
	}
	
	private static Object[] getParams(Method method, Map<String, Object> executionState){
		List<String> paramNames = getParamNames(method);
		Object[] params = new Object[paramNames.size()];
		
		int i = 0;
		for(String paramName:paramNames){
			params[i] = executionState.get(paramName);
			i++;
		}
		
		return params;
	}
	
	public static List<String> getParamNames(Method method){
		Annotation[][] annotations = method.getParameterAnnotations();
		List<String> paramNames = new ArrayList<String>();
		for(Annotation paramAnnotations[]:annotations){
			for(Annotation annotation:paramAnnotations){
				if(annotation instanceof Named){
					paramNames.add(((Named) annotation).value());
				}
			}
		}
		return paramNames;
	}
	
	public static String getReturnValueName(Method method){
		if(method.isAnnotationPresent(Named.class)){
			return method.getAnnotation(Named.class).value();
		}
		return null;
	}
}
