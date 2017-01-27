package dk.kb.mets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.Node;

/**
 * Operations for retrieving information from METS files, to the GUI in 
 * OpenCms module dk.kb.mets
 *
 * @author Jacob Larsen (jac@kb.dk)
 */
public class MetsElements
{
    private Document metsDoc;
    private String lang;
    private String div = "m:div";


    /**
     * Public constructor
     * @param doc mets-xml document parsed into org.dom4j.Document
     * @param lang the GUI language
     * @param var false: the main imgage of a page is retrieved, true: a variant of the page is retrieved
     */
    public MetsElements(Document doc, String lang, boolean var)
    {
        this.metsDoc = doc;
        if(!lang.equals(""))
        {
            this.lang = "[@xml:lang='"+lang+"']";
        }
        this.lang = lang;

        if(var)
        {
            this.div="";
        }
    }

    /**
     *Sets the var  parameter
     * @param v false: the main imgage of a page is retrieved, true: a variant of the page is retrieved
     */
    public void setVar(boolean v)
    {
        if(v)
        {
            this.div="";
        }else
        {
            this.div = "m:div";
        }
    }

    /**
     * Resolves page name to a unique ID, for a given ressource.
     * @param page page label, eg. '1 verso', '2',  'binding'
     * @return the globally unique id of a page. 
     */
    public String getDivFromPage(String page)
    {
        return metsDoc.valueOf("/m:mets/m:structMap[@type='physical']/m:div/m:div[@ORDERLABEL='"+page+"']"+lang+"/@ID");
    }

    /**
     * Retrieves information of a given variants href
     * @param page id of the current page
     * @param var the string representation of a variant (e.g. rotation, forstørrelse)
     * @return the href of the img.
     */
    public String getVarFromDivid(String page, String var){
        return metsDoc.valueOf("/m:mets/m:structLink/m:smLink[@xlink:from='"+page+"']["+var+"]/@xlink:to");
    }

    /**
     * @deprecated replaced by {@link #getSubNavigation()}
     */

    @Deprecated public List getTopNavigation()
    {
        List divs = metsDoc.selectNodes("/m:mets/m:structMap[@type='physical']/m:div/m:div[@LABEL]"+lang);
        ArrayList arr = new ArrayList();
        for(Iterator iter = divs.iterator(); iter.hasNext();)
        {
            Node n = (Node)iter.next();
            String id = n.valueOf("@ID");
            String label=n.valueOf("@LABEL");
            String orderLabel=n.valueOf("@ORDERLABEL");
            String[] tmp = {id, orderLabel, label};

            arr.add(tmp);
        }
        return arr;
    }

    /**
     * Get a list, of elements used to build a navigation menu
     * @return List of {divid, orderlabel, label}
     */
    public List getSubNavigation()
    {
        List divs = metsDoc.selectNodes("/m:mets/m:structMap[@type='physical']/m:div/m:div[@ORDERLABEL]"+lang);
        ArrayList arr = new ArrayList();
        for(Iterator iter = divs.iterator(); iter.hasNext();)
        {
            Node n = (Node)iter.next();
            String id = n.valueOf("@ID");
            String label=n.valueOf("@LABEL");
            String orderLabel=n.valueOf("@ORDERLABEL");
            String[] tmp = {id, orderLabel, label};

            arr.add(tmp);
        }
        return arr;
    }

    /**
     * Retrives the link to a .pdf file
     * @return the URL to the pdf
     */
    public String getPdf()
    {
        return metsDoc.valueOf("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:identifier[@type='pdf']");
    }


    /**
     * Retrives the thumbnail to display on the frontpage
     * @return The URL to an img
     */
    public String getThumb()
    {
        return metsDoc.valueOf("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:identifier[@type='thumb']");
    }

    /**
     * Retrives the img from a divid. (Not the variant)
     * @return The URL to the imgage file
     */
    public String getCurrImg(String id)
    {
        String fptr = metsDoc.valueOf("/m:mets/m:structMap/"+div+"/m:div[@ID='"+id+"']/m:fptr/@FILEID");
        return metsDoc.valueOf("/m:mets/m:fileSec/m:fileGrp/m:file[@ID='"+fptr+"']/m:FLocat/@xlink:href");
    }

