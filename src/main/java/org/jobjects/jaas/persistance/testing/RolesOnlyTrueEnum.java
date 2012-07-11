/**
 * 
 */
package org.jobjects.jaas.persistance.testing;

/**
 * @author x113446
 *
 */
public enum RolesOnlyTrueEnum {
	
	ADMIN("admin"),
	TOMCAT("tomcat");
	
	private RolesOnlyTrueEnum(String name) {
		this.name=name;
	}
	
	private String name;	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
