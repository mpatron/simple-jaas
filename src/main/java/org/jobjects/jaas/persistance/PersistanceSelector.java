package org.jobjects.jaas.persistance;

import java.util.Map;
import java.util.logging.Logger;

import org.jobjects.jaas.persistance.jdbc.UserRoleInformationJDBC;
import org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyFalse;
import org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyTrue;

public class PersistanceSelector {

	private static Logger LOGGER = Logger
			.getLogger(UserRoleInformationJDBC.class.getName());

	public final static String PERSISTANCE_MODE = "persistanceMode";

	public PersistanceSelector() {
	}

	public UserRoleInformation getUserRoleInformation(Map<String, ?> options) {
		UserRoleInformation returnValue = null;
		
		String persistanceMode=null;
		try {
			persistanceMode= (String) options.get(PERSISTANCE_MODE);
		} catch (ClassCastException cce) {
			LOGGER.warning(PERSISTANCE_MODE+"=<Not a String>. ONLY_FALSE activé.");
			persistanceMode=PersistanceModeEnum.ONLY_FALSE.name();
		}
		
		/*
		 * Transformation de la valeur en object PersistanceModeEnum
		 */
		PersistanceModeEnum pme = null;
		try {
			pme = Enum.valueOf(PersistanceModeEnum.class, persistanceMode);
		} catch (IllegalArgumentException | NullPointerException iae) {
			LOGGER.warning(PERSISTANCE_MODE+"="+persistanceMode+". ONLY_FALSE activé.");
			pme=PersistanceModeEnum.ONLY_FALSE;
		}
		switch (pme) {
		case JDBC:
			returnValue = new UserRoleInformationJDBC();
			break;
		case ONLY_TRUE:
			returnValue = new UserRoleInformationOnlyTrue();
			break;
		case ONLY_FALSE:
			returnValue = new UserRoleInformationOnlyFalse();
			break;
		default:
			returnValue = new UserRoleInformationOnlyTrue();
			break;
		}
		returnValue.init(options);
		return returnValue;
	}
}
