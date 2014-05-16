/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;

/**
 *
 * @author mcnabba
 */
public class Settings {
    
    private final static String PREFS_FILE = "/xml/prefs.xml";
    private static String serverAddress;
    private static String fileFolderLocation;
    
    
    public void readSettings()  {
        DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(getClass().getResourceAsStream(PREFS_FILE));
            doc.getDocumentElement().normalize();
            
            
            
        } catch (SAXException | IOException | ParserConfigurationException ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
