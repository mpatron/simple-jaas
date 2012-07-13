package org.jobjects.jaas.persistance.testing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jobjects.jaas.persistance.UserRoleInformation;

public class UserRoleInformationOnlyFalse implements UserRoleInformation {

	@Override
	public boolean init(Map<String, ?> options) {
		return true;
	}

	@Override
	public boolean isValidUser(String login, char[] password) {
		return false;
	}

	@Override
	public List<String> getRoles(String login) {
		List<String> returnValue = new ArrayList<String>();
		return returnValue;
	}

}
