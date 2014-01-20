package dk.kb.dup.metsApi;

import gnu.getopt.*;

public class Main 
{
  public Main()
  {
  }


  /**
   * All the processing takes place in the main method of the class. The parsing
   * takes place using regular expression.
   * 
   * @throws java.io.IOException
   * @param args
   */
   public static void main(String[] args)
   {
      
     LongOpt[] options = new LongOpt[4];
     
     StringBuffer sb = new StringBuffer();
     options[0] = new LongOpt("help",       LongOpt.NO_ARGUMENT,        null, 'h');
     options[1] = new LongOpt("mods",       LongOpt.NO_ARGUMENT,        null, 'r');
     options[2] = new LongOpt("manus-id",   LongOpt.REQUIRED_ARGUMENT,  sb,   'm'); 
     options[3] = new LongOpt("out",        LongOpt.OPTIONAL_ARGUMENT,  sb,   'o');
     
     String msID             = "";
     String outputFile       = "";
     boolean modsOnly        = false;
    
     String sorry = "Sorry, but something is wrong with your command line\n";
     char   opt; 
     int c;
     Getopt g   = new Getopt("METS tool",args,"hrmo",options);
  
     while( (c = g.getopt()) != -1) {
        
        switch(c) 
        {
          case 0:   opt = (char)(new Integer(sb.toString())).intValue();
                    String val = g.getOptarg();
                    System.out.println(opt + " " + val);
                    if(opt == 'm') {
                      msID = val;
                    } else if(opt == 'r') {
                      modsOnly = true;
                    } else if (opt == 'o') {
                      outputFile = val;
                    } else if (opt == 'h') {
                      System.out.println(message());
                      System.exit(0);
                    } else {
                      System.out.println(sorry + message());
                      System.exit(0);
                    }                   
                    break;
          case 'h': System.out.println(message());
                    System.exit(0);
          case 'r': modsOnly   = true;
                    break;
          case 'm': msID       = g.getOptarg();
                    break;
          case 'o': outputFile = g.getOptarg();
                    break;  
          case '?': System.out.println(sorry + message());
                    System.exit(0);
        }
     }
        System.out.println("natti natti.." + msID);      
     if(msID.equalsIgnoreCase(""))
     {
       System.err.println(message());
       System.exit(1);
     }
     
     System.out.println(msID);
     Manus manuscript = new Manus(msID);
     if(modsOnly) {
        System.out.println(manuscript.mods());
     } else {
        System.out.println(manuscript.getMets());
     }
     
     
     
  }
  
  static String message() {
    return "\nUse which the following options\n" +
           "\t--manus-id\t<manuscriptId>\n" +
           "\t--mods\t<filename>\n" +
           "\t--out\t<filename>\n" +
           "where\n" +
           "\tmanuscriptId\tis a valid ID for an object in the manus application\n" +
           "\tfilename\t\tis the name of the file where the record should be written\n";
  }
  
}