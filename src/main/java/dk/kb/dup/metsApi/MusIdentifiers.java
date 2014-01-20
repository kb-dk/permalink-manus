package dk.kb.dup.metsApi;

import java.util.Collection;
import java.util.Iterator;

public class MusIdentifiers 
{

  private ManusSearch search        = new ManusSearch();
  private Collection  musIDs      = null;
  private Iterator    musIterator = null;
  private Collection  projIDs      = null;
  private Iterator    projIterator = null;

  public MusIdentifiers()
  {
    this.musIDs      = this.getData();
    this.musIterator = this.getData().iterator();
    this.projIDs      = this.getProj();
    this.projIterator = this.getProj().iterator();
  }
  
  public String getNextItem() {
    DatabaseRow row = (DatabaseRow)this.musIterator.next();
    return row.get("NODE_ID")+"";
  }
  public String getNextProj() {
    DatabaseRow row = (DatabaseRow)this.projIterator.next();
    return row.get("PROJEKT_ID")+"";
  }
  
  
  public boolean hasNext() {
    return this.musIterator.hasNext();
  }
  public boolean hasNextProj() {
    return this.projIterator.hasNext();
  }
  
  private Collection getData() {
    String SQL = "select node_id from mus.mus_node order by node_id";
    Collection data = this.search.executeQuery(SQL,10000);
    return data;
  }
  
   private Collection getProj() {
    String SQL = "select * from mus.mus_projekt order by projekt_id";
    Collection data = this.search.executeQuery(SQL,10000);
    return data;
  } 
}
