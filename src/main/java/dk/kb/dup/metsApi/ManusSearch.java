package dk.kb.dup.metsApi;

import java.util.Collection;
import java.sql.Connection;

public class ManusSearch 
{

    private String manusId = "";
    private String session = "manussession";

    private String username          = "manus";
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
	java.util.Properties connectionProps = new Properties();
	connectionProps.put("user",     user);
	connectionProps.put("password", password);

	this.conn = DriverManager.getConnection(jdbcUri,connectionProps);

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
