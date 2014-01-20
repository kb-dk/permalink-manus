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
public class Mus
{
  private Collection projekts = null;
  private Collection node = null;
  private Collection projekt = null;
  private String basisPdf = "";
  private String basisJpg = "";
  
  private String musicID;
  private String projectID;
  private ManusSearch search  = new ManusSearch();
  private Metadata mdMods     = new Metadata();
  private Namespace metsNS    = new Namespace("m","http://www.loc.gov/METS/");
  private Namespace modsNS    = new Namespace("md","http://www.loc.gov/mods/v3");
  private Namespace xlinkNS   = new Namespace("xlink","http://www.w3.org/1999/xlink");

  public Mus(String mID, String pID)
  {
    this.musicID = mID;
    this.projectID = pID;
  }
  
  
  public Document getProjectMets()
  {
    boolean komponist = false;
    String infoUrl = "";
    if(this.getMusViewMode(this.projectID).equals("3")){
      this.projekt = this.getProjektOrderKomp();
      komponist = true;
    }else
    {
      this.projekt = this.getProjekt();
    }
    DOMDocument doc = new DOMDocument();
    Element root    = new DOMElement("mets",metsNS);
    root.add(xlinkNS);
    root.add(modsNS);
    root.addNamespace("","http://www.w3.org/1999/xhtml");
    root.addAttribute("OBJID","mus:"+this.projectID);
    doc.add(root);
    
    Element dmds    = new DOMElement("dmdSec",metsNS);
    dmds.addAttribute("ID","md-root");
    Element mdwrap  = new DOMElement("mdWrap",metsNS);
    mdwrap.addAttribute("MDTYPE","MODS");
    Element xdata   = new DOMElement("xmlData",metsNS);
    Element mdsroot = new DOMElement("mods",modsNS);
    Element titleInfo = new DOMElement("titleInfo",modsNS);
    titleInfo.addAttribute("xml:lang","dan");
    
    
    Element topTitle = new DOMElement("title", modsNS);
    topTitle.add(new DOMText(""));
    titleInfo.add(topTitle);
    mdsroot.add(titleInfo);
    
    Element relatedItem = new DOMElement("relatedItem",modsNS);
    relatedItem.addAttribute("type","host");
    titleInfo = new DOMElement("titleInfo",modsNS);
    titleInfo.addAttribute("xml:lang","dan");
    Element title = new DOMElement("title", modsNS);
    title.add(new DOMText("Musik- og Teaterafdelingen"));
    titleInfo.add(title);
    relatedItem.add(titleInfo);
    Element hostUri = new DOMElement("identifier",modsNS);
    hostUri.addAttribute("type","uri");
    hostUri.addAttribute("xml:lang","dan");
    hostUri.add(new DOMText("/da/kb/nb/mta/"));
    relatedItem.add(hostUri);
    mdsroot.add(relatedItem);
    
    Element linkRelatedItem = new DOMElement("relatedItem",modsNS);
    linkRelatedItem.addAttribute("type","menuitem");
    titleInfo = new DOMElement("titleInfo",modsNS);
    titleInfo.addAttribute("xml:lang","dan");
    Element linkTitle = new DOMElement("title", modsNS);
    linkTitle.add(new DOMText(""));
    titleInfo.add(linkTitle);
    linkRelatedItem.add(titleInfo);
    Element linkUri = new DOMElement("identifier",modsNS);
    linkUri.addAttribute("type","uri");
    linkUri.addAttribute("xml:lang","dan");
    linkUri.add(new DOMText(""));
    linkRelatedItem.add(linkUri);
    mdsroot.add(linkRelatedItem);    
    
    xdata.add(mdsroot);
    mdwrap.add(xdata);
    dmds.add(mdwrap);  
    root.add(dmds);
    
    Element structMap = new DOMElement("structMap", metsNS);
    structMap.addAttribute("type","logical");
    
    String projTitle = "";
    
    for(Iterator prorator = this.projekt.iterator(); prorator.hasNext();){
      DatabaseRow row = null;
      if(prorator.hasNext()) {
         row = (DatabaseRow)prorator.next();
      }    
      if(row != null) {
        infoUrl = row.get("INFOURL")+"";
        dmds    = new DOMElement("dmdSec",metsNS);
        dmds.addAttribute("ID","mus:"+this.projectID+":dmdl:"+row.get("NODE_ID"));
        mdwrap  = new DOMElement("mdWrap",metsNS);
        mdwrap.addAttribute("MDTYPE","MODS");
        xdata   = new DOMElement("xmlData",metsNS);
        mdsroot = new DOMElement("mods",modsNS);
        
        projTitle = row.get("PROJEKT_NAVN")+"";
        
              
                
        //We allways add titel and komponist, but in two different ways
        if(!komponist){        
          String titleRep = row.get("TITEL")+"";  
          if(!titleRep.equalsIgnoreCase("null")){
            titleInfo = new DOMElement("titleInfo",modsNS);
            titleInfo.addAttribute("xml:lang","dan");
            title = new DOMElement("title", modsNS);
            title.add(new DOMText(titleRep));
            titleInfo.add(title);
            mdsroot.add(titleInfo);
          }
        
          String nameRep = row.get("KOMPONIST")+"";
          if(!nameRep.equalsIgnoreCase("null")) {
            Element name = new DOMElement("name",modsNS);
            name.addAttribute("xml:lang","dan");
            Element partName = new DOMElement("partName", modsNS);
            partName.add(new DOMText(nameRep));
            Element role = new DOMElement("role", modsNS);
            Element roleTerm = new DOMElement("roleTerm", modsNS);
            roleTerm.add(new DOMText("komponist"));
            role.add(roleTerm);
            name.add(partName);
            name.add(role);
            mdsroot.add(name);
          }    
        }else{
          String titleRep = row.get("TITEL")+"";  
          String nameRep = row.get("KOMPONIST")+"";
          if(!titleRep.equalsIgnoreCase("null")){
            titleInfo = new DOMElement("titleInfo",modsNS);
            titleInfo.addAttribute("xml:lang","dan");
            title = new DOMElement("title", modsNS);
            if(nameRep.equalsIgnoreCase("null")) {
              title.add(new DOMText(titleRep));
            }else{
              title.add(new DOMText(nameRep + " - " + titleRep));              
            }
            titleInfo.add(title);
            mdsroot.add(titleInfo);
          } 
        }
      
        
        String yearRep = row.get("AARSTAL")+"";
          if(!yearRep.equalsIgnoreCase("null")){  
            Element originInfo = new DOMElement("originInfo", modsNS);
            originInfo.addAttribute("xml:lang","dan");
            Element date = new DOMElement("dateIssued",modsNS);
            date.add(new DOMText(yearRep));
            mdsroot.add(date);
          }
        
        //if we ned to link to a pdf
        if((row.get("BASISURL_JPG")+"").equals("null"))
        {  
          //We ad the pdf link
          Element identifierPdf = new DOMElement("note", modsNS);
          identifierPdf.addAttribute("type","pdf");
          identifierPdf.add(new DOMText(""+row.get("BASISURL_PDF")+row.get("FILNAVN_PDF")));
          mdsroot.add(identifierPdf);
                
                     
          Element div = new DOMElement("div",metsNS); 
          div.addAttribute("xml:lang","dan");
          
          div.addAttribute("ID","mus:"+this.projectID+":div:logical:"+row.get("NODE_ID"));
          div.addAttribute("DMDID","mus:"+this.projectID+":dmd:"+row.get("NODE_ID"));
          structMap.add(div);          
              
        }else{
          
          Element div = new DOMElement("div",metsNS); 
          div.addAttribute("xml:lang","dan");
          div.addAttribute("ID","mus:"+this.projectID+":div:logical:"+row.get("NODE_ID"));
          div.addAttribute("DMDID","mus:"+this.projectID+":dmd:"+row.get("NODE_ID"));
          
          Element metsPtr = new DOMElement("metsPtr",metsNS);
          metsPtr.addAttribute("ID","mus:"+this.projectID+":metsPtr:"+row.get("NODE_ID"));
          metsPtr.addAttribute("loctype","DOI");
          metsPtr.addAttribute("xlink:href",row.get("NODE_ID")+"");
          
          div.add(metsPtr);
          structMap.add(div);          
             
        }
        if(!infoUrl.equals("null")){
          topTitle.setText(projTitle);        
          linkTitle.setText("Info om " +projTitle);
        }
        linkUri.setText(infoUrl);
        
        
        xdata.add(mdsroot);
        mdwrap.add(xdata);
        dmds.add(mdwrap);  
        root.add(dmds);
       }
    }
    
    root.add(structMap);
    return doc;
  }
     
