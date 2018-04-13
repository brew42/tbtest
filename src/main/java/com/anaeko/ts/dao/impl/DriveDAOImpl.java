
package com.anaeko.ts.dao.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.influxdb.impl.InfluxDBResultMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.anaeko.ts.dao.IDriveDAO;
import com.anaeko.ts.pojo.Drive;

/**
 * @author tombrewster
 */
@Component
public class DriveDAOImpl extends InfluxDAO implements IDriveDAO {

	private static final Logger log = LogManager.getLogger("TimeSeries.logging");
	
	private final int CHUNK_SIZE = 100;
	private final int DAY = 1;
	private static final String CHUNK_READ_COMPLETE = "DONE";
	private static final String DRIVES_TABLE = "ten_days.driveStatus";
	private static final String DRIVE_STATUS_DM = "driveStateChange_dm";
	private static final String DRIVE_STATUS_SP = "driveStateChange_sp";
	private static final String DRIVE_STATUS_SYS = "driveStateChange_sys";
	
	private static final String AFR_DM = "afr_dm";
	private static final String AFR_SP = "afr_sp";
	private static final String AFR_SYS = "afr_sys";
	
	private static final String GROUP_BY_DM = "driveModel";
	private static final String GROUP_BY_SP = "storagePoolGroup,systemUuid";
	private static final String GROUP_BY_SYS = "systemUuid";

	
	private InfluxDBResultMapper resultMapper;
	
	

	
	@Value("${influx_name}")
	private String dbName;
	
	

	@Override
	public void processDriveStatus(String systemUuid,Timestamp startTime,Timestamp endTime) 
	{
		//Create influx client
		createInfluxClient();
				
		//Init connection
		resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
		
		//Call initial method to get last ingestion
		getLastIngestion(systemUuid, startTime, endTime);
		
	}
	
	@Override
	public List<String> getLatestSystems(Timestamp startTime,Timestamp endTime) {
		
		//Create influx client
		createInfluxClient();
		
		//Init connection
		resultMapper = new InfluxDBResultMapper(); // thread-safe - can be reused
		
		String query = "select last(driveModel) from " + DRIVES_TABLE + " WHERE " +
				"time > '" + startTime + "' AND " + 
				"time < '" + endTime + "' " +
				"GROUP BY systemUuid";
		log.info("Get Latest Ingestion Query>>" + query);
		
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		
		List<String> systemUuids = new ArrayList<String>();
		for (Result result : queryResult.getResults())
		{
			if(result == null || result.getSeries() == null)
				return systemUuids;
			
			for (Series series : result.getSeries())
			{
				Map<String, String> tagMap = series.getTags();
				systemUuids.add(tagMap.get("systemUuid"));
			}
		}
		
		return systemUuids;
	}
	
	
	
	
	/*
	 * Get latest drive ingestion
	 * 
	 * @param systemUuid
	 * @param startTime
	 * @param endTime
	 */
	private void getLastIngestion(String systemUuid, Timestamp startTime,Timestamp endTime)
	{
		//Build Query
		String query = "SELECT smart9RawValue,driveModel,driveSerial,storagePoolGroup,driveStatus,systemUuid,driveBay,deviceHostname "
				+ "FROM " + DRIVES_TABLE + " WHERE systemUuid = '" + systemUuid +"' AND " +  
				"time > '" + startTime + "' AND " + 
				"time < '" + endTime + "'";
		
		log.debug("Get Last Ingestion Query>>" + query);
		
		//Init list
		List<Drive> latestDrives = new ArrayList<Drive>();
		
		//Chuncked Query
		influxDB.query(new Query(query, dbName), CHUNK_SIZE, 
				new  Consumer<QueryResult>() {
			     @Override
			     public void accept(QueryResult result) {
			 
			         if(CHUNK_READ_COMPLETE.equalsIgnoreCase(result.getError()))
			         {
			        	 	log.info("Last Ingested System:" + systemUuid + " with " 
			        	 			+ latestDrives.size() + " drives on " + endTime);
			        	 	
			        	 	//Check time when system was last ingested over the past week   
		                Calendar cal = Calendar.getInstance();
		                cal.setTimeInMillis(startTime.getTime());
		                cal.add(Calendar.DATE, -DAY*7);
		                Timestamp prevIngestionStart = new Timestamp(cal.getTime().getTime());
		                
		                //Call to compare drives
		        	 		if(latestDrives.isEmpty())
		        	 		{
		        	 			log.info("Nothing to compare this time around");
		        	 			return;
		        	 		}
		        	 	
		                //Get the previous ingestion of drives
		                getPreviousIngestion(latestDrives, systemUuid, prevIngestionStart, startTime, endTime);
			         }
			         else
			         {

			        	 	//Write data to internal memory
			        	 	latestDrives.addAll(resultMapper.toPOJO(result, Drive.class));
					 	
			         }
			     }
			 });
	}
	
