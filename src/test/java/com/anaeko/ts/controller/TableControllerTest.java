
package com.anaeko.ts.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
public class TableControllerTest {

	private MockMvc mockMvc;
	
	private static final Logger log = LogManager.getLogger("TimeSeries.logging");

	
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
	 * Get list of tables
	 */
	@Test
	public void get() throws Exception {
		
		
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/tsapi/v1/table")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken))
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

		    org.json.JSONArray aggList = json.getJSONArray("list");
		    Assert.assertTrue(aggList.length() > 0);

		}
		
	}
	
	/*
	 * Get table tags
	 */
	@Test
	public void getTableTags() throws Exception {
		
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/tsapi/v1/table/gdi")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.param("type", "TAG"))
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

		    org.json.JSONArray aggList = json.getJSONArray("list");
		    Assert.assertTrue(aggList.length() > 0);

		}
		
	}
	
	
	
	/*
	 * Get table fields
	 */
	@Test
	public void getTableFields() throws Exception {
		
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.get("/tsapi/v1/table/gdi")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.param("type", "FIELD"))
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

		    org.json.JSONArray aggList = json.getJSONArray("list");
		    Assert.assertTrue(aggList.length() > 0);

		}
		
	}
	
	
}