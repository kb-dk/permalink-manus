package dk.kb.dup.metsApi;

import java.util.Collection;
import java.util.Iterator;

public class LumIdentifiers 
{

  private ManusSearch search        = new ManusSearch();
  private Collection  lumIDs      = null;
  private Iterator    lumIterator = null;
  
  public LumIdentifiers()
  {
    this.lumIDs      = this.getData();
    this.lumIterator = this.getData().iterator();
    
  }
  
  public String getNextItem() {
    DatabaseRow row = (DatabaseRow)this.lumIterator.next();
    return row.get("LOEBENR")+"";
  }
   
  
  public boolean hasNext() {
    return this.lumIterator.hasNext();
  }
    
  private Collection getData() {
    String SQL = "select * from lum.hsk_manuskripter t";
    Collection data = this.search.executeQuery(SQL,10000);
    return data;
  }
  
  
}
