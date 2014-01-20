package dk.kb.dup.metsApi;
import dk.kb.dup.modsApi.Metadata;
import java.util.Collection;
import java.util.Iterator;
import oracle.toplink.publicinterface.DatabaseRow;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMText;

public class Lum 
{
  private Collection data = null;
  private String musicID;
  private ManusSearch search = new ManusSearch();
  private Metadata mdMods     = new Metadata();
  private Namespace metsNS    = new Namespace("m","http://www.loc.gov/METS/");
  private Namespace modsNS    = new Namespace("md","http://www.loc.gov/mods/v3");
  private Namespace xlinkNS   = new Namespace("xlink","http://www.w3.org/1999/xlink");


  //Used for getting all in "lum"
  public Lum()
  {
    this.data = this.getProj();
  }

  //Used for getting single record mets
  public Lum(String mID)
  {
    this.musicID = mID;
    this.data = this.getData();
  }
  
  public Document getProjectMets()
  {
    Element root    = new DOMElement("mets",metsNS);
    root.add(xlinkNS);
    root.add(modsNS);
    root.add(metsNS);
    root.addNamespace("","http://www.w3.org/1999/xhtml");
    root.addAttribute("OBJID","lum");
    
    DOMDocument doc = new DOMDocument();
    doc.add(root);
    Metadata m= new Metadata();
    m.addTitle("H.C. Lumbyes Danse i trykte klaverudgaver","dan");
    m.addRelatedItem("host",this.getHostData());
    Document mds = m.getModsDocument();
    Element mdsroot = mds.getRootElement();
    mdsroot.detach();
    Element dmds    = new DOMElement("dmdSec",metsNS); 
    dmds.addAttribute("ID","md-root");
    Element mdwrap  = new DOMElement("mdWrap",metsNS);
    mdwrap.addAttribute("MDTYPE","MODS");
    Element xdata   = new DOMElement("xmlData",metsNS);
    
    xdata.add(mdsroot);
    mdwrap.add(xdata);
    dmds.add(mdwrap);  
    root.add(dmds);
    
    Element structMap =  new DOMElement("structMap", metsNS);
    structMap.addAttribute("type","logical");
    for(Iterator iter = data.iterator(); iter.hasNext();){
      DatabaseRow row = null;
      if(iter.hasNext()) {
        row = (DatabaseRow)iter.next();
      }
      if(row != null) {
        dmds    = new DOMElement("dmdSec",metsNS);
        dmds.addAttribute("ID","lum:"+row.get("LOEBENR")+":dmd");
        mdwrap  = new DOMElement("mdWrap",metsNS);
        mdwrap.addAttribute("MDTYPE","MODS");
        xdata   = new DOMElement("xmlData",metsNS);
        mdsroot = new DOMElement("mods",modsNS);
        
        String titleRep = row.get("TITEL")+"";  
        if(!titleRep.equalsIgnoreCase("null")){
          DOMElement titleInfo = new DOMElement("titleInfo",modsNS);
          titleInfo.addAttribute("xml:lang","dan");
          DOMElement title = new DOMElement("title", modsNS);
          title.add(new DOMText(titleRep));
          titleInfo.add(title);
          mdsroot.add(titleInfo);
        }
        
        Element div = new DOMElement("div",metsNS); 
        div.addAttribute("xml:lang","dan");
        div.addAttribute("ID","lum:"+row.get("LOEBENR")+":div");
        div.addAttribute("DMDID","lum:"+row.get("LOEBENR")+":dmd");
          
        Element metsPtr = new DOMElement("metsPtr",metsNS);
        metsPtr.addAttribute("ID","lum:"+row.get("LOEBENR")+":metsPtr");
        metsPtr.addAttribute("loctype","DOI");
        metsPtr.addAttribute("xlink:href",row.get("LOEBENR")+"");
        
        div.add(metsPtr);
        structMap.add(div);
        
        xdata.add(mdsroot);
        mdwrap.add(xdata);
        dmds.add(mdwrap);  
        root.add(dmds);       
      }    
    }    
    root.add(structMap);
    return doc;
       
  }
   
   
  public Document getMets() {
  
    Element root    = new DOMElement("mets",metsNS);
    root.add(xlinkNS);
    root.add(modsNS);
    root.add(metsNS);
    root.addNamespace("","http://www.w3.org/1999/xhtml");
    root.addAttribute("OBJID","lum:"+this.musicID);
      
    DOMDocument doc = new DOMDocument();
    doc.add(root);
    Document mds = this.mods();
    Element mdsroot = mds.getRootElement();
    mdsroot.detach();
    Element dmds    = new DOMElement("dmdSec",metsNS); 
    dmds.addAttribute("ID","md-root");
    Element mdwrap  = new DOMElement("mdWrap",metsNS);
    mdwrap.addAttribute("MDTYPE","MODS");
    Element xdata   = new DOMElement("xmlData",metsNS);
    
    xdata.add(mdsroot);
    mdwrap.add(xdata);
    dmds.add(mdwrap);  
    root.add(dmds);
    
    Element structMap =  new DOMElement("structMap", metsNS);
    structMap.addAttribute("type","logical");
    Element div =  new DOMElement("div", metsNS);
    div.addAttribute("ID","struct-root");
    div.addAttribute("DMDID","md-root");
    div.addAttribute("xml:lang","dan");
    
    structMap.add(div);
    
    root.add(structMap);      
    
    return doc;
  }
  
    
  
  public Document mods()
  {
    Iterator datarator = this.data.iterator();
    DatabaseRow row = null;
    if(datarator.hasNext()) {
      row = (DatabaseRow)datarator.next();
    }
    if(row != null) {
      mdMods.addTitle(row.get("TITEL")+"","dan");
      mdMods.addOriginInfo("","","",row.get("AARSTAL")+"");
      mdMods.addIdentifier("danFogNr",row.get("PRAESENTATION_DK")+"");
      mdMods.addIdentifier("pdf",row.get("DOWNLOAD_URL")+"");
      mdMods.addIdentifier("thumb",row.get("THUMBNAIL")+"");
      mdMods.addRelatedItem("host",this.getHostData());
    }
    return mdMods.getModsDocument();
  }
  private Metadata getHostData() {
    Metadata hostData = new Metadata();
    hostData.addTitle("Musikafdelingen","dan");
    hostData.addIdentifier("uri","/da/kb/nb/mta/","dan");
      
    return hostData;
  }
  
  private String selelctData()
  {
    return "select * from lum.hsk_manuskripter t where loebenr =" + this.musicID;
  }
  
  private Collection getData()
  {
    search.setSession("manussession");
    return search.executeQuery(this.selelctData(), 9999);
  }
  
  private String selelctProj()
  {
    return "select titel, loebenr from lum.hsk_manuskripter order by titel";
  }
  
  private Collection getProj()
  {
    search.setSession("manussession");
    return search.executeQuery(this.selelctProj(), 9999);
  }
  
  
  
}