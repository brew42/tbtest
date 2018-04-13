package com.anaeko.ts.pojo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class User {

	
	@Id
    @GeneratedValue
    @Column(name = "name")
    private String name;
	
	@Id
    @GeneratedValue
    @Column(name = "admin")
    private int admin;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAdmin() {
		return admin;
	}

	public void setAdmin(int admin) {
		this.admin = admin;
	}
	
	
}
