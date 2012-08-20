package it.polimi.adam.engine.util;

import it.polimi.adam.engine.util.Reflection;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class ReflectionTest {

	@Test
	public void testExecMethod() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException, NoSuchMethodException, SecurityException{
		Map<String, Object> executionState = new HashMap<String, Object>();
		
		String result = (String)Reflection.execMethod("it.polimi.adam.engine.util.DumbClass.method1", executionState);
		Assert.assertEquals("method1 executed.", result);
		Assert.assertEquals("method1 executed.", executionState.get("returnMethod1"));
	}
	
	@Test
	public void testGetClassName() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method getClassName = Reflection.class.getDeclaredMethod("getClassName",String.class);
		getClassName.setAccessible(true);
		String className = (String) getClassName.invoke(null,"it.polimi.adam.engine.util.DumbClass.method1");
			
		Assert.assertEquals("it.polimi.adam.engine.util.DumbClass", className);
		
	}
	
	@Test
	public void testGetMethodName() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method getMethodName = Reflection.class.getDeclaredMethod("getMethodName",String.class);
		getMethodName.setAccessible(true);
		String method = (String) getMethodName.invoke(null,"it.polimi.adam.engine.util.DumbClass.method1");
			
		Assert.assertEquals("method1", method);
		
	}
	
	@Test
	public void testLoadClass() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method loadClass = Reflection.class.getDeclaredMethod("loadClass",String.class);
		loadClass.setAccessible(true);
		Class clazz = (Class) loadClass.invoke(null,"it.polimi.adam.engine.util.DumbClass.method1");
			
		Assert.assertEquals(DumbClass.class, clazz);
	}
	
	@Test
	public void findMethod() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method findMethod = Reflection.class.getDeclaredMethod("findMethod",String.class, Class.class);
		findMethod.setAccessible(true);
		Method method = (Method) findMethod.invoke(null,"method1", DumbClass.class);
			
		Assert.assertEquals(DumbClass.class.getDeclaredMethod("method1"), method);	
	}
	
	@Test
	public void getParamNames() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method getParamNamesMethod = Reflection.class.getDeclaredMethod("getParamNames", Method.class);
		getParamNamesMethod.setAccessible(true);
		
		Method method2 = DumbClass.class.getDeclaredMethod("method2",String.class, String.class);
		List<Object> params = (List<Object>) getParamNamesMethod.invoke(null,method2);
			
		Assert.assertEquals(2, params.size());
		Assert.assertEquals("paramName", params.get(0));
		Assert.assertEquals("paramName2", params.get(1));
	}
	
	@Test
	public void getReturnValueName() throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method getReturnValueNameMethod = Reflection.class.getDeclaredMethod("getReturnValueName",Method.class);
		getReturnValueNameMethod.setAccessible(true);
		
		Method method1 = DumbClass.class.getDeclaredMethod("method1");
		String returnValueName = (String) getReturnValueNameMethod.invoke(null,method1);
			
		Assert.assertEquals("returnMethod1", returnValueName);	
	}
	
	
}
