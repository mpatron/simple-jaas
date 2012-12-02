package org.jobjects.jaas.persistance.jdbc.pool;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Cette classe est un encapsuleur autour d'un objet Connection, elle surcharge
 * la methode close(), pour informer le JdbcConnectionPool de sa fermeture.
 * Lorsque l'application appelle close() sur notre objet JdbcWrapper, elle
 * appelle wrapperClosed() en transmettant l'objet Connection reel en parametre.
 * On remplace la connexion dans le pool, decremente le compteur de connexions
 * reservees et notifie d'eventuels threads en attente. Ensuite la methode
 * isClosed(), pour utiliser son etat au lieu de l'objet Connection reel. <br>
 * Copyright (c) 2002 JObjects
 * 
 * @author Micka�l Patron
 * @version 1.0
 */
class JdbcConnection implements Connection {
	// realConn should be private but we use package scope to
	// be able to test removal of bad connections
	/**
	 * C'est la connexion reelle de l'objet Connection.
	 */
	Connection realConn;
	/**
	 * Le pool de d'objet JdbcConnectionPool.
	 */
	private JdbcPool pool;
	/**
	 * Le booleen qui permet de savoir si la connexion est fermee ou pas. Il est
	 * initialise � false.
	 */
	private boolean isClosed = false;

	/**
	 * Le constructeur obtient une reference � l'objet Connection reel et
	 * l'enregistre dans la la variable d'instance realConn. Il obtient aussi
	 * une reference � l'objet JdbcConnectionPool et l'enregistre dans la
	 * variable pool.
	 * 
	 * @param realConn
	 *            un objet Connection
	 * @param pool
	 *            JdbcConnectionPool
	 */
	public JdbcConnection(Connection realConn, JdbcPool pool) {
		this.realConn = realConn;
		this.pool = pool;
	}

	/**
	 * Informe la JdbcConnectionPool que la JdbcConnection est ferm�.
	 * 
	 * @see #JdbcConnection(Connection realConn, JdbcPool pool)
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public void close() throws SQLException {
		isClosed = true;
		pool.freeConnection(realConn);
	}

	/**
	 * Cette methode permet d'utiliser son etat au lieu de l'objet Connection
	 * reel.
	 * 
	 * @return <code>true</code> si la Connection est accept�e,
	 *         <code>false</code> sinon
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public boolean isClosed() throws SQLException {
		return isClosed;
	}

	/*
	 * Les methodes d'encapsulation envoyees dans l'objet Connection.
	 */

