package org.jobjects.jaas.persistance.jdbc.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * The driver for use with JDBC connection pools. When this class is loaded, it
 * registers itself with the DriverManager. This driver accepts JDBC connection
 * URLs of the form:
 * <P>
 * 
 * <ul>
 * <tt>jdbc:protomatter:pool:<i>PoolName</i></tt>
 * </ul>
 * <P>
 * 
 * This class also keeps a static reference to the list of all known connection
 * pools, which is updated by creating a new JdbcPool object. Pools can be
 * un-registered by calling <tt>unRegisterPool()</tt> on the JdbcPool object.
 * 
 * @see java.sql.DriverManager
 * @see JdbcPool
 * @see JdbcPoolManager
 */
public class JdbcDriver implements Driver {

	public static String URL_PREFIX = "jdbc:jobjects:pool:";

	@Override
	public void finalize() throws Throwable {
		JdbcPoolManager.getInstance().release();
	}

	/**
	 * Retrieves whether the driver thinks that it can open a connection to the
	 * given URL.
	 */
	@Override
	public boolean acceptsURL(String url) {
		return url.startsWith(URL_PREFIX);
	}

	// ---------------------------------------------------------------------------

	/**
	 * Attempts to make a database connection to the given URL.
	 */
	@Override
	public Connection connect(String url, Properties info) {

		Connection returnValue = null;

		if (!url.startsWith(URL_PREFIX))
			return null;

		Enumeration<?> e = info.keys();
		System.out.println("++++++++++++++++++++++++++");
		while (e.hasMoreElements()) {
			String chaine = (String) e.nextElement();
			System.out.println("" + chaine + "=" + info.getProperty(chaine));
		}
		System.out.println("--------------------------");

		String poolName = url.substring(URL_PREFIX.length());
		returnValue = JdbcPoolManager.getInstance().getConnection(poolName);
		return returnValue;
	}

	// ---------------------------------------------------------------------------

	/**
	 * Retrieves the driver's major version number.
	 */
	@Override
	public int getMajorVersion() {
		int returnValue = 0;
		return returnValue;
	}

	// ---------------------------------------------------------------------------

	/**
	 * Gets the driver's minor version number.
	 */
	@Override
	public int getMinorVersion() {
		int returnValue = 0;
		return returnValue;
	}

	// ---------------------------------------------------------------------------

	/**
	 * Gets information about the possible properties for this driver.
	 */
	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
		DriverPropertyInfo[] returnValue = null;
		return returnValue;
	}

	// ---------------------------------------------------------------------------

	/**
	 * Reports whether this driver is a genuine JDBC CompliantTM driver.
	 */
	@Override
	public boolean jdbcCompliant() {
		boolean returnValue = true;
		return returnValue;
	}

	// ---------------------------------------------------------------------------

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}
}
