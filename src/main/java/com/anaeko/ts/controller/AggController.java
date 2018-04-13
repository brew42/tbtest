package com.anaeko.ts.controller;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.anaeko.ts.config.ApiName;
import com.anaeko.ts.pojo.AggFilter;
import com.anaeko.ts.pojo.User;
import com.anaeko.ts.service.IAggService;
import com.anaeko.ts.service.IAuthService;
import com.anaeko.ts.service.ITableService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * @author tombrewster
 */
@Controller
@RequestMapping(value = "/tsapi/v1/aggregation")
@Api(value = ApiName.AGGREGATION)
public class AggController {

	private final static String TAGS = "tag"; 
	private final static String SYSTEM_UUID = "systemUuid"; 
	private final static int ADMIN_USER = 1; 

	@Autowired
	private IAggService aggService;
	
	@Autowired
	private IAuthService authService;
	
	@Autowired
	private ITableService tableService;
	
	private static final Logger log = LogManager.getLogger("TimeSeries.logging");
	

	/**
	 * Get Aggregation Query
	 * 
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiOperation(value = "Aggregation Query", notes = "Aggregation Query")
	@ResponseBody
	@ApiResponses(value = {
		    @ApiResponse(code = 200, message = "Valid Query Executed"),
		    @ApiResponse(code = 400, message = "Bad Request"),
			@ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 500, message = "Server Error")
		    }
		)
	public ResponseEntity<Map<String, Object>> authQuery(
			@RequestHeader(value = "appToken", defaultValue="", required=true)final String appToken,
			@Valid @RequestBody AggFilter filter,
			HttpServletRequest request
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
		
		//Get session Id from cookies
		String sessionId  = "";
		Cookie[] cookies = request.getCookies();
		if(cookies == null)
		{
			log.warn("No SessionId so exit");
			map.put("success", 0);
			map.put("message", "Invalid SessionId in cookie");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
			
		}
		for (Cookie cookie : cookies) {
		     if ("session".equals(cookie.getName())) 
		    	 		sessionId = cookie.getValue();
		}
		
		//Validate session Id
		if(StringUtils.isBlank(sessionId))
		{
			log.warn("Invalid SessionId so exit");
			map.put("success", 0);
			map.put("message", "Invalid SessionId in cookie");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		
		
		//Validate User
		User thisUser = null;
		try {
			thisUser = authService.getUserDetails(sessionId);
			
		} catch (AccessDeniedException e) {
			map.put("error", "Access Denied");
			map.put("success", false);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
		}
		
		
		
		//Check table to verfiy if we need to implement systemUuid policy check
		List<Result> tagList = tableService.get(filter.getTableName(), TAGS);
		if(implementPolicy(tagList, thisUser))
		{
			//Authenticate User for systems
			List<Map<String, Object>> authSystems = null;
			try {
				
				authSystems = authService.authSystems(sessionId);
				
			} catch (AccessDeniedException e) {
				map.put("error", "Access Denied");
				map.put("success", false);
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(map);
			}
			
			
			//Call Service
			List<Result> aggList;
			try {
				aggList = aggService.query(filter, authSystems);
				map.put("list", aggList);
				map.put("success", true);
				
			} catch (JSONException e) 
			{
				map.put("error", e.getLocalizedMessage());
				map.put("success", false);
			}
		}
		else
		{
			//Call Service with no list of systems
			List<Result>aggList = aggService.query(filter);
			map.put("list", aggList);
			map.put("success", true);
		}
		
		
		
		return ResponseEntity.ok(map);
	}

	/*
	 * Check table and user admin priv
	 * 
	 * Do not implement policy if user is admin or if table does not contain systemUuid
	 * @param tagList
	 * @return
	 */
	private boolean implementPolicy(List<Result> tagList, User thisUser) {
		
		//Check user
		if(thisUser.getAdmin() == ADMIN_USER)
			return false;
		
		//Get Result
		if(tagList.isEmpty())
			return false;
		Result result = tagList.get(0);
		List<Series> seriesList = result.getSeries();
		
		//Get Series
		if(seriesList.isEmpty())
			return false;
		Series series = seriesList.get(0);
		List<List<Object>> valueList = series.getValues();
		for(List<Object> value : valueList)
		{
			if(value.isEmpty())
				return false;
			String valueStr = (String) value.get(0);
			if(valueStr.equalsIgnoreCase(SYSTEM_UUID))
				return true;
		}
		
		return false;
	}
	
	
	
	
	
}