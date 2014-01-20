package dk.kb.dup.modsApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.XPath;
import org.dom4j.dom.DOMAttribute;
import org.dom4j.dom.DOMDocument;
import org.dom4j.dom.DOMElement;
import org.dom4j.dom.DOMText;
import org.dom4j.io.SAXReader;

/**
 * <p>Simple Java bindings to Metadata Object Description Schema, MODS
 * Cf., <a href="http://www.loc.gov/standards/mods/">http://www.loc.gov/standards/mods/</a>
 * 
 * <p>The class has an array, elementSet, of supported elements. While building,
 * data is stored as a HashMap of List of DOMElement. The generated MODS document 
 * will contain the elements in the order they appear in the list, regardless in
 * which order you add them. If elements are repeated, the elements of each kind
 * will appear in the ordered they were created.</p>
 * 
 * <p>We use overloading for devising alternative methods for creating the same
 * element with different level of complexity.</p>
 * 
 * @author Sigfrid Lundberg (slu@kb.dk)
 * 
 * Last $Revision: 1.5 $ $Date: 2007/01/19 13:32:46 $ by $Author: slu $.
 * Current checkout was made as build $Name:  $
 */
public class Metadata 
{

   private String modsVersion  = "3.2";
   private HashMap elements    = new HashMap();
   private Namespace modsNS    = new Namespace("md","http://www.loc.gov/mods/v3");
   
   private String[] elementSet = {"titleInfo",
                                  "name",
                                  "typeOfResource",
                                  "genre",
                                  "originInfo",
                                  "language",
                                  "physicalDescription",
                                  "abstract",
                                  "tableOfContents",
                                  "targetAudience",
                                  "note",
                                  "subject",
                                  "classification",
                                  "relatedItem",
                                  "identifier",
                                  "location",
                                  "accessCondition",
                                  "part",
                                  "extension",
                                  "recordInfo"};

  /**
   * The constructor builds the framework for the data structure we use for
   * storing our data prior to assembling the MODS document
   */
  public Metadata()
  {
    for(int i=0;i<this.elementSet.length;i++) 
    {
      ArrayList elelist = null;
      elements.put(this.elementSet[i],elelist);
    }
  }
  
  /**
   * Simple Dublin core like title
   * @param title
   */
  public void addTitle(String title, String langCode) {
    this.addTitle("","",title,"","","",langCode);
  }
  
  
  /**
   * I admit that this is ugly, but I could not find out a neater way of doing it
   * @param subTitle
   * @param title
   * @param nonSortable   a part of title which should not be used for sorting, 
   *                      such as 'the', 'a' or 'an'
   * @param type          abbreviated, translated, alternative, uniform,
   *                      empty string
   */
  public void addTitle(String type, 
                       String nonSortable,
                       String title, 
                       String subTitle,
                       String partNumber,
                       String partName,
                       String langCode) 
  {
    Element element    = new  DOMElement("titleInfo",modsNS);
    
    if(!langCode.equalsIgnoreCase("")) {
      DOMAttribute attr = new DOMAttribute(new QName("xml:lang"),langCode);
      element.add(attr);
    }
    
    if(!nonSortable.equalsIgnoreCase("")) {
      element.add(this.createSubElement("nonSortable",nonSortable));
    }
   
    element.add(this.createSubElement("title",title));
    
    if(!subTitle.equalsIgnoreCase("")) {
      element.add(this.createSubElement("subTitle",subTitle));
    }
    
    if(!partNumber.equalsIgnoreCase("")) {
      element.add(this.createSubElement("partNumber",partNumber));
    }
    
    if(!partName.equalsIgnoreCase("")) {
      element.add(this.createSubElement("partName",partName));
    }
    this.appendToTopElement(element,"titleInfo");
  }
  
  /**
   * The simplest case of name, i.e. an author. It is just a call to the method
   * this.addName("author",author,langCode) (see below)
   * @param author
   */
  public void addAuthor(String author,String langCode) {
    this.addName("author",author,langCode);
  }
  
