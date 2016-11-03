package dk.kb.dup.metsApi;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManusSearch 
{
    private Logger     LOGGER        = LoggerFactory.getLogger(ManusSearch.class);
    private String     manusId       = "";

    private ManusDataSource source   = null;

    private String session           = "manussession";
  
    public ManusSearch()
    {
	this.source = ManusDataSource.getInstance();
    }

    public void setSession(String sess) {
	this.session = sess;
    }

    public String getSession() {
	return this.session;
    }

    public Collection executeQuery(String query, int maximumRecords)
    {                              
	LOGGER.debug("About to execute " + query);
	Collection coll = new ArrayList<DatabaseRow>();
	Statement  stmt = null;
	Connection conn = this.source.getConnection();

	try {
	    stmt         = conn.createStatement();
	    ResultSet result       = stmt.executeQuery(query);
	    ResultSetMetaData rsmd = result.getMetaData();
	    int colCount           = rsmd.getColumnCount();
	    int rows               = 0;
	    while (result.next() && rows++ < maximumRecords) {
		LOGGER.debug("This is row number " + rows);
		DatabaseRow dbrow = new DatabaseRow();
		int colsAdded = 0;
		for(int col = 1; col<=colCount;col++) {		    
		    String key = rsmd.getColumnLabel(col) + "";
		    String val = result.getString(key)    + "";
		    if(val.length() > 0 && !val.equals("null")) {
			dbrow.put(key,val);
			colsAdded++;
		    }
		}
		if(colsAdded > 0) {
		    coll.add(dbrow);
		}
	    }    
	    stmt.close(); 
	    conn.close();
	} catch (SQLException e ) {
	    e.printStackTrace();
	} finally {
	    if (stmt != null) { 
		try {
		    stmt.close(); 
		} catch (SQLException e ) {
		    e.printStackTrace();
		}
	    }
	}
	LOGGER.debug("Done with query");
	return coll;
    }


    public void close()
    {
	//	try {
	    LOGGER.debug("closing connection");

	    //	    this.conn.close();
	    this.source.close();
	    //	} catch(SQLException sqlproblem) {
	    //	    LOGGER.debug(sqlproblem.getMessage());
	    //	}
    }
   
   
}
