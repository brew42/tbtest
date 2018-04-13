package com.anaeko.ts.dao;

import java.util.List;
import java.util.Map;

import org.influxdb.dto.QueryResult.Result;

/**
 * @author tombrewster
 */

public interface IAggDAO  {

	/**
	 * Execute agg query with list of auth systems
	 * 
	 * @param filter
	 * @param authSystems 
	 * @return
	 */
	List<Result> query(String tableName, String whereClause, 
			String filter, String groupBy, List<Map<String, Object>> authSystems);

	/**
	 * Execute agg query with no system filter
	 * 
	 * @param tableName
	 * @param whereClause
	 * @param filter
	 * @param groupBy
	 * @return
	 */
	List<Result> query(String tableName, String whereClause, 
			String filter, String groupBy);

}
