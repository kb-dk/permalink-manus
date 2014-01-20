package dk.kb.dup.metsApi;

import java.util.Collection;
import java.util.Iterator;

public class ManusIdentifiers 
{

  private ManusSearch search        = new ManusSearch();
  private Collection  manusIDs      = null;
  private Iterator    manusIterator = null;

  public ManusIdentifiers()
  {
    this.manusIDs      = this.getData();
    this.manusIterator = this.getData().iterator();
  }
  
  public String getNextItem() {
    DatabaseRow row = (DatabaseRow)this.manusIterator.next();
    return row.get("MANUSID")+"";
  }
  
  public boolean hasNext() {
    return this.manusIterator.hasNext();
  }
  
  private Collection getData() {
    String SQL = "select manusid from manus.manus";
    Collection data = this.search.executeQuery(SQL,10000);
    return data;
  }
  
  
}
