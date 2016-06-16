package dk.kb.mets;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertNotEquals;

/**
 * Created with IntelliJ IDEA.
 * User: jac
 * Date: 28-11-13
 * Time: 09:30
 * To change this template use File | Settings | File Templates.
 */

@RunWith(JUnit4.class)
public class MetsElementsTest {

    private static final String METS_PATH ="dk.kb.mets/src/test/data/mets/";
    private org.dom4j.Document doc, doc2 = null;
    private org.dom4j.io.SAXReader reader = new org.dom4j.io.SAXReader();
    private MetsElements mElem, mElem2 = null;

    private void initDMB() throws DocumentException {
        java.io.File metsFile = new java.io.File(METS_PATH + "mus/DMB/metsfile.xml");
        java.io.File metsFile2 = new java.io.File(METS_PATH + "lum/alle/metsfile.xml");

        doc = reader.read(metsFile);
        doc2 = reader.read(metsFile2);
        mElem = new dk.kb.mets.MetsElements(doc, "", false);
        mElem2 = new dk.kb.mets.MetsElements(doc2, "", false);
    }

    @Test
    public void testDMBgetList () throws DocumentException {
        initDMB();
        String list = mElem.getList("mus");
        String list2 = mElem2.getList("lum");


        System.out.println(list);
        System.out.println(list2);

        assertNotEquals("<p><ul>", list2);
    }




}
