package com.anaeko.ts.pojo;

import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;



/**
 * F	ilter object for data access queries
 * @author tombrewster
 *
 */
@Entity
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AggFilter{
	

	private String tableName;
	private String queryFields;
	private String groupBy;
	private String filter;
	
	
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getQueryFields() {
		return queryFields;
	}
	public void setQueryFields(String queryFields) {
		this.queryFields = queryFields;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	
	

}
