
package com.anaeko.ts.dao;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.function.Consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.anaeko.TimeSeriesApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TimeSeriesApplication.class)
@TestPropertySource(locations="classpath:application-test.properties")
public class DaoTest {
	
	private static final Logger log = LogManager.getLogger("TimeSeries.logging");

	private MockMvc mockMvc;
	
	 @Autowired
	 private WebApplicationContext webApplicationContext;
	 
	 @Autowired
	 ConfigurableApplicationContext ctx;
	
	
	
	@Autowired
	private Environment env;
	private final String tableName = "gdi";
	private final String tableName_Branko = "branko_gdi";

	private final String appToken = "ef2acf52c1ee1142fc58fdf052b035150afc2d45e493db06d083c6b9b70a4cf4";

	
	@Before
    public void setup() throws Exception {
		EnvironmentTestUtils.addEnvironment(ctx, "host:192.168.8.101");
		EnvironmentTestUtils.addEnvironment(ctx, "port:8086");
		EnvironmentTestUtils.addEnvironment(ctx, "user:admin");
		EnvironmentTestUtils.addEnvironment(ctx, "pass:admin");
		EnvironmentTestUtils.addEnvironment(ctx, "dbName:LOCAL");
	}
	
	
	@Test
	public void testChunckedQuery() throws InterruptedException {
		
		String systemUuid = "123";
		
		Timestamp now = new Timestamp(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(now.getTime());
        cal.add(Calendar.HOUR, -24);
        Timestamp previousDay = new Timestamp(cal.getTime().getTime());

		InfluxDB influxDB = InfluxDBFactory.connect("http://" + env.getProperty("host") + ":" + env.getProperty("port"), 
				env.getProperty("user"), env.getProperty("pass"));
		String query = "SELECT systemUuid,deviceSerial,driveModel,driveSerial,driveBay,driveFirmware,driveUuid,driveStatus,driveCapacity "
				+ "FROM drives WHERE systemUuid = '" + systemUuid +"' AND " +  
				"time > '" + previousDay + "' AND " + 
				"time < '" + now + "'";
		
		//Chuncked Query
		influxDB.query(new Query(query, env.getProperty("dbName")), 1, 
				new  Consumer<QueryResult>() {
			     @Override
			     public void accept(QueryResult result) {
			         log.info("accept");
			         
			     }

			 });
		
		
		 Thread.sleep(10000);
	
	}
	
	
}