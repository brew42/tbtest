
package com.anaeko.ts.config;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author tombrewster
 */
@Component
public class Database implements IDataBase {

	private static final Logger logger = LogManager.getLogger(Database.class);
	// private Connection conn = null;

	@Autowired
	 private APIConnectionManager apiConnectionManager;

	/**
	 * Return Main Connection Template, NamedParameterJdbcTemplate
	 * 
	 * @return
	 */
	public NamedParameterJdbcTemplate connection() {

		NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;
		try {
			namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getDatasource());
		} catch (SQLException e) {
			logger.error("connection SQLException>",  e);
		}

		return namedParameterJdbcTemplate;
	}

	/**
	 * Return Main Connection Template, JdbcTemplate
	 * 
	 * @return
	 */
	public JdbcTemplate jdbcConnection() {

		JdbcTemplate jdbcTemplate = null;
		try {
			jdbcTemplate = new JdbcTemplate(getDatasource());
		} catch (SQLException e) {

			logger.error("jdbcConnection SQLException>", e);
		}

		return jdbcTemplate;
	}

	/**
	 * 
	 * @return
	 */
	public Connection getConnection() {

		Connection conn = null;
		try {
			
			conn = apiConnectionManager.getConnection();

		} catch (SQLException e) {
			logger.error("getConnection SQLException>", e);
		}
		return conn;
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	private DataSource getDatasource() throws SQLException {

		return apiConnectionManager.getDatasource();
	}

}
