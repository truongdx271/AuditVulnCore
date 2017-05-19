/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.process;

import java.io.File;
import java.io.IOException;

import com.viettel.audit.def.*;
import com.viettel.audit.util.SearchUtil;
import java.io.FileInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import org.openide.util.Exceptions;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author longnt39
 */
public class SearchPageInXMLFile {

    List<String> listPageName = new ArrayList<>();
    List<ItemObject> listPageNameItem = new ArrayList<>();
    String fileXMLPath;
    //Map<String,String[]> mapJavaPageActionResponse = new HashMap<>();
    
    Map<ItemObject, String> mapActionItemJavaPath = new HashMap<>();
    
    String javaFolder;

    public SearchPageInXMLFile(String foundJspPath, List<String> listXMLPath, String javaFolder) {
        this.javaFolder = javaFolder;
        String pathJspToFind = "";
        //Get String JSP path, used to appear in XML mapping
        int cutIndexJsp = foundJspPath.indexOf("WEB-INF");
        
        //if file jsp is not is folder WEB-INF, return
        if (cutIndexJsp<0 && foundJspPath.contains("::")){
            String projectPath = foundJspPath.split("::")[0];
            String pathJspInXML = foundJspPath.split("::")[1];
            foundJspPath = projectPath + pathJspInXML;
            pathJspToFind = pathJspInXML.replace(File.separator, "/");
            String[] pathJsp= pathJspToFind.split("/");
            pathJspToFind = pathJsp[pathJsp.length-1];
        } else {
            pathJspToFind = foundJspPath.substring(cutIndexJsp).replace(File.separator, "/");
        }
                       
        for (String XMLPath : listXMLPath) {
            if(XMLPath.contains("build"))
                continue;
            final Scanner scanner;
            try {
                File fileXML = new File(XMLPath);
                FileInputStream inputFileXml = new FileInputStream(fileXML);
                scanner = new Scanner(inputFileXml);
                
                //FileObject foXML = FileUtil.toFileObject(fileXML);
                //scanner = new Scanner(foXML.asText());
                
                while (scanner.hasNextLine()) {
                    final String lineFromFile = scanner.nextLine();
                    if (lineFromFile.contains(pathJspToFind)) {
                        listPageNameItem = processXMLtoListPageName(foundJspPath, pathJspToFind, XMLPath);
                        break;
                    }
                }
            } catch (IOException ex) {
               // Exceptions.printStackTrace(ex);
            }
            if (!listPageNameItem.isEmpty()) {
                break;
            }
        }

    }

