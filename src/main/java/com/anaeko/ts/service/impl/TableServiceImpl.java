package com.anaeko.ts.service.impl;

import java.util.List;

import org.influxdb.dto.QueryResult.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anaeko.ts.dao.ITableDAO;
import com.anaeko.ts.service.ITableService;


/**
 * @author tombrewster
 * 
 */
@Service
public class TableServiceImpl implements ITableService {


    @Autowired
    private ITableDAO tableDAO;

   
    @Override
	public List<Result> list() 
	{
        return tableDAO.list();

    }


	@Override
	public List<Result> get(String tableName, String type) {
		
		return tableDAO.get(tableName, type);
	}

}