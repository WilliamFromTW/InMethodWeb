package inmethod;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public class parseWebXml extends org.xml.sax.helpers.DefaultHandler {
	
   static String sKey = null;
   static String sValue = null;
   String sSearch = "";
   public parseWebXml(){}
  public parseWebXml(String sKey){
	sSearch = sKey;
  }
  
 
  


  public String lookup(String sFile,String sKey){
	    SAXParserFactory factory = SAXParserFactory.newInstance();
	    factory.setValidating(false); // We don't want validation
	    factory.setNamespaceAware(false); // No namespaces please
	    SAXParser parser;
	    sSearch = sKey;
	    this.sValue = null;
	    //System.out.println("key="+sSearch);
		try {
			parser = factory.newSAXParser();
		    parser.parse(new File(sFile), new parseWebXml(sKey));
			String sReturn = sValue;
			this.sKey = null;
			this.sValue = null;
		    return sReturn;
		} catch (Exception e) {
			e.printStackTrace();
		}
	  
	  return null;
  }

  StringBuffer accumulator; // Accumulate text


  // Called at the beginning of parsing. We use it as an init() method
  public void startDocument() {
    accumulator = new StringBuffer();
  }

  public void characters(char[] buffer, int start, int length) {
	  accumulator  = new StringBuffer();
    accumulator.append(buffer, start, length);
  }

  public void startElement(String namespaceURL, String localName, String qname,
      Attributes attributes) {

  }

  public void endElement(String namespaceURL, String localName, String qname) {
	//  System.out.println(sValue);
	if( sValue!=null) return;
    if (qname.equals("env-entry-name")) { // Store servlet name
    //	System.out.println(accumulator.toString().trim());
    //	System.out.println("rrrrr="+sSearch);
      if( sSearch.equalsIgnoreCase(accumulator.toString().trim()) ){
    //	  System.out.println("dddd");
        sKey = accumulator.toString().trim();
      }  
    } else if (qname.equals("env-entry-value")) {
      if( sKey!=null )
    	  sValue = accumulator.toString().trim();
    }  
  }

  public void endDocument() {
  }

  // Issue a warning
  public void warning(SAXParseException exception) {
    System.err
        .println("WARNING: line " + exception.getLineNumber() + ": " + exception.getMessage());
  }

  // Report a parsing error
  public void error(SAXParseException exception) {
    System.err.println("ERROR: line " + exception.getLineNumber() + ": " + exception.getMessage());
  }

  // Report a non-recoverable error and exit
  public void fatalError(SAXParseException exception) throws SAXException {
    System.err.println("FATAL: line " + exception.getLineNumber() + ": " + exception.getMessage());
    throw (exception);
  }
}

   