	/*
	 * Get number of drive models for time period
	 * 
	 * @param driveModel
	 * @param startTime
	 * @param endTime
	 */
	private double getDriveModelCount(String driveModel, Timestamp auditTime)
	{
		double modelCount = 0;
		
		Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(auditTime.getTime());
        cal.add(Calendar.DATE, -1);
        Timestamp startTime = new Timestamp(cal.getTime().getTime());
        
		//Build Query
		String query = "select count(\"driveModel\")"
				+ "FROM " + DRIVES_TABLE + " WHERE driveModel = '" + driveModel +"' AND " +  
				"time > '" + startTime + "' AND " + 
				"time < '" + auditTime + "'";
		log.debug("Get Model Count>>" + query);
		
		
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		
		for (Result result : queryResult.getResults())
		{
			if(result == null || result.getSeries() == null)
				return 0;
			
			for (Series series : result.getSeries())
			{
				List<List<Object>> resultList = series.getValues();
				if(!resultList.isEmpty())
				{
					List<Object> valueList = resultList.get(0);
					modelCount = (double) valueList.get(1);
				}
			}
		}
		
		return modelCount;
	}
	
	/*
	 * Get Drive count for SP
	 * 
	 * @param systemUuid
	 * @param storagePoolGroup
	 * @param auditTime
	 * @return
	 */
	private double getDriveSPCount(String systemUuid, String storagePoolGroup, Timestamp auditTime) 
	{
		
		double spCount = 0;
		
		Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(auditTime.getTime());
        cal.add(Calendar.DATE, -1);
        Timestamp startTime = new Timestamp(cal.getTime().getTime());
        
		//Build Query
		String query = "select count(\"storagePoolGroup\") "
				+ "FROM " + DRIVES_TABLE + " WHERE systemUuid = '" + systemUuid + "' AND storagePoolGroup = '" + storagePoolGroup + "' AND "  + 
				"time > '" + startTime + "' AND " + 
				"time < '" + auditTime + "'";
		log.debug("Get SP Count>>" + query);
		
		
		QueryResult queryResult = influxDB.query(new Query(query, dbName));
		
		for (Result result : queryResult.getResults())
		{
			if(result == null || result.getSeries() == null)
				return 0;
			
			for (Series series : result.getSeries())
			{
				List<List<Object>> resultList = series.getValues();
				if(!resultList.isEmpty())
				{
					List<Object> valueList = resultList.get(0);
					spCount = (double) valueList.get(1);
				}
			}
		}
		
		return spCount;
	}
	
