package dk.kb.mets;
import dk.kb.dup.metsApi.ManusDataSource;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


public class Term
{
    private HashMap resMap = new HashMap();
    private ManusDataSource source   = ManusDataSource.getInstance();

    public Term(){}

    public Term(String langcode)
    {
        String sql = "select termtrans, termcode from manus.langterm where langcode = '"+langcode+"'";
        getHashMapFromDB(sql,999);
    }

    public HashMap getLangTerm()
    {
        return this.resMap;
    }

    public String getLang(String lang)
    {
        String sql = "select langdesc from manus.lang where langcode ='"+lang+"'";
        return getStringFromDb(sql,999, "LANGDESC");
    }

    public String getMusViewMode(String project)
    {
        String sql = "select app_mode_id from mus.mus_projekt where projekt_id='"+project+"'";
        return getStringFromDb(sql,999, "APP_MODE_ID");

    }



    public String getStringFromDb(String query, int maximumRecords,String field)
    {
        try{
	    Connection conn = this.source.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            res.next();
            return res.getString(field);
        } catch(SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void getHashMapFromDB(String query, int maximumRecords)
    {
        try {
	    Connection conn = this.source.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet res = stmt.executeQuery(query);
            while(res.next()) {
                resMap.put(res.getString("TERMCODE"), res.getString("TERMTRANS"));
            }
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
    }
}
