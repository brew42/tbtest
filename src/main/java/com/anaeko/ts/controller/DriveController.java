package com.anaeko.ts.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.anaeko.ts.config.ApiName;
import com.anaeko.ts.service.IDriveService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author tombrewster
 */
@Controller
@RequestMapping(value = "/tsapi/v1/drive")
@Api(value = ApiName.DRIVE)
public class DriveController {

	

	@Autowired
	private IDriveService driveService;
	
	@Singleton
	private ExecutorService executorService = Executors.newFixedThreadPool(10);

	private static final Logger log = LogManager.getLogger("TimeSeries.logging");
	

	/**
	 * Start Drive Post processing
	 * 
	 * @return
	 */
	@RequestMapping(value = "/postprocess/{systemUuid}", method = RequestMethod.POST)
	@ApiOperation(value = "Start Drive Post Processing", notes = "Start Drive Post Processing")
	@ResponseBody
	@ResponseStatus(value = HttpStatus.ACCEPTED)
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Valid Query Executed"),
		    @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Server Error")
		    }
		)
	public ResponseEntity<Map<String, Object>> postProcess(
			@PathVariable String systemUuid,
			@RequestHeader(value = "appToken", defaultValue="", required=true)final String appToken,
			@RequestParam(value = "endTime", defaultValue="", required=true) final Timestamp endTime

	) 
	{
		Map<String, Object> map = new HashMap<String, Object>();
		
		//Validate App has permission
		if (!appToken.equals("75b08df4da2984f028060d72d6d65bf4")) {
			log.warn("Invalid App Token so exit");
			map.put("success", 0);
			map.put("message", "Invalid App Token");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
	
		
		//Execute task sequentially
		executorService.execute(new Runnable() {
			public void run() {
				
				//Call Service
				try {
					
					driveService.postprocess(systemUuid, endTime);
					map.put("success", true);
					
				} catch (JSONException e) 
				{
					map.put("error", e.getLocalizedMessage());
					map.put("success", false);
				}
			}
			
		});
	
		map.put("success", true);
		map.put("statusMessage", "Your create request has been accepted and is being processed.");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(map);
	}
	

	/**
	 * Get list of systems reporting drives
	 * 
	 * @return
	 */
	@RequestMapping(value = "/systems", method = RequestMethod.GET)
	@ApiOperation(value = "Get List of systems", notes = "Get list of systems reporting drives")
	@ResponseBody
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Valid Query Executed"),
		    @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Server Error")
		    }
		)
	public ResponseEntity<Map<String, Object>> listSystems(
			@RequestHeader(value = "appToken", defaultValue="", required=true)final String appToken,
			@RequestParam(value = "startTime", defaultValue="", required=true) final Timestamp startTime,
			@RequestParam(value = "endTime", defaultValue="", required=true) final Timestamp endTime

	) 
	{
		log.info("DriveController get systems");
		Map<String, Object> map = new HashMap<String, Object>();
	
		//Validate App has permission
		if (!appToken.equals("75b08df4da2984f028060d72d6d65bf4")) {
			log.warn("Invalid App Token so exit");
			map.put("success", 0);
			map.put("message", "Invalid App Token");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		
		//Call Service
		List<String> systemList = driveService.listSystems(startTime, endTime);
		map.put("list", systemList);
		map.put("success", true);
			
		
		
		
		return ResponseEntity.ok(map);
	}
	
	
}