  public Document getNodeMets() {
    this.node = this.getNode();
    DOMDocument doc = new DOMDocument();
    Iterator siderator = this.node.iterator();
    DatabaseRow row = null;
    if(siderator.hasNext()) {
       row = (DatabaseRow)siderator.next();
    }
    if((row.get("BASISURL_JPG")+"").equals("null"))
    {
      Element root    = new DOMElement("mets",metsNS);
      doc.add(root);
      return doc;
    }
    
  
    Element root    = new DOMElement("mets",metsNS);
    root.add(xlinkNS);
    root.add(modsNS);
    root.addNamespace("","http://www.w3.org/1999/xhtml");
    root.addAttribute("OBJID","mus:"+this.musicID);
      
    doc.add(root);
        
    
    if(row != null) {
      Element dmds    = new DOMElement("dmdSec",metsNS);
      dmds.addAttribute("ID","md-root");
      Element mdwrap  = new DOMElement("mdWrap",metsNS);
      mdwrap.addAttribute("MDTYPE","MODS");
      Element xdata   = new DOMElement("xmlData",metsNS);
      Element mdsroot = new DOMElement("mods",modsNS);
      
      String titleRep = row.get("TITEL")+"";  
      if(!titleRep.equalsIgnoreCase("null")){
        Element titleInfo = new DOMElement("titleInfo",modsNS);
        titleInfo.addAttribute("xml:lang","dan");
        Element title = new DOMElement("title", modsNS);
        title.add(new DOMText(titleRep));
        titleInfo.add(title);
        mdsroot.add(titleInfo);
      }
      
      String nameRep = row.get("KOMPONIST")+"";
      if(!nameRep.equalsIgnoreCase("null")) {
        Element name = new DOMElement("name",modsNS);
        name.addAttribute("xml:lang","dan");
        Element partName = new DOMElement("partName", modsNS);
        partName.add(new DOMText(nameRep));
        Element role = new DOMElement("role", modsNS);
        Element roleTerm = new DOMElement("roleTerm", modsNS);
        roleTerm.add(new DOMText("komponist"));
        role.add(roleTerm);
        name.add(partName);
        name.add(role);
        mdsroot.add(name);
      }  
    
        Element relatedItem = new DOMElement("relatedItem",modsNS);
        relatedItem.addAttribute("type","host");
        Element titleInfo = new DOMElement("titleInfo",modsNS);
        titleInfo.addAttribute("xml:lang","dan");
        Element title = new DOMElement("title", modsNS);
        title.add(new DOMText("Musik- og Teaterafdelingen"));
        titleInfo.add(title);
        relatedItem.add(titleInfo);
        Element hostUri = new DOMElement("identifier",modsNS);
        hostUri.addAttribute("type","uri");
        hostUri.addAttribute("xml:lang","dan");
        hostUri.add(new DOMText("/da/kb/nb/mta/"));
        relatedItem.add(hostUri);
        mdsroot.add(relatedItem);
        
      
      String noteRep = row.get("BESKRIVELSE")+"";
      if(!noteRep.equalsIgnoreCase("null")){
        Element note = new DOMElement("note", modsNS);
        note.addAttribute("xml:lang","dan");
        note.addAttribute("type","presentation");
        note.add(new DOMText(row.get("BESKRIVELSE")+""));
        mdsroot.add(note);
      }
      
      Element identifierPdf = new DOMElement("identifier", modsNS);
      identifierPdf.addAttribute("type","pdf");
      identifierPdf.add(new DOMText(""+row.get("BASISURL_PDF")+row.get("FILNAVN_PDF")));
      mdsroot.add(identifierPdf);
      
      Element identifierJpg = new DOMElement("identifier", modsNS);
      identifierJpg.addAttribute("type","thumb");
      identifierJpg.add(new DOMText(""+row.get("BASISURL_JPG")+row.get("FILNAVN_JPG"))); 
      mdsroot.add(identifierJpg);
      
      
      String extRep = ""+row.get("EKSTERNURL");
      if(!extRep.equalsIgnoreCase("null")){
        Element identifierExt = new DOMElement("note", modsNS);
        identifierExt.addAttribute("type","extUrl");
        identifierExt.addAttribute("xlink:href",extRep);
        mdsroot.add(identifierExt);
      }
      
      String yearRep = row.get("AARSTAL")+"";
      if(!yearRep.equalsIgnoreCase("null")){  
        Element originInfo = new DOMElement("originInfo", modsNS);
        originInfo.addAttribute("xml:lang","dan");
        Element date = new DOMElement("dateIssued",modsNS);
        date.add(new DOMText(yearRep));
      }
      
      xdata.add(mdsroot);
      mdwrap.add(xdata);
      dmds.add(mdwrap);  
      root.add(dmds);
    }
       
    
    return doc;
  }
 
  
  
  
  private String selelctNode()
  {
  
    return  "select t.*, p.basisurl_pdf, p.basisurl_jpg, p.inforurl  from mus.mus_node t, mus.mus_projekt p " + 
            "where p.projekt_id = t.projekt_id " +
            " and node_id = " + this.musicID;
    }
  
