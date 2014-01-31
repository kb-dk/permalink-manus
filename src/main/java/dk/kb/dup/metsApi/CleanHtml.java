package dk.kb.dup.metsApi;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.XPath;
import org.dom4j.io.DOMReader;
import org.w3c.tidy.Tidy;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.ByteArrayInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A layer on top of <a href="http://jtidy.sourceforge.net/apidocs/index.html?org/w3c/tidy/package-summary.html">org.w3c.tidy.Tidy</a>
 * which is built with the aim to return either a <a href="http://www.dom4j.org/apidocs/org/dom4j/Document.html">org.dom4j.Document</a>
 * or <a href="http://www.dom4j.org/apidocs/org/dom4j/Element.html">org.dom4j.Element</a>
 * 
 * @author Sigfrid Lundberg (slu@kb.dk)
 * 
 * Last $Revision: 1.3 $ $Date: 2006/11/16 09:23:33 $ by $Author: slu $.
 * Current checkout was made as build $Name:  $
 */
public class CleanHtml 
{

    private Logger     LOGGER        = LoggerFactory.getLogger(ManusSearch.class);

    private Map xmlns = new HashMap();
  
    public CleanHtml()
    {
    }
  
    /**
     * This is the work horse of this class. Give it HTML and it will return xhtml 1.0
     * @return dom4j document, i.e., you may handle this with standard XML technologies
     * @param text - HTML text to be cleaned (could be a fragment or a whole document)
     */
    public Document parseAndTidy(String text) 
    { 
	Document doc = null;
    
	Document d = null;
	Tidy tidy = new Tidy(); 
	tidy.setXHTML(true);
	tidy.setInputEncoding("UTF-8");
	tidy.setOutputEncoding("UTF-8");
	tidy.getDropProprietaryAttributes();
    
	try 
	    { 
		tidy.setErrout(new PrintWriter(new java.io.FileWriter("errOut"), true)); 
		ByteArrayInputStream ins = new ByteArrayInputStream(text.getBytes());
		org.w3c.dom.Document tdoc = tidy.parseDOM(ins, null);
		DOMReader    dreader   = new DOMReader();
		doc  = dreader.read(tdoc);         
	    } catch ( IOException e ) 
	    { 
		//System.out.println( "error" ); 
	    } 
	return doc;
    } 
  
    /**
     * This is meant for html fragments. If you give html tidy a fragment, it will
     * return a complete document. This method takes a fragment (a piece of html
     * without body, head etc) and returns the body. Prior to making the cleaning
     * the fragment is put into &lt;div&gt; ... &lt/div&gt;, so then we "just have to look
     * that up in the document, detach it and return it.
     * 
     * @return the xhtml body embedded in a div tag, which is returned as a dom4j Element.
     * @param htmlFragment
     */
    public Element getBody (String htmlFragment) {

	Element div = null;

	String presentation = "<div>" + htmlFragment + "<div>";
	Document presDoc = this.parseAndTidy(presentation);

	if(presDoc != null) {
	    XPath path       = presDoc.createXPath("/h:html/body/div");
        
	    this.xmlns.put("h","http://www.w3.org/1999/xhtml");
	    path.setNamespaceURIs(xmlns);
	    List elelist     = path.selectNodes(presDoc);  
	    Iterator eleiter = elelist.iterator();     

	    if(eleiter.hasNext()) {
		div = (Element)eleiter.next();   
		if(div != null) {
		    div.detach();
		}
	    }
	}
	return div;
    }
  
}
