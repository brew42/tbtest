
package com.anaeko.ts.config;

import java.sql.Connection;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public interface IDataBase {

	/**
	 * Return Main Connection Template, NamedParameterJdbcTemplate
	 * 
	 * @return
	 */
	public NamedParameterJdbcTemplate connection();

	/**
	 * Return Main Connection Template, JdbcTemplate
	 * 
	 * @return
	 */
	public JdbcTemplate jdbcConnection();

	/**
	 * @return
	 */
	public Connection getConnection();

}
