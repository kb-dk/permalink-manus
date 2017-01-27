package dk.kb.dup.metsApi;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.dom.DOMElement;
import java.util.HashMap;
import dk.kb.dup.modsApi.Metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>Simple Java bindings to the MANUS database. The classes in this API retrieves
 * all data for the content of a manuscript and the various components needed
 * for composing a complete METS XML object. The creation of the METS object is
 * done in the Manus class, which obtains data from this class.</p>
 * 
 * <p>The class was christened after the table in Oracle from which it gathers
 * its data. This is not a single page object, it contain all data on all pages.</p>
 * 
 * @author Sigfrid Lundberg (slu@kb.dk)
 * 
 * Last $Revision: 1.13 $ $Date: 2006/12/01 10:53:30 $ by $Author: slu $.
 * Current checkout was made as build $Name:  $
 */
public class Page 
{
    private Namespace   metsNS               = new Namespace("m","http://www.loc.gov/METS/");
    private Namespace   xlinkNS              = new Namespace("xlink","http://www.w3.org/1999/xlink");
    private Metadata    mods                 = new Metadata();
    private Collection  data                 = null;
    private Collection  variantData          = null;
    private ManusSearch search               = null;
    private Element     fileSection          = null;
    private Element     logicalStructure     = null;
    private Element     variantStructure     = null;
    private Element     crossReferences      = null;
  
    private String      dataSourceLabel      = "";
    private String      manusId              = "";
    private String      baseUrl              = "";
  
    private String      altLang              = "";
    private String      mainLang             = "";

    private Logger      LOGGER               = LoggerFactory.getLogger(Page.class);
  
    private HashMap     correlationsAltLang  = new HashMap();
    private HashMap     correlationsMainLang = new HashMap();
    private HashMap     divIdByORDERLAB      = new HashMap();
    private List        orderLabels          = new ArrayList();
  
    private List        dmdsections          = new ArrayList();

    /**
     * The constructor doesn't do any thing than, yeah, construction work...
     */
    public Page(ManusSearch search) {
	this.search = search;
    }
  
    public void init() {
	this.getData(search);  
    }
  
    /**
     * If you already have a Page object, you may reuse it for a new item in the
     * database. By setting Manuscript ID, you will trigger the retrieval of data,
     * in the same way as when you create a new object (using the constructor).
     * 
     * @param manuscriptId
     */
    public void setManuscriptId(String manuscriptId) {
	if(manuscriptId.length()>0) {
	    this.manusId = manuscriptId;
	    LOGGER.debug("ID = " + manuscriptId);
	} else {
	    this.manusId = "";
	    LOGGER.warn("No manuscriptId");
	}
	this.init();

    }
  
    /**
     * In case you forgot what you're up to...
     * 
     * @return manuscriptId
     */
    public String getManuscriptId() {
	return this.manusId;
    }
  
    /**
     * The baseUrl is the "home directory" of the image file collection described.
     * Page gets this information from Manus
     * @param baseURL
     */
    public void setBaseUrl(String baseURL) {
	this.baseUrl = baseURL;
    }
  
    /**
     * Sets the data source label, which is used for creating uniq div id across
     * the entire set of mets files in the repository
     * @param label
     */
    public void setDataSourceLabel(String label) {
	this.dataSourceLabel = label;
    }
  
    /**
     * Gets the data source label.
     * @return Data source label
     */
    public String getDataSourceLabel() {
	return this.dataSourceLabel;
    }
  
    /**
     * Again, in case you forgot what you're doing, or to make the bean aspect of
     * the java coding complete ;-)
     * @return baseURL 
     */
    public String getBaseUrl() {
	return this.baseUrl;
    }
  
    /**
     * The Page table knows that there are two languages for the user interface, the
     * main language and the alternative one. Here we tell out page object what language
     * is the main one.
     * 
     * @param language
     */
    public void setMainLang(String language) {
	this.mainLang = language;
    }
  
