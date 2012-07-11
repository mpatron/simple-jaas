package org.jobjects.jaas.persistance.testing;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.jobjects.jaas.persistance.UserRoleInformation;

public class UserRoleInformationOnlyTrue implements UserRoleInformation {

	@Override
	public boolean init(Map<String, ?> options) {
		return true;
	}

	@Override
	public boolean isValidUser(String login, char[] password) {
		return true;
	}

	@Override
	public List<String> getRoles(String login) {
		List<String> returnValue = new ArrayList<String>();
		EnumSet<RolesOnlyTrueEnum> ee = EnumSet.allOf(RolesOnlyTrueEnum.class);
		for (RolesOnlyTrueEnum en : ee) {
			returnValue.add(en.getName());
		}
		return returnValue;
	}

}
