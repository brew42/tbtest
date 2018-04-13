package com.anaeko.ts.dao.impl;

import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.springframework.beans.factory.annotation.Value;

import okhttp3.OkHttpClient;

public abstract class InfluxDAO {
	
	@Value("${influx_host}")
	private String dbHost;
	
	@Value("${influx_port}")
	private String dbPort;
	
	@Value("${influx_username}")
	private String dbUser;
	
	@Value("${influx_password}")
	private String dbPass;

	@Value("${influx_connection_timeout}")
	private long connectionTimeout;
	
	@Value("${influx_read_timeout}")
	private long readTimeout;
	

	protected InfluxDB influxDB;

	/**
	 * Create influx client
	 */
	protected void createInfluxClient()
	{
		
		OkHttpClient.Builder client = new OkHttpClient.Builder()
				.connectTimeout(connectionTimeout, TimeUnit.SECONDS)
				.readTimeout(readTimeout, TimeUnit.SECONDS)
				.retryOnConnectionFailure(true);
		
		influxDB = InfluxDBFactory.connect("http://" + dbHost + ":" + dbPort, dbUser, dbPass, client);

	}

}
