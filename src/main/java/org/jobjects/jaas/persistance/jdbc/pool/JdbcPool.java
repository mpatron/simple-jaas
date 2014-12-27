package org.jobjects.jaas.persistance.jdbc.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Cette classe permet de traiter les connexions de pool.
 * 
 * <p>
 * <b>JdbcPool</b>
 * </p>
 * <br>
 * <p>
 * Elle contient des methodes permettant d'effectuer les operations suivantes :<br>
 * <br>
 * <li>Obtenir une connexion ouverte, issue d'un pool.</li>
 * <br>
 * <li>Retourner une connexion jusqu'au pool.</li>
 * <br>
 * <li>Liberer les ressources et fermer les connexions lors de l'arret.</li>
 * </p>
 * <p>
 * Un objet JdbcPool porte un nom utilise par les clients. Il represente un pool
 * de connexions à une base de donnees, identifiee par une URL JDBC. <br>
 * Une URL JDBC indique trois elements :<br>
 * <li>Le protocole(jdbc),</li>
 * <li>Le pilote (par exemple, odbc, idb, oracle),</li>
 * <li>La base de donnees (format propre au pilote).</li>
 * </p>
 * <p>
 * Il est possible d'associer un nom d'utilisateur et un mot de passe aux bases
 * de donnees exigeant un compte correspondant aux connexions.
 * </p>
 * <p>
 * Les autres attributs de ConnectionPoolsont les suivants :<br>
 * <li>Nombre maximal de connexions,</li>
 * <li>Nombre de connexions à ouvrir à la creation du pool,</li>
 * <li>Delai (en secondes) pendant lequel l'utilisateur attend la disponibilite
 * d'une connexion,</li>
 * <li>Informations relatives au type des messages du journal qui vous
 * interessent et ou vous souhaitez ecrire.</li>
 * </p>
 * <p>
 * JdbcPool stocke les objets JDBC Connection accessibles aux clients dans un
 * vecteur appelé freeConnections. Cette classe doit aussi garder une trace du
 * nombre de connexions du pool. Cela lui permet de determiner à quel moment le
 * nombre maximal de connexions est atteint. A cet effet, elle a recours à une
 * simple variable de type int checkedOut. Le nombre de connexions contenues
 * dans le pool correspond à la somme de la valeur de checkedOut et du nombre
 * d'elementsdu vecteur freeConnection.
 * </p>
 * <br>
 * Copyright (c) 2002 JObjects
 * 
 * @author Mickaël Patron
 * @version 1.0
 */
public class JdbcPool {
	/**
	 * Le nom de la base de donnees
	 */
	private String name;
	/**
	 * URL JDBC de la base de donnees
	 */
	private String URL;
	/**
	 * Le nom d'utilisateur du compte de la base de donnees
	 */
	private String user;
	/**
	 * Le mot de passe du compte de la base de donnees
	 */
	private String password;
	/**
	 * Le nombre maximal de connexions contenues dans le pool, 0 correspond à un
	 * nombre illimité
	 */
	private int maxConns;
	/**
	 * Le delai d'attente, en secondes, avant d'obtenir une connexion, si aucune
	 * n'est disponible et que le nombre maximum soit atteint.
	 */
	private int timeOut;
	/**
	 * La categorie de login.
	 */
	private Logger LOGGER = Logger.getLogger(getClass().getName());

	/* Variables d'instance . */
	/**
	 * Le nombre de connexions contenues dans le pool.
	 */
	private int checkedOut;
	/**
	 * Le vecteur pour garder le nombre de connexions.
	 */
	private Vector<Connection> freeConnections = new Vector<Connection>();

	/**
	 * Le Constructeur qui enregistre la valeur de la plupart des parametres
	 * dans des variables d'instance. Il appelle la methode initPool() pour
	 * ouvrir le nombre de connexions initiales indique.
	 * 
	 * @param name
	 *            le nom
	 * @param URL
	 *            l'url
	 * @param user
	 *            l'utilisateur
	 * @param password
	 *            le mot de passe
	 * @param maxConns
	 *            le nombre maximal de connexions
	 * @param initConns
	 *            le nombre initial de connexion
	 * @param timeOut
	 *            le temps de login
	 * @param log
	 *            le login
	 */
	public JdbcPool(String name, String URL, String user, String password,
			int maxConns, int initConns, int timeOut) {
		this.name = name;
		this.URL = URL;
		this.user = user;
		this.password = password;
		this.maxConns = maxConns;
		this.timeOut = timeOut > 0 ? timeOut : 5;

		initPool(initConns);
		String Chaine = "New pool created [" + " name=" + name + " url=" + URL
				+ " user=" + user + " password=" + password + " initconns="
				+ initConns + " maxconns=" + maxConns + " logintimeout="
				+ this.timeOut + "] : " + getStats();
		LOGGER.log(Level.FINER, Chaine);
	}

