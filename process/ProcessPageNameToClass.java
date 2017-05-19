/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.process;

import com.viettel.audit.def.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author longnt39
 */
public class ProcessPageNameToClass {

    List<ItemObject> listActionItem = new ArrayList<>();
    Map<ItemObject,String> mapActionItemJavaPath = new HashMap<>();
    Map<ItemObject,ItemObject> mapActionItemPageNameItem = new HashMap<>();

    public ProcessPageNameToClass(String foundJspPath, List<ItemObject> listPageNameItem, List<String> listDefineXML) {
        List<String> listJavaPath = new ArrayList<>();
        if (listPageNameItem.size() == 1) {
            if (listPageNameItem.get(0).getDisplayTxt().toLowerCase().contentEquals("global")) {
                //jsp path file in global-results Tag
                listJavaPath.add("global");

            } else if (listPageNameItem.get(0).getDisplayTxt().toLowerCase().matches("(.*).page")) {
                //jsp path file in put-attribute Tag and Definition Tag (call once time)
                SearchJavaInXMLFile searchJavaInXMLFile = new SearchJavaInXMLFile(foundJspPath, listPageNameItem, listDefineXML);
                listActionItem = searchJavaInXMLFile.getListActionItem();
                mapActionItemJavaPath = searchJavaInXMLFile.getMapActionItemJavaPath();
                //listJavaPath = searchJavaInXMLFile.ExportListJavaPath();
                //mapJavaActionResponse.putAll(searchJavaInXMLFile.getMapJavaActionResponse());
            }  else {
                //Page name in special case. Eg: permissionPageNodeB, VibaRouteMng.result.AddImg
                SearchJavaInXMLFile searchJavaInXMLFile = new SearchJavaInXMLFile(foundJspPath, listPageNameItem, listDefineXML);
                mapActionItemJavaPath = searchJavaInXMLFile.getMapActionItemJavaPath();

            }
        } else if (listPageNameItem.size() > 1) {
            //jsp path file in Definition Tag (and called many time)
            SearchJavaInXMLFile searchJavaInXMLFile = new SearchJavaInXMLFile(foundJspPath, listPageNameItem, listDefineXML);
            mapActionItemJavaPath = searchJavaInXMLFile.getMapActionItemJavaPath();
            mapActionItemPageNameItem = searchJavaInXMLFile.getMapActionItemPageNameItem();

        }

        if (!listJavaPath.isEmpty()) {
            boolean isGlobal = false;
            for (String javaPath : listJavaPath) {
                if (javaPath.contentEquals("global")) {
                    isGlobal = true;
                }
            }
            if (isGlobal) {
                    //Case .page only in Global tag, find Java has name Result return
                //mapJspGlobal.put(foundJspPath, "global");
            } else {
                //mapJspGlobal.put(foundJspPath, "no");
            }
        }
    }
    
    public Map<ItemObject,String> getMapActionItemJavaPath(){
        return mapActionItemJavaPath;
    }

    public Map<ItemObject,ItemObject> getMapActionItemPageNameItem(){
        return mapActionItemPageNameItem;
    }
}
