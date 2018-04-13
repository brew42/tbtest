package com.anaeko.ts.dao;

import java.sql.Timestamp;
import java.util.List;

public interface IDriveDAO 
{

	/**
	 * Execute post process for defined time
	 * 
	 * @param filter
	 * @return
	 */
	void processDriveStatus(String systemUuid, Timestamp startTime,Timestamp endTime);

	/**
	 * return list of system uuids
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<String> getLatestSystems(Timestamp startTime, Timestamp endTime);
}
