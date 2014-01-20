package dk.kb.dup.metsApi;
import java.util.ArrayList;
import java.util.List;

public class Collection 
{

  List modsDocuments = null;

  public Collection()
  {
  }
  
  public void aggregateMetadata() {
    ManusIdentifiers identifiers = new ManusIdentifiers();
    this.modsDocuments = new ArrayList();
    while(identifiers.hasNext()) {
      String ident = identifiers.getNextItem();
      Manus manus  = new Manus(ident);
      this.modsDocuments.add(manus.mods());
    }
  }  
}