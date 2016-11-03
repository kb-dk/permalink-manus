package dk.kb.dup.metsApi;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManusDataSource
{
    private Logger     LOGGER        = LoggerFactory.getLogger(ManusDataSource.class);
    private String     manusId       = "";
    private String     user          = "manus";
    private String     password      = "ft6yh";
    private String     jdbcUri       = "jdbc:oracle:thin:@oracle-test-03.kb.dk:1521:TEST3";
//  private String     jdbcUri       = "jdbc:oracle:thin:@oracledb.kb.dk:1521:prod";
    private static ManusDataSource instance = null;
    

    private static com.mchange.v2.c3p0.ComboPooledDataSource pooledDataSource = null; 

    public ManusDataSource() {
	this.pooledDataSource = this.configure();
    }

    public static ManusDataSource getInstance () {
	if(instance == null) {
	    instance = new ManusDataSource();
	}
	return instance;
    }

    private com.mchange.v2.c3p0.ComboPooledDataSource configure() {
	com.mchange.v2.c3p0.ComboPooledDataSource source = null;
	try {
	    source = new com.mchange.v2.c3p0.ComboPooledDataSource(); 
	    source.setDriverClass("oracle.jdbc.OracleDriver"); 
	    //loads the jdbc driver 
	    source.setJdbcUrl(jdbcUri); 
	    source.setUser(user); 
	    source.setPassword(password); 
	    // the settings below are optional -- c3p0 can work with defaults 
	    // the default MaxPoolSize is 100, so why limit it more.
	    source.setMinPoolSize(1); 
	    source.setAcquireIncrement(5); 
	    source.setMaxPoolSize(50);
	    LOGGER.debug("configured data source");
	} catch (java.beans.PropertyVetoException beanNotCooked) {
	    LOGGER.warn("bean not cooked " + beanNotCooked.getMessage());
	    beanNotCooked.printStackTrace();
	}
	return source;
    }

    public Connection getConnection() {

	Connection conn = null;

	try {
	    if(this.pooledDataSource == null) {
		this.pooledDataSource = this.configure();
	    }
	    conn = this.pooledDataSource.getConnection();
	} catch(SQLException sqlproblem) {
	    LOGGER.warn("sql problem in get connection " + sqlproblem.getMessage());
	    sqlproblem.printStackTrace();
	}
	return conn;
    }

    public void close() {
	//		this.pooledDataSource.close();
	//	this.pooledDataSource = null;
    }

}