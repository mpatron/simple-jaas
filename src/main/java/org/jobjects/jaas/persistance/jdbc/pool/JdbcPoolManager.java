package org.jobjects.jaas.persistance.jdbc.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe JdbcPoolManager permet d'avoir recours à plusieurs pools de
 * connexions.
 * 
 * <p>
 * <b>JdbcPoolManager </b>
 * </p>
 * <br>
 * <p>
 * La creation d'un objet ralentit toujours les traitements, car il faut allouer
 * la memoire et initialiser l'objet. De plus, on doit conserver la trace de
 * l'objet, et de le detruire une fois qu'il devient inutile. C'est pour ces
 * raisons que l'on doit creer un pool de ressources. La réutilisation d'un
 * groupe d'objets est appelee pool d'objets. Elle fournit l'interface utilise
 * par les applications client et effectue les les operations suivantes :
 * </p>
 * <p>
 * <li>chargement et enregistrement des pilotes JDBC,</li>
 * <li>creation d'objets JdbcConnectionPool à partir du contenu d'un fichier de
 * prorietes,</li>
 * <li>correspondance entre noms de pools de connexions et objets
 * JdbcConnectionPool,</li>
 * <li>envoie des requetes client dans un objet JdbcConnectionPool nomme</li>
 * <li>suivi des clients des pools de connexions, en vue de l'arret en douceur
 * des pools lorsque le dernier client à termine.</li>
 * </p>
 * <br>
 * <p>
 * <b><i>Proprietes du Pool </i> </b>
 * </p>
 * <br>
 * <p>
 * JdbcPoolManager obtient les informations relatives aux pilotes JDBC é
 * utiliser et aux pools à creer en consultant le fichier de proprietes
 * db.properties. Celui doit etre situe dans le CLASSPATH du processus serveur.
 * <br>
 * db.properties, au format Java Properties, contient des paires cle-valeur
 * definissant les pools de connexions, ainsi que des proprietes communes é
 * ceux-ci.Ces proprietes sont:
 * </p>
 * <li>drivers : Liste de noms de classes de pilotes JDBC, separes par un espace
 * </li> <li>logfile : Chemin absolu d'un fichier journal.</li> <br>
 * <p>
 * Un autre groupe de proprietes permet de definir un pool. Le nom de la
 * propriete commence par celui de pool de connexions correspondant :
 * </p>
 * <p>
 * <table align=center width=100% border>
 * <tr>
 * <td><poolname>.url</td>
 * <td>URL JDBC de la base de donnees</td>
 * </tr>
 * <tr>
 * <td><poolname>.user</td>
 * <td>Le nom d'utilisateur du compte de la base de donnees</td>
 * </tr>
 * <tr>
 * <td><poolname>.password</td>
 * <td>Le mot de passe du compte de la base de donnees</td>
 * </tr>
 * <tr>
 * <td><poolname>.maxconns</td>
 * <td>Le nombre maximal de connexions contenues dans le pool, 0 correspond à un
 * nombre illimité</td>
 * </tr>
 * <tr>
 * <td><poolname>.initconns</td>
 * <td>Le nombre de connexions initiales à ouvrir pour le pool.</td>
 * </tr>
 * <tr>
 * <td><poolname>.logintimeout</td>
 * <td>Le delai d'attente, en secondes, avant d'obtenir une connexion, si aucune
 * n'est disponible et que le nombre maximum soit atteint.</td>
 * </tr>
 * </table>
 * </p>
 * <br>
 * <p>
 * Toutes les proprietes, à l'exception de url, sont facultatives. Le nom
 * d'utilisateur et le mot de passe associe doivent etre valide pour la base de
 * donnees definies par l'URL.
 * </p>
 * 
 * Voici un exemple de fichier db.properties :
 * <p>
 * <table bgcolor=#eeeeff align=center>
 * <tr align=left>
 * <td>drivers=sun.jdbc.odbc.JdbcOdbcDriver oracle.jdbc.driver.OracleDriver <br>
 * </td>
 * </tr>
 * <tr>
 * </tr>
 * <tr align=left>
 * <td>AQUILON.url=jdbc:oracle:thin:@ORACLENETX:1521:EDI <br>
 * AQUILON.user=b4one <br>
 * AQUILON.maxconns=5 <br>
 * </td>
 * </tr>
 * <tr>
 * </tr>
 * <tr align=left>
 * <td>SCOTT.url=jdbc:oracle:thin:@ORACLENETX:1521:EDI <br>
 * SCOTT.user=scott <br>
 * SCOTT.password=tiger <br>
 * SCOTT.maxconns=6 <br>
 * SCOTT.initconns=2 <br>
 * </td>
 * </tr>
 * <tr>
 * </tr>
 * <tr align=left>
 * <td>access.url=jdbc:odbc:demo <br>
 * access.user=demo <br>
 * access.password=demopw <br>
 * access.initconns=5 <br>
 * access.maxconns=10 <br>
 * </td>
 * </tr>
 * </table>
 * </p>
 * <p>
 * Voici un exemple de code :
 * </p>
 * 
 * <p>
 * <table bgcolor=#eeeeff align=center>
 * <tr>
 * <td><font size=-1> <b><i>// Renvoie la seule instance de la classe. </i> </b>
 * </font> <br>
 * JdbcPoolManager pdb = JdbcPoolManager.getInstance(); <br>
 * <br>
 * <font size=-1> <b><i>// Recupere la connexion avec le nom du pool en
 * parametre et envoie l'appel à l'objet JdbcConnectionPool correspondant. </i>
 * </b> </font> <br>
 * Connection connection=pdb.getConnection(databaseName); <br>
 * <br>
 * String SQL = " <b>SELECT * FROM </b>(table_name)"; <br>
 * try <br>
 * {<br>
 * &nbsp;&nbsp;&nbsp; <font size=-1> <b><i>// Cree un objet Statement pour
 * envoyer des statements en SQL à la base de donnees. </i> </b> </font> <br>
 * &nbsp;&nbsp;&nbsp;Statement stmt = connection.createStatement(); <br>
 * &nbsp;&nbsp;&nbsp;try <br>
 * &nbsp;&nbsp;&nbsp;{ log.debug(SQL); <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ResultSet rs = stmt.executeQuery(SQL); <br>
 * &nbsp;&nbsp;&nbsp;} <br>
 * &nbsp;&nbsp;&nbsp; <font size=-1> <b><i>// Informe la JdbcConnectionPool que
 * la JdbcConnection est fermee. </i> </b> </font> <br>
 * &nbsp;&nbsp;&nbsp;finally {stmt. <b><i>close() </i> </b>;} <br>
 * <br>
 * finally { connection. <b><i>close() </i> </b>; }</td>
 * </tr>
 * </table>
 * </p>
 * <br>
 * 
 * <p>
 * Meme exemple avec la base de donnees AQUILON :
 * </p>
 * <p>
 * <table bgcolor=#eeeeff align=center>
 * <tr>
 * <td>JdbcPoolManager pdb = JdbcPoolManager.getInstance(); <br>
 * Connection connection=pdb.getConnection("AQUILON"); <br>
 * String SQL = " <b>SELECT * FROM &nbsp; LOCATIONS </b>"; <br>
 * try <br>
 * {<br>
 * &nbsp;&nbsp;&nbsp;Statement stmt = connection.createStatement(); <br>
 * &nbsp;&nbsp;&nbsp;try <br>
 * &nbsp;&nbsp;&nbsp;{ log.debug(SQL); <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ResultSet rs = stmt.executeQuery(SQL); <br>
 * &nbsp;&nbsp;&nbsp;} <br>
 * &nbsp;&nbsp;&nbsp;finally {stmt. <b><i>close() </i> </b>;} <br>
 * <br>
 * finally { connection. <b><i>close() </i> </b>; }</td>
 * </tr>
 * </table>
 * </p>
 * <br>
 * 
 * <p>
 * Meme exemple avec la base de donnees SCOTT :
 * </p>
 * <p>
 * <table bgcolor=#eeeeff align=center>
 * <tr>
 * <td>JdbcPoolManager pdb = JdbcPoolManager.getInstance(); <br>
 * Connection connection=pdb.getConnection("SCOTT"); <br>
 * String SQL = " <b>SELECT * FROM &nbsp; DEPT </b>"; <br>
 * try <br>
 * {<br>
 * &nbsp;&nbsp;&nbsp;Statement stmt = connection.createStatement(); <br>
 * &nbsp;&nbsp;&nbsp;try <br>
 * &nbsp;&nbsp;&nbsp;{ log.debug(SQL); <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ResultSet rs = stmt.executeQuery(SQL); <br>
 * &nbsp;&nbsp;&nbsp;} <br>
 * &nbsp;&nbsp;&nbsp;finally {stmt. <b><i>close() </i> </b>;} <br>
 * <br>
 * finally { connection. <b><i>close() </i> </b>; }</td>
 * </tr>
 * </table>
 * </p>
 * <br>
 * <br>
 * Copyright (c) 2002 JObjects
 * 
 * @author Mickaël Patron
 * @version 1.0
 */

public class JdbcPoolManager {

	// Variables d'instance, pour garder la trace des operations
	/**
	 * Conserve l'instance de JdbcPoolManager.
	 */
	static private JdbcPoolManager instance = null;

	/**
	 * Conserve le nombre des clients.
	 */
	static private int clients = 0;

	/**
	 * Conserve la categorie de login.
	 */
	private Logger LOGGER = Logger.getLogger(getClass().getName());

	/**
	 * Conserve la trace des pilotes JDBC.
	 */
	private Vector<Driver> drivers = new Vector<Driver>();

	/**
	 * Conserve des instances de JdbcConnectionPool.
	 */
	private Map<String, JdbcPool> pools = new Hashtable<String, JdbcPool>();

	private Properties props = new Properties();

	public static final String applicationDir = ".asprc";

	public static final String fileProperties = "asp.properties";

	/**
	 * Le Constructeur .
	 */
	private JdbcPoolManager() {
		init();
	}

	/**
	 * Renvoie la seule instance de la classe. Les clients de JdbcPoolManager
	 * appellent cette methode pour obtenir une reference à l'instance.
	 * 
	 * <br>
	 * Cette methode est de type <code>synchronized</code>.
	 * 
	 * @return l'unique instance de <code>JdbcPoolManager</code>
	 */
	static synchronized public JdbcPoolManager getInstance() {
		if (instance == null) {
			instance = new JdbcPoolManager();
		}
		clients++;
		return instance;
	}

	/**
	 * Initialise l'objet.
	 */
	private void init() {
		loadDrivers();
		createPools();
	}

	/**
	 * Chargement du gestionnaire JDBC (Drivers)
	 */
	private void loadDrivers() {
		String driverClasses = props.getProperty("drivers");
		StringTokenizer st = new StringTokenizer(driverClasses);
		while (st.hasMoreElements()) {
			String driverClassName = st.nextToken().trim();
			try {
				Driver driver = (Driver) Class.forName(driverClassName)
						.newInstance();
				DriverManager.registerDriver(driver);
				drivers.addElement(driver);
				LOGGER.log(Level.FINER, "Registered JDBC driver "
						+ driverClassName);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Can't register JDBC driver: "
						+ driverClassName, e);
			}
		}
	}

	/**
	 * Methode privee pour la creation des objets JdbcConnectionPool.
	 * 
	 * <br>
	 * Nous creons un objet Enumeration des noms de proprietes et recherchons
	 * ceux qui se terminent par .url. Lorsque nous en trouvons, nous extrayons
	 * le nom du pool et lisons la premiere propriete du pool de connexions
	 * correspondant. Nous procedons ainsi pour toutes les proprietes. exception
	 * géré : NumberFormatException <br>
	 * si erreur dans la valeur du nombre maximum de connexions <br>
	 * si erreur dans la valeur initiale des connexions <br>
	 * si erreur dans la valeur du temps de login <br>
	 * exception géré : IOException S'il y a une erreur, c'est un probleme sur
	 * props.
	 */
	private void createPools() {
		Enumeration<?> propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".url")) {
				String poolName = name.substring(0, name.lastIndexOf("."));
				String url = props.getProperty(poolName + ".url");
				if (url == null) {
					LOGGER.log(Level.SEVERE, "No URL specified for " + poolName);
					continue;
				}

				String user = props.getProperty(poolName + ".user");
				String password = props.getProperty(poolName + ".password");

				String maxConns = props
						.getProperty(poolName + ".maxconns", "0");
				int max;
				try {
					max = Integer.valueOf(maxConns).intValue();
				} catch (NumberFormatException e) {
					LOGGER.log(Level.SEVERE, "Invalid maxconns value "
							+ maxConns + " for " + poolName, e);
					max = 0;
				}

				String initConns = props.getProperty(poolName + ".initconns",
						"0");
				int init;
				try {
					init = Integer.valueOf(initConns).intValue();
				} catch (NumberFormatException e) {
					LOGGER.log(Level.SEVERE, "Invalid initconns value "
							+ initConns + " for " + poolName, e);
					init = 0;
				}

				String loginTimeOut = props.getProperty(poolName
						+ ".logintimeout", "5");
				int timeOut;
				try {
					timeOut = Integer.valueOf(loginTimeOut).intValue();
				} catch (NumberFormatException e) {
					LOGGER.log(Level.SEVERE, "Invalid logintimeout value "
							+ loginTimeOut + " for " + poolName, e);
					timeOut = 5;
				}

				JdbcPool pool = new JdbcPool(poolName, url, user, password,
						max, init, timeOut);
				pools.put(poolName, pool);
			}
		}
	}

	/**
	 * Recupere la connexion avec le nom du pool en parametre et envoie l'appel
	 * à l'objet JdbcConnectionPool correspondant. Ensuite la methode publique
	 * getConnection() dans JdbcConnectionPool permet de reserver une connexion.
	 * Elle tente de retourner un objet Connection par l'appel d'une methode
	 * privee surchargee, getConnection(), en transmettant en parametres le
	 * delai d'attente maximal indique. Cette derniere methode retourne un objet
	 * Connection depuis le pool. Si aucun objet n'est disponible, nous en
	 * creons un, dans la limite du nombre maximal de connexions. Pour cela nous
	 * en appelons la methode privee getPooledConnection(). Si aucune connexion
	 * n'est disponible et que le nombre maximal est atteint, nous attendons
	 * qu'un client execute dans un autre thread retourne un objet Connection au
	 * pool. Un appel à la methode wait declenche une interruption, jusqu'é ce
	 * que l'un des evenements suivents se produise : le delai indique expire ou
	 * un autre thread appelle la methode notify ou notifyAll sur l'objet que
	 * nous attendons, ce qui genere une exception InterruptedException. Elle
	 * lance une exception SQLException s'il est impossible, pour une raison
	 * quelconque, de le signaler. Dans la methode privee, l'exception est
	 * consignee dans le journal et envoyee au client.
	 * 
	 * @param name
	 *            le nom
	 * @see JdbcPool#getConnection()
	 * @return <code>Connection</code> un objet connexion.
	 */
	public Connection getConnection(String name) {
		Connection conn = null;
		JdbcPool pool = (JdbcPool) pools.get(name);
		if (pool != null) {
			try {
				conn = pool.getConnection();
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Exception getting connection from "
						+ name, e);
			}
		}
		return conn;
	}

	/**
	 * Libere la connexion avec le nom du pool et la connexion en parametres
	 * envoie l'appel à l'objet JdbcConnectionPool correspondant. Au lieu de
	 * retourner l'objet Connection du pool, nous creons un objet JdbcConnection
	 * que nous envoyons à l'application client. Ensuite nous remplacons
	 * freeConnection() par wrapperClosed().
	 * 
	 * @param name
	 *            le nom
	 * @param con
	 *            la connexion
	 * @see JdbcPool#freeConnection(Connection)
	 */
	public void freeConnection(String name, Connection con) {
		JdbcPool pool = (JdbcPool) pools.get(name);
		if (pool != null) {
			pool.freeConnection(con);
		}
	}

	/**
	 * Arret en douceur des pools de connexions. Chaque client de poolManager
	 * appelle la methode statique getInstance(), pour obtenir une reference au
	 * gestionnaire de pools. Sachant que l'une des variables de cette derniere
	 * methode permet de conserver la trace du nombre de clients.chaque client
	 * doit aussi appeller la methode release(), lors de l'arret du serveur,
	 * afin que le compteur du nombre de client puisse etre incremente. Lorsque
	 * le dernier client appelle la methode release(), le JdbcPoolManager
	 * appelle egalement sur tous les objets JdbcConnectionPool, pour fermer
	 * toutes les connexions.
	 * 
	 * <br>
	 * Cette methode est de type <code>synchronized</code>.
	 * 
	 * @see JdbcPool#release()
	 */
	public synchronized void release() {
		// Wait until called by the last client
		if (--clients != 0) {
			return;
		}

		for (Iterator<Map.Entry<String, JdbcPool>> iterator = pools.entrySet()
				.iterator(); iterator.hasNext();) {
			Map.Entry<String, JdbcPool> entry = iterator.next();
			entry.getValue().release();
		}

		Enumeration<Driver> allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements()) {
			Driver driver = (Driver) allDrivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				LOGGER.log(Level.FINER, "Deregistered JDBC driver "
						+ driver.getClass().getName());
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Couldn't deregister JDBC driver: "
						+ driver.getClass().getName(), e);
			}
		}
	}
}