    /**
     * Get the name of the last page, used for the 'last' button, in the navigation bar
     * @return the orderlabel of the last page
     */
    public String getLast()
    {
        return metsDoc.valueOf("/m:mets/m:structMap/"+div+"/m:div[@ORDERLABEL]"+lang+"[last()]/@ORDERLABEL");
    }

    /**
     * Get the name of the first page, used for the 'first' button, in the navigation bar
     * @return the orderlabel of the first page
     */
    public String getFirst()
    {
        return metsDoc.valueOf("/m:mets/m:structMap/"+div+"/m:div[@ORDERLABEL]"+lang+"[1]/@ORDERLABEL");
    }

    /**
     * Get the name of the next page
     * @param id current page
     * @return the orderlabel of the next page
     */
    public String getNext(String id)
    {
        return metsDoc.valueOf("/m:mets/m:structMap/"+div+"/m:div[@ID='"+id+"']"+lang+"/following-sibling::node()"+lang+"[position()=1]/@ORDERLABEL");
    }

    /**
     * Get the name of the previous page
     * @param id current page
     * @return the orderlabel of the previous page
     */
    public String getPrev(String id)
    {
        return metsDoc.valueOf("/m:mets/m:structMap/"+div+"/m:div[@ID='"+id+"']"+lang+"/preceding-sibling::node()"+lang+"[position()=1]/@ORDERLABEL");
    }

    /**
     * Gets the annotation of a single item (page)
     * @param id current page
     * @return Annotation of the current page
     */
    public String getDescription(String id)
    {

        String dmd = metsDoc.valueOf("/m:mets/m:structMap/m:div/m:div[@ID='"+id+"']/@DMDID");
        return metsDoc.valueOf("/m:mets/m:dmdSec[@ID='"+dmd+"']/m:mdWrap/m:xmlData/md:mods/md:note[@type='annotation']"+lang);
    }

    /**
     * Resolves the orderlabel from an id
     * @param id current page
     * @return orderlabel of the current page
     */
    public String getOrderLabel(String id)
    {
        return metsDoc.valueOf("/m:mets/m:structMap/"+div+"/m:div[@ID='"+id+"']"+lang+"/@ORDERLABEL");
    }

    /**
     * Gets the textual description of a manuscript. May contain HTML fragments
     * @return A piece of unstructured XML as a string
     */
    public String getManusDescription()
    {
        Node node =  metsDoc.selectSingleNode("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:note[@type='presentation']"+lang);
        return node.asXML();
    }

    /**
     * Get the title of a manusscript. A concat between two bibliographic fields.
     * @return the title to display
     */
    public String getManusTitle()
    {
        String part = metsDoc.valueOf("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:name"+lang+"/md:partName");
        if(!part.equalsIgnoreCase(""))
        {
            part = part + ": ";
        }
        String sig = "";
        return part + sig + metsDoc.valueOf("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:titleInfo"+lang+"/md:title");
    }

    /**
     * Get the 'partname' from a mets object
     * @return the part name
     */
    public String getPartName()
    {
        return metsDoc.valueOf("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:name"+lang+"/md:partName");
    }

    /**
     * Get a list, containing elements used to create var links
     * @param id current page
     * @return List containing {href, link title, links number}
     */
    public List getVarLink(String id)
    {
        List nodes = metsDoc.selectNodes("/m:mets/m:structLink/m:smLink[@xlink:from='"+id+"']"+lang);
        List arr = new ArrayList();
        int i = 1;
        for(Iterator iter = nodes.iterator(); iter.hasNext();)
        {
            Node tmp = (Node)iter.next();
            String[] arrTmp = {tmp.valueOf("@xlink:to"),tmp.valueOf("@xlink:title"), i+""};
            arr.add(arrTmp);
            i++;
        }
        try{
            return arr;
        }catch(NullPointerException e)
        {
            return null;
        }
    }