	/*
	 * Get previous drive ingestion
	 * 
	 * @param latestDrives
	 * @param systemUuid
	 * @param startTime
	 * @param endTime
	 * @param auditTime
	 */
	private void getPreviousIngestion(List<Drive> latestDrives, String systemUuid, Timestamp startTime,Timestamp endTime, Timestamp auditTime)
	{
		//Build Query
		String query = "SELECT smart9RawValue,driveModel,driveSerial,storagePoolGroup,driveStatus,systemUuid,driveBay,deviceHostname "
				+ "FROM " + DRIVES_TABLE + " WHERE systemUuid = '" + systemUuid +"' AND " +  
				"time > '" + startTime + "' AND " + 
				"time < '" + endTime + "'";
		log.debug("Get Previous Ingestion Query>>" + query);
		
		//Init list
		List<Drive> previousDrives = new ArrayList<Drive>();
		
		//Chuncked Query
		influxDB.query(new Query(query, dbName), CHUNK_SIZE, 
				new  Consumer<QueryResult>() {
			     @Override
			     public void accept(QueryResult result) {
			 
			         if(CHUNK_READ_COMPLETE.equalsIgnoreCase(result.getError()))
			         {
			        	 	log.info("Previous System:" + systemUuid + " with " + previousDrives.size() + " drives");
			        	 	
			        	 	//Call to compare drives
			        	 	if(!latestDrives.isEmpty() && !previousDrives.isEmpty())
			        	 	{	
			        	 		compareDrives(latestDrives, previousDrives, auditTime);
			        	 	}
			        	 	else
			        	 		log.info("Nothing to compare this time around");
			         }
			         else
			         {
			        	 	//Write data to internal memory
			        	 	previousDrives.addAll(resultMapper.toPOJO(result, Drive.class));
					 	
			         }
			         
			     }

			 });
	}

	/*
	 * Compare each drive ingestion set
	 * 
	 * @param previousDrives
	 * @param latestDrives
	 * @param endTime
	 */
	private void compareDrives(List<Drive> latestDrives, List<Drive> previousDrives, Timestamp auditTime)
	{
	    	log.info("Compare drives");
	    	int i = 0;
	    	List<Drive> statusChangeList = new ArrayList<Drive>();
    		for (Drive latestDrive : latestDrives) {
    			
    			i++;
    			log.info("Outer counter>>" + i);
    			if(StringUtils.isBlank(latestDrive.getDriveBay()))
    				continue;
    			
    			if(!previousDrives.contains(latestDrive))
    			{
    				log.debug("Previous ingestion doesnt contain drive in bay");
    				
    				log.debug("Latest Drive serial:" + latestDrive.getDriveSerial());
    				log.debug("Latest Drive bay:" + latestDrive.getDriveBay());
    				log.debug("Latest Device alias:" + latestDrive.getDeviceHostname());
    				//Check if previous ingestion has a different serial number in that bay
    				for (Drive prevDrive : previousDrives) 
    				{
    					if(StringUtils.isBlank(prevDrive.getDriveBay()))
    	    					continue;
    					
    					//If same device and same bay but different serial then report replaced
    					if(prevDrive.getDriveBay().equalsIgnoreCase(latestDrive.getDriveBay())
    							&& prevDrive.getDeviceHostname().equalsIgnoreCase(latestDrive.getDeviceHostname()))
    					{
    						//If we have caught replaced drive already lets continue
    						if(statusChangeList.contains(prevDrive))
    							continue;
    						
    						//Get Model Count for this day
    						double modelCount = getDriveModelCount(prevDrive.getDriveModel(), auditTime);
    						prevDrive.setNumberModelDrives(modelCount);
    						
    						//Get SP Count
    						double spCount = getDriveSPCount(prevDrive.getSystemUuid(), prevDrive.getStoragePoolGroup(), auditTime);
    						prevDrive.setNumberSPDrives(spCount);
    						
    						//Reporting replaced drive
    						log.info("*Replaced Drive:" + prevDrive.getDriveSerial());
    						log.debug("*Previous Drive serial:" + prevDrive.getDriveSerial());
    	    					log.debug("*Previous Drive bay:" + prevDrive.getDriveBay());
    	    					log.debug("*Previous Device alias:" + prevDrive.getDeviceHostname());
    						prevDrive.setNumberSystemDrives(latestDrives.size());
    						statusChangeList.add(prevDrive);

    						log.info("Model Drive Count:" + modelCount);
    						log.info("SP Drive Count:" + spCount);
    						log.info("Sys Drive Count:" + latestDrives.size());
    					}
    					else
    					{
    						//log.info("***Missing Drive:" + latestDrive.getDriveSerial());
    					}
    				}
    			}
    			
    		}
    		
    		log.info("Hey im done with loops");
    		//Write status changes to influx
    		if(!statusChangeList.isEmpty())
    			writeStateChanges(statusChangeList, auditTime);
    		else
    			log.info("No state changes to report");
    	}
	
	

