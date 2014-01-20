package dk.kb.dup.metsApi;

import java.lang.String;
import java.util.HashMap;

public class DatabaseRow
{
    
    private HashMap<String,String> row = null;
    
    public DatabaseRow () {
	this.row =  new HashMap<String,String>();
    }

    public void put(String key,String val) {
	this.row.put(key,val);
    }

    public String get(String s) {
	return this.row.get(s);
    }

}
