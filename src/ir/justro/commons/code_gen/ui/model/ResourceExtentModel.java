package ir.justro.commons.code_gen.ui.model;

/**
 * The Class ResourceExtentModel.
 */
public class ResourceExtentModel {
	
	private String key;
	public String getKey() {
		return this.key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
	private String ficClassName;
	public String getFicClassName() {
		return this.ficClassName;
	}
	public void setFicClassName(String ficClassName) {
		this.ficClassName = ficClassName;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getSimpleName()+"{" +
				"key: " + this.getKey()
				+ ", " +
				"ficClass: " + this.getFicClassName()
				+"}";
	}
}
