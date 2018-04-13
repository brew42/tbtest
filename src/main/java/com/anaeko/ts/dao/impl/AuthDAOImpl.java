package com.anaeko.ts.dao.impl;

import java.nio.file.AccessDeniedException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Component;

import com.anaeko.ts.dao.IAuthDAO;
import com.anaeko.ts.pojo.User;



/**
 * @author tombrewster
 */
@Component
public class AuthDAOImpl extends BaseDAO implements IAuthDAO {

	private static final Logger log = LogManager.getLogger("TimeSeries.logging");
	
	private final static String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	

	@Override
	public List<Map<String, Object>> getAuthSystems(String sessionId) throws AccessDeniedException 
	{
		//Get UserId
		int userId = getUserId(sessionId);

		NamedParameterJdbcTemplate namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);

		String SELECT_AUTH_SYSTEMS = "SELECT s.id AS aclSystemId, s.originalUuid as systemUuid, a.`id` as aclId FROM users u " + 
				"	JOIN acl a ON (u.id = a.userId OR u.role_id = a.roleId) AND a.access = 1 " + 
				"	JOIN systems s ON ((s.id = a.entityId AND a.entityType = 'system') || (s.customerId = a.entityId AND a.entityType = 'customer')) " + 
				"	LEFT JOIN acl black ON ((black.entityId = s.id AND black.entityType = 'system') || (black.entityId = s.customerId AND a.entityType = 'customer')) AND black.access = 0 " + 
				"	WHERE u.id = :userId " + 
				"	AND black.access is null GROUP BY s.id";
		
		List<Map<String, Object>> systemMap = namedParameterJdbcTemplate.queryForList(SELECT_AUTH_SYSTEMS, params);
		if(systemMap.isEmpty())
		{
			log.warn("AuthDAOImpl:User has no access to systems");
			throw new AccessDeniedException("Forbidden Access");
		}
			
		return systemMap;
	}
	
	
	@Override
	public User getUserDetails(String sessionId) throws AccessDeniedException 
	{
		//Get UserId
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", sessionId);
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		params.put("expires_on", dateFormat.format(new Date()));


		String SELECT_USER = "SELECT u.login_name as name, u.admin as admin from users u "
				+ "inner join  sessions s on s.user_id = u.id "
				+ "where s.id = :id and s.expires_on > :expires_on "
				+ "and u.status = 1";
		
		User thisUser = null;
		try {
			thisUser = namedParameterJdbcTemplate.queryForObject(SELECT_USER, params, 
					ParameterizedBeanPropertyRowMapper.newInstance(User.class));
		} catch (DataAccessException e) 
		{
			throw new AccessDeniedException("Forbidden Access");
		}
		
		if(thisUser == null)
		{
			log.warn("AuthDAOImpl:User has no access to systems");
			throw new AccessDeniedException("Forbidden Access");
		}
			
		return thisUser;
	}
	
	
	/*
	 * 
	 * @param sessionId
	 * @return
	 * @throws AccessDeniedException
	 */
	private int getUserId(String sessionId) throws AccessDeniedException 
	{
		//Get UserId
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = getNamedParameterJdbcTemplate();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", sessionId);
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
		params.put("expires_on", dateFormat.format(new Date()));


		String SELECT_USER_SESSION = "SELECT user_id from sessions where id = :id and expires_on > :expires_on";
		
		Map<String, Object> userMap = new HashMap<String, Object>();
		try {
			userMap = namedParameterJdbcTemplate.queryForMap(SELECT_USER_SESSION, params);
		} catch (DataAccessException e) 
		{
			throw new AccessDeniedException("Forbidden Access");
		}
		
		if(userMap.isEmpty() || !userMap.containsKey("user_id"))
		{
			log.warn("AuthDAOImpl:User has no access to systems");
			throw new AccessDeniedException("Forbidden Access");
		}
			
		return (int) userMap.get("user_id");
	}


}
