package dk.kb.dup.metsApi;

import java.util.Collection;
import oracle.toplink.publicinterface.DatabaseSession;
import oracle.toplink.tools.sessionconfiguration.XMLLoader;
import oracle.toplink.tools.sessionmanagement.SessionManager;
import oracle.toplink.publicinterface.DatabaseRow;
import oracle.toplink.queryframework.DataReadQuery;

public class ManusSearch 
{

  private String manusId = "";
  private String session = "manussession";
  
  public ManusSearch()
  {
  }
   
  public Collection executeQuery(String query, int maximumRecords)
  {
  
    XMLLoader xmlLoader = new   XMLLoader("META-INF/sessions.xml");
    SessionManager sessionManager =  SessionManager.getManager();
    
    
    DatabaseSession session =  (DatabaseSession)sessionManager.getSession(xmlLoader,this.session, this.getClass().getClassLoader());
    
    
    session.setExceptionHandler(new MyExceptionHandler());
    
    DataReadQuery dReadQuery = new DataReadQuery();
    dReadQuery.setSQLString(query);
    if(maximumRecords!=999){
      dReadQuery.setMaxRows(maximumRecords);
    }
    return (Collection)session.executeQuery(dReadQuery);
   }
   
   public void setSession(String s)
   {
     this.session = s;
   }
   
}