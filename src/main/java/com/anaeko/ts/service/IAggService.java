package com.anaeko.ts.service;

import java.util.List;
import java.util.Map;

import org.influxdb.dto.QueryResult.Result;
import org.json.JSONException;

import com.anaeko.ts.pojo.AggFilter;

/**
 * @author tombrewster
 * 
 */
public interface IAggService {

	/**
	 * Aggregation Query with list of auth systems
	 * 
	 * @param filter 
	 * @param authSystems 
	 * 
	 * @
	 * @return
	 * @throws JSONException
	 */
	List<Result> query(AggFilter filter, List<Map<String, Object>> authSystems)throws JSONException;

	/**
	 * Agg Query without list of systems
	 * 
	 * @param filter
	 * @return
	 */
	List<Result> query(AggFilter filter);


}
