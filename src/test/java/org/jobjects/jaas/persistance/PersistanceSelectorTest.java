package org.jobjects.jaas.persistance;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jobjects.jaas.persistance.testing.UserRoleInformationOnlyTrue;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PersistanceSelectorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetUserRoleInformation() {
		UserRoleInformation uri;
		
		Map<String,Object> options = new HashMap<String,Object>();
		PersistanceSelector ps = new PersistanceSelector();		
		Assert.assertNotNull(ps.getUserRoleInformation(options));
		
		options.clear();
		options.put(PersistanceSelector.PERSISTANCE_MODE, "inconnu");
		Assert.assertNotNull(ps.getUserRoleInformation(options));
		
		options.clear();
		options.put(PersistanceSelector.PERSISTANCE_MODE, PersistanceModeEnum.ONLY_TRUE.name());
		uri=ps.getUserRoleInformation(options);
		Assert.assertTrue("Type attendu UserRoleInformationOnlyTrue.", uri instanceof UserRoleInformationOnlyTrue);
	}

}
