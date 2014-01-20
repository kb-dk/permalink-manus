package dk.kb.dup.metsApi;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.Properties;

public class ManusSearch 
{

    private String manusId = "";
    private String session = "manussession";

    private String user              = "manus";
    private String password          = "ft6yh";
    private String jdbcUri           = "jdbc:oracle:thin:@oracledb.kb.dk:1521:prod";
    private java.sql.Connection conn = null;

  
    public ManusSearch()
    {
	this.initConnection(this.user,this.password,this.jdbcUri);	
    }

    public ManusSearch(String user, String password, String jdbcUri) {
	this.initConnection(user,password,jdbcUri);
    }

    private void initConnection(String user, String password, String jdbcUri) {
	Properties connectionProps = new Properties();
	connectionProps.put("user",     user);
	connectionProps.put("password", password);
	try {
	    this.conn = DriverManager.getConnection(jdbcUri,connectionProps);
	}
	catch(SQLException sqlproblem) {
	}

    }
   
    public Collection executeQuery(String query, int maximumRecords)
    {                              
	Collection coll = new ArrayList<DatabaseRow>();
	Statement stmt  = null;

	try {
	    stmt         = this.conn.createStatement();
	    ResultSet result       = stmt.executeQuery(query);
	    ResultSetMetaData rsmd = result.getMetaData();
	    int colCount           = rsmd.getColumnCount();
	    int rows               = 0;
	    while (result.next() && rows++ < maximumRecords) {
		DatabaseRow dbrow = new DatabaseRow();
		for(int col = 0; col<colCount;col++) {
		    String key = rsmd.getColumnLabel(col) + "";
		    String val = result.getString(key)    + "";
		    dbrow.put(key,val);
		}
		coll.add(dbrow);
	    }    
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
	return coll;
    }
   
    public void setSession(String s)
    {
	this.session = s;
    }
}
