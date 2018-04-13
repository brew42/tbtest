
package com.anaeko.ts.config;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Provides abstraction from underlaying library used to manage connections.
 * 
 */
@Component
public class APIConnectionManager {
	private static final Logger logger = LogManager.getLogger(APIConnectionManager.class);
	private final String dbHost;
	private final String dbPort;
	private final String dbName;
	private final String username;
	private final String password;
	private final int poolSize;
	private final int abandonedTimeout;
	private final int removeAbandonedTimeout;
	private DataSource dataSource;

	/**
	 * 
	 * @param property
	 * @throws Exception
	 */
	@Autowired
	public APIConnectionManager(Environment env) throws Exception {

		// Init connection details
		this.dbHost = env.getProperty("mysql_host");
		this.dbPort = env.getProperty("mysql_port");
		this.dbName = env.getProperty("mysql_name");
		this.username = env.getProperty("mysql_username");
		this.password = env.getProperty("mysql_password");
		this.poolSize = Integer.parseInt(env.getProperty("mysql_poolsize"));
		this.abandonedTimeout = Integer.parseInt(env.getProperty("jdbc_connection_pool_removeAbandonedTimeout"));
		this.removeAbandonedTimeout = Integer.parseInt(env.getProperty("remove_abandoned_timeout"));

		logger.info("Db Connection Url " + this.dbHost + "================== ");
		logger.info("Db Connection Port " + this.dbPort + "================== ");
		logger.info("Db Connection Pool Size " + this.poolSize + "================== ");

		initializePool();
	}

	/**
	 * initializePool
	 * 
	 * @throws Exception
	 */
	private void initializePool() throws Exception {
		logger.info("initializePool");

		PoolProperties pool = new PoolProperties();
		String connection = "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
				+ "?useUnicode=yes&characterEncoding=UTF-8&autoReconnect=true&failOverReadOnly=false&maxReconnects=10";
		pool.setUrl(connection);
		pool.setDriverClassName("com.mysql.jdbc.Driver");
		pool.setUsername(username);
		pool.setPassword(password);
		pool.setTestWhileIdle(true);
		pool.setTestOnBorrow(false);
		pool.setValidationQuery("SELECT 1");
		pool.setValidationInterval(30000);
		pool.setMaxActive(poolSize * 4);
		pool.setInitialSize(poolSize);
		pool.setMinIdle(poolSize / 2);
		pool.setMaxIdle(poolSize);
		pool.setMaxAge(360000);
		pool.setTimeBetweenEvictionRunsMillis(5000);
		pool.setMaxWait(5000);
		pool.setRemoveAbandoned(true);
		pool.setRemoveAbandonedTimeout(abandonedTimeout);
		pool.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		pool.setDefaultAutoCommit(true);// Mark as true for API
		pool.setName("mainPool");
		pool.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.StatementCache;"
				+ "org.apache.tomcat.jdbc.pool.interceptor.SlowQueryReportJmx(threshold=5000)");

		// Init Data sources & set Pool
		dataSource = new DataSource();
		dataSource.setPoolProperties(pool);
		logger.info("Total main connections ==> " + dataSource.getMaxActive());

		// Add Shutdown hook to be called when process is stopped
		Runtime.getRuntime().addShutdownHook(new Thread("Shutdown-Tomcat-JDBC Connection") {

			@Override
			public void run() {
				try {
					logger.info("Shutting down Tomcat-JDBC pool {}", dataSource.getUrl());
				} catch (Exception bug) {
					logger.debug("Could not Run", bug);
				}
				if (null != dataSource) {
					dataSource.close(true);
				}

			}

		});
	}

	/**
	 * Shutdown hook to close connections
	 */
	public void shutdown() {
		logger.info("Shutdown");
		try {
			if (dataSource != null) {
				dataSource.close();
			}
		} catch (Exception e) {
			logger.error("Exception occurred shutting down database manager.", e);
		}
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {

		logger.debug("Pool Name ==> " + dataSource.getName());
		logger.debug("Total Active connections ==> " + dataSource.getActive());
		logger.debug("Total Idle connections ==> " + dataSource.getIdle());
		logger.debug("Total Pool size ==> " + dataSource.getPoolSize());
		logger.debug("Normal Url ==> " + dataSource.getUrl());
		return dataSource.getConnection();
	}

	/**
	 * 
	 * @return
	 */
	public DataSource getDatasource() {
		return dataSource;
	}

}