package org.jobjects.jaas.persistance;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PersistanceModeEnumTest {

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
	public void test() {
		List<String> valeurs=Arrays.asList("JDBC","ONLY_TRUE","ONLY_FALSE");
		EnumSet<PersistanceModeEnum> ee = EnumSet.allOf(PersistanceModeEnum.class);
		Assert.assertTrue("Il doit y avoir plusieur type.", ee.size()>0);
		for (PersistanceModeEnum en : ee) {
			Assert.assertTrue(valeurs.contains(en.name()));
		}
	}

}