	/**
	 * Cette methode ouvre le nombre de connexions indique et les rajoute dans
	 * le vecteur freeConnections. Elle est privee, appelee depuis le
	 * constructeur de JdbcPool.
	 * 
	 * @param initConns
	 *            le nombre de connexions initiales
	 */
	private void initPool(int initConns) {
		for (int i = 0; i < initConns; i++) {
			try {
				Connection pc = newConnection();
				freeConnections.addElement(pc);
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Can not create connection.", e);
			}
		}
	}

	/**
	 * Cette methode publique permet de reserver une connexion. Elle tente de
	 * retourner un objet Connection par l'appel d'une methode privee
	 * surchargee, getConnection(), en transmettant en parametres le delai
	 * d'attente maximal indique.
	 * 
	 * @see JdbcPoolManager#getConnection(String)
	 * @see JdbcConnection
	 * @return <code>Connection</code>
	 * @throws SQLException
	 *             en cas d'erreur d'acces à la base de donnees.
	 */
	public Connection getConnection() throws SQLException {
		LOGGER.log(Level.FINER, "Request for connection received");
		try {
			Connection conn = getConnection(timeOut * 1000);
			return new JdbcConnection(conn, this);
		} catch (SQLException e) {
			LOGGER.log(Level.SEVERE, "Exception getting connection", e);
			throw e;
		}
	}

	/**
	 * Cette methode retourne un objet Connection depuis le pool. Si aucun objet
	 * n'est disponible, nous en creons un, dans la limite du nombre maximal de
	 * connexions. Pour cela nous en appelons la methode privee
	 * getPooledConnection(). Si aucune connexion n'est disponible et que le
	 * nombre maximal est atteint, nous attendons qu'un client execute dans un
	 * autre thread retourne un objet Connection au pool. Un appel à la methode
	 * wait declenche une interruption, jusqu'é ce que l'un des evenements
	 * suivents se produise :<br>
	 * <li>le delai indique expire</li> <li>Un autre thread appelle la methode
	 * notify ou notifyAll sur l'objet que nous attendons, ce qui genere une
	 * exception InterruptedException.</li> <br>
	 * Cette methode privee, lance une exception SQLException s'il est
	 * impossible, pour une raison quelconque, pour le signaler. Dans la methode
	 * privee, l'exception est consignee dans le journal et envoyee au client.
	 * 
	 * <br>
	 * Cette methode est de type <code>synchronized</code>.
	 * 
	 * @param timeout
	 *            le temps d'attente avant d'obtenir une connexion
	 * @see #getPooledConnection()
	 * @see #isConnectionOK(Connection conn)
	 * @return une connexion<code>Connection</code>
	 * @throws SQLException
	 *             en cas d'erreur d'acces à la base de donnees.
	 */
	private synchronized Connection getConnection(long timeout)
			throws SQLException {
		long startTime = System.currentTimeMillis();
		long remaining = timeout;
		Connection conn = null;
		while ((conn = getPooledConnection()) == null) {
			try {
				LOGGER.log(Level.FINER, "Waiting for connection. Timeout="
						+ remaining);
				wait(remaining);
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, "Exception getting connection", e);
			}
			remaining = timeout - (System.currentTimeMillis() - startTime);
			if (remaining <= 0) {
				// Timeout a expiré
				LOGGER.log(Level.FINER, "Time-out while waiting for connection");
				throw new SQLException("getConnection() timed-out");
			}
		}

