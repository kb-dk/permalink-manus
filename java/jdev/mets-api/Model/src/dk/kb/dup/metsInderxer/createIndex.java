package dk.kb.dup.metsInderxer;
import java.util.HashMap;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.*;

public class createIndex 
{

  String metsFile = "";
  String modsns   = "http://www.loc.gov/mods/v3";
  HashMap xmlns   = new HashMap();
 
  public createIndex(String metsFileName)
  {
    this.metsFile = metsFileName;
    xmlns.put("md",modsns);
  }
  
  
  
}