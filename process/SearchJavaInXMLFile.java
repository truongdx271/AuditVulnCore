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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
class SearchJavaInXMLFile {

    List<ItemObject> listActionItem = new ArrayList<>();
    Map<ItemObject, String> mapActionItemJavaPath = new HashMap<>();
    Map<ItemObject,ItemObject> mapActionItemPageNameItem = new HashMap<>();

    SearchJavaInXMLFile(String foundJspPath, List<ItemObject> listPageNameItem, List<String> listDefineXML) {
        for (ItemObject pageNameItem : listPageNameItem) {
            for (String defineXML : listDefineXML) {
                String tempJavaPath = null;
                try {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    //set for XML disable check dtd
                    dbf.setValidating(false);
                    dbf.setNamespaceAware(true);
                    dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

                    File file = new File(defineXML);
                    DocumentBuilder dBuilder = dbf.newDocumentBuilder();
                    Document doc = dBuilder.parse(file);

                    //if file has no tag XML, read next file
                    if (!doc.hasChildNodes()) {
                        continue;
                    }

                    //get Result Tag
                    NodeList nListRes = doc.getElementsByTagName("result");

                    //if file has not tag result, scan next file
                    if (nListRes.getLength() < 1) {
                        continue;
                    }

                    for (int count = 0; count < nListRes.getLength(); count++) {
                        String valueResponse = null;
                        Node tempResNode = nListRes.item(count);

                        String result = tempResNode.getTextContent();
                        String valueAction = null;

                        //if result content has not page name value, read next node
                        if (!result.contains(pageNameItem.getDisplayTxt())) {
                            continue;
                        }

                        Node parrentNode = tempResNode.getParentNode();
                        if (parrentNode.getAttributes().getNamedItem("class") != null) {
                            String valuePath = parrentNode.getAttributes().getNamedItem("class").getNodeValue();
                            valueAction = parrentNode.getAttributes().getNamedItem("name").getNodeValue();
                            //get path to Java file
                            String[] tempPath = valuePath.split("\\.");
                            String shortPath = "";
                            for (String tempPath1 : tempPath) {
                                shortPath += File.separator + tempPath1;
                            }
                            if (defineXML.contains(File.separator + "classes" + File.separator)) {
                                int endIndex = defineXML.indexOf(File.separator + "classes" + File.separator);
                                tempJavaPath = defineXML.substring(0, endIndex) + File.separator + "classes" + shortPath + ".java";
                            } else if (defineXML.contains(File.separator + "WEB-INF" + File.separator)) {
                                int endIndex = defineXML.indexOf(File.separator + "WEB-INF" + File.separator);
                                tempJavaPath = defineXML.substring(0, endIndex) + File.separator + "WEB-INF" + File.separator + "classes" + shortPath + ".java";
                            }

                        } else if (parrentNode.getNodeName().contains("global-results")) {
                            //if path file in global-results Tag
                            tempJavaPath = "global";
                            valueAction = "In global-results tag";
                        }

                        //read response value in tag result
                        if (tempResNode.getAttributes().getNamedItem("name") != null) {
                            valueResponse = tempResNode.getAttributes().getNamedItem("name").getNodeValue();
                        }
                        if (tempJavaPath != null) {
                            
                            int lineNumber = 0;
                            
                            if (!valueAction.equalsIgnoreCase("In global-results tag")){
                                String keyword = "\"" + valueAction + "\"";
                                lineNumber = SearchUtil.findLineString(keyword, defineXML);
                            }else {
                                String keyword = "<global-results>";
                                lineNumber = SearchUtil.findLineString(keyword, defineXML);
                            }
                            
                            ItemObject tempActionItem = new ItemObject("",valueAction + "::" + valueResponse, defineXML, lineNumber, "");
                            listActionItem.add(tempActionItem);

                            mapActionItemJavaPath.put(tempActionItem, tempJavaPath);
                            mapActionItemPageNameItem.put(tempActionItem, pageNameItem);
                        }

                    }

                } catch (ParserConfigurationException | SAXException | IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    List<ItemObject> getListActionItem() {
        return listActionItem;
    }

    Map<ItemObject, String> getMapActionItemJavaPath() {
        return mapActionItemJavaPath;
    }
    
    Map<ItemObject,ItemObject> getMapActionItemPageNameItem(){
        return mapActionItemPageNameItem;
    }
}