	/*
	 *  Write status changes to the db
	 *  
	 * @param statusChangeList
	 * @param auditTime
	 */
	private void writeStateChanges( 	List<Drive> statusChangeList, Timestamp auditTime)
	{
		log.info("Write state " +  statusChangeList.size() + " changes for system" 
				+ statusChangeList.get(0).getSystemUuid() 
				+ " on " + auditTime);
		
		influxDB.enableBatch(1000, 100, TimeUnit.MILLISECONDS);
		influxDB.setDatabase(dbName);
		
		
		for(Drive thisDrive : statusChangeList)
		{
			//Verify Data & write to db
			
			//Model
			if(StringUtils.isNotBlank(thisDrive.getDriveModel()))
			{
				influxDB.write(Point.measurement(DRIVE_STATUS_DM)
						.time(auditTime.getTime(), TimeUnit.MILLISECONDS)
						.tag("driveModel", thisDrive.getDriveModel())
						.tag("driveSerial", thisDrive.getDriveSerial())
						.addField("poh", thisDrive.getPoh()) 
						.addField("driveCount", thisDrive.getNumberModelDrives())
						.build());
			}
			
			//SP
			if(StringUtils.isNotBlank(thisDrive.getStoragePoolGroup()) &&
					StringUtils.isNotBlank(thisDrive.getSystemUuid()))
			{
				influxDB.write(Point.measurement(DRIVE_STATUS_SP)
						.time(auditTime.getTime(), TimeUnit.MILLISECONDS)
						.tag("systemUuid", thisDrive.getSystemUuid())
						.tag("storagePoolGroup", thisDrive.getStoragePoolGroup())
						.tag("driveSerial", thisDrive.getDriveSerial())
						.addField("poh", thisDrive.getPoh())
						.addField("driveCount", thisDrive.getNumberSPDrives())
						.build());
			}
			
			//System
			if(StringUtils.isNotBlank(thisDrive.getSystemUuid()))
			{
				influxDB.write(Point.measurement(DRIVE_STATUS_SYS)
						.time(auditTime.getTime(), TimeUnit.MILLISECONDS)
						.tag("systemUuid", thisDrive.getSystemUuid())
						.tag("driveSerial", thisDrive.getDriveSerial())
						.addField("poh", thisDrive.getPoh())
						.addField("driveCount", thisDrive.getNumberSystemDrives())
						.build());
			}
		}
		
		//Refresh AFR Views
		refreshAFR(DRIVE_STATUS_DM, AFR_DM, GROUP_BY_DM);
		refreshAFR(DRIVE_STATUS_SP, AFR_SP, GROUP_BY_SP);
		refreshAFR(DRIVE_STATUS_SYS, AFR_SYS, GROUP_BY_SYS);

		influxDB.close();
	}
	
	
	/*
	 * Get number of drive models for time period
	 * 
	 * @param sourceTable
	 * @param afrView
	 * @param groupBy
	 */
	private void refreshAFR(String sourceTable, String afrView, String groupBy)
	{
        
		//Build Query
		String query = "select CUMULATIVE_SUM(count(\"driveCount\"))*100/mean(\"driveCount\") as afr "
				+ "INTO " + afrView + " from " + sourceTable + 
				" GROUP BY time(1d), " + groupBy;
		log.info("Afr view refresh>>" + query);
		influxDB.query(new Query(query, dbName));
		
	}
	
	
	    	
}
