package com.anaeko.ts.service.impl;

import java.util.List;
import java.util.Map;

import org.influxdb.dto.QueryResult.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anaeko.ts.dao.IAggDAO;
import com.anaeko.ts.pojo.AggFilter;
import com.anaeko.ts.service.IAggService;


/**
 * @author tombrewster
 * 
 */
@Service
public class AggServiceImpl implements IAggService {


    @Autowired
    private IAggDAO aggDAO;
   

   
    /*
     * Query including list of systems to filter on
     * 
     * (non-Javadoc)
     * @see com.anaeko.ts.service.IAggService#query(com.anaeko.ts.pojo.AggFilter, java.util.List)
     */
    @Override
	public List<Result> query(AggFilter filter, List<Map<String, Object>> authSystems)
	{
        return aggDAO.query(filter.getTableName(), filter.getQueryFields(), 
        		filter.getFilter(), filter.getGroupBy(), authSystems);

    }



    /*
     * Query without system list
     * 
     * (non-Javadoc)
     * @see com.anaeko.ts.service.IAggService#query(com.anaeko.ts.pojo.AggFilter)
     */
    @Override
	public List<Result> query(AggFilter filter)
	{
        return aggDAO.query(filter.getTableName(), filter.getQueryFields(), 
        		filter.getFilter(), filter.getGroupBy());

    }

	

}