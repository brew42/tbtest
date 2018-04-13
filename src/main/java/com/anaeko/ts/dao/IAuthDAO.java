package com.anaeko.ts.dao;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import com.anaeko.ts.pojo.User;

/**
 * @author tombrewster
 */
public interface IAuthDAO {

	/**
	 * Get list of authorised systems
	 * 
	 * @param sessionId
	 * 
	 * @return List of Maps of users
	 * @throws AccessDeniedException 
	 */
	List<Map<String, Object>> getAuthSystems(String sessionId) throws AccessDeniedException;

	/**
	 * Gets user details
	 * 
	 * @param sessionId
	 * @return
	 * @throws AccessDeniedException
	 */
	User getUserDetails(String sessionId) throws AccessDeniedException;
}