    /**
     * Get the link to default img, of a page
     * @param id current page
     * @return href to default image file.
     */
    public List getDefaultLink(String id)
    {
        List nodes = metsDoc.selectNodes("/m:mets/m:structLink/m:smLink[@xlink:to='"+id+"']"+lang);
        List arr = new ArrayList();
        for(Iterator iter = nodes.iterator(); iter.hasNext();)
        {
            Node tmp = (Node)iter.next();
            String[] arrTmp = {tmp.valueOf("@xlink:from"),"Almindelig"};
            arr.add(arrTmp);
        }
        try{
            return arr;
        }catch(NullPointerException e)
        {
            return null;
        }
    }

    /**
     * Set the GUI language
     * @param l new language (3 letter abbrewation)
     */
    public void setLang(String l)
    {
        if(!l.equals("")){
            this.lang = "[@xml:lang='"+l+"']";
        }else
        {
            this.lang = l;
        }
    }

    /**
     * Get a list of all available GUI languages
     * @return list of String representations of the languages available
     */
    public List getLang()
    {
        List n = metsDoc.selectNodes("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:titleInfo/@xml:lang");
        ArrayList arr = new ArrayList();
        for(Iterator iter = n.iterator(); iter.hasNext();)
        {
            Node node = (Node)iter.next();
            arr.add(node.getText());
        }
        return arr;
    }

    /**
     * Get link information about the owner / creator of the mets record (Håndskriftsafdelingen etc)
     * @return {link title, link href}
     */
    public String[] getHost()
    {
        String[] ret = {metsDoc.valueOf("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:relatedItem[@type='host']/md:titleInfo"+lang+"/md:title"),metsDoc.valueOf("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:relatedItem[@type='host']/md:identifier[@type='uri']"+lang)};
        return ret;
    }

    /**
     * Get link information of link to external ressourcer, typically on www2.
     * @return list of {link title, link href}
     *
     */
    public List getMenuItem()
    {
        List n = metsDoc.selectNodes("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:relatedItem[@type='menuitem']/md:titleInfo"+lang+"/md:title");
        List links = metsDoc.selectNodes("/m:mets/m:dmdSec[@ID='md-root']/m:mdWrap/m:xmlData/md:mods/md:relatedItem[@type='menuitem']/md:identifier[@type='uri']"+lang);
        ArrayList arr = new ArrayList();
        Iterator linkIter = links.iterator();
        for(Iterator iter = n.iterator(); iter.hasNext();)
        {
            Node link = (Node)linkIter.next();
            Node node = (Node)iter.next();
            String[] a = {node.getText(),link.getText()};
            arr.add(a);
        }
        return arr;
    }

