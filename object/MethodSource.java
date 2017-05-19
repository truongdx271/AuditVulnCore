/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.object;

import com.viettel.security.config.ModuleConfig;
import com.viettel.security.util.ModuleConstants;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author longnt39
 */
public class MethodSource {

    //declare vulnerability or high priority method for scan
    private String packageName;
    private String className;
    private String methodName;
    private List<String> listArgs = new ArrayList<>();
    private String outputType;
    private int[] injectableArgs;
    private int scanType;
    private List<Integer> listBaselineItem = new ArrayList<>();

    public MethodSource(String lineDescription) {
        if (lineDescription == null && !lineDescription.contains(":")) {
            return;
        }

        String[] partDescription = lineDescription.split("\\:");

        if (partDescription.length < 4) {
            return;
        }

//        if(partDescription.length > 4){
//            this.checkFormat = partDescription[4];
//        }
        //bind data from line to object
        setClassMethodParam(partDescription[0]);
        setInjectableArg(partDescription[1]);
        setScanType(partDescription[2]);
        setListBaselineItem(partDescription[3]);
    }

    private void setClassMethodParam(String fullMethodName) {
        //Update case ex getSession.createSQL,...
        ModuleConfig config = new ModuleConfig();
        List<String> listMethod = config.readConfig("methods_exception_case");
//        if (fullMethodName.contains("getSession()")) {
        for (String temp : listMethod) {
            if (fullMethodName.contains(temp)) {
                int openIndex = fullMethodName.lastIndexOf("(");
                if (openIndex < 0) {
                    return;
                }
                String tmp = fullMethodName.substring(0, openIndex);
                String[] partMethod = tmp.split("\\.");
                this.className = partMethod[0];
//                if ("HHBExcep".equals(this.className)) {
//                    this.className = "";
//                }
                this.methodName = partMethod[1];
                int closeIndex = fullMethodName.lastIndexOf(')');
                if (closeIndex < openIndex) {
                    return;
                }
                String argDescription = fullMethodName.substring(openIndex + 1, closeIndex);

                String[] arrayArgDes = argDescription.split("\\;");
                listArgs.clear();
                for (String argDes : arrayArgDes) {
                    listArgs.add(argDes);
                }
                //get output Type
                String outputType = fullMethodName.substring(closeIndex + 1);
                if (outputType.endsWith(";")) {
                    outputType = outputType.substring(0, outputType.length() - 1);
                }
                this.outputType = outputType;
                this.packageName = null;
                return;
            }
        }
//        if (fullMethodName.contains("getSession()")) {
//            int openIndex = fullMethodName.lastIndexOf("(");
//            if (openIndex < 0) {
//                return;
//            }
//            String tmp = fullMethodName.substring(0, openIndex);
//            String[] partMethod = tmp.split("\\.");
//            this.className = partMethod[0];
//            this.methodName = partMethod[1];
//            int closeIndex = fullMethodName.lastIndexOf(')');
//            if (closeIndex < openIndex) {
//                return;
//            }
//            String argDescription = fullMethodName.substring(openIndex + 1, closeIndex);
//
//            String[] arrayArgDes = argDescription.split("\\;");
//            listArgs.clear();
//            for (String argDes : arrayArgDes) {
//                listArgs.add(argDes);
//            }
//            //get output Type
//            String outputType = fullMethodName.substring(closeIndex + 1);
//            if (outputType.endsWith(";")) {
//                outputType = outputType.substring(0, outputType.length() - 1);
//            }
//            this.outputType = outputType;
//            this.packageName = null;
//            return;
//        }
        int openIndex = fullMethodName.indexOf('(');
        if (openIndex < 0) {
            return;
        }

        String classAndMethodName = fullMethodName.substring(0, openIndex);
        int slashIndex = classAndMethodName.lastIndexOf('/');

        //set first portion to package name
        this.packageName = classAndMethodName.substring(0, slashIndex);
        //replace character / by character .
        this.packageName = this.packageName.replace("/", ".");

        String shortName = classAndMethodName.substring(slashIndex + 1);
        if (shortName.endsWith(ModuleConstants.CONSTRUCTOR_NAME)) {
            shortName = shortName.substring(0, shortName.indexOf('.'));
            this.className = shortName;
            this.methodName = ModuleConstants.CONSTRUCTOR_NAME;
        } else {
            String[] classMethod = shortName.split("\\.");
            this.className = classMethod[0];
            this.methodName = classMethod[1];
        }

        //get list Argument input from line
        int closeIndex = fullMethodName.lastIndexOf(')');
        if (closeIndex < openIndex) {
            return;
        }

        String argDescription = fullMethodName.substring(openIndex + 1, closeIndex);

        String[] arrayArgDes = argDescription.split("\\;");
        listArgs.clear();
        for (String argDes : arrayArgDes) {
            listArgs.add(argDes);
        }

        //get output Type
        String outputType = fullMethodName.substring(closeIndex + 1);
        if (outputType.endsWith(";")) {
            outputType = outputType.substring(0, outputType.length() - 1);
        }
        this.outputType = outputType;
    }

