package dk.kb.mets;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: jac
 * Date: 20-11-13
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */

@RunWith(JUnit4.class)
public class TermTest {
    Term t;

    @Test
    public void testGetLantTerm(){
        t = new Term("dan");
        assertEquals(5,t.getLangTerm().size());
    }

    @Test
    public void testGetLang(){
        t = new Term("dan");
        assertEquals("Dansk", t.getLang("dan"));
    }

    @Test
    public void testViewMode(){
        t = new Term("dan");
        assertEquals("1", t.getMusViewMode("REVY"));
    }


}
