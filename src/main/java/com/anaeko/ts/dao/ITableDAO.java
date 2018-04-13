package com.anaeko.ts.dao;

import java.util.List;

import org.influxdb.dto.QueryResult.Result;

/**
 * @author tombrewster
 */

public interface ITableDAO  {

	/**
	 * List series in influx db
	 * @return
	 */
	List<Result> list();

	/**
	 * Get specific table details
	 * 
	 * @param tableName
	 * @param type
	 * 
	 * @return
	 */
	List<Result> get(String tableName, String type);

}
