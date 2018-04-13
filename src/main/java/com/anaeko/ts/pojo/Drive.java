package com.anaeko.ts.pojo;

import java.time.Instant;

import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

@Measurement(name = "driveStatus")
public class Drive 
{
	
    @Column(name = "time")
    private Instant time;
    
    @Column(name = "systemUuid", tag = true)
    private String systemUuid;
    
    @Column(name = "smart9RawValue")
    private Double poh;
    
    @Column(name = "driveModel", tag = true)
    private String driveModel;
    
    @Column(name = "driveSerial", tag = true)
    private String driveSerial;
    
    @Column(name = "driveStatus", tag = true)
    private String driveStatus;
    
    @Column(name = "storagePoolGroup", tag = true)
    private String storagePoolGroup;
    
    @Column(name = "driveBay", tag = true)
    private String driveBay;
   
    @Column(name = "deviceHostname", tag = true)
    private String deviceHostname;
   
 
    private double numberSPDrives;
    private double numberSystemDrives;
    private double numberModelDrives;

	public Instant getTime() {
		return time;
	}



	public void setTime(Instant time) {
		this.time = time;
	}



	public String getSystemUuid() {
		return systemUuid;
	}



	public void setSystemUuid(String systemUuid) {
		this.systemUuid = systemUuid;
	}





	public String getDriveSerial() {
		return driveSerial;
	}



	public void setDriveSerial(String driveSerial) {
		this.driveSerial = driveSerial;
	}




	public String getDriveModel() {
		return driveModel;
	}



	public void setDriveModel(String driveModel) {
		this.driveModel = driveModel;
	}






	public String getDriveStatus() {
		return driveStatus;
	}



	public void setDriveStatus(String driveStatus) {
		this.driveStatus = driveStatus;
	}


	
  
	



	public String getDriveBay() {
		return driveBay;
	}



	public void setDriveBay(String driveBay) {
		this.driveBay = driveBay;
	}



	



	


	public Double getPoh() {
		return poh;
	}



	public void setPoh(Double poh) {
		this.poh = poh;
	}



	



	public String getStoragePoolGroup() {
		return storagePoolGroup;
	}



	public void setStoragePoolGroup(String storagePoolGroup) {
		this.storagePoolGroup = storagePoolGroup;
	}
	
	



	

	public String getDeviceHostname() {
		return deviceHostname;
	}



	public void setDeviceHostname(String deviceHostname) {
		this.deviceHostname = deviceHostname;
	}


	

	public double getNumberSystemDrives() {
		return numberSystemDrives;
	}



	public void setNumberSystemDrives(double numberSystemDrives) {
		this.numberSystemDrives = numberSystemDrives;
	}



	public double getNumberModelDrives() {
		return numberModelDrives;
	}



	public void setNumberModelDrives(double numberModelDrives) {
		this.numberModelDrives = numberModelDrives;
	}



	public double getNumberSPDrives() {
		return numberSPDrives;
	}



	public void setNumberSPDrives(double numberSPDrives) {
		this.numberSPDrives = numberSPDrives;
	}



	@Override
    public boolean equals(Object object)
    {
		if (object instanceof Drive){
	        Drive temp = (Drive)object;
	        if (this.driveSerial.equals(temp.getDriveSerial()) 
	        		&& this.driveBay.equals(temp.getDriveBay()) 
	        		&& this.deviceHostname.equals(temp.getDeviceHostname()))
	            return true;
	    }
	    return false;
    }
    
   
    
}