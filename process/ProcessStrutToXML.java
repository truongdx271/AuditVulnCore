/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author longnt39
 */
public class ProcessStrutToXML {

    List<String> listXMLNeedCheck = new ArrayList<>();

    public ProcessStrutToXML(String foFullPath){
        //add file struts.xml to List
        listXMLNeedCheck.add(foFullPath);
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try{
            //disable dtd check
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            File file = new File(foFullPath);
            int endIndex = foFullPath.indexOf("struts.xml");
            String parrentPath = foFullPath.substring(0, endIndex);

            DocumentBuilder dBuilder = dbf.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            //Get include tag
            NodeList nList = doc.getElementsByTagName("include");
            
            //if struts file does not have include tag, exit
            if (nList.getLength()<1){
                return;
            }
            
            for (int count = 0; count < nList.getLength(); count++) {
                Node tempNode = nList.item(count);
                String value = tempNode.getAttributes().getNamedItem("file").getNodeValue();
                if(!value.isEmpty()&&value!=null){
                    String pathItem = parrentPath + value.replaceAll("/", File.separator + File.separator);
                    listXMLNeedCheck.add(pathItem);  
                }
            }
            
        }catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
    public List<String> getListXMLNeedCheck(){
        return listXMLNeedCheck;
    }    
}
