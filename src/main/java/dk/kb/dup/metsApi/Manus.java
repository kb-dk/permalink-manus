package dk.kb.dup.metsApi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oracle.toplink.publicinterface.DatabaseRow;
import org.apache.xerces.dom.DocumentImpl;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.DOMReader;
import org.dom4j.XPath;
import org.w3c.tidy.DOMDocumentImpl;
import org.w3c.tidy.Node;
import org.w3c.tidy.Tidy; 
import dk.kb.dup.modsApi.Metadata;

/**
 * Simple Java bindings to the MANUS database. The classes in this API retrieves
 * all data for a single manuscript and encodes them as a METS XML object. For
 * information on the Metadata Encoding and Transmission Standard (METS), please
 * refer to <a href="http://www.loc.gov/standards/mets/">http://www.loc.gov/standards/mets/</a>
 * 
 * @author Sigfrid Lundberg (slu@kb.dk)
 * 
 * Last $Revision: 1.19 $ $Date: 2007/01/19 14:40:44 $ by $Author: jac $.
 * Current checkout was made as build $Name:  $
 */
public class Manus 
{

  private Collection data     = null;
  private Collection 
                  relatedData = null;
  private Map xmlns           = new HashMap();

  private ManusSearch search  = new ManusSearch();
  private Metadata mdMods     = new Metadata();
  private Page pageContent    = new Page();
  private Namespace metsNS    = new Namespace("m","http://www.loc.gov/METS/");
  private Namespace xlinkNS   = new Namespace("xlink","http://www.w3.org/1999/xlink");
  private Namespace modsNS    = new Namespace("md","http://www.loc.gov/mods/v3");
  
  private String dtaSrcLbl    = "manus";
  private String imageBaseUri = "";
  private String manusId      = "";
  private String mainLang     = "";
  private String altLang      = "";
  private String projectCode  = "";
  
  /**
   * The constructor does two things: 1. It sets the Manuscript ID. This is
   * the Oracle ID for the object. 2. It tacitly retrieves the data concerning
   * the object from the database.
   * 
   * @param manuscriptId
   */
  public Manus(String manuscriptId)
  {
    this.manusId = manuscriptId;
    this.data = this.getData();
    this.pageContent.setManuscriptId(manusId);
    this.pageContent.setDataSourceLabel(this.dtaSrcLbl);
    this.relatedData = this.getRelatedLinkData();
  }
  
  
  /**
   * If you already have a Manus object, you may reuse it for a new item in the
   * database. By setting Manuscript ID, you will trigger the retrieval of data,
   * in the same way as when you create a new object (using the constructor).
   * 
   * @param manuscriptId
   */
  public void setManuscriptId(String manuscriptId) {
    manusId = manuscriptId;
    this.data = this.getData();
  }
  
  /**
   * Just to ensure that we don't forgetting what we're doing...
   * It returns Manuscript ID refers to the object's ID in the MANUS database
   * 
   * @return String getManuscriptId() 
   */
  public String getManuscriptId() {
    return manusId;
  }
  
