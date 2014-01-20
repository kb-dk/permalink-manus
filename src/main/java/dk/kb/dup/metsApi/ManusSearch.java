package dk.kb.dup.metsApi;

import java.util.Collection;
import java.sql.Connection;

public class ManusSearch 
{

    private String manusId = "";
    private String session = "manussession";
  
    public ManusSearch()
    {
    }
   
    public Collection executeQuery(String query, int maximumRecords)
    {
	Collection coll = null;
	return coll;
    }
   
    public void setSession(String s)
    {
	this.session = s;
    }
}
