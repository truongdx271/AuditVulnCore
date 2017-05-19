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
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author longnt39
 */
public class ProcessJspToVulnParam {

    public static void main(String[] args) {
        String regex = "\\$\\{(?!(fn:escapeXml|contextPath|lst))";
        String text = "<td>${username}</td><td>${fullname}</td>";

        String patternString = "is";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        int count = 0;
        while (matcher.find()) {
            count++;
            System.out.println("found: " + count + " : "
                    + matcher.start() + " - " + matcher.end());
        }
    }
    public List<ItemObject> listItemJspFound = new ArrayList<>();

    public ProcessJspToVulnParam(List<String> listAllJsp, List<String> patternRegexSearch, String projectPath, boolean checkxss) {
        for (String jspPath : listAllJsp) {
//            System.out.println("jspPath "+jspPath);
//            System.out.println("projectPath "+ projectPath);
            String tempJspPath = jspPath.replaceAll("\\\\", "/");
            String tempProjectPath = projectPath.replaceAll("\\\\", "/");
            if ((tempJspPath.contains(tempProjectPath + "/build/")) || (tempJspPath.contains(tempProjectPath + "build/"))) {
                continue;
            }
            //declare list vuln param in file jsp
            List<String> listFindParam = new ArrayList<>();
            for (String patternRegex : patternRegexSearch) {
                Pattern pattern = Pattern.compile(patternRegex);
                try {
                    File fileJsp = new File(jspPath);
                    FileInputStream inputFileJsp = new FileInputStream(fileJsp);
                    //FileObject foJsp = FileUtil.toFileObject(fileJsp);
                    //final Scanner scanner = new Scanner(foJsp.asText());
                    final Scanner scanner = new Scanner(inputFileJsp);
                    int lineNumber = 0;
                    while (scanner.hasNextLine()) {

                        final String lineFromFile = scanner.nextLine();

                        //if line is comment, scan next line
                        String lineReplaceSpace = lineFromFile.replaceAll("\\s+", "");
                        if (lineReplaceSpace.startsWith("<%--") || lineReplaceSpace.startsWith("//") || lineReplaceSpace.startsWith("/*") || lineReplaceSpace.endsWith("--%>") || lineReplaceSpace.endsWith("//")) {
                            lineNumber = lineNumber + 1;
                            continue;
                        }

//                        Matcher matcher = pattern.matcher(lineFromFile);
                        Matcher matcher = pattern.matcher(lineReplaceSpace);
                        //IF for regex search
//                        if (!matcher.find()) {
//                            lineNumber = lineNumber + 1;
//                            continue;
//                        }
                        while (matcher.find()) {
                            //Get name of Parametter
                            //int beginIndex = lineFromFile.indexOf(matcher.group(0));
                            int beginIndex = matcher.start();
                            String param = null;
                            if (matcher.group().contains("${")) {
//                            int endIndex = lineFromFile.indexOf("}", beginIndex);
                                int endIndex = lineReplaceSpace.indexOf("}", beginIndex);
                                if (endIndex > 0) {
//                                param = lineFromFile.substring(beginIndex + 2, endIndex).replaceAll("\\s+", "");
                                    param = lineReplaceSpace.substring(beginIndex + 2, endIndex).replaceAll("\\s+", "");
                                } else {
                                    //One Code comand in 2 line. Fix by input file as FileObject.asText
                                    System.out.println("Error!! one command code in 2 line. File: " + jspPath);
                                }

                            } else if (matcher.group().contains("<%")) {
//                            int endIndex = lineFromFile.indexOf("%>", beginIndex);
                                int endIndex = lineReplaceSpace.indexOf("%>", beginIndex);
                                if (endIndex > 0) {
//                                param = lineFromFile.substring(beginIndex + 3, endIndex).replaceAll("\\s+", "");
                                    param = lineReplaceSpace.substring(beginIndex + 3, endIndex).replaceAll("\\s+", "");
                                }
                            }
                            //Add param to List and Control Line of foud Param
                            if (param == null) {
                                lineNumber = lineNumber + 1;
                                continue;
                            }

//                        System.out.println("Param find by regex: " + param);
                            //if param is request.getAttribute("msg"), get value msg
                            if (param.contains("getAttribute")) {
                                List<String> listVulnParamAfterRegexGet = new ArrayList<>();
                                //true regex: \.getAttribute\("(.*?)"\)
                                String regexForGet = "\\.getAttribute\\(\"(.*?)\"\\)";

                                listVulnParamAfterRegexGet = SearchUtil.findRegex(regexForGet, param);
                                if (!listVulnParamAfterRegexGet.isEmpty()) {
                                    if (!"".equals(listVulnParamAfterRegexGet.get(0))) {
                                        param = listVulnParamAfterRegexGet.get(0);
                                    }
                                }
                            }

                            //if var is (val), get value val
                            if (param.startsWith("(") && param.endsWith(")")) {
                                int endString = param.length() - 1;
                                String tempParam = param.substring(1, endString);
                                param = tempParam;
                            }

                            //if param in format StringUtils.escapeHTML(request.getContextPath()), take value request.getContextPath()
                            //if param in format request.getAttribute(), don't process
                            if (param.contains("()") && !param.endsWith("()")) {
                                List<String> listVulnParamAfterRegex = new ArrayList<>();
                                //true regex (\((.*)\)$)
                                String regexForBlank = "(\\((.*)\\)$)";
                                listVulnParamAfterRegex = SearchUtil.findRegex(regexForBlank, param);
                                if (!listVulnParamAfterRegex.isEmpty()) {
                                    if (!"".equals(listVulnParamAfterRegex.get(0))) {
                                        String tempParam = listVulnParamAfterRegex.get(0);
                                        int endString2 = tempParam.length() - 1;

                                        String tempParam2 = tempParam.substring(1, endString2);

                                        if (!tempParam2.startsWith(")")) {
                                            param = tempParam2;
                                        }
                                    }
                                }
                            }

                            //if param in format reportHT.qualityReportId, get value reportHT only
                            if (param.contains(".")) {
                                String tempParam = param.split("\\.")[0];
                                param = tempParam;
                            }

//                        System.out.println("Param after filter: " + param);
                            //param in list is unique
                            if (!listFindParam.contains(param)) {
                                ItemObject tempFindJspItem;
                                if (checkxss) {
                                    tempFindJspItem = new ItemObject("" + "602", param, jspPath, lineNumber, "WARNING");
                                } else {
                                    tempFindJspItem = new ItemObject("" + "602", param, jspPath, lineNumber, "FAIL");
                                }
                                listItemJspFound.add(tempFindJspItem);
                                listFindParam.add(param);
                            }
                        }

                        lineNumber = lineNumber + 1;

                    }

                } catch (IOException ex) {
                    //Exceptions.printStackTrace(ex);
                    System.out.println(jspPath);
                }
            }
        }
    }

    public List<ItemObject> getListItemJspFound() {
        return listItemJspFound;
    }

}