  /**
   * This is a very tedious method (it was tedious to write and it is even worse
   * to read). It is the main work horse of this class. It returns a 
   * <a href="http://www.loc.gov/standards/mets/">METS object</a>
   * in the form of a <a href="http://www.dom4j.org/">dom4j</a> 
   * <a href="http://www.dom4j.org/apidocs/org/dom4j/Document.html">Document</a>
   * 
   * @return <a href="http://www.dom4j.org/apidocs/org/dom4j/Document.html">Document</a> getMets()
   */
  public Document getMets() {
  
  // root is the document element, which is called mets  
    
    Element root    = new DOMElement("mets",metsNS);
    root.add(xlinkNS);
    
  // we use xhtml mark-up in the note elements in the metadata sections
    
    root.addNamespace("","http://www.w3.org/1999/xhtml");
    root.add(modsNS);
    root.addAttribute("OBJID","manus:"+this.getManuscriptId());
    
  // here we instantiate a document, and add our root element to that object  
    
    DOMDocument doc = new DOMDocument();
    doc.add(root);
    
    Element metsHdr    = new DOMElement("metsHdr",metsNS);  
    Element agent      = new DOMElement("agent",metsNS);
    metsHdr.add(agent);
    Element agentName  = new DOMElement("name",metsNS);
    Element agentNote  = new DOMElement("note",metsNS);
    agentName.addText("dk.kb.dup.metsApi software agent using data from Manuscript\n"+
                      "department at Royal Library, Copenhagen, DK.");
    agentNote.addText("Software release $Id: Manus.java,v 1.19 2007/01/19 14:40:44 jac Exp $");
    agent.add(agentName);
    agent.add(agentNote);
    agent.addAttribute("ROLE","CREATOR");
    /*
    Element altRecordID = new DOMElement("altRecordID");
    
    altRecordID.addText(this.getManuscriptId());
    
    metsHdr.add(altRecordID);
    */
    root.add(metsHdr);
    
  /*
   * the metadata for the top level object, the manuscript itself is encoded in   
   * Metadata Object Description Schema, MODS. The creation of this object take place
   * in the mods method of this class, but the actual encoding occurs in the
   * Metadata class
   */
    
   /*
    * mods returns a document. This is more general, and we can reuse this method
    * for creating stand-alone MODS metadata. This would be useful for the implementing
    * stuff like an OAI server, or for indexing purposes. Therefore we immeadiately
    * detach the documents root element, and then we include it in the XML-structure
    * devised for entering XML metadata in a mets document.
    */
    
    Document mds = this.mods();
    Element mdsroot = mds.getRootElement();
    mdsroot.detach();
    Element dmds    = new DOMElement("dmdSec",metsNS); 
    dmds.addAttribute("ID","md-root");
    Element mdwrap  = new DOMElement("mdWrap",metsNS);
    mdwrap.addAttribute("MDTYPE","MODS");
    Element xdata   = new DOMElement("xmlData",metsNS);
    root.add(dmds);
    dmds.add(mdwrap);
    mdwrap.add(xdata);
    xdata.add(mdsroot);
       

    
   /*
    * From now on we turn our attention to the manuscript's actual content.
    * All data on that is retrieved using the pageContent object, which is an
    * instantiation of the Page class.
    * 
    * The build() method triggers retrieval of data from several tables in the 
    * database, and although the name "Page" implies that it contains info on a
    * single page it builds the whole structure.
    */
    this.pageContent.build();
    
    /*
     * Here we get metadata for relevant pages, i.e., those pages that are annotated
     * in some way or another.
     * 
     * The metadata are encoded as <dmdSection> ... </dmdSection> which should
     * be added directly to the root element. Hence we get them as a list, and then
     * we add them, one at a time.
     */
    
    List pageMdList = pageContent.getDmdSections();
    Iterator mediterator = pageMdList.iterator();
    while(mediterator.hasNext()) {
      Element md = (Element)mediterator.next();
      root.add(md);
    }
    
    /*
     * These are individual elements, each of which containing extensive sub-trees.
     */
    Element filesec   = pageContent.getFileSec();
    root.add(filesec);
    Element structMap = pageContent.getStructMap();
    root.add(structMap);
    Element variantMap = pageContent.getVariantStructMap();
    root.add(variantMap);
    Element structlink = pageContent.getStructLinks();
    root.add(structlink);
    return doc;
  }
  
   
  /**
   * The mods() method unpacks data from an Oracle toplink
   * <a href="http://www.oracle.com/technology/products/ias/toplink/doc/1013/MAIN/b13698/oracle/toplink/publicinterface/DatabaseRow.html">DatabaseRow</a>
   * The encoding is performed in the Metadata class.
   * 
   * @return <a href="http://www.dom4j.org/apidocs/org/dom4j/Document.html">Document</a> mods()  
   */
  public Document mods() {
    Iterator datarator = this.data.iterator();
    DatabaseRow row = null;
    if(datarator.hasNext()) {
      row = (DatabaseRow)datarator.next();
    }
    if(row != null) {

      this.mainLang = row.get("PRESENTATIONLANGMAIN")+"";
      if(row.get("MANUSTITLE_ALTLANG") != null) {
        this.altLang = row.get("PRESENTATIONLANGALT") + "";
      }
      
    // Tedious code, we have to have it somewhere :-)
      mdMods.addTitle( row.get("MANUSTITLE")+"",
                       row.get("PRESENTATIONLANGMAIN")+"");
      if(row.get("MANUSTITLE_ALTLANG") != null) {
        mdMods.addTitle( row.get("MANUSTITLE_ALTLANG")+"",
                        row.get("PRESENTATIONLANGALT")+"");
      }
      mdMods.addAuthor(row.get("MANUSAUTHOR")+"",
                       row.get("PRESENTATIONLANGMAIN")+"");
      if(row.get("MANUSAUTHOR_ALTLANG") != null) {
        mdMods.addAuthor(row.get("MANUSAUTHOR_ALTLANG")+"",
                         row.get("PRESENTATIONLANGALT")+"");      
      }
      mdMods.addSignature(row.get("SIGNATURE")+""); 
      if(row.get("MANUSLANG") != null) {
        mdMods.addLanguage(row.get("MANUSLANG")+"");
      }
      
      if(row.get("PROJECTCODE") != null) {
        this.projectCode = row.get("PROJECTCODE")+"";
      }
      
      /*
       * We need some other data for generating the page related data (the actual
       * content. These data are retrieved here, and passed to the Page class here.
       * <b>NB</b>, this must be done before using the pageContent instantiation
       * of this class.
       */
      this.imageBaseUri = row.get("BASEURLPAGEIMG") +"";
      this.pageContent.setBaseUrl(this.imageBaseUri);
      this.pageContent.setMainLang(row.get("PRESENTATIONLANGMAIN")+"");
      if(row.get("PRESENTATIONLANGALT") != null) {
        this.pageContent.setAltLang(row.get("PRESENTATIONLANGALT")+"");
      }
      
      /*
       * There are important pieces of data stored as HTML fragments. That was
       * a dirty hack at the time, and to preserve it is an even more dirty hack,
       * but we would not be able to reimplement the service unless we did it this way.
       */
      cleanHtml cleaner = new cleanHtml();
      if(row.get("MANUSINFO") != null) {
        Element div = cleaner.getBody(row.get("MANUSINFO") +"");       
        if(div != null) {
          mdMods.addNote("presentation",div,row.get("PRESENTATIONLANGMAIN")+"");
        }
      }
      if(row.get("MANUSINFO_ALTLANG") != null) {
        Element div = cleaner.getBody(row.get("MANUSINFO_ALTLANG") +"");       
        if(div != null) {
          mdMods.addNote("presentation",div,row.get("PRESENTATIONLANGALT")+"");
        }
      }
      if(row.get("URLMANUSFRONTIMG") != null) {
        mdMods.addIdentifier("thumb",row.get("URLMANUSFRONTIMG")+"");
      }    
      List links = this.relatedItems();
      Iterator linkerator = links.iterator();
      while(linkerator.hasNext()) {
        mdMods.addRelatedItem("menuitem",(Metadata)linkerator.next());
      }
      mdMods.addRelatedItem("host",this.getHostData());
    }
    return mdMods.getModsDocument();
  }
  
