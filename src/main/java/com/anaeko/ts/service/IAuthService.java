package com.anaeko.ts.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.anaeko.ts.pojo.User;

/**
 * @author tombrewster
 * 
 */
public interface IAuthService {

	/**
	 * Authenticate user and return list of systems
	 * 
	 * @param sessionId 
	 * 
	 * @
	 * @return
	 * @throws JSONException
	 * @throws AccessDeniedException 
	 */
	List<Map<String, Object>> authSystems(String sessionId)throws AccessDeniedException;

	/**
	 * Get user details
	 * 
	 * @param sessionId
	 * @return
	 */
	User getUserDetails(String sessionId) throws AccessDeniedException;


}
