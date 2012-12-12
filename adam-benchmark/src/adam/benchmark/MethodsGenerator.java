package adam.benchmark;

import it.polimi.adam.model.Requirement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MethodsGenerator {

	static int n = 500;
	static int a = 3;
	static int m = 5;

	public static void main(String[] args) throws IOException {
		
		String destination = args[0];
		
		File f = new File(
				destination+"/adam_benchmark_"+ n + "_" + a + "_" + m + ".adam");
		if (f.exists()) {
			f.delete();
		}
		f.createNewFile();
		generateEmbeddedModel(f);
	}

	public static void generateComponents() {
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= a; j++) {
				System.out.println("public @Named(\"param_" + i
						+ "\") String method_" + i + "_alternative_" + j
						+ "(@Named(\"param_" + (i - 1) + "\") String param){");
				System.out.println("execution();");
				System.out.println("return \"method" + i + "_alternative_" + j
						+ "\";");
				System.out.println("}");
			}
		}
	}

	public static void generateMethodCallSequence(int n) {
		System.out.println("String result_0 = \"adam\";");
		for (int i = 1; i <= n; i++) {
			System.out.println("String result_" + i
					+ "= new Components().method_" + (i)
					+ "_alternative_1(result_" + (i - 1) + ");");
		}
	}

	public static void generateEmbeddedModel(File f) throws IOException {
		JsonObject embeddedModel = new JsonObject();
		embeddedModel.addProperty("initial_state",
				"adam.benchmark.Components.method_1_alternative_1");
		JsonObject metrics = new JsonObject();
		for (int i = 1; i <= m; i++) {
			JsonObject metric = new JsonObject();
			metric.addProperty("name", "m" + i);
			metric.addProperty("limit", "upper");
			metric.addProperty("aggregator", "+");

			metrics.add("m" + i, metric);
		}
		JsonArray requirements = new JsonArray();
		for (int i = 1; i <= m; i++) {
			JsonObject requirement = new JsonObject();
			requirement.addProperty("name", "rq" + i + "_1");

			JsonObject metric = new JsonObject();
			metric.addProperty("name", "m" + i);

			requirement.add("metric", metric);
			requirement.addProperty("value", n * 1000);
			requirement.addProperty("essential", true);

			requirements.add(requirement);

			requirement = new JsonObject();
			requirement.addProperty("name", "rq" + i + "_2");

			requirement.add("metric", metric);
			requirement
					.addProperty("policy", Requirement.Policy.min.toString());
			requirement.addProperty("essential", true);

			requirements.add(requirement);

		}

		JsonObject states = new JsonObject();
		Random r = new Random();
		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= a; j++) {
				JsonObject state = new JsonObject();
				String stateName = getMethodName(i, j);
				state.addProperty("name", stateName);

				JsonObject impact = new JsonObject();
				for (int w = 1; w <= m; w++) {
					impact.addProperty("m" + w, r.nextInt(1000));
				}
				state.add("impacts", impact);

				JsonObject intervals = new JsonObject();
				for (int w = 1; w <= m; w++) {
					JsonObject interval = new JsonObject();
					int min = r.nextInt(500);
					if (i == n) {
						interval.addProperty("min", impact.get("m" + w)
								.getAsInt());
						interval.addProperty("max", impact.get("m" + w)
								.getAsInt());
						intervals.add("m" + w, interval);

					} else {
						interval.addProperty("min", min * (n - i + 1));
						interval.addProperty("max", (min + r.nextInt(1000))
								* (n - i + 1));
						intervals.add("m" + w, interval);
					}
				}
				state.add("intervals", intervals);

				JsonArray connections = new JsonArray();
				for (int w = 1; w <= a; w++) {
					JsonObject connection = new JsonObject();
					JsonObject nextState = new JsonObject();
					nextState.addProperty("name", getMethodName(i + 1, w));
					connection.add("state", nextState);
					connections.add(connection);
				}
				state.add("impacts", impact);
				if (i != n) {
					state.add("connections", connections);
				}

				states.add(stateName, state);
			}
		}

		embeddedModel.add("metrics", metrics);
		embeddedModel.add("requirements", requirements);
		embeddedModel.add("states", states);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonOutput = gson.toJson(embeddedModel);

		FileOutputStream fos = new FileOutputStream(f);
		fos.write(jsonOutput.getBytes());
		// System.out.println(jsonOutput);

	}

	private static String getMethodName(int i, int a) {
		return "adam.benchmark.Components.method_" + i + "_alternative_" + a;
	}

}