		// Vérifie si la connexion est encore OK
		if (!isConnectionOK(conn)) {
			// Si ce n'est pas le cas, essayer encore avec le temps restant
			LOGGER.log(Level.WARNING,
					"Removed selected bad connection from pool");
			return getConnection(remaining);
		}
		checkedOut++;
		LOGGER.log(Level.FINER, "Delivered connection from pool : "
				+ getStats());
		return conn;
	}

	/**
	 * Essaie de creer un statement pour voir si la connexion est etablie ou
	 * pas. Avec la methode isClosed(), nous pouvons demander au pilote si la
	 * connexion est fermee. Si tel est le cas, il est impossible d'utiliser
	 * l'objet Connection, nous generons donc la valeur false. Si la valeur true
	 * est generee, cela signifie seulement que le pilote JDBC considere la
	 * connexion ouverte. Si la connexion a ete fermee depuis le serveur de la
	 * base de donnees, le pilote JDBC peut ne pas en etre informe et generer la
	 * valeur true.
	 * 
	 * @param conn
	 *            la connexion.
	 * @see JdbcConnection#isClosed()
	 * @return <code>true</code> si la Connection est acceptée,
	 *         <code>false</code> sinon
	 */
	private boolean isConnectionOK(Connection conn) {
		Statement testStmt = null;
		try {
			if (!conn.isClosed()) {
				// Try to createStatement to see if it's really alive
				testStmt = conn.createStatement();
				testStmt.close();
			} else {
				return false;
			}
		} catch (SQLException e) {
			if (testStmt != null) {
				try {
					testStmt.close();
				} catch (SQLException se) {
					LOGGER.log(Level.SEVERE, se.getMessage(), se);
				}
			}
			LOGGER.log(Level.SEVERE, "Pooled Connection was not okay", e);
			return false;
		}
		return true;
	}

	/**
	 * Cette methode permet de generer un objet Connection s'il est disponible,
	 * sinon nous appelons newConnection() pour ouvrir un nouvel objet, dans la
	 * limite du nombre maximal de connexions contenues dans le pool. Si
	 * maxConns vaut zero, cela signifie qu'aucun nombre maximal n'est defini.
	 * 
	 * @return <code>Connection</code> un objet Connection
	 * @throws SQLException
	 *             en cas d'erreur d'acces à la base de donnees.
	 */
	private Connection getPooledConnection() throws SQLException {
		Connection conn = null;
		if (freeConnections.size() > 0) {
			// Prend la première connexion dans le Vecteur.
			conn = (Connection) freeConnections.firstElement();
			freeConnections.removeElementAt(0);
		} else if (maxConns == 0 || checkedOut < maxConns) {
			conn = newConnection();
		}
		return conn;
	}

	/**
	 * Cette methode utilise l'une des variantes de la methode JDBC
	 * DriverManager.getConnection(), selon qu'un mot de passe et le nom
	 * d'utilisateur sont associes ou non au pool.
	 * 
	 * @see #getConnection()
	 * @see JdbcPoolManager#getConnection(String)
	 * @return une connexion<code>Connection</code>
	 * @throws SQLException
	 *             en cas d'erreur d'acces à la base de donnees.
	 */
	private Connection newConnection() throws SQLException {
		Connection conn = null;
		if (user == null) {
			conn = DriverManager.getConnection(URL);
		} else {
			conn = DriverManager.getConnection(URL, user, password);
		}
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("ALTER SESSION SET NLS_SORT = BINARY");
		stmt.executeUpdate("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS'");
		stmt.executeUpdate("ALTER SESSION SET NLS_NUMERIC_CHARACTERS = '. '");
		stmt.close();
		LOGGER.log(Level.FINER, "Opened a new connection for " + name);
		return conn;
	}

	/**
	 * Cette methode ajoute l'objet Connection retourne à la fin du vecteur
	 * freeConnections et decremente le compteur des connexions reservees.
	 * 
	 * <br>
	 * Cette methode est de type <code>synchronized</code>.
	 * 
	 * @param conn
	 *            l'objet Connection
	 */
	public synchronized void freeConnection(Connection conn) {
		// Met la connexion à la fin du Vecteur.
		freeConnections.addElement(conn);
		checkedOut--;
		notifyAll();
		LOGGER.log(Level.FINER, "Returned connection to pool : " + getStats());
	}

	/**
	 * Libere les connexions. L'objet JdbcPool propose une methode qui permet de
	 * liberer en douceur toutes les ressources du pool. JdbcPoolManager appelle
	 * la methode release() lorsque le dernier client annonce qu'il n'est plus
	 * interesse par les services du pool de connexions. Et la methode release()
	 * parcourt le vecteur freeConnections et appelle la methode close() é
	 * chaque objet Connection. Une fois les objets Connection fermes, ils sont
	 * suprimes du vecteur.
	 * 
	 * <br>
	 * Cette methode est de type <code>synchronized</code>.
	 * 
	 * @see JdbcPoolManager#release()
	 * @see JdbcConnection#close()
	 */
	public synchronized void release() {
		Enumeration<Connection> allConnections = freeConnections.elements();
		while (allConnections.hasMoreElements()) {
			Connection con = (Connection) allConnections.nextElement();
			try {
				con.close();
				LOGGER.log(Level.FINER, "Closed connection");
			} catch (SQLException e) {
				LOGGER.log(Level.SEVERE, "Couldn't close connection", e);
			}
		}
		freeConnections.removeAllElements();
	}

	/**
	 * Recupere le Statement.
	 */
	private String getStats() {
		return "Total connections: " + (freeConnections.size() + checkedOut)
				+ " Available: " + freeConnections.size() + " Checked-out: "
				+ checkedOut;
	}
}
