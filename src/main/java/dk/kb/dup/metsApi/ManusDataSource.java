package dk.kb.dup.metsApi;

import java.util.ArrayList;
import java.util.Collection;
import java.sql.*;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManusDataSource
{
    private Logger     LOGGER        = LoggerFactory.getLogger(ManusSearch.class);
    private String     manusId       = "";
    private String     user          = "manus";
    private String     password      = "ft6yh";
    private String     jdbcUri       = "jdbc:oracle:thin:@oracle-test-03.kb.dk:1521:TEST3";
//  private String     jdbcUri       = "jdbc:oracle:thin:@oracledb.kb.dk:1521:prod";

    private static ManusDataSource datasource;
    private com.mchange.v2.c3p0.ComboPooledDataSource pooledDataSource = null; 

    public static ManusDataSource getInstance() {
	if(datasource == null) {
	    datasource = new ManusDataSource();
	}
	return datasource;
    }

    private ManusDataSource() {
	this.configure();
    }

    private void configure() {
	try {
	    this.pooledDataSource = new com.mchange.v2.c3p0.ComboPooledDataSource(); 
	    this.pooledDataSource.setDriverClass("oracle.jdbc.OracleDriver"); 
	    //loads the jdbc driver 
	    this.pooledDataSource.setJdbcUrl(jdbcUri); 
	    this.pooledDataSource.setUser(user); 
	    this.pooledDataSource.setPassword(password); 
	    // the settings below are optional -- c3p0 can work with defaults 
	    this.pooledDataSource.setMinPoolSize(10); 
	    this.pooledDataSource.setAcquireIncrement(5); 
	    this.pooledDataSource.setMaxPoolSize(30);
	    LOGGER.debug("configured data source");
	} catch (java.beans.PropertyVetoException beanNotCooked) {
	    LOGGER.warn(beanNotCooked.getMessage());
	    beanNotCooked.printStackTrace();
	} /* catch(SQLException sqlproblem) {
	    LOGGER.warn(sqlproblem.getMessage());
	    sqlproblem.printStackTrace();
	    }*/
    }

    public Connection getConnection() {
	Connection conn = null;
	try {
	    conn = this.pooledDataSource.getConnection();
	} catch(SQLException sqlproblem) {
	    LOGGER.warn(sqlproblem.getMessage());
	    sqlproblem.printStackTrace();
	    this.configure();
	}
	return conn;
    }

    public void close() {
	this.pooledDataSource.close();
    }

}