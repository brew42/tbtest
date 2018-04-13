package com.anaeko.ts.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.QueryResult.Result;
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

import com.anaeko.ts.config.ApiName;
import com.anaeko.ts.service.ITableService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author tombrewster
 */
@Controller
@RequestMapping(value = "/tsapi/v1/table")
@Api(value = ApiName.TABLE)
public class TableController {

	

	@Autowired
	private ITableService tableService;
	

	private static final Logger log = LogManager.getLogger("TimeSeries.logging");
	

	/**
	 * Get Schema details
	 * 
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiOperation(value = "Table Query", notes = "Table Query")
	@ResponseBody
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Valid Query Executed"),
		    @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Server Error")
		    }
		)
	public ResponseEntity<Map<String, Object>> list(
			@RequestHeader(value = "appToken", defaultValue="", required=true)final String appToken
	) 
	{
		log.info("Table Controller query");
		Map<String, Object> map = new HashMap<String, Object>();
	
		//Validate App has permission
		if (!appToken.equals("75b08df4da2984f028060d72d6d65bf4")) {
			log.warn("Invalid App Token so exit");
			map.put("success", 0);
			map.put("message", "Invalid App Token");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		
		//Call Service
		List<Result> tableList = tableService.list();
		map.put("list", tableList);
		map.put("success", true);
		
		return ResponseEntity.ok(map);
	}
	
	
	/**
	 * Get specific details for a defined table
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{tableName}", method = RequestMethod.GET)
	@ApiOperation(value = "Table Query", notes = "Get table info on tag or field keys")
	@ResponseBody
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Valid Query Executed"),
		    @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Server Error")
		    }
		)
	public ResponseEntity<Map<String, Object>> get(
			@PathVariable String tableName,
			@RequestHeader(value = "appToken", defaultValue="", required=true)final String appToken,
			@RequestParam(value = "type", defaultValue="TAG or FIELD", required=true) final String type
			) 
	{
		log.info("Table Controller query");
		Map<String, Object> map = new HashMap<String, Object>();
	
		//Validate App has permission
		if (!appToken.equals("75b08df4da2984f028060d72d6d65bf4")) {
			log.warn("Invalid App Token so exit");
			map.put("success", 0);
			map.put("message", "Invalid App Token");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		
		//Call Service
		List<Result> tableList = tableService.get(tableName, type);
		map.put("list", tableList);
		map.put("success", true);
			
		return ResponseEntity.ok(map);
	}
	
	
	
}