    private void setInjectableArg(String injectDescrption) {
        if (!injectDescrption.contains(",")) {
            int[] arrayInjectArgs = new int[1];
            arrayInjectArgs[0] = Integer.parseInt(injectDescrption);
            this.injectableArgs = arrayInjectArgs;
            return;
        }

        String[] arrayInjectArgs = injectDescrption.split(",");
        int[] injectArgs = new int[arrayInjectArgs.length];
        for (int i = 0; i < arrayInjectArgs.length; i++) {
            injectArgs[i] = Integer.parseInt(arrayInjectArgs[i]);
        }

        this.injectableArgs = injectArgs;
    }

    private void setScanType(String scanType) {
        this.scanType = Integer.parseInt(scanType);
    }

    private void setListBaselineItem(String allBaseLine) {
        String[] arrayBaseLines = allBaseLine.split("\\;");
        listBaselineItem.clear();
        //add baseline Item to list
        for (String baseLine : arrayBaseLines) {
            listBaselineItem.add(Integer.parseInt(baseLine));
        }
    }

    public boolean isValid() {
        if (this.packageName == null || this.packageName.isEmpty()
                || this.className == null || this.className.isEmpty()
                || this.methodName == null || this.methodName.isEmpty()
                || this.outputType == null || this.outputType.isEmpty()
                || this.injectableArgs == null || this.injectableArgs.length < 1
                || this.listArgs == null || this.listArgs.isEmpty()
                || this.listBaselineItem == null || this.listBaselineItem.isEmpty()
                || this.scanType < 0) {
            return false;
        }
        return true;
    }

    public String toString() {
        String listArgToString = "";
        for (String argment : this.listArgs) {
            listArgToString += argment + "&";
        }

        String injectableToString = "";
        for (int inject : this.injectableArgs) {
            injectableToString += inject + "&";
        }

        String baselineToString = "";
        for (int itemBaseline : this.listBaselineItem) {
            baselineToString += itemBaseline + "&";
        }

        return "Package: " + this.packageName + " | "
                + "Class: " + this.className + " | "
                + "Method: " + this.methodName + " | "
                + "Input : " + listArgToString + " | "
                + "Output: " + this.outputType + " | "
                + "Inject point: " + injectableToString + " | "
                + "Scan Type: " + this.scanType + " | "
                + "Baseline Item: " + baselineToString + " | ";
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public String getOutputType() {
        return this.outputType;
    }

    public void setListArgs(List<String> listArgs) {
        this.listArgs = listArgs;
    }

    public List<String> getListArgs() {
        return this.listArgs;
    }

    public void setInjectableArgs(int[] injectableArgs) {
        this.injectableArgs = injectableArgs;
    }

    public int[] getInjectableArgs() {
        return this.injectableArgs;
    }

    public void setScanType(int scanType) {
        this.scanType = scanType;
    }

    public int getScanType() {
        return this.scanType;
    }

    public void getListBaselineItem(List<Integer> listBaselineItem) {
        this.listBaselineItem = listBaselineItem;
    }

    public List<Integer> getListBaselineItem() {
        return this.listBaselineItem;
    }

    public MethodSource(String packageName, String className, String methodName, String outputType,
            int[] injectableArgs, int scanType, List<String> listArgs, List<Integer> listBaselineItem) {
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.outputType = outputType;
        this.injectableArgs = injectableArgs;
        this.scanType = scanType;
        this.listArgs = listArgs;
        this.listBaselineItem = listBaselineItem;
    }

}