  /**
   * Dublin Core like author/creator/contributor and the like
   * @param role  e.g., creator
   * @param name  Just a name in general
   */
  public void addName(String role,String name,String langCode) { 
    Element element    = new  DOMElement("name",modsNS);
    Element rolement   = new  DOMElement("role",modsNS); 
    rolement.add(this.createSubElement("roleTerm",role));
       
    if(!langCode.equalsIgnoreCase("")) {
      element.addAttribute("xml:lang",langCode);
    }
    element.add(this.createSubElement("partName",name));
    element.add(rolement);
    this.appendToTopElement(element,"name");   
  }
  
  /**
   * The most general addName. The author of the this class didn't feel any urgent
   * need to implement it. If you want it, feel free to add it.
   * @param langCode
   * @param displayform
   * @param date
   * @param family
   * @param given
   * @param role
   */
  public void addName(String role,
                      String given,
                      String family,
                      String date,
                      String displayform,
                      String langCode) { 
  // Not implemented
  }
  
  /**
   * Not implemented
   * @param typeOfResource
   */
  public void addTypeOfResource(String typeOfResource) {
  // Not implemented
  }
  
  /**
   * A fairly general implementation of mods originalInfo. There are problems with
   * using this for manuscripts, where dateIssued is best implemented as a time interval
   * (we need notBefore and notAfter dates).
   * 
   * @param dateIssued
   * @param langCode
   * @param place
   * @param publisher
   */
  public void addOriginInfo(String publisher, 
                            String place,
                            String langCode,
                            String dateIssued) {
    HashMap date = new HashMap();
    date.put("type","dateIssued");
    date.put("value",dateIssued);
    List list = new ArrayList();
    list.add(date);
    this.addOriginInfo(publisher,place,langCode,list);                       
  }
  
  /**
   * A completely general implementation of originInfo, which includes at least
   * all possible types of dates. Since the date sub-element is repeatable, you have 
   * to provide it as a list of HashMap, where the HashMap has two keys, type and value.
   * Refer to 
   * <a href="http://www.loc.gov/standards/mods/v3/mods-userguide-elements.html#origininfo">originInfo</a>
   * in MODS user guide.
   * @param dates
   * @param langCode
   * @param place
   * @param publisher
   */
  public void addOriginInfo(String publisher, 
                            String place,
                            String langCode,
                            List dates) {
                            
    Element element    = new  DOMElement("originInfo",modsNS);
    
    if(!langCode.equalsIgnoreCase("")) {
      element.addAttribute("xml:lang",langCode);
    }
    if(!publisher.equalsIgnoreCase("")) {
      element.add(this.createSubElement("publisher",publisher));
    }
    if(!place.equalsIgnoreCase("")) {
      element.add(this.createSubElement("place",place));
    }
    
/* Potentially we should be able to support: dateIssued, dateCreated, dateCaptured,
 * dateValid, dateModified, copyrightDate, dateOther -- so please understand */
 
    Iterator diterator = dates.iterator();
    while(diterator.hasNext()) {
      HashMap date = (HashMap)diterator.next();
      element.add(this.createSubElement(""+date.get("type"),
                                        (String)date.get("value")));
    }
    this.appendToTopElement(element,"originInfo");
  }
  
  /**
   * MODS may include metadata for related objects. This is the way to describe an
   * article in a book using MODS. 
   * @param item - a mods record for the related item encoded as a Metadata object
   * @param relation - on of the relations: preceding, succeeding, original, host, constituent, series, otherVersion, otherFormat, isReferencedBy
   */
  public void addRelatedItem(String relation, Metadata item) {
    Element element = new  DOMElement("relatedItem",modsNS);
    element.addAttribute("type",relation);
    Document doc = item.getModsDocument();
    Element root = doc.getRootElement();
    List kids = root.elements();
    Iterator kiderator = kids.iterator();
    while(kiderator.hasNext()) {
      Element kid = (Element)kiderator.next();
      kid.detach();
      element.add(kid);
    }
    this.appendToTopElement(element,"relatedItem");
  }
  
  /**
   * This adds a signature (call number) as a MODS identifier.
   * @param signature
   */
  public void addSignature(String signature) {
    this.addIdentifier("signature",signature);
  }
  