    /**
     * The alternative language is set here. See setMainLang.
     * @param language
     */
    public void setAltLang(String language) {
	this.altLang  = language;
    }
  
    /**
     * <p>When everything has been set, we can build all the stuff we are supposed to
     * get paid for.</p>
     * 
     * <p>This is a complex method. The source code is heavily commented.</p>
     */
    public void build() {

	LOGGER.debug("start building");
  
	/*
	 * This is for building the fileSec
	 */
	DOMElement fileSec = new DOMElement("fileSec",metsNS);
	fileSec.add(xlinkNS);
	DOMElement fileGrp = new DOMElement("fileGrp",metsNS);
	fileSec.add(fileGrp);
  
    
	/*
	 * This is for the structMap
	 */
    
	this.logicalStructure = new DOMElement("structMap",metsNS);
	this.logicalStructure.addAttribute("type","physical");
	DOMElement logdiv = new DOMElement("div",metsNS);
    
	/*
	 * The structMap is built up of divs, the top level one is the one containing
	 * the entire work. Here we hard code that this node has ID 'struct-root'
	 * in all our files. This a convention we use, not a general rule for mets-users
	 * in general. Appart from ID, some divs have an IDREF DMDID, which is referring 
	 * to the corresponding metadata, i.e., in this case metadata for a single page.
	 * 
	 * In manus the page level metadata is basically an annotation. We encode this as
	 * a note in mods. See below.
	 */
    
	logdiv.addAttribute("ID",this.getDataSourceLabel() + ":" +
			    this.getManuscriptId() + ":physical:" + "root");
	logdiv.addAttribute("DMDID","md-root");
	this.logicalStructure.add(logdiv);
    
	// here we iterate over all pages

	Iterator datarator = data.iterator();
	while(datarator.hasNext()) {
	    DOMElement file = new DOMElement("file",metsNS);
	    DatabaseRow row = (DatabaseRow)datarator.next();
	    DOMElement  div = new DOMElement("div",metsNS);
      
	    /*
	     * Here we construct file entries and divs and IDs and IDREFs from
	     * the file pointers to the files
	     */
	    String id       = "file:default:"+this.getManuscriptId()+":"+row.get("PAGEID");
	    String divid    = this.getDataSourceLabel() + ":" +
		this.getManuscriptId() + 
		":div:physical:" + 
		row.get("PAGEID");

	    LOGGER.info("id=" + id  + " divid=" + divid);

	    div.addAttribute("ID",divid);
	    file.addAttribute("ID",id);
	    DOMElement flocat = new DOMElement("FLocat",metsNS);
	    flocat.addAttribute("LOCTYPE","URL");
	    flocat.addAttribute("xlink:href",this.baseUrl+"/"+row.get("PAGEIMGFILE"));
	    file.add(flocat);
	    fileGrp.add(file);


      
	    /*
	     * Here we gather data for the metadata secion corresponding to this object.
	     * We do it first for the main language
	     */
     
	    CleanHtml cleaner = new CleanHtml();
	    Metadata mods = new Metadata();
	    if(row.get("PAGEINFO") != null) {
		Element pageinfo = cleaner.getBody(row.get("PAGEINFO")+"");
		if(pageinfo != null) {
		    mods.addNote("annotation",pageinfo,this.mainLang);
		}
	    } else {
		LOGGER.debug("PAGEINFO is null");
	    }

	    LOGGER.debug("Adding file metadata");
      
	    /*
	     * And then for the alternative language
	     */
	    if(row.get("PAGEINFO_ALTLANG") != null && this.altLang.length()>0 ) {
		Element pageinfo = cleaner.getBody(row.get("PAGEINFO_ALTLANG")+"");
		if(pageinfo != null) {
		    mods.addNote("annotation",pageinfo,this.altLang);
		}
	    }

	    LOGGER.debug("Survived PAGEINFO_ALTLANG");
      
	    /*
	     * Once we've gathered the annotations, we have to create a place for them
	     * to live. The mods object return a document which we need to detach before we
	     * add it to xmlData, which has to be added to mdWrap which has to be added to
	     * dmdSec. The dmdSecs are gathered in a list, which upon request is delived
	     * to a manus object.
	     * 
	     * There they are entered as children to the root element of the mets file.
	     */
	    if(row.get("PAGEINFO") != null) {
		Document mds = mods.getModsDocument();
		Element mdsroot = mds.getRootElement();
		mdsroot.detach();

		Element dmds    = new DOMElement("dmdSec",metsNS); 
		String dmdId = "dmd:"+this.getManuscriptId()+":"+row.get("PAGEID");
		dmds.addAttribute("ID",dmdId);
		div.addAttribute("DMDID",dmdId);
		Element mdwrap  = new DOMElement("mdWrap",metsNS);
		mdwrap.addAttribute("MDTYPE","MODS");
		Element xdata   = new DOMElement("xmlData",metsNS);

		xdata.add(mdsroot);
		mdwrap.add(xdata);
		dmds.add(mdwrap);
              
		this.dmdsections.add(dmds);
		LOGGER.debug("put mods into mets");
	    }
	    LOGGER.debug("after page info");
      
	    /* These are preparations for the structLink section. We gather data here
	     * but we have to wait until we've got all data before we can actually do
	     * the work. See below.
	     */
     
	    /*
	     * A list of page numbers
	     */
	    this.orderLabels.add(row.get("PAGENO")+"");
      
	    /*
	     * A hash which given a page number translates it to
	     * an ID to a div the structMap
	     */
	    this.divIdByORDERLAB.put(row.get("PAGENO")+"",divid);
      
	    /*
	     * These are placeholders for list of correlations, i.e., smLink elements
	     * in the structLink section. Since the xlink:title are needed for the user
	     * interface, we need two of these, one for each language.
	     */
	    this.correlationsMainLang.put(divid,null);
	    this.correlationsAltLang.put(divid,null);
      
	    /*
	     * Now, this might be a hack. We now create an exact copy of the div we've
	     * assembled above. Then we add language specific information, the main language
	     * info to the original div, and alternative language to the copy.
	     * 
	     * We make this if and only if the altLang exists.
	     */
	    Element altDiv = null;
	    if(!this.altLang.equalsIgnoreCase("")) {
		altDiv = div.createCopy();
	    }
      
	    /*
	     * This is main language stuff to the original div
	     */
	    div.addAttribute("xml:lang",this.mainLang);
	    div.addAttribute("ORDERLABEL",row.get("PAGENO")+"");
	    if(row.get("MANINDEXLINKTEXT") != null) {
		div.addAttribute("LABEL",row.get("MANINDEXLINKTEXT")+"");
	    }
	    DOMElement fptr  = new DOMElement("fptr",metsNS);
	    fptr.addAttribute("FILEID",id);
	    div.add(fptr);
	    logdiv.add(div);
      
	    /*
	     * This is alternative language stuff, which is added to the copy. If it exeists.
	     */
      
	    if(altDiv != null) {
		altDiv.addAttribute("xml:lang",this.altLang);
		altDiv.addAttribute("ORDERLABEL",row.get("PAGENO_ALTLANG")+"");
		if(row.get("MANINDEXLINKTEXT_ALTLANG")   != null) {
		    altDiv.addAttribute("LABEL",row.get("MANINDEXLINKTEXT_ALTLANG")+"");
		}        
		altDiv.add(fptr.createCopy());
		logdiv.add(altDiv);
	    }
	}
    
	/*
	 * This for adding information on available magnifications and other image
	 * variations (detailed images and the like). They are stored in a seperate
	 * structMap, having the type "variant", and the corresponding files have
	 * a fileGrp of their own.
	 * 
	 * Here we don't gather any metadata, but we do gather data for the structLink 
	 * section
	 */
	this.variantStructure = new DOMElement("structMap",metsNS);
	this.variantStructure.addAttribute("type","variant");
	DOMElement fileGrpVariant = new DOMElement("fileGrp",metsNS);
	fileSec.add(fileGrpVariant);
    
	/*
	 * These data are stored in a seperate table in the database, hence we have
	 * a seperate java Collection to iterate.
	 */
	datarator = variantData.iterator();
	while(datarator.hasNext()) {
    
	    /*
	     * All this is for the divs and files, exactly as above
	     */
	    DOMElement file = new DOMElement("file",metsNS);
	    DatabaseRow row = (DatabaseRow)datarator.next();
	    String id =  "file:variant:"+this.getManuscriptId()+":"+row.get("PAGEVARID");
	    file.addAttribute("ID",id);
	    DOMElement div  = new DOMElement("div",metsNS);
	    String divid    = this.getDataSourceLabel() + ":" +
		this.getManuscriptId()    +  ":div:variant:" +
		row.get("PAGEVARID");
	    div.addAttribute("ID",divid);      
	    DOMElement flocat = new DOMElement("FLocat",metsNS);
	    flocat.addAttribute("LOCTYPE","URL");
	    flocat.addAttribute("xlink:href",this.baseUrl+"/"+row.get("PAGEVARIMGFILE"));
	    file.add(flocat);
	    fileGrpVariant.add(file);
	    String pageNumber = row.get("PAGENO")+"";
	    div.addAttribute("ORDERLABEL",pageNumber);
      
	    /*
	     * Here we gather data on linkage between the main structure and 
	     */
	    if(this.divIdByORDERLAB.containsKey(pageNumber)) {
		/*
		 * Create a correlation
		 */
		HashMap corr = new HashMap();
		corr.put(divid,row.get("PAGEVARLINKTEXT")+"");
		List corrList = null;
		/*
		 * First we check if the correlation is initialized. If it is we retrieve
		 * the corresponding list. If it isn't we create one
		 */
		boolean isInitialized =
		    this.correlationsMainLang.get(this.divIdByORDERLAB.get(pageNumber)) != null;
		if(isInitialized) {
		    corrList = 
			(List)this.correlationsMainLang.get(this.divIdByORDERLAB.get(pageNumber));
		} else {
		    corrList = new ArrayList();        
		}
		/*
		 * Add the new, or updated, list to the correlation map for the main language
		 */
		corrList.add(corr);
		this.correlationsMainLang.put(this.divIdByORDERLAB.get(pageNumber),corrList);
        
		/*
		 * The correlations may have different names in the two languages. If the
		 * alternative language exists, we repeat the procedure.
		 */
		if(!this.altLang.equalsIgnoreCase("")) {
		    HashMap acorr = new HashMap();
		    acorr.put(divid,row.get("PAGEVARLINKTEXT_ALTLANG")+"");
		    List acorrList = null;
		    boolean aIsInitialized =
			this.correlationsAltLang.get(this.divIdByORDERLAB.get(pageNumber)) != null;
		    if(aIsInitialized) {
			acorrList = 
			    (List)this.correlationsAltLang.get(this.divIdByORDERLAB.get(pageNumber));
		    } else {
			acorrList = new ArrayList();        
		    }
		    acorrList.add(acorr);
		    this.correlationsAltLang.put(this.divIdByORDERLAB.get(pageNumber),acorrList);
		}
	    }
	    /*
	     * Now we're back to the task of inserting stuff into one of our DOM
	     * trees: the file pointer fptr to our div and our div to the variant structure.
	     */
	    DOMElement fptr  = new DOMElement("fptr",metsNS);
	    fptr.addAttribute("FILEID",id);
	    div.add(fptr);
	    this.variantStructure.add(div);
	}
	/*
	 * Then we store the fileSec in a global variable
	 */
	this.fileSection = fileSec;
    }
  