  private String selelctProjekt()
  {
      return  " select t.*, p.projekt_navn, p.basisurl_pdf, p.basisurl_jpg, p.infourl  from mus.mus_node t, mus.mus_projekt p " + 
            " where p.projekt_id = '"+this.projectID+"' " + 
            " and t.projekt_id = '"+this.projectID+"' " + 
            " order by t.titel ";
  }
  
  private String selelctProjektOrderKomp()
  {
    return  " select t.*, p.projekt_navn, p.basisurl_pdf, p.basisurl_jpg, p.infourl  from mus.mus_node t, mus.mus_projekt p " + 
            " where p.projekt_id = '"+this.projectID+"' " + 
            " and t.projekt_id = '"+this.projectID+"' " + 
            " order by t.komponist";
  }
  
  
  private Collection getNode()
  {
    search.setSession("manussession");
    return search.executeQuery(this.selelctNode(), 9999);
  }
   
  private Collection getProjekt()
  {
    search.setSession("manussession");
    return search.executeQuery(this.selelctProjekt(), 9999);
  }
  
  private Collection getProjektOrderKomp()
  {
    search.setSession("manussession");
    return search.executeQuery(this.selelctProjektOrderKomp(), 9999);
  }
  
  public String getMusViewMode(String project)
  {
     String sql = "select app_mode_id from mus.mus_projekt where projekt_id='"+project+"'";
     Collection res = search.executeQuery(sql,999);   
     return ((DatabaseRow)res.iterator().next()).get("APP_MODE_ID")+"";    
  }
} 
   
 