    private List<ItemObject> processXMLtoListPageName(String originalJspPath, String tempPathJsp, String tempFileXMLPath) {
        
        String tempPageName = null;
        
        List<ItemObject> tempListPageNameItem = new ArrayList<>();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //set for XML disable check dtd
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

            File file = new File(tempFileXMLPath);
            DocumentBuilder dBuilder = dbf.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            //Search in file XML
            if (doc.hasChildNodes()) {
                NodeList nListAtt = doc.getElementsByTagName("put-attribute");
                NodeList nListDef = doc.getElementsByTagName("definition");
                NodeList nListRes = doc.getElementsByTagName("result");
                //find Jsp path file in put-attribute tag 
                if (nListAtt.getLength() > 0) {
                    for (int count = 0; count < nListAtt.getLength(); count++) {
                        Node tempNode = nListAtt.item(count);
                        Node tempValueNode = tempNode.getAttributes().getNamedItem("value");
                        if (tempValueNode != null) {
                            String value = tempValueNode.getNodeValue();
                            if (value != null) {
                                if (value.contains(tempPathJsp)) {
                                    Node parrentNode = tempNode.getParentNode();
                                    tempPageName = parrentNode.getAttributes().getNamedItem("name").getNodeValue();
                                    break;
                                }
                            }

                        }
                    }
                }
                //if can't path file JSP in put-attribute Tag, find again in definition Tag
                if (tempPageName == null && (nListDef.getLength() > 0)) {
                    for (int count = 0; count < nListDef.getLength(); count++) {
                        Node tempNode = nListDef.item(count);
                        Node tempTemplateNode = tempNode.getAttributes().getNamedItem("template");
                        if (tempTemplateNode != null) {
                            String templateValue = tempTemplateNode.getNodeValue();
                            if (templateValue != null) {
                                if (templateValue.contains(tempPathJsp)) {
                                    tempPageName = tempNode.getAttributes().getNamedItem("name").getNodeValue();
                                    break;
                                }
                            }
                        }
                    }
                }
                //if can't path file JSP in put-attribute Tag and Definition Tag, find again in result Tag 
                if (tempPageName == null && (nListRes.getLength() > 0)) {
                    for (int count = 0; count < nListRes.getLength(); count++) {
                        Node tempNode = nListRes.item(count);
                        String result = tempNode.getTextContent();
                        String valueResponse = null;
                        if (tempNode.getAttributes().getNamedItem("name") != null) {
                            valueResponse = tempNode.getAttributes().getNamedItem("name").getNodeValue();
                        }
                        if (result.contains(tempPathJsp)) {
                            Node parrentNode = tempNode.getParentNode();
                            //if path file in Action tag
                            if (parrentNode.getAttributes().getNamedItem("class") != null) {
                                tempPageName = parrentNode.getAttributes().getNamedItem("class").getNodeValue();
                                
                                //process to get path
                                //************************************
                                //process if page name in Action tag in normal form
//                                if (!tempPageName.toLowerCase().contains("com.viettel")){
//                                    continue;
//                                }
                                //jsp path file in action Tag
                                
                                //get path to Java file
                                String[] tempPath = tempPageName.split("\\.");
                                String shortPath = "";
                                for (String tempPath1 : tempPath) {
                                    shortPath += File.separator + tempPath1;
                                }
                                String tempJavaPath = javaFolder + shortPath + ".java";
                                
                                String valueAction = parrentNode.getAttributes().getNamedItem("name").getNodeValue();
                                
                                String valueMethod = "execute";
                                if(parrentNode.getAttributes().getNamedItem("method").getNodeValue()!=null)
                                {
                                    valueMethod= parrentNode.getAttributes().getNamedItem("method").getNodeValue();
                                }
                                String keyword = "\"" + valueAction + "\"";
                                int lineNumber = SearchUtil.findLineString(keyword, tempFileXMLPath);

                                ItemObject tempActionItem = new ItemObject("",valueAction + "::" + valueMethod, tempFileXMLPath, lineNumber,"");
                                
                                mapActionItemJavaPath.put(tempActionItem, tempJavaPath);
                                
                                break;
                            } else if(parrentNode.getNodeName().contains("global-results")){
                                //if path file in global-results Tag
                                tempPageName = "global";
                            }
                        }
                    }
                }
                // if tempPage contain .definition, find  Page name extends it
                if (tempPageName != null) {
                    if (tempPageName.contains(".definition")) {
                        for (int count = 0; count < nListDef.getLength(); count++) {
                            Node tempNode = nListDef.item(count);
                            Node tempExtendsNode = tempNode.getAttributes().getNamedItem("extends");
                            if (tempExtendsNode != null) {
                                String extendsValue = tempExtendsNode.getNodeValue();
                                if (extendsValue.contains(tempPageName)) {
                                    String realPageName = tempNode.getAttributes().getNamedItem("name").getNodeValue();
                                    if (realPageName.toLowerCase().contains(".page")){
                                        String keyword = "\"" + realPageName + "\"";
                                        int lineNumber = SearchUtil.findLineString(keyword, tempFileXMLPath);
                                        ItemObject tempPageNameItem = new ItemObject("",realPageName, tempFileXMLPath, lineNumber, "");
                                        tempListPageNameItem.add(tempPageNameItem);
                                        //break;
                                    }
                                }
                            }
                        }
                    } else {
                        
                        String keyword = "\"" + tempPageName + "\"";
                        int lineNumber = SearchUtil.findLineString(keyword, tempFileXMLPath);
                        
                        ItemObject tempPageNameItem = new ItemObject("",tempPageName, tempFileXMLPath, lineNumber, "");
                        tempListPageNameItem.add(tempPageNameItem);
                        
                    }
                }

            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println(e.getMessage());
        }
        return tempListPageNameItem;
    }


    public List<ItemObject> getListPageNameItem() {
        return listPageNameItem;
    }
    
    public Map<ItemObject, String> getMapActionItemJavaPath() {
        return mapActionItemJavaPath;
    }
}
