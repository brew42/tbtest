package com.anaeko.ts.service;

import java.util.List;

import org.influxdb.dto.QueryResult.Result;
import org.json.JSONException;

/**
 * @author tombrewster
 * 
 */
public interface ITableService {

	/**
	 * Table Query - Get list of tables
	 * 
	 * @
	 * @return
	 * @throws JSONException
	 */
	List<Result> list();

	/**
	 * Get table details
	 * 
	 * @param tableName
	 * @parm type
	 * 
	 * @return
	 */
	List<Result> get(String tableName, String type);


}
