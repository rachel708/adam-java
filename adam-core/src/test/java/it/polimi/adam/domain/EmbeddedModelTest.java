package it.polimi.adam.domain;

import it.polimi.adam.app.ClassWithImplementations;
import it.polimi.adam.domain.EmbeddedModel;
import it.polimi.adam.engine.UncertaintyManager;
import it.polimi.adam.engine.UncertaintyManagerImpl;
import it.polimi.adam.model.EmbeddedModelParser;

import java.io.InputStream;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
 

public class EmbeddedModelTest {

	private EmbeddedModel embeddedModel;
	
	@Before
	public void getEmbeddedModel(){
		InputStream modelStream = getClass().getResourceAsStream("/embedded.txt");		
		EmbeddedModelParser parser = new EmbeddedModelParser();
		embeddedModel = parser.parse(modelStream);
	}
	
	@Test
	public void testRun() throws Exception{
		UncertaintyManager uncertaintyManager = new UncertaintyManagerImpl(embeddedModel);
		Map<String, Object> executinResult = embeddedModel.run(uncertaintyManager);
		Assert.assertEquals("Executing method two one", executinResult.get("a_object"));
		Assert.assertEquals("Executing method two one at three!", executinResult.get("finalResult"));
	}
	
	@Test
	public void testRun2() throws Exception{
		UncertaintyManager uncertaintyManager = new UncertaintyManagerImpl(embeddedModel);
		ClassWithImplementations.HAS_AUTO_FOCUS = false;

		Map<String, Object> executinResult = embeddedModel.run(uncertaintyManager);
		Assert.assertEquals("one", executinResult.get("a_object"));
		Assert.assertEquals("one at three!", executinResult.get("finalResult"));
	}
}
