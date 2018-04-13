
package com.anaeko.ts.dao.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.anaeko.ts.dao.IAggDAO;

/**
 * @author tombrewster
 */
@Component
public class AggDAOImpl extends InfluxDAO implements IAggDAO {

	private static final Logger log = LogManager.getLogger("TimeSeries.logging");
	
	@Value("${influx_name}")
	private String dbName;
	
	@Override
	@Cacheable("aggsAuth")
	public List<Result> query(String tableName, String whereClause, 
			String filter, String groupBy,
			List<Map<String, Object>> authSystems)
	{
		//Create influx client
		createInfluxClient();
		
		//Add Filter
		String query = "SELECT " + whereClause + " FROM " + tableName;
		if(StringUtils.isNotBlank(filter))
		{
			query += " " + filter;
			query += " AND (";
			query += buildFilter(authSystems);
			query += " )";
		}
		else
		{
			query += " WHERE (";
			query += buildFilter(authSystems);
			query += " )";
		}
		
		//Add group by
		if(StringUtils.isNotBlank(groupBy))
			query += " " + groupBy;
		
		log.info("Query:" + query);
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		
		return queryResult.getResults();
		
	}
	
	
	@Override
	@Cacheable("aggs")
	public List<Result> query(String tableName, String whereClause, 
			String filter, String groupBy)
	{
		//Create influx client
		createInfluxClient();
		
		
		//Add Filter
		String query = "SELECT " + whereClause + " FROM " + tableName;
		if(StringUtils.isNotBlank(filter))
		query += " " + filter;
		
		//Add group by
		if(StringUtils.isNotBlank(groupBy))
			query += " " + groupBy;
		
		log.info("Query:" + query);
	
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		
		return queryResult.getResults();
		
	}
	
	/*
	 * Build filter with systemIds for policy
	 * 
	 * @param authSystems
	 * @return
	 */
	private String buildFilter(List<Map<String, Object>> authSystems)
	{
		//Update query with authenticated systems.
		String query = "";
		boolean isFirst = true;
		for(Map<String, Object> system : authSystems)
		{
			String systemUuid = (String)system.get("systemUuid");
			if(isFirst)
			{
				query += " systemUuid = '" + systemUuid + "'";
				isFirst = false;
			}
			else
				query += " OR systemUuid = '" + systemUuid + "'";
				
		}
		
		return query;
	}
	
	
	

	

}