    /**
     * Here we may harvest the fruits of our labour. This method returns the metadata
     * gathered in build()
     * @return 
     */
    public List getDmdSections() {
	return this.dmdsections;
    }
  
    /**
     * This generates and delivers the structLink section.
     * @return 
     */
    public Element getStructLinks() {
	DOMElement structLink = new DOMElement("structLink",metsNS);
    
	/*
	 * We iteratate over all pages known.
	 */
	Iterator linkerator   = this.orderLabels.iterator();
	while(linkerator.hasNext()) {
	    String pageNumber = (String)linkerator.next();
	    /*
	     * Do we know the page number, i.e., is there any corresponding ID
	     */
	    if(this.divIdByORDERLAB.containsKey(pageNumber)) {
		String id = (String)this.divIdByORDERLAB.get(pageNumber);
        
		/*
		 * Then, if we have a correlation list for that ID
		 */
		if(this.correlationsMainLang.get(id) != null) {
        
		    /*
		     * ... then we iterate that list
		     */
		    List   li = (List)this.correlationsMainLang.get(id);
		    Iterator links = li.iterator();
		    while(links.hasNext()) { 
          
			/*
			 * Each member of that list is a hash
			 */
			HashMap lnk = (HashMap)links.next();       
			if(lnk.keySet().iterator().hasNext()) {
			    /*
			     * Create a link
			     */
			    DOMElement smLink = new DOMElement("smLink",metsNS);
               
			    /*
			     * Containing the ID of a div and the corresponding title
			     * in the main language.
			     */
			    String toId      = (String)lnk.keySet().iterator().next();
			    String linktitle = (String)lnk.get(toId);
			    /*
			     * Now, we can then add our data to the link
			     */
			    smLink.addAttribute("xml:lang",this.mainLang);
			    smLink.addAttribute("xlink:title",linktitle);
			    smLink.addAttribute("xlink:from",id);
			    smLink.addAttribute("xlink:to",toId);
			    /*
			     * Then finally we add the link to the main element for this 
			     * part of the tree.
			     */
			    structLink.add(smLink);
			}
		    }
		} 
		/*
		 * Nothing new below, we just repeat the procedure for the alternative
		 * language.
		 */
		if(this.correlationsAltLang.get(id) != null) {
		    List   li = (List)this.correlationsAltLang.get(id);
		    Iterator links = li.iterator();
		    while(links.hasNext()) {       
			HashMap lnk = (HashMap)links.next();       
			if(lnk.keySet().iterator().hasNext()) {
			    DOMElement smLink = new DOMElement("smLink",metsNS);
			    String toId      = (String)lnk.keySet().iterator().next();
			    String linktitle = (String)lnk.get(toId);
			    smLink.addAttribute("xml:lang",this.altLang);
			    smLink.addAttribute("xlink:title",linktitle);
			    smLink.addAttribute("xlink:from",id);
			    smLink.addAttribute("xlink:to",toId);
			    structLink.add(smLink);
			}
		    }
		} 
	    }   
	}

	LOGGER.debug("This should be the end of it");

	/*
	 * We're done
	 */
	return structLink;
    }
  
    /**
     * Again we're just delivering data encoded before
     * @return 
     */
    public Element getFileSec() {
	return this.fileSection;
    }
  
    /**
     * Again we're just delivering data encoded before
     * @return 
     */
    public Element getStructMap() {
	return this.logicalStructure;
    }
  
    /**
     * Again we're just delivering data encoded before
     * @return 
     */
    public Element getVariantStructMap() {
	return this.variantStructure;
    }
  
    private void getData(ManusSearch search) {
	String SQL = "select * from manus.page where manusid=" +
	    this.getManuscriptId() + 
	    " order by pageseqno";

	this.data = search.executeQuery(SQL,10000);
    
	SQL = "select * from manus.pagevar where manusid=" +  this.getManuscriptId() +
	    " order by pagevarseqno";

	this.variantData = search.executeQuery(SQL,10000);
    }
 
  
}
