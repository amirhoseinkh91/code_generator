package example.address.base;

import java.io.Serializable;


/**
 *  Base class for entity "example.address.Address".
 *  It is an automatically generated file and should not be edited.
 *
 * @hibernate.class
 *  table="justro.USERS"
 */

public abstract class BaseAddress  implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	/** The constant referring the name of class "Address". */
	public static final String REF = "Address";
	
	/** The constant referring the property "city". */
	public static final String PROP_CITY = "city";
	
	
	/** The name of table for this class. */
	public static final String TABLE = "justro.USERS";
	
	/** Name of column referring the property "city". */
	public static final String PROPCOLUMN_CITY = "address_city";
	
	
	
	
	
	
	
	// fields
	private java.lang.String city;

	

	
	
	
	/**
	 * Getter for "city".
	 * column= address_city
	 *
	 * @return the value of city
	 */
	public java.lang.String getCity() {
		return this.city;
	}
	
	/**
	 * Setter for property "city".
	 * column= address_city
	 *
	 * @param city the new value for city
	 */
	public void setCity(java.lang.String city) {
		this.city = city;
	}



	
	

	
	
	protected String toStringData() {
		return "";
	}
	
	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{" + this.toStringData() + "}";
	}

	
	
}