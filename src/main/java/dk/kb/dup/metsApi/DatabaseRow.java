package dk.kb.dup.metsApi;

import java.lang.String;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseRow
{

    private Logger              LOGGER = LoggerFactory.getLogger(ManusSearch.class);    
    private HashMap<String,String> row = null;
    
    public DatabaseRow () {
	LOGGER.debug("making new database row");
	this.row =  new HashMap<String,String>();
    }

    public void put(String key,String val) {
	this.row.put(key,val);
    }

    public String get(String s) {
	return this.row.get(s);
    }

}
