package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import ninja.Result;
import ninja.Results;

@Singleton
public class TableController {

	private Logger logger;
	
	@Inject
	Provider<EntityManager> entityManagerProvider;
	
	public Result tableDetails() {
		
		logger = LoggerFactory.getLogger(this.getClass());
		
		EntityManager em = entityManagerProvider.get();
		
		String colQuery = "select column_name, data_type from information_schema.columns where table_schema = 'salesforce' and table_name = 'account'";
		Query nameQuery = em.createNativeQuery(colQuery);
		logger.info("Executing query [{}]", colQuery);
		
		List<Object[]> columns = (List<Object[]>)nameQuery.getResultList();
		
		if(logger.isInfoEnabled()) {
			for(Object[] s : columns) {
				logger.info("Col [{}] - data type [{}]",s[0], s[1]);
			}
		}
		
		Query q = em.createNativeQuery("select * from salesforce.account");
		logger.info("Executing query [{}]", "select * from salesforce.account");
		List<Object[]> values = (List<Object[]>)q.getResultList();
		
		List<List<String>> data = new ArrayList<>();
		
		List<String> colHeaders = new ArrayList<String>();
		for(Object[] obj : columns) {
			colHeaders.add(obj[0]+"");
		}
		data.add(colHeaders);
		// loop through and split into row data strings
		// there is probably a better way to do this for Freemarker! 
		for(Object[] objArray : values) {
			List<String> valData = new ArrayList<String>();
			
			for(Object obj : objArray) {	
				String val = "";
				if (obj != null) {
					val = obj + "";
					logger.info("We have an obj [{}]", obj);
				}
				valData.add(val);
			}
			data.add(valData);
		}
		Result r = Results.html();
		r.render("data", data);
		return r;
	}
}
