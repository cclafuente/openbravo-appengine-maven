package com.j256.ormlite.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.db.DatabaseTypeUtils;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.BaseConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.support.DatabaseConnectionProxyFactory;

/**
 * Implementation of the ConnectionSource interface that supports what is needed by ORMLite. This is not thread-safe nor
 * synchronized. For other dataSources, see the {@link DataSourceConnectionSource} class.
 * 
 * <p>
 * <b> NOTE: </b> If you are using the Spring type wiring in Java, {@link #initialize} should be called after all of the
 * set methods. In Spring XML, init-method="initialize" should be used.
 * </p>
 * 
 * @author graywatson
 */
public class JdbcConnectionSource extends BaseConnectionSource implements ConnectionSource {

	private static Logger logger = LoggerFactory.getLogger(JdbcConnectionSource.class);

	private String url;
	private String username;
	private String password;
	private DatabaseConnection connection;
	protected DatabaseType databaseType;
	protected boolean initialized = false;
	private static DatabaseConnectionProxyFactory connectionProxyFactory;

	/**
	 * Constructor for Spring type wiring if you are using the set methods. If you are using Spring then your should
	 * use: init-method="initialize"
	 */
	public JdbcConnectionSource() {
		// for spring type wiring
	}

	/**
	 * Create a data source for a particular database URL.
	 * 
	 * @param url
	 *            The database URL which should start jdbc:...
	 * @throws SQLException
	 *             If the driver associated with the database driver is not found in the classpath.
	 */
	public JdbcConnectionSource(String url) throws SQLException {
		this(url, null, null, null);
	}

	/**
	 * Create a data source for a particular database URL. The databaseType is usually determined from the databaseUrl
	 * so most users should call {@link #JdbcConnectionSource(String)} instead. If, however, you need to force the class
	 * to use a specific DatabaseType then this constructor should be used.
	 * 
	 * @param url
	 *            The database URL which should start jdbc:...
	 * @param databaseType
	 *            Database to associate with this connection source.
	 * @throws SQLException
	 *             If the driver associated with the database driver is not found in the classpath.
	 */
	public JdbcConnectionSource(String url, DatabaseType databaseType) throws SQLException {
		this(url, null, null, databaseType);
	}

	/**
	 * Create a data source for a particular database URL with username and password permissions.
	 * 
	 * @param url
	 *            The database URL which should start jdbc:...
	 * @param username
	 *            Username for permissions on the database.
	 * @param password
	 *            Password for permissions on the database.
	 * @throws SQLException
	 *             If the driver associated with the database driver is not found in the classpath.
	 */
	public JdbcConnectionSource(String url, String username, String password) throws SQLException {
		this(url, username, password, null);
	}

	/**
	 * Create a data source for a particular database URL with username and password permissions. The databaseType is
	 * usually determined from the databaseUrl so most users should call
	 * {@link #JdbcConnectionSource(String, String, String)} instead. If, however, you need to force the class to use a
	 * specific DatabaseType then this constructor should be used.
	 * 
	 * @param url
	 *            The database URL which should start jdbc:...
	 * @param username
	 *            Username for permissions on the database.
	 * @param password
	 *            Password for permissions on the database.
	 * @param databaseType
	 *            Database to associate with this connection source.
	 * @throws SQLException
	 *             If the driver associated with the database driver is not found in the classpath.
	 */
	public JdbcConnectionSource(String url, String username, String password, DatabaseType databaseType)
			throws SQLException {
		this.url = url;
		this.username = username;
		this.password = password;
		this.databaseType = databaseType;
		initialize();
	}

	/**
	 * Initialize the class after the setters have been called. If you are using the no-arg constructor and Spring type
	 * wiring, this should be called after all of the set methods.
	 * 
	 * @throws SQLException
	 *             If the driver associated with the database URL is not found in the classpath.
	 */
	public void initialize() throws SQLException {
		if (initialized) {
			return;
		}
		if (url == null) {
			throw new SQLException("url was never set on " + getClass().getSimpleName());
		}
		if (databaseType == null) {
			databaseType = DatabaseTypeUtils.createDatabaseType(url);
		}
		databaseType.loadDriver();
		databaseType.setDriver(DriverManager.getDriver(url));
		initialized = true;
	}

