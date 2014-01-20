package dk.kb.dup.metsApi;

import dk.kb.dup.modsApi.Metadata;
import java.util.Collection;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMText;

public class Musik
{
  private Collection projekts = null;
   
  private ManusSearch search  = new ManusSearch();
  private Metadata mdMods     = new Metadata();
  private Namespace metsNS    = new Namespace("m","http://www.loc.gov/METS/");
  private Namespace modsNS    = new Namespace("md","http://www.loc.gov/mods/v3");
  private Namespace xlinkNS   = new Namespace("xlink","http://www.w3.org/1999/xlink");

  public Musik(){}
  
  
  public Document getProjectsMets()
  {
    this.projekts = this.getProjekts();
    DOMDocument doc = new DOMDocument();
    Element root    = new DOMElement("mets",metsNS);
    root.add(xlinkNS);
    root.add(modsNS);
    root.addNamespace("","http://www.w3.org/1999/xhtml");
    root.addAttribute("OBJID","musik");
    doc.add(root);
    
    
    Element dmds    = new DOMElement("dmdSec",metsNS);
    dmds.addAttribute("ID","md-root");
    Element mdwrap  = new DOMElement("mdWrap",metsNS);
    mdwrap.addAttribute("MDTYPE","MODS");
    Element xdata   = new DOMElement("xmlData",metsNS);
    Element mdsroot = new DOMElement("mods",modsNS);
    Element titleInfo = new DOMElement("titleInfo",modsNS);
    titleInfo.addAttribute("xml:lang","dan");
    Element title = new DOMElement("title", modsNS);
    title.add(new DOMText("Musik Afdelingens Elektroniske Samling"));
    titleInfo.add(title);
    mdsroot.add(titleInfo);
    xdata.add(mdsroot);
    mdwrap.add(xdata);
    dmds.add(mdwrap);  
    root.add(dmds);
    
    Element structMap = new DOMElement("structMap", metsNS);
    structMap.addAttribute("type","logical");
        
    for(Iterator prorator = this.projekts.iterator(); prorator.hasNext();){
      DatabaseRow row = null;
      if(prorator.hasNext()) {
         row = (DatabaseRow)prorator.next();
      }    
      if(row != null) {
        dmds    = new DOMElement("dmdSec",metsNS);
        dmds.addAttribute("ID","dmd:default:"+row.get("PROJEKT_ID"));
        mdwrap  = new DOMElement("mdWrap",metsNS);
        mdwrap.addAttribute("MDTYPE","MODS");
        xdata   = new DOMElement("xmlData",metsNS);
        mdsroot = new DOMElement("mods",modsNS);
      
        titleInfo = new DOMElement("titleInfo",modsNS);
        titleInfo.addAttribute("xml:lang","dan");
        title = new DOMElement("title", modsNS);
        title.add(new DOMText(row.get("PROJEKT_NAVN")+""));
        titleInfo.add(title);
        mdsroot.add(titleInfo);
        
        
        String infoRep =  row.get("INFOURL")+"";
        if(!infoRep.equalsIgnoreCase("null")){
          Element info = new DOMElement("note",modsNS);
          info.addAttribute("xml:lang","dan");
          info.addAttribute("type","infoUrl");
          info.addAttribute("xlink:href",infoRep);
          mdsroot.add(info);
        }
        
        Element div = new DOMElement("div",metsNS); 
        div.addAttribute("xml:lang","dan");
        div.addAttribute("ID","div:default:"+row.get("PROJEKT_ID"));
        div.addAttribute("DMDID","dmd:default:"+row.get("PROJEKT_ID"));
          
        Element metsPtr = new DOMElement("metsPtr",metsNS);
        metsPtr.addAttribute("ID","metsPtr:default:"+row.get("PROJEKT_ID"));
        metsPtr.addAttribute("loctype","DOI");
        metsPtr.addAttribute("xlink:href",row.get("PROJEKT_ID")+"");
          
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

  private String selectProjekts()
  {
    return "select * from mus.mus_projekt";
  }
  
  private Collection getProjekts()
  {
    search.setSession("lumsession");
    return search.executeQuery(this.selectProjekts(), 9999);
  }
  
}
  