	/**
	 * Met en claire tous les warnings pour cet objet Connection.
	 * 
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public void clearWarnings() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.clearWarnings();
	}

	/**
	 * Met en permanence tous les changements fait depuis la derniere commit et
	 * libere la fermeture de la base de donnees pris par l'objet Connection.
	 * 
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public void commit() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.commit();
	}

	/**
	 * Cree un objet Statement pour envoyer des statements en SQL � la base de
	 * donnees.
	 * 
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public Statement createStatement() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcStatement(realConn.createStatement());
	}

	/**
	 * Cree un objet Statement qui va generer des objets ResultSet avec un type
	 * et un Concurrency donnes.
	 * 
	 * @param resultSetType
	 * @param resultSetConcurrency
	 * @see #isClosed()
	 * @see #createStatement()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		// NF:Pas possible de c�er un statement autrement sous peine de probl�me
		// d'affichage des accents.
		return new JdbcStatement(realConn.createStatement(resultSetType,
				resultSetConcurrency));
	}

	/**
	 * Recupere l'actuel etat de l'auto-commit.
	 * 
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public boolean getAutoCommit() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.getAutoCommit();
	}

	/**
	 * Retourne l'actuel nom de catalogue de l'objet Connection.
	 * 
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public String getCatalog() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.getCatalog();
	}

	/**
	 * Recupere le MetaData en rapport � cette connexion � la base de donnees.
	 * 
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.getMetaData();
	}

	/**
	 * Recupere l'actuel niveau de l'isolation de la transaction de cette
	 * Connection.
	 * 
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public int getTransactionIsolation() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.getTransactionIsolation();
	}

	/**
	 * Retourne le premier warning par appel � cette Connection.
	 * 
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public SQLWarning getWarnings() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.getWarnings();
	}

	/**
	 * Teste pour voir si la connexion est en mode Lecture-seule ou pas. .
	 * 
	 * @see #isClosed()
	 * @return <code>true</code> si la connexion est en mode Lecture-seule,
	 *         <code>false</code> sinon
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public boolean isReadOnly() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.isReadOnly();
	}

	/**
	 * Convertit le SQL statement donne en native systeme de la grammaire SQL .
	 * 
	 * @param sql
	 *            la requete
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public String nativeSQL(String sql) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.nativeSQL(sql);
	}

	/**
	 * Cree un objet CallableStatement pour appeller les procedures stockees de
	 * la base de donnees.
	 * 
	 * @param sql
	 *            la requete
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcCallableStatement(realConn.prepareCall(sql));
	}

	/**
	 * Cree un objet CallableStatement qui va generer des objets ResultSet avec
	 * le type et la concurrency donnees.
	 * 
	 * @param sql
	 *            la requete
	 * @param resultSetType
	 *            le type
	 * @param resultSetConcurrency
	 *            la Concurrency
	 * @see #isClosed()
	 * @see #prepareCall(String sql)
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcCallableStatement(realConn.prepareCall(sql,
				resultSetType, resultSetConcurrency));
	}

	/**
	 * Cree un objet PreparedStatement pour envoyer des SQL statements
	 * parametises � la base de donnees.
	 * 
	 * @param sql
	 *            la requete
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcPreparedStatement(realConn.prepareStatement(sql));
	}

	/**
	 * Cree un objet PreparedStatement qui va generer des objets ResultSet avec
	 * le type et la concurrency donnes.
	 * 
	 * @param sql
	 *            la requete
	 * @param resultSetType
	 *            le type
	 * @param resultSetConcurrency
	 *            la Concurrency
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcPreparedStatement(realConn.prepareStatement(sql,
				resultSetType, resultSetConcurrency));
	}

	/**
	 * Enleve tout changements faits depuis le dernier commit/rollback et libere
	 * toute fermeture de la base de donnees actuellement tenue par cette
	 * Connection.
	 * 
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public void rollback() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.rollback();
	}

	/**
	 * Met le mode d'auto-commit de cette connection.
	 * 
	 * @param autoCommit
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.setAutoCommit(autoCommit);
	}

	/**
	 * Met un nom de catalogue pour selectionner un subspace de cette Connection
	 * de la base dans lequel travailler.
	 * 
	 * @param catalog
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public void setCatalog(String catalog) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.setCatalog(catalog);
	}

	/**
	 * Met cette connexion en mode lecture-seule pour donner � la base de
	 * donnees la possibilite d'optimisations.
	 * 
	 * @param readOnly
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.setReadOnly(readOnly);
	}

	/**
	 * Permet de changer le niveau de l'isolation de la transaction � celui
	 * donne.
	 * 
	 * @param level
	 *            le niveau
	 * @see #isClosed()
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.setTransactionIsolation(level);
	}

	/**
	 * Recupere l'objet type map associe avec cette connexion.
	 * 
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.getTypeMap();
	}

	/**
	 * Installe le type map donne comme le type map pour cette connexion.
	 * 
	 * @throws SQLException
	 *             en cas d'erreur d'acces � la base de donnees.
	 */
	@Override
	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.setTypeMap(map);
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.setHoldability(holdability);
	}

	@Override
	public int getHoldability() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.getHoldability();
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.setSavepoint();
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return realConn.setSavepoint(name);
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.rollback(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		realConn.releaseSavepoint(savepoint);
	}

	@Override
	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcStatement(realConn.createStatement(resultSetType,
				resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcPreparedStatement(realConn.prepareStatement(sql,
				resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcCallableStatement(realConn.prepareCall(sql,
				resultSetType, resultSetConcurrency, resultSetHoldability));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcPreparedStatement(realConn.prepareStatement(sql,
				autoGeneratedKeys));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcPreparedStatement(realConn.prepareStatement(sql,
				columnIndexes));
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		if (isClosed) {
			throw new SQLException("Pooled connection is closed");
		}
		return new JdbcPreparedStatement(realConn.prepareStatement(sql,
				columnNames));
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return realConn.createArrayOf(typeName, elements);
	}

	@Override
	public Blob createBlob() throws SQLException {
		return realConn.createBlob();
	}

	@Override
	public Clob createClob() throws SQLException {
		return realConn.createClob();
	}

	@Override
	public NClob createNClob() throws SQLException {
		return realConn.createNClob();
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return realConn.createSQLXML();
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return realConn.createStruct(typeName, attributes);
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return realConn.getClientInfo();
	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return realConn.getClientInfo(name);
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return realConn.isValid(timeout);
	}

	@Override
	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		realConn.setClientInfo(properties);
	}

	@Override
	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		realConn.setClientInfo(name, value);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return realConn.isWrapperFor(iface);
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return realConn.unwrap(iface);
	}

	@Override
	public void abort(Executor executor) throws SQLException {
		realConn.abort(executor);
	}

	@Override
	public int getNetworkTimeout() throws SQLException {
		return realConn.getNetworkTimeout();
	}

	@Override
	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return realConn.getSchema();
	}

	@Override
	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		realConn.setNetworkTimeout(executor, milliseconds);
	}

	@Override
	public void setSchema(String schema) throws SQLException {
		realConn.setSchema(schema);

	}
}
