package com.anaeko.ts.schedule;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.anaeko.ts.service.IDriveService;

/**
 * @author tombrewster - 26 Sep 2016
 *
 */
@Component
public class ScheduledTasks {


	@Autowired
	private IDriveService driveService;
	

	private static final Logger log = LogManager.getLogger("TimeSeries.logging");
	
	@Scheduled(cron = "0 0 12 * * *'")
	public void postProcessDrives() throws JSONException, IOException, InterruptedException 
    { 
        log.info("Post process drives");

        //Get list of systems reported in previous day
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
        
        //Call List
        List<String> systemUuids = driveService.listSystems(start, end);
        
        //Call Post process method for each systemUUid
        log.info("Number of Systems to check:" + systemUuids.size()); 
        int i = 1;
        for(String systemUuid : systemUuids)
        {
        		log.info("Run check " + i + " of " + systemUuids.size() + " for " + start.toString() + " to " + end.toString());
        		log.info(" Start Post process system>>" + systemUuid);
        		driveService.postprocess(systemUuid);
        		Thread.sleep(5000);
        		i++;
        }
        
        return;

    }
	
	

}