	public void close() throws SQLException {
		if (!initialized) {
			throw new SQLException(getClass().getSimpleName() + " was not initialized properly");
		}
		if (connection != null) {
			connection.close();
			logger.debug("closed connection #{}", connection.hashCode());
			connection = null;
		}
	}

	public void closeQuietly() {
		try {
			close();
		} catch (SQLException e) {
			// ignored
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public DatabaseConnection getReadOnlyConnection() throws SQLException {
		if (!initialized) {
			throw new SQLException(getClass().getSimpleName() + " was not initialized properly");
		}
		return getReadWriteConnection();
	}

	public DatabaseConnection getReadWriteConnection() throws SQLException {
		if (!initialized) {
			throw new SQLException(getClass().getSimpleName() + " was not initialized properly");
		}
		if (connection != null) {
			if (connection.isClosed()) {
				throw new SQLException("Connection has already been closed");
			} else {
				return connection;
			}
		}
		connection = makeConnection(logger);
		return connection;
	}

	public void releaseConnection(DatabaseConnection connection) throws SQLException {
		if (!initialized) {
			throw new SQLException(getClass().getSimpleName() + " was not initialized properly");
		}
		// noop right now
	}

	@SuppressWarnings("unused")
	public boolean saveSpecialConnection(DatabaseConnection connection) throws SQLException {
		// noop since this is a single connection source
		return true;
	}

	public void clearSpecialConnection(DatabaseConnection connection) {
		// noop since this is a single connection source
	}

	public DatabaseType getDatabaseType() {
		if (!initialized) {
			throw new IllegalStateException(getClass().getSimpleName() + " was not initialized properly");
		}
		return databaseType;
	}

	public boolean isOpen() {
		return connection != null;
	}

	// not required
	public void setUsername(String username) {
		this.username = username;
	}

	// not required
	public void setPassword(String password) {
		this.password = password;
	}

	// not required
	public void setDatabaseType(DatabaseType databaseType) {
		this.databaseType = databaseType;
	}

	/**
	 * Set to enable connection proxying. Set to null to disable.
	 */
	public static void setDatabaseConnectionProxyFactory(DatabaseConnectionProxyFactory connectionProxyFactory) {
		JdbcConnectionSource.connectionProxyFactory = connectionProxyFactory;
	}

	/**
	 * Make a connection to the database.
	 * 
	 * @param logger
	 *            This is here so we can use the right logger associated with the sub-class.
	 */
	/*protected DatabaseConnection makeConnection(Logger logger) throws SQLException {
		Properties properties = new Properties();
		if (username != null) {
			properties.setProperty("user", username);
		}
		if (password != null) {
			properties.setProperty("password", password);
		}
		DatabaseConnection connection = new JdbcDatabaseConnection(DriverManager.getConnection(url, properties));
		// by default auto-commit is set to true
		connection.setAutoCommit(true);
		if (connectionProxyFactory != null) {
			connection = connectionProxyFactory.createProxy(connection);
		}
		logger.debug("opened connection to {} got #{}", url, connection.hashCode());
		return connection;
	}*/
	
	protected DatabaseConnection makeConnection(Logger logger) throws SQLException {
		logger.warn(" ****************** Adquiriendo conexion de appengine ******************** ");
		DatabaseConnection connection;
		String url;
		if (System.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {
			// Check the System properties to determine if we are running on appengine or not
			// Google App Engine sets a few system properties that will reliably be present on a remote
			// instance.
			url = System.getProperty("ae-cloudsql.cloudsql-database-url");
			try {
				// Load the class that provides the new "jdbc:google:mysql://" prefix.
				Class.forName("com.mysql.jdbc.GoogleDriver");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Error loading Google JDBC Driver", e);
			}
    		} else {
    			// Set the url with the local MySQL database connection url when running locally
    			url = System.getProperty("ae-cloudsql.local-database-url");
    		}
		try {
			connection = new JdbcDatabaseConnection(DriverManager.getConnection(url));
		} catch (Exception e) {
			throw new RuntimeException("SQL error", e);
		}
		return connection;
	}
	
}