package ir.viratech.commons.code_gen.model.resource;




public class SnippetContext {

	/**
	 * Constructor
	 */
	public SnippetContext() {
	}

	/**
	 * Transform the snippet matching the name given with the current context and return the results
	 * @param name the snippet name
	 * @return the post transformation results
	 */
	public String get(String name) {
		//System.out.print(name+":     ");
		Snippet s = ResourceManager.getInstance().getSnippet(name);
		//System.out.println(((s==null) ? "null" : (""+s.getUrl())));
		if (null == s) return null;
		else return s.merge(ContextHolder.getCurrentContext());
	}
}