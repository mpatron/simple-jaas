package org.jobjects.jaas.persistance;

import java.util.Map;

import org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC;

public class PersistanceSelector {

	public PersistanceSelector() {		
	}
	
	public UserRoleInformation getUserRoleInformation(Map<String, ?> options) {
		UserRoleInformation returnValue=new UserRoleInformationJDBC();
		returnValue.init(options);
		return returnValue;
	}
}
