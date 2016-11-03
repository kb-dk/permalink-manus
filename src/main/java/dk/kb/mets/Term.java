package dk.kb.mets;
import dk.kb.dup.metsApi.ManusDataSource;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


public class Term
{
    private HashMap resMap = new HashMap();

    public Term(){}

    public Term(String langcode)
    {
        String sql = "select termtrans, termcode from manus.langterm where langcode = '"+langcode+"'";
	//	ManusDataSource source = new ManusDataSource();
	ManusDataSource source = ManusDataSource.getInstance();
	Connection conn = source.getConnection();
        this.getHashMapFromDB(conn,sql,999);
	source.close();
	try {conn.close();} catch(SQLException ex) { ex.printStackTrace(); }
    }

    public HashMap getLangTerm()
    {
        return this.resMap;
    }

    public String getLang(String lang)
    {
        String sql = "select langdesc from manus.lang where langcode ='"+lang+"'";
	//	ManusDataSource source = new ManusDataSource();
	ManusDataSource source = ManusDataSource.getInstance();
	Connection conn = source.getConnection();
	String desc = getStringFromDb(conn, sql,999, "LANGDESC");
	source.close();
	try {conn.close();} catch(SQLException ex) { ex.printStackTrace(); }
        return desc;
    }

    public String getMusViewMode(String project)
    {
        String sql = "select app_mode_id from mus.mus_projekt where projekt_id='"+project+"'";
	//	ManusDataSource source = new ManusDataSource();
	ManusDataSource source = ManusDataSource.getInstance();
	Connection conn = source.getConnection();
	String mode = getStringFromDb(conn,sql,999, "APP_MODE_ID");
	source.close();
	try {conn.close();} catch(SQLException ex) { ex.printStackTrace(); }
        return mode;

    }



    private String getStringFromDb(Connection conn, String query, int maximumRecords,String field)
    {
        try{
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            res.next();
            return res.getString(field);
        } catch(SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void getHashMapFromDB(Connection conn, String query, int maximumRecords)
    {
        try {
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while(res.next()) {
                this.resMap.put(res.getString("TERMCODE"), res.getString("TERMTRANS"));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
}
