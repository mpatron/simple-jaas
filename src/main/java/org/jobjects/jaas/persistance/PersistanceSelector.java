package org.jobjects.jaas.persistance;

import java.util.Map;

import org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC;
import org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyTrue;

public class PersistanceSelector {

	private PersistanceSelector() {		
	}
	
	private static PersistanceSelector instance= new PersistanceSelector();
	
	public static PersistanceSelector getInstance(){
		return instance;
	}
	
	private UserRoleInformation userRoleInformation=null;
	
	public synchronized UserRoleInformation getUserRoleInformation(Map<String, ?> options) {
		//if(userRoleInformation==null) {
			String persistanceMode= (String) options.get("persistanceMode");
			if("JDBC".equals(persistanceMode)) {
				userRoleInformation=new UserRoleInformationJDBC();
			} else if("ONLY_TRUE".equals(persistanceMode)) {
				userRoleInformation=new UserRoleInformationOnlyTrue();
			}
			userRoleInformation.init(options);
		//}
		return userRoleInformation;
	}
}