  List relatedItems() {
    List itemList = new ArrayList();
    Iterator datarator = this.relatedData.iterator();    
    while (datarator.hasNext()) {
       Metadata item = new Metadata();
       DatabaseRow row = (DatabaseRow)datarator.next();
       if(row.get("MANLINKURL") != null) {
          item.addIdentifier("uri",row.get("MANLINKURL")+"",this.mainLang);
          item.addTitle(row.get("MANLINKTEXT")+"",this.mainLang);
       }
       if(row.get("MANLINKURL_ALTLANG") != null) {
          item.addIdentifier("uri",row.get("MANLINKURL_ALTLANG")+"",this.altLang);
          item.addTitle(row.get("MANLINKTEXT_ALTLANG")+"",this.altLang);
       }
       itemList.add(item);
    }
    return itemList;
  }
  
  /**
   * The private method retrieving data from Oracle.
   * @return 
   */
  private Collection getData() {
    String SQL = "select * from manus.manus where manusid="+manusId;
    Collection data = search.executeQuery(SQL,10);
    return data;
  }
  
  private Collection getRelatedLinkData() {
    String SQL = "select * from manus.manlink where manusid=" + 
                  manusId +
                 " order by manlinkseqno";
    Collection data = search.executeQuery(SQL,10);
    return data;
  }
  
  private Collection getRdbmsData(String SQL) {                    
    Collection data = search.executeQuery(SQL,100);
    return data;
  }
  

  private Metadata getHostData() {
    Metadata hostData = new Metadata();
    
    String SQL = 
      "select * from manus.project where projectcode='" + 
      this.projectCode + "'"; 
      
    Collection project = this.getRdbmsData(SQL);
    Iterator datarator = project.iterator();    
    if(datarator.hasNext()) {
       DatabaseRow row = (DatabaseRow)datarator.next();
       String deptCode = row.get("DEPARTMENTCODE") + "";
       String dptSQL      = "select * from manus.department " +
          "where DEPARTMENTCODE='" + deptCode + "'";
       Collection dept = this.getRdbmsData(dptSQL);
       datarator = dept.iterator();
       if(datarator.hasNext()) {
          row = (DatabaseRow)datarator.next();
          hostData.addIdentifier("uri",row.get("DEPARTMENTURLDK") + "","dan");
          hostData.addIdentifier("uri",row.get("DEPARTMENTURLUK") + "","eng");
       }       
       String dptTransSQL = "select * from manus.depnametrans "+
          "where departmentcode='" + deptCode + "'";
       Collection deptTrans = this.getRdbmsData(dptTransSQL);
       Iterator deptDatarator = deptTrans.iterator();
       while(deptDatarator.hasNext()) {
         DatabaseRow drow = (DatabaseRow)deptDatarator.next();
         hostData.addTitle( drow.get("DEPARTMENTNAME") + "" ,
                            drow.get("LANGCODE") + "");
       }
    }
    return hostData;
  }

 
}