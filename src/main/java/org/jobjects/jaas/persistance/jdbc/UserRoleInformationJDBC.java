/**
 * 
 */
package org.jobjects.jaas.persistance.jdbc;

import java.util.List;
import java.util.Map;

import org.jobjects.jaas.persistance.UserRoleInformation;

/**
 * @author Mickael
 * 
 */
public class UserRoleInformationJDBC implements UserRoleInformation {
	
	public UserRoleInformationJDBC() {		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jobjects.jaas.persistance.UserRoleInformation#init(Map<String, ?> options)
	 */
	@Override
	public boolean init(Map<String, ?> options) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jobjects.jaas.persistance.UserRoleInformation#isValidUser(java.lang
	 * .String, char[])
	 */
	@Override
	public boolean isValidUser(String login, char[] password) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jobjects.jaas.persistance.UserRoleInformation#getRoles()
	 */
	@Override
	public List<String> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

}
