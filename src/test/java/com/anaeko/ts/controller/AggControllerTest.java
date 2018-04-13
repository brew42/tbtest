
package com.anaeko.ts.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.Cookie;

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
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.anaeko.TimeSeriesApplication;
import com.anaeko.ts.pojo.AggFilter;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TimeSeriesApplication.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class AggControllerTest {

	private MockMvc mockMvc;
	
	private static final Logger log = LogManager.getLogger("TimeSeries.logging");

	
	 @Autowired
	 private WebApplicationContext webApplicationContext;
	 
	 @Autowired
	 ConfigurableApplicationContext ctx;
	
	
	
	@Autowired
	private Environment env;
	private final static String AFR_DM_TABLE = "afr_dm";
	private final static String AFR_SP_TABLE = "afr_sp";
	private final static String PSC_TABLE = "psc_spx";
	private final static String appToken = "75b08df4da2984f028060d72d6d65bf4";
	private final static String SESSION_COOKIE = "session";
	private final String USER_SESSION = "12345";
	private final String USER_ADMIN = "ehahh8ubrh8b2dq0s2nmvavn";
	private final String USER_INVALID_SESSION = "blah";
	private final String USER_SESSION_EXPIRE = "424234";
	private final String USER_TOKEN_ONE_SYSTEM = "dknsdjkfnsdfjkn";
	private final String USER_NO_SYS = "fsdfdsfsdfsddf";
	
	private final String DRIVE_MODEL = "Virtual";
	private final String SP_GROUP = "4";
	private final String SYS_UUID = "e4:f4:b1:81:6b:1a:60:3b:6e:bb:08:c2:34:8b:21:0a:ef:16:ba:69";
	
	@Before
    public void setup() throws Exception {
		
		EnvironmentTestUtils.addEnvironment(ctx, "influx_name:test");
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
 
	}
	
	
	@Test
	public void testEnvironmentVariables() {
	    Assert.assertEquals(env.getProperty("influx_name"), "test");
	}
	
	
	/**
	 * GDI TESTS
	 */
	
	/*
	 * Query Aggregation
	 */
	@Test
	public void queryAgg() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("mean(afr)");
		filter.setGroupBy("GROUP BY time(1d), driveModel");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
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
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
	}
	
	
	@Test
	public void queryAggSPAdmin() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(PSC_TABLE);
		filter.setQueryFields("count(smartRawValue)");
		filter.setGroupBy("GROUP BY time(1d), systemUuid");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation/")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE,USER_ADMIN))
				.content(filterStr)
				)
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
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
	}
	
	@Test
	public void queryAggSPUser() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(PSC_TABLE);
		filter.setQueryFields("count(smartRawValue)");
		filter.setGroupBy("GROUP BY time(1d), systemUuid");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation/")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE,USER_SESSION))
				.content(filterStr)
				)
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
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
	}
	
	@Test
	public void queryAggSP() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(PSC_TABLE);
		filter.setQueryFields("count(smartRawValue)");
		filter.setGroupBy("GROUP BY time(1d), systemUuid");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation/")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE,USER_NO_SYS))
				.content(filterStr)
				)
				.andReturn();
		
		//Process Content if required
		String resultStr = getResult.getResponse().getContentAsString();
		log.info("result>>" + resultStr);
		Assert.assertTrue(resultStr.length() > 0);
		if(StringUtils.isNotBlank(resultStr))
		{
			JSONObject json = new JSONObject(getResult.getResponse().getContentAsString()); 
			boolean success = (boolean) json.get("success");
		    Assert.assertFalse(success);
		}
		
	}
	
	@Test
	public void queryAggCache() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("mean(afr)");
		filter.setGroupBy("GROUP BY time(1d), driveModel");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
				.andReturn();
		
		//Process Content if required
		String resultStr = getResult.getResponse().getContentAsString();
		//log.info("result>>" + resultStr);
		Assert.assertTrue(resultStr.length() > 0);
		if(StringUtils.isNotBlank(resultStr))
		{
			JSONObject json = new JSONObject(getResult.getResponse().getContentAsString()); 
			boolean success = (boolean) json.get("success");
		    Assert.assertTrue(success);

		    org.json.JSONArray aggList = json.getJSONArray("list");
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
		 Thread.sleep(5*1000);
		
		
		mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
				.andReturn();
		
		 Thread.sleep(5*1000);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
				.andReturn();
		
	}
	
	@Test
	public void queryAggWithGroupBy() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("mean(afr)");
		filter.setGroupBy("GROUP BY time(1d), driveModel");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
				.andDo(print())
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
	}
	
	@Test
	public void queryAggWithWhereNot() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("mean(afr)");
		filter.setFilter("WHERE driveModel = 'not there'");
		filter.setGroupBy("GROUP BY time(1d), driveModel");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() == 1);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue( jsonEle.get("series").equals(null));
		   
		    
		}
		
	}
	
	
	@Test
	public void queryAggWithWhereMulti() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("sum(afr)");
		filter.setFilter("WHERE driveModel = '" + DRIVE_MODEL + "'");
		filter.setGroupBy("GROUP BY time(1d), driveModel");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
	}
	
	
	@Test
	public void queryAggWithWhereMulti2() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_SP_TABLE);
		filter.setQueryFields("count(afr)");
		filter.setFilter("WHERE storagePoolGroup = '"+ SP_GROUP + "' AND systemUuid = '"+ SYS_UUID + "'");
		filter.setGroupBy("GROUP BY time(1d), systemUuid, storagePoolGroup");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation/")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
	}
	
	
	@Test
	public void queryAggWithWhereTime() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("mean(afr)");
		java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf("2018-01-01 10:10:10.0");
		filter.setFilter("WHERE driveModel ='" + DRIVE_MODEL + "' AND time > '" + timestamp + "'");
		filter.setGroupBy("GROUP BY time(1d), driveModel");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
				.andDo(print())
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
	}
	
	
	@Test
	public void queryAggWithWhereTimeRange() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("max(afr)");
		java.sql.Timestamp startTime = java.sql.Timestamp.valueOf("2018-01-01 10:10:10.0");
		java.sql.Timestamp endTime = java.sql.Timestamp.valueOf("2018-02-02 10:10:10.0");
		filter.setFilter("WHERE driveModel ='"+ DRIVE_MODEL +"' AND time > '" + startTime 
				+ "' AND time < '" + endTime + "'");
		filter.setGroupBy("GROUP BY time(1d), driveModel");

		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
	}
	
	
	@Test
	public void queryAggWithWhereTimeRangeNot() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("max(afr)");
		java.sql.Timestamp startTime = java.sql.Timestamp.valueOf("2018-01-01 10:10:10.0");
		java.sql.Timestamp endTime = java.sql.Timestamp.valueOf("2018-01-01 10:10:10.0");
		filter.setFilter("WHERE driveModel ='" + DRIVE_MODEL + "' AND time > '" + startTime 
				+ "' AND time < '" + endTime + "'");
		filter.setGroupBy("GROUP BY time(1d), driveModel");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() == 1);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue( jsonEle.get("series").equals(null));
		}
		
	}
	
	
	@Test
	public void queryAggWithGroupByTime() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("mean(afr)");
		filter.setGroupBy("GROUP BY time(1d)");
		
		String filterStr = this.json(filter);
		log.info("Filter:" + filterStr);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
				.andDo(print())
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
	}
	
	@Test
	public void queryAggWithGroupByTimeNoAuth() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("mean(afr)");
		filter.setGroupBy("GROUP BY time(1d)");
		
		String filterStr = this.json(filter);
		log.info("Filter:" + filterStr);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_INVALID_SESSION))
				.content(filterStr)
				)
				.andDo(print())
				.andReturn();
		
		//Process Content if required
		String resultStr = getResult.getResponse().getContentAsString();
		log.info("result>>" + resultStr);
		Assert.assertTrue(resultStr.length() > 0);
		if(StringUtils.isNotBlank(resultStr))
		{
			JSONObject json = new JSONObject(getResult.getResponse().getContentAsString()); 
			boolean success = (boolean) json.get("success");
		    Assert.assertFalse(success);

		  

		}
		
	}
	
	@Test
	public void queryAggWithGroupByTimeExpireAuth() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_DM_TABLE);
		filter.setQueryFields("mean(afr)");
		filter.setGroupBy("GROUP BY time(1d)");
		
		String filterStr = this.json(filter);
		log.info("Filter:" + filterStr);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION_EXPIRE))
				.content(filterStr)
				)
				.andDo(print())
				.andReturn();
		
		//Process Content if required
		String resultStr = getResult.getResponse().getContentAsString();
		log.info("result>>" + resultStr);
		Assert.assertTrue(resultStr.length() > 0);
		if(StringUtils.isNotBlank(resultStr))
		{
			JSONObject json = new JSONObject(getResult.getResponse().getContentAsString()); 
			boolean success = (boolean) json.get("success");
		    Assert.assertFalse(success);

		  

		}
		
	}
	
	@Test
	public void queryAggWithGroupByMulti() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(PSC_TABLE);
		filter.setQueryFields("count(smartRawValue)");
		filter.setGroupBy("GROUP BY time(1d), systemUuid");
		
		String filterStr = this.json(filter);
		
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_ADMIN))
				.content(filterStr)
				)
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);
		    Assert.assertTrue(jsonEle.getJSONArray("series").length() > 2);
		    

		}
		
	}
	
	
	@Test
	public void queryAggWithGroupByMultiOneSystem() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(PSC_TABLE);
		filter.setQueryFields("count(smartRawValue)");
		filter.setGroupBy("GROUP BY time(1d), systemUuid");
		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation/")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_TOKEN_ONE_SYSTEM))
				.content(filterStr)
				)
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);
		    Assert.assertTrue(jsonEle.getJSONArray("series").length() == 2);//TWO BSKYB

		}
		
	}
	
	
	/**
	 * DRIVE TESTS
	 */

	
	@Test
	public void queryDriveAgg() throws Exception {
		
		AggFilter filter = new AggFilter();
		filter.setTableName(AFR_SP_TABLE);
		filter.setQueryFields("first(afr)");
		filter.setFilter("WHERE systemUuid ='"+ SYS_UUID +"'");
		filter.setGroupBy("GROUP BY time(1d), systemUuid");

		
		String filterStr = this.json(filter);
    	
		MvcResult getResult = mockMvc.perform(MockMvcRequestBuilders.post("/tsapi/v1/aggregation/")
				.contentType(MediaType.APPLICATION_JSON)
				.header("appToken", appToken)
				.cookie(new Cookie(SESSION_COOKIE, USER_SESSION))
				.content(filterStr)
				)
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
		    log.info("aggList list>>" + aggList.length());
		    Assert.assertTrue(aggList.length() > 0);
		    JSONObject jsonEle = aggList.getJSONObject(0);
		    Assert.assertTrue(jsonEle.getJSONArray("series") != null);

		}
		
	}

	/**
	 * Helper methods for junit
	 */
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        Assert.assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }
	
	protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
	
	
	
	
	
}