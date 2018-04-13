
package com.anaeko.ts.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.anaeko.TimeSeriesApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TimeSeriesApplication.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class DriveControllerTest {

	private static final Logger log = LogManager.getLogger("TimeSeries.logging");

	private MockMvc mockMvc;
	
	 @Autowired
	 private WebApplicationContext webApplicationContext;
	 
	 @Autowired
	 ConfigurableApplicationContext ctx;
	

	
	@Autowired
	private Environment env;
	private final static String appToken = "75b08df4da2984f028060d72d6d65bf4";

	
	@Before
    public void setup() throws Exception {
		
		EnvironmentTestUtils.addEnvironment(ctx, "influx_name:test");
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
 
	}
	
	
	@Test
	public void testEnvironmentVariables() {
	    Assert.assertEquals(env.getProperty("influx_name"), "test");
	}

	
	/*
	 * Post process drives with set historic start and end times
	 */
	@Test
	public void postprocessSetTime() throws Exception {
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -2);//Start with -5 tomorrow
        Timestamp end = new Timestamp(cal.getTime().getTime());
		
        
		String systemUuid = "99:1b:96:de:c0:86:45:79:4b:2d:56:1a:1b:7a:bb:0d:1c:1e:4c:b4";
    	
		MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/drive/postprocess/" + systemUuid)
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.param("endTime", end.toString()))
				.andReturn();
		
		//Process Content if required
		String resultStr = postResult.getResponse().getContentAsString();
		log.info("result>>" + resultStr);
		Assert.assertTrue(resultStr.length() > 0);
		if(StringUtils.isNotBlank(resultStr))
		{
			JSONObject json = new JSONObject(postResult.getResponse().getContentAsString()); 
			boolean success = (boolean) json.get("success");
		    Assert.assertTrue(success);
		    
		    Thread.sleep(300*1000);

		}
		
	}
	
	/*
	 * Post process drives
	 */
	@Test
	public void postprocess() throws Exception {
		
		String systemUuid = "00:c6:6b:d1:c2:35:f8:64:ea:2d:de:5e:00:1c:1e:f1:55:96:46:fa";
    	
		MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/drive/postprocess/" + systemUuid)
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken))
				.andReturn();
		
		//Process Content if required
		String resultStr = postResult.getResponse().getContentAsString();
		log.info("result>>" + resultStr);
		Assert.assertTrue(resultStr.length() > 0);
		if(StringUtils.isNotBlank(resultStr))
		{
			JSONObject json = new JSONObject(postResult.getResponse().getContentAsString()); 
			boolean success = (boolean) json.get("success");
		    Assert.assertTrue(success);
		    
		    Thread.sleep(30*60*1000);

		}
		
		
		
	}
	
	/*
	 * Get list of systems
	 */
	@Test
	public void getSystems() throws Exception {
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        cal.add(Calendar.DATE, -6);
        Timestamp start = new Timestamp(cal.getTime().getTime());

		MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.get("/tsapi/v1/drive/systems")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.param("startTime", start.toString())
				.param("endTime", end.toString()))
				.andReturn();
		
		//Process Content if required
		String resultStr = postResult.getResponse().getContentAsString();
		log.info("result>>" + resultStr);
		Assert.assertTrue(resultStr.length() > 0);
		if(StringUtils.isNotBlank(resultStr))
		{
			JSONObject json = new JSONObject(postResult.getResponse().getContentAsString()); 
			boolean success = (boolean) json.get("success");
		    Assert.assertTrue(success);
		   

		}
		
	}
	
	/*
	 * Test full scheduled run for yesterday
	 */
	@Test
	public void testFullScheduleRun() throws Exception {
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        cal.add(Calendar.DATE, -12);
        Timestamp start = new Timestamp(cal.getTime().getTime());
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/tsapi/v1/drive/systems")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.param("startTime", start.toString())
				.param("endTime", end.toString()))
				.andReturn();
		
		//Process Content if required
		String resultStr = getResult.getResponse().getContentAsString();
		log.info("result>>" + resultStr);
		Assert.assertTrue(resultStr.length() > 0);
		if(StringUtils.isNotBlank(resultStr))
		{
			JSONObject json = new JSONObject(getResult.getResponse().getContentAsString()); 
			boolean success = (boolean) json.get("success");
		    Assert.assertTrue(success);
		    
		    
		    
		    JSONArray systemList = json.getJSONArray("list");
		    log.info("Number of Systems to check:" + systemList.length());
		    for(int n = 0; n < systemList.length(); n++)
		    {
			    log.info("Run check:" + n + " of " + systemList.length());
		        String systemUuid = systemList.getString(n);
		        
		        //Call post process for each system reported
			    MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/drive/postprocess/" + systemUuid)
						.contentType(MediaType.APPLICATION_JSON)
						.header("appToken", appToken))
						.andReturn();
				
				//Process Content if required
				String postResultStr = postResult.getResponse().getContentAsString();
				log.info("result>>" + postResultStr);
				Assert.assertTrue(postResultStr.length() > 0);
				if(StringUtils.isNotBlank(postResultStr))
				{
					JSONObject postJson = new JSONObject(postResult.getResponse().getContentAsString()); 
					boolean postSuccess = (boolean) postJson.get("success");
				    Assert.assertTrue(postSuccess);
				    
				    Thread.sleep(5000);

				}
		    }
		    
		}
		
	}
	
	
	/*
	 * Test full scheduled run for set time
	 */
	@Test
	public void testFullScheduleRunSetTime() throws Exception {
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DATE, -0);
        Timestamp end = new Timestamp(cal.getTime().getTime());
        cal.add(Calendar.DATE, -1);
        Timestamp start = new Timestamp(cal.getTime().getTime());
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/tsapi/v1/drive/systems")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.param("startTime", start.toString())
				.param("endTime", end.toString()))
				.andReturn();
		
		//Process Content if required
		String resultStr = getResult.getResponse().getContentAsString();
		log.info("result>>" + resultStr);
		Assert.assertTrue(resultStr.length() > 0);
		if(StringUtils.isNotBlank(resultStr))
		{
			JSONObject json = new JSONObject(getResult.getResponse().getContentAsString()); 
			boolean success = (boolean) json.get("success");
		    Assert.assertTrue(success);
		    
		    
		    
		    JSONArray systemList = json.getJSONArray("list");
		    log.info("Number of Systems to check:" + systemList.length());
		    for(int n = 0; n < systemList.length(); n++)
		    {
			    log.info("Run check:" + n + " of " + systemList.length());
		        String systemUuid = systemList.getString(n);
		        
		        //Call post process for each system reported
			    MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/drive/postprocess/" + systemUuid)
						.contentType(MediaType.APPLICATION_JSON)
						.header("appToken", appToken)
			    			.param("endTime", end.toString()))
						.andReturn();
				
				//Process Content if required
				String postResultStr = postResult.getResponse().getContentAsString();
				log.info("result>>" + postResultStr);
				Assert.assertTrue(postResultStr.length() > 0);
				if(StringUtils.isNotBlank(postResultStr))
				{
					JSONObject postJson = new JSONObject(postResult.getResponse().getContentAsString()); 
					boolean postSuccess = (boolean) postJson.get("success");
				    Assert.assertTrue(postSuccess);
				    
				    Thread.sleep(3000);

				}
		    }
		    
		}
		
	}
	
	
	/*
	 * Test full scheduled run for week
	 */
	@Test
	public void testWeekScheduleRunSetTime() throws Exception {
		
		int i=30;
		while(i > 28)
		{
			Timestamp now = new Timestamp(System.currentTimeMillis());
	        Calendar cal = Calendar.getInstance();
	        cal.setTimeInMillis(now.getTime());
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
	        cal.add(Calendar.DATE, -i);
	        Timestamp end = new Timestamp(cal.getTime().getTime());
	        cal.add(Calendar.DATE, -1);
	        Timestamp start = new Timestamp(cal.getTime().getTime());
	    	
			MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/tsapi/v1/drive/systems")
					.contentType(MediaType.APPLICATION_JSON)
					.header("appToken", appToken)
					.param("startTime", start.toString())
					.param("endTime", end.toString()))
					.andReturn();
			
			//Process Content if required
			String resultStr = getResult.getResponse().getContentAsString();
			log.info("result>>" + resultStr);
			Assert.assertTrue(resultStr.length() > 0);
			if(StringUtils.isNotBlank(resultStr))
			{
				JSONObject json = new JSONObject(getResult.getResponse().getContentAsString()); 
				boolean success = (boolean) json.get("success");
			    Assert.assertTrue(success);
			    
			    
			    
			    JSONArray systemList = json.getJSONArray("list");
			    log.info("Number of Systems to check:" + systemList.length());
			    for(int n = 0; n < systemList.length(); n++)
			    {
				    log.info("Run check:" + n + " of " + systemList.length() + " for " + start.toString() + " to " + end.toString());
			        String systemUuid = systemList.getString(n);
			        
			        //Call post process for each system reported
				    MvcResult postResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/drive/postprocess/" + systemUuid)
							.contentType(MediaType.APPLICATION_JSON)
							.header("appToken", appToken)
				    			.param("endTime", end.toString()))
							.andReturn();
					
					//Process Content if required
					String postResultStr = postResult.getResponse().getContentAsString();
					log.info("result>>" + postResultStr);
					Assert.assertTrue(postResultStr.length() > 0);
					if(StringUtils.isNotBlank(postResultStr))
					{
						JSONObject postJson = new JSONObject(postResult.getResponse().getContentAsString()); 
						boolean postSuccess = (boolean) postJson.get("success");
					    Assert.assertTrue(postSuccess);
					    
					    Thread.sleep(5000);

					}
			    }
			    
			}
			i--;
		}
		
		
		
	}
	
	
	
	
	
}