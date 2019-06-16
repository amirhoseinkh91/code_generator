package ir.viratech.commons.code_gen.model.resource;

import org.apache.velocity.context.Context;

public class ContextHolder {
	
	private static Context currentContext;
	
	public static Context getCurrentContext() {
		return currentContext;
	}
	
	public static void setCurrentContext(Context currentContext) {
		ContextHolder.currentContext = currentContext;
	}
	
}
