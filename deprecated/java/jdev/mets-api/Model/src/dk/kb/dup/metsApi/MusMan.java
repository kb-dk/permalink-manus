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

public class MusMan
{
  private Collection sider = null;
  private Collection manus = null;
  private Collection indices = null;
  private Collection project = null;
  
  private String musicID;
  private String pId = "1";
  private ManusSearch search  = new ManusSearch();
  private Metadata mdMods     = new Metadata();
  private Namespace metsNS    = new Namespace("m","http://www.loc.gov/METS/");
  private Namespace modsNS    = new Namespace("md","http://www.loc.gov/mods/v3");
  private Namespace xlinkNS   = new Namespace("xlink","http://www.w3.org/1999/xlink");

  public MusMan()
  {
    this.sider = this.getSider();
    this.manus = this.getManus();
    this.indices =this.getIndices();
  }

  public MusMan(String mID)
  {
    this.musicID = mID;
    this.sider = this.getSider();
    this.manus = this.getManus();
    this.indices = this.getIndices();
  }
  
  public void setProject(String pid)
  {
    this.pId = pid;
  }
  
  public Document getProjectMets()
  {
    this.project = this.getProject();
    DOMDocument doc = new DOMDocument();
    Element root    = new DOMElement("mets",metsNS);
    root.add(xlinkNS);
    root.add(modsNS);
    root.addNamespace("","http://www.w3.org/1999/xhtml");
    root.addAttribute("OBJID","musman");
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
    title.add(new DOMText("J.P.E. Hartmann"));
    titleInfo.add(title);
    mdsroot.add(titleInfo);
    
    Element relatedItem = new DOMElement("relatedItem",modsNS);
    relatedItem.addAttribute("type","host");
    titleInfo = new DOMElement("titleInfo",modsNS);
    titleInfo.addAttribute("xml:lang","dan");
    title = new DOMElement("title", modsNS);
    title.add(new DOMText("Musikafdelingen"));
    titleInfo.add(title);
    relatedItem.add(titleInfo);
    Element hostUri = new DOMElement("identifier",modsNS);
    hostUri.addAttribute("type","uri");
    hostUri.addAttribute("xml:lang","dan");
    hostUri.add(new DOMText("http://www.kb.dk/musik"));
    mdsroot.add(hostUri);
    mdsroot.add(relatedItem);
    
    xdata.add(mdsroot);
    mdwrap.add(xdata);
    dmds.add(mdwrap);  
    root.add(dmds);
        
    Element structMap = new DOMElement("structMap", metsNS);
    structMap.addAttribute("type","logical");
       
    for(Iterator prorator = this.project.iterator();prorator.hasNext();)
    {
      DatabaseRow row = null;
      if(prorator.hasNext()) {
        row = (DatabaseRow)prorator.next();
      }
      if(row != null) {
        dmds    = new DOMElement("dmdSec",metsNS);
        dmds.addAttribute("ID","musman:"+row.get("LOEBENR")+":dmd");
        mdwrap  = new DOMElement("mdWrap",metsNS);
        mdwrap.addAttribute("MDTYPE","MODS");
        xdata   = new DOMElement("xmlData",metsNS);
        mdsroot = new DOMElement("mods",modsNS);
        
        //We allways add titel and komponist
        String titleRep = row.get("TITEL")+"";  
        if(!titleRep.equalsIgnoreCase("null")){
          titleInfo = new DOMElement("titleInfo",modsNS);
          titleInfo.addAttribute("xml:lang","dan");
          title = new DOMElement("title", modsNS);
          title.add(new DOMText(titleRep));
          titleInfo.add(title);
          mdsroot.add(titleInfo);
        }
      
        relatedItem = new DOMElement("relatedItem",modsNS);
        relatedItem.addAttribute("type","host");
        titleInfo = new DOMElement("titleInfo",modsNS);
        titleInfo.addAttribute("xml:lang","dan");
        title = new DOMElement("title", modsNS);
        title.add(new DOMText("Musikafdelingen"));
        titleInfo.add(title);
        relatedItem.add(titleInfo);
        hostUri = new DOMElement("identifier",modsNS);
        hostUri.addAttribute("type","uri");
        hostUri.addAttribute("xml:lang","dan");
        hostUri.add(new DOMText("/da/kb/nb/mta/"));
        mdsroot.add(hostUri);
        mdsroot.add(relatedItem);
      
        Element div = new DOMElement("div",metsNS); 
        div.addAttribute("xml:lang","dan");
        div.addAttribute("ID","musman:"+row.get("LOEBENR")+":div");
        div.addAttribute("DMDID","musman:"+row.get("LOEBENR")+":dmd");
          
        Element metsPtr = new DOMElement("metsPtr",metsNS);
        metsPtr.addAttribute("ID","musman:"+row.get("LOEBENR")+":metsPtr");
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
    root.addNamespace("","http://www.w3.org/1999/xhtml");
    root.addAttribute("OBJID","musman:"+this.musicID);
      
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
    
    Element fs = this.fileSec();
    root.add(fs);
    
    Element sm = this.structMap();
    root.add(sm);      
    
    return doc;
  }
  
  public Element fileSec()
  {
    Element fSec = new DOMElement("fileSec",metsNS);
    Element fGrp = new DOMElement("fileGrp",metsNS);
    
    for(Iterator siderator = this.sider.iterator();siderator.hasNext();)
    {
      DatabaseRow row = null;
      if(siderator.hasNext()) {
        row = (DatabaseRow)siderator.next();
      }
      if(row != null) {
        Element file = new DOMElement("file", metsNS);  
        file.addAttribute("ID","musman:"+this.musicID+":file:"+row.get("SIDENR"));
        Element fLocat = new DOMElement("FLocat",metsNS);
        fLocat.addAttribute("LOCTYPE","URL");
        fLocat.addAttribute("xlink:href",row.get("URL_IMG")+"");
        file.add(fLocat);
        fGrp.add(file);
      }
    }
    fSec.add(fGrp);
    return fSec;
  }
  
  public Element structMap()
  {
    Element sMap =  new DOMElement("structMap", metsNS);
    sMap.addAttribute("type","logical");
    Element rootDiv =  new DOMElement("div", metsNS);
    rootDiv.addAttribute("ID","struct-root");
    rootDiv.addAttribute("DMDID","md-root");
        
    String currentPage = "";
    Iterator indirator = this.indices.iterator();
    DatabaseRow indiRow = null;
    if(indirator.hasNext())
    {
      indiRow = (DatabaseRow)indirator.next();
      currentPage = indiRow.get("HI_SIDENR")+"";
    }    
    
    for(Iterator siderator = this.sider.iterator();siderator.hasNext();)
    {
      DatabaseRow row = null;
      if(siderator.hasNext()) {
        row = (DatabaseRow)siderator.next();
      }
      if(row != null) {
        String sideNr = row.get("SIDENR")+"";
        Element div = new DOMElement("div", metsNS);
        div.addAttribute("ID","musman"+this.musicID+":div:logical:"+row.get("SIDENR"));
        div.addAttribute("xml:lang","dan");
        div.addAttribute("ORDERLABEL",row.get("PSEUDOINFO_1_DK")+"");
        if(sideNr.equalsIgnoreCase(currentPage))
        {
          div.addAttribute("LABEL",(indiRow.get("TEKST_DK")+"").replaceAll("<BR>"," "));    
          if(indirator.hasNext())
          {
              indiRow = (DatabaseRow)indirator.next();
              currentPage = indiRow.get("HI_SIDENR")+"";
          }
        }        
        Element fptr =  new DOMElement("fptr",metsNS);
        fptr.addAttribute("FILEID","musman:"+this.musicID+":file:"+row.get("SIDENR"));
        
        div.add(fptr);
        
        rootDiv.add(div);
      }
    }
    sMap.add(rootDiv);
    return sMap;
  }
  
  
  
    
  
  public Document mods()
  {
    Iterator datarator = this.manus.iterator();
    DatabaseRow row = null;
    if(datarator.hasNext()) {
      row = (DatabaseRow)datarator.next();
    }
    if(row != null) {
      mdMods.addTitle(row.get("TITEL")+"","dan");
      mdMods.addOriginInfo("","","",row.get("AARSTAL")+"");
      mdMods.addSignature(row.get("SIGNATUR")+"");
      mdMods.addIdentifier("thumb",row.get("THUMBNAIL")+"");
      cleanHtml cleaner = new cleanHtml();
      if(row.get("PRAESENTATION_DK")!=null)
      {
        Element div = cleaner.getBody(row.get("PRAESENTATION_DK") +"");       
        if(div != null) {
          mdMods.addNote("presentation",div,"dan");
        }        
      }
      if(row.get("DOWNLOAD_TEKST")!=null)
      {
        Element div = cleaner.getBody(row.get("DOWNLOAD_TEKST") +"");       
        if(div != null) {
          mdMods.addNote("caption",div,"dan");
        }        
      }
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
  
  private String selelctSider()
  {
    return "select * from musman.hsk_sider t where hs_loebenr =" + this.musicID + " order by sidenr";
  }
  
  private String selelctManus()
  {
    return "select * from musman.hsk_manuskripter t where loebenr = " + this.musicID;
  }
  
  private String selectIndices()
  {
    return "select * from musman.hsk_indices t where hi_hs_loebenr = " + this.musicID + " order by hi_sidenr";
  }
  
  private String selectProject()
  {
    return "select * from musman.hsk_manuskripter t where hm_projektnr = " +  pId + " order by titel";
  }
  
   
  private Collection getSider()
  {
    search.setSession("manussession");
    return search.executeQuery(this.selelctSider(), 9999);
  }
  
  private Collection getManus()
  {
    search.setSession("manussession");
    return search.executeQuery(this.selelctManus(), 9999);
  }
  
  private Collection getIndices()
  {
    search.setSession("manussession");
    return search.executeQuery(this.selectIndices(), 9999);
  }
  
  private Collection getProject()
  {
    search.setSession("manussession");
    return search.executeQuery(this.selectProject(), 9999);
  }
  
}