  /**
   * Adds a MODS identifier. 
   * @param identifier - The identifier as a String
   * @param type - the type of identifier. See <a href="http://www.loc.gov/standards/mods/mods-outline.html#identifier">types</a> provided by Library of Congress
   */
  public void addIdentifier(String type,String identifier) {
    Element id = this.createSubElement("identifier",identifier);
    id.addAttribute("type",type);
    this.appendToTopElement(id,"identifier");
  }
  
  /**
   * Adds a MODS identifier. 
   * @param identifier - The identifier as a String
   * @param type - the type of identifier. See <a href="http://www.loc.gov/standards/mods/mods-outline.html#identifier">types</a> provided by Library of Congress
   * @param langCode - To be able to identifier and link to instances of a resource in different languages
   */
  public void addIdentifier(String type,String identifier,String langCode) {
    Element id = this.createSubElement("identifier",identifier);
    id.addAttribute("type",type);    
    if(!langCode.equalsIgnoreCase("")) {
      DOMAttribute attr = new DOMAttribute(new QName("xml:lang"),langCode);
      id.add(attr);
    }    
    this.appendToTopElement(id,"identifier");    
  }
  
  /**
   * Adds a language tag. <b>NB:</b> We take if for granted that it is a three letter 
   * (iso639-2b) language code.
   * 
   * @param langCode
   */
  public void addLanguage(String langCode) {
    Element language = new DOMElement("language",modsNS);
    Element term = this.createSubElement("languageTerm",langCode);
    term.addAttribute("type","code");
    term.addAttribute("authority","iso639-2b");
    language.add(term);
    this.appendToTopElement(language,"language");
  }
  
  /**
   * Here we add a note.
   * @param langCode
   * @param noteBody
   * @param type -- MODS supports all the complexity of MARC 21 notes. You won't like the have the gory details. In case you would <a href="http://www.loc.gov/standards/mods/mods-notes.html">click here</a>
   */
  public void addNote( String type, String noteBody, String langCode) {
    Element note = this.createSubElement("note",noteBody);
    note.addAttribute("type",type);
    note.addAttribute("xml:lang",langCode);
    this.appendToTopElement(note,"note");   
  }
  
  /**
   * Here we add a note.
   * @param langCode
   * @param noteBody
   * @param type -- MODS supports all the complexity of MARC 21 notes. You won't like the have the gory details. In case you would <a href="http://www.loc.gov/standards/mods/mods-notes.html">click here</a>
   */
  public void addNote( String type, Element noteBody, String langCode) {
    Element element = new  DOMElement("note",modsNS);
    element.addAttribute("type",type);
    element.addAttribute("xml:lang",langCode);
    element.add(noteBody);
    this.appendToTopElement(element,"note");   
  }
  
  /**
   * When you call this, then you're done. You then obtain a nice org.dom4j.Document
   * @return 
   */
  public Document getModsDocument () {
    Element root    = new  DOMElement("mods",modsNS);
    root.addNamespace("","http://www.w3.org/1999/xhtml");
    DOMDocument doc = new DOMDocument();
    doc.add(root);
    for(int i=0;i<this.elementSet.length;i++) {
      if(this.elements.get(this.elementSet[i]) != null) {
        ArrayList elelist = (ArrayList)this.elements.get(this.elementSet[i]);
        Iterator elerator = elelist.iterator();
        while(elerator.hasNext()) {
         root.add((Element)elerator.next());
        }
      }
    }
    return doc;
  }
  
  /**
   * This one calls the getModsDocument() method, and then returns a serialization 
   * of the document as a String
   * @return 
   */
  public String toString() {
    Document doc = this.getModsDocument();
    return doc.asXML();
  }
  
  private Element createSubElement(String qName,String value) {
    Element subelement = new  DOMElement(qName,modsNS);
    subelement.add(new DOMText(value));
    return subelement;
  }
  
  private void appendToTopElement(Element element, String modsElement) {  
    ArrayList elelist = null;
    if(this.elements.get(modsElement) == null) {
      elelist = new ArrayList();
    } else {
      elelist = (ArrayList)elements.get(modsElement);
    }
    elelist.add(element);
    this.elements.put(modsElement,elelist);  
  }
  
}

