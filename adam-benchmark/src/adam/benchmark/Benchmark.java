package adam.benchmark;

import it.polimi.adam.domain.EmbeddedModel;
import it.polimi.adam.engine.UncertaintyManagerImpl;
import it.polimi.adam.model.EmbeddedModelParser;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Benchmark {
	
	static EmbeddedModel embeddedModel;
	
	public static void main(String[] args) throws Exception {
		
		EmbeddedModelParser parser = new EmbeddedModelParser();
		String modelName = "adam_benchmark_10_1_1";
		
		benchmark(parser, modelName);
		
//		modelName = "adam_benchmark_500_3_2";
//		benchmark(parser, modelName);
//
//		modelName = "adam_benchmark_500_3_4";
//		benchmark(parser, modelName);
//
//		modelName = "adam_benchmark_500_3_5";
//		benchmark(parser, modelName);
		
	}
	
	public static void benchmark(EmbeddedModelParser parser, String modelName) throws Exception{
		System.out.println("Running model "+modelName);
		embeddedModel = parser.parse(new Benchmark().getClass().getResourceAsStream("/"+modelName+".adam"));
		File f = new File(modelName+".txt");
		if(f.exists()){
			f.delete();
		}
		f.createNewFile();
		run(new FileOutputStream(f));

	}
	
	public static void run(FileOutputStream fos) throws Exception{
		for(int i = 0;i<35;i++){
			long begin = System.currentTimeMillis();
			//method();
			adam();
			long end = System.currentTimeMillis();
			fos.write((""+(end - begin)+"\n").getBytes());
		}
	}

	




	private static void method() {
	}






	public static void adam() throws Exception{
		Map<String, Object> executionState = new HashMap<String, Object>();
		executionState.put("param_0", "adam");
		
		embeddedModel.run(executionState, new UncertaintyManagerImpl(embeddedModel));

	}
}
