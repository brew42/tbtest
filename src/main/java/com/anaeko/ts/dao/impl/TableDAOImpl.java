
package com.anaeko.ts.dao.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.anaeko.ts.dao.ITableDAO;

/**
 * @author tombrewster
 */
@Component
public class TableDAOImpl extends InfluxDAO implements ITableDAO {

	private static final Logger log = LogManager.getLogger("TimeSeries.logging");

	
	@Value("${influx_name}")
	private String dbName;
	
	
	@Override
	public List<Result> list()
	{
		//Create influx client
		createInfluxClient();
		
		String query = "SHOW SERIES";
	
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		
		return queryResult.getResults();
		
	}

	@Override
	@Cacheable("tableTags")
	public List<Result> get(String tableName, String type) 
	{
		//Create influx client
		createInfluxClient();
		String query = "SHOW " + type + " KEYS from " + tableName;
	
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		
		return queryResult.getResults();
	}

	

}
