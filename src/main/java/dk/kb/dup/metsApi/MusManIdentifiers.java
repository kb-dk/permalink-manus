package dk.kb.dup.metsApi;

import oracle.toplink.publicinterface.DatabaseRow;
import java.util.Collection;
import java.util.Iterator;

public class MusManIdentifiers 
{

  private ManusSearch search        = new ManusSearch();
  private Collection  musmanIDs      = null;
  private Iterator    musmanIterator = null;

  public MusManIdentifiers()
  {
    this.musmanIDs      = this.getData();
    this.musmanIterator = this.getData().iterator();
  }
  
  public String getNextItem() {
    DatabaseRow row = (DatabaseRow)this.musmanIterator.next();
    return row.get("LOEBENR")+"";
  }
  
  public boolean hasNext() {
    return this.musmanIterator.hasNext();
  }
  
  private Collection getData() {
    String SQL = "select loebenr from musman.hsk_manuskripter";
    Collection data = this.search.executeQuery(SQL,10000);
    return data;
  }
  
  
}