    /**
     * List all og Musikafdelingens digitalizations
     * Method is custom made for ome sepcific purpose. 
     * @param app applikation name (One of 'MUS', 'MUSMAN', 'LUM')
     * @return A String of HTML, to write directly to the GUI
     */
    public String getList(String app)
    {
        if(app.equals("musik"))
        {
            return "" +
                    " <p><ul>" +
                    "  <li><a href=\"/permalink/2006/mus/DMB/\">Danmarks Melodibog</a></li>" +
                    "  <li><a href=\"/permalink/2006/mus/DANKAM/\">Dansk Kammermusik før 1900 - tryk og manuskripter</a></li>" +
                    "  <li><a href=\"/permalink/2006/mus/DANKLAV/\">Dansk klavermusik før 1900</a></li>" +
                    "  <li><a href=\"/permalink/2006/mus/SANGMUSIK/\">Dansk Sangmusik</a></li>" +
                    "  <li><a href=\"/permalink/2006/lum/alle/ \">H.C. Lumbyes Danse i trykte klaverudgaver</a></li>"+
                    "  <li><a href=\"/permalink/2006/musman/alle/ \">J.P.E. Hartmann - Udvalgte Værker i autograf</a></li>"+
                    "  <li><a href=\"/permalink/2006/mus/REVY/\">Københavnske 'Sommerrevyer' 1876-1900</a></li>" +
                    "  <li><a href=\"/permalink/2006/mus/BOURNON2/\">Musik til Aug. Bournonvilles balletter m.v.</a></li>" +
                    "  <li><a href=\"/permalink/2006/mus/MUSMUS/\">Musikalsk Museum for Pianoforte</a></li>" +
                    "  <li><a href=\"/permalink/2006/mus/UKAM/\">Udenlandsk kammer- og orkestermusik   før 1900</a></li>" +
                    "  <li><a href=\"/permalink/2006/mus/TEA1800/\">1700- og 1800-tallets danske teatermusik</a></li>" +
                    "</ul></p> ";
        }

        List div = metsDoc.selectNodes("/m:mets/m:structMap[@type='logical']/m:div");

        Iterator<Node> sIter = div.iterator(); // <m:div xml:lang="dan" ID="mus:DMB:div:logical:900" DMDID="mus:DMB:dmd:900"/>


        /**
         *  Since some of outr Metsfiles has problems in their ID's we start by checking whether the current metsfile
         *  has theese errors or not. If TRUE, we set a flag to implement a HACK (Described futher down)
         */
        boolean applyHack = false;



        String ret = "<p><ul>";
        int titleIndex = 0;
        while(sIter.hasNext())
        {
            Node metsPtr = sIter.next();
            if(titleIndex == 0){
                if(  metsDoc.selectSingleNode("/m:mets/m:dmdSec[@ID='"+metsPtr.valueOf("@DMDID")+"']/m:mdWrap/m:xmlData") == null ){
                    applyHack = true;
                }
            }

            String id = metsPtr.valueOf("@DMDID");
            if(applyHack) {
                id = id.replaceAll("dmd","dmdl");
            }

            Node dmdNode = metsDoc.selectSingleNode("/m:mets/m:dmdSec[@ID='"+id+"']/m:mdWrap/m:xmlData"); // Get the MODS Element
            String title = dmdNode.valueOf("md:mods/md:titleInfo/md:title"); // Get the Title
            String pdf = dmdNode.valueOf("md:mods/md:note"); // Get the PFD link
            String name = dmdNode.valueOf("md:mods/md:name/md:partName"); // Get the composer
            if(!name.equals("")){
                name = "- " + name;
            }
            if(!pdf.equals(""))
            {
                ret = ret + "<li><a href='"+pdf+"'>"+ title+ " " + name + " [pdf]</a></li>\n";
            }else{
                String href = metsPtr.valueOf("m:metsPtr/@xlink:href");
                ret = ret + "<li><a href='/permalink/2006/"+app+"/"+href+"/'>"+ title+"</a></li>\n";
            }
            titleIndex++;
        }
        ret = ret + "</ul></p>";

        return ret;

    }



    /**
     * @deprecated Use {@link #getList(String app)}
     */
    @Deprecated public String getListTitle(String id)
    {
        String dmd = metsDoc.valueOf("/m:mets/m:structMap/m:div[@ID='"+id+"']/@DMDID");
        return metsDoc.valueOf("/m:mets/m:dmdSec[@ID='"+dmd+"']/m:mdWrap/m:xmlData/md:mods/md:titleInfo"+lang+"/md:title");
    }

    /**
     * @deprecated Use {@link #getList(String app)}
     */
    @Deprecated public List getStructMap()
    {
        return metsDoc.selectNodes("/m:mets/m:structMap/m:div");
    }

    /**
     * @deprecated Use {@link #getList(String app)}
     */
    @Deprecated public String getMetsPtrId(String id)
    {
        return metsDoc.valueOf("/m:mets/m:structMap/m:div[@ID='"+id+"']/m:metsPtr/@xlink:href");
    }

    /**
     * @deprecated Use {@link #getList(String app)}
     */
    @Deprecated public String getPdf(String id)
    {
        String dmd = metsDoc.valueOf("/m:mets/m:structMap/m:div[@ID='"+id+"']/@DMDID");
        return metsDoc.valueOf("/m:mets/m:dmdSec[@ID='"+dmd+"']/m:mdWrap/m:xmlData/md:mods/md:note[@type='pdf']/@xlink:href");
    }

}
