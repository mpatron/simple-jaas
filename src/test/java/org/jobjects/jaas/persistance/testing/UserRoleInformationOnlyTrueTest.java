/**
 * 
 */
package org.jobjects.jaas.persistance.testing;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author x113446
 * 
 */
public class UserRoleInformationOnlyTrueTest {

	UserRoleInformationOnlyTrue instance = new UserRoleInformationOnlyTrue();

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyTrue#init(java.util.Map)}
	 * .
	 */
	@Test
	public void testInit() {
		assertTrue(instance.init(null));
	}

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyTrue#isValidUser(java.lang.String, char[])}
	 * .
	 */
	@Test
	public void testIsValidUser() {
		assertTrue(instance.isValidUser(null, null));
	}

	/**
	 * Test method for
	 * {@link org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyTrue#getRoles(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetRoles() {
		assertTrue(instance.getRoles(null).size() > 0);
	}

}
