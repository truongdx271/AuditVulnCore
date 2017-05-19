/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.object;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author quangbx1
 */
public class MethodInfor {
    private String packageName;
    private String className;
    private String methodName;
    private List<String> listArgs = new ArrayList<>();
    private String outputType;
    private int[] injectableArgs;
    private int scanType;
    private List<Integer> listBaselineItem;

    public MethodInfor() {
    }

    public MethodInfor(String packageName, String className, String methodName, String outputType, int[] injectableArgs, int scanType) {
        this.packageName = packageName;
        this.className = className;
        this.methodName = methodName;
        this.outputType = outputType;
        this.injectableArgs = injectableArgs;
        this.scanType = scanType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getListArgs() {
        return listArgs;
    }

    public void setListArgs(List<String> listArgs) {
        this.listArgs = listArgs;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public int[] getInjectableArgs() {
        return injectableArgs;
    }

    public void setInjectableArgs(int[] injectableArgs) {
        this.injectableArgs = injectableArgs;
    }

    public int getScanType() {
        return scanType;
    }

    public void setScanType(int scanType) {
        this.scanType = scanType;
    }

    public List<Integer> getListBaselineItem() {
        return listBaselineItem;
    }

    public void setListBaselineItem(List<Integer> listBaselineItem) {
        this.listBaselineItem = listBaselineItem;
    }
    
    
}
