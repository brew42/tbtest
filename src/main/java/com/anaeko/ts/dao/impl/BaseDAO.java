package com.anaeko.ts.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.anaeko.ts.config.IDataBase;

public abstract class BaseDAO {

	@Autowired
	private IDataBase database;


	protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return database.connection();
	}

	protected JdbcTemplate getJdbcTemplate() {
		return database.jdbcConnection();
	}

	
}