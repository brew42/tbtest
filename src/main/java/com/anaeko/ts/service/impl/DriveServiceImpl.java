package com.anaeko.ts.service.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anaeko.ts.dao.IDriveDAO;
import com.anaeko.ts.service.IDriveService;


/**
 * @author tombrewster
 * 
 */
@Service
public class DriveServiceImpl implements IDriveService {

    @Autowired
    private IDriveDAO driveDAO;
  

	@Override
	public void postprocess(String systemUuid) throws JSONException 
	{
		
		//Process drive status
		Timestamp now = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        cal.add(Calendar.DATE, -1);
        Timestamp start = new Timestamp(cal.getTime().getTime());
		driveDAO.processDriveStatus(systemUuid, start, end);
	
	}

	/**
	 * List latest systems
	 */
	@Override
	public List<String> listSystems(Timestamp startTime, Timestamp endTime) {
		
		return driveDAO.getLatestSystems(startTime, endTime);
	}

	@Override
	public void postprocess(String systemUuid, Timestamp endTime) throws JSONException {
		
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(endTime.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -1);
        Timestamp start = new Timestamp(cal.getTime().getTime());
        
		driveDAO.processDriveStatus(systemUuid, start, endTime);

	}


}