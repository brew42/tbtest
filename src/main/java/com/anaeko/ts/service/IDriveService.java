package com.anaeko.ts.service;

import java.sql.Timestamp;
import java.util.List;

import org.json.JSONException;

/**
 * @author tombrewster
 * 
 */
public interface IDriveService {

	/**
	 * Post process drives
	 * 
	 * @param filter 
	 * 
	 * @
	 * @return
	 * @throws JSONException
	 */
	void postprocess(String systemUuid)throws JSONException;
	
	/**
	 * Post process drives for defined time period
	 * 
	 * @param systemUuid
	 * @param endTime
	 * @throws JSONException
	 */
	void postprocess(String systemUuid, Timestamp endTime)throws JSONException;

	/**
	 * List systems reporting in time period
	 * 
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<String> listSystems(Timestamp startTime, Timestamp endTime);


}
