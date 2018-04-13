package com.anaeko.ts.service.impl;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anaeko.ts.dao.IAuthDAO;
import com.anaeko.ts.pojo.User;
import com.anaeko.ts.service.IAuthService;


/**
 * @author tombrewster
 * 
 */
@Service
public class AuthServiceImpl implements IAuthService {


    @Autowired
    private IAuthDAO authDAO;

   
    @Override
	public List<Map<String, Object>> authSystems(String sessionId) throws AccessDeniedException
	{
        return authDAO.getAuthSystems(sessionId);

    }


	@Override
	public User getUserDetails(String sessionId) throws AccessDeniedException {
		
		return authDAO.getUserDetails(sessionId);
	}

}