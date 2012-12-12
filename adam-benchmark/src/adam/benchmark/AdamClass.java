package adam.benchmark;

import it.polimi.adam.annotation.Model;

import javax.inject.Named;

public interface AdamClass {

	@Model("adam_benchmark")
	public void execAdam(@Named("param_0") String param);
	
}
