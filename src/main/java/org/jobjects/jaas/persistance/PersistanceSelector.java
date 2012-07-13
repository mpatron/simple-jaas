package org.jobjects.jaas.persistance;

import java.util.Map;

import org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC;
import org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyTrue;

public class PersistanceSelector {

	public PersistanceSelector() {		
	}
	
	public UserRoleInformation getUserRoleInformation(Map<String, ?> options) {
		UserRoleInformation returnValue=null;
			String persistanceMode= (String) options.get("persistanceMode");
			if("JDBC".equals(persistanceMode)) {
				returnValue=new UserRoleInformationJDBC();
			} else if("ONLY_TRUE".equals(persistanceMode)) {
				returnValue=new UserRoleInformationOnlyTrue();
			}
			returnValue.init(options);
		return returnValue;
	}
}
