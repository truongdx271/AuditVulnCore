/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.finder;

import com.viettel.security.antlr.JavaBaseListener;
import com.viettel.security.antlr.JavaParser;
import com.viettel.security.config.ModuleConfig;
import com.viettel.security.constant.ConstantCode;
import com.viettel.security.object.MethodSource;
import com.viettel.security.object.VariableDefine;
import com.viettel.security.tracer.TraceForCase102;
import com.viettel.security.tracer.TraceForCase106;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author longnt39
 * @update quangbx1
 */
public class FindUsedMethodInClass extends JavaBaseListener {

    JavaParser parser;
    private Map<ParserRuleContext, List<MethodSource>> mapJavaVulnToCheck = new HashMap<>();

    List<MethodSource> listMethodSources;
    List<MethodSource> listNewMethodSources;
    String packageName;
    List<VariableDefine> listGlobalVar = new ArrayList<VariableDefine>();
    List<String> listSpecialCase;

    public FindUsedMethodInClass(JavaParser parser, List<MethodSource> listMethodSources) {
        this.parser = parser;
        this.listMethodSources = listMethodSources;
        ModuleConfig config = new ModuleConfig();
        this.listSpecialCase = config.readConfig("methods_exception_case");
    }

    @Override
    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
//        System.out.println("Package: "+ctx.getChild(1).getText());
        this.packageName = ctx.getChild(1).getText();
    }

    //Rule: variableModifier* type variableDeclarators
    @Override
    public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        //get child respresent type of varable
        String type = "";

        String varId = "";

        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree childTree = ctx.getChild(i);
            if (childTree instanceof JavaParser.TypeContext) {
                type = childTree.getText();
            } else if (childTree instanceof JavaParser.VariableDeclaratorsContext) {
                //Rule1: variableDeclarators: variableDeclarator (',' variableDeclarator)*;
                //Rule2: variableDeclarator: variableDeclaratorId ('=' variableInitializer)?;

                varId = childTree.getChild(0).getChild(0).getText();

            }
        }

        //if can't find type, return
        if (type.isEmpty() || varId.isEmpty()) {
            return;
        }

        //get method contains this declare
        ParserRuleContext parrentOfTree = ctx.getParent();

        while (!(parrentOfTree instanceof JavaParser.MethodDeclarationContext)
                && !(parrentOfTree instanceof JavaParser.ConstructorDeclarationContext)) {
            try {
                parrentOfTree = parrentOfTree.getParent();
                if (parrentOfTree == null) {
                    return;
                }
            } catch (Exception ex) {
                System.out.println("====>" + ex.toString() + " : " + ctx.getText());
                return;
            }

        }

        getCalledMethod(type, varId, parrentOfTree);

    }

    //find in Parameter in put of a method
    //Rule: variableModifier* type variableDeclaratorId
    @Override
    public void enterFormalParameter(JavaParser.FormalParameterContext ctx) {
        //Expect: formalParameter->formalParameterList->formalParameters->methodDeclaration
        ParserRuleContext ancesstorOfTree = ctx.getParent().getParent().getParent();

        if (!(ancesstorOfTree instanceof JavaParser.MethodDeclarationContext)) {
            return;
        }

        int childLength = ctx.getChildCount();

        String type = "";
        String varId = "";

        for (int i = 0; i < childLength; i++) {
            ParseTree childTree = ctx.getChild(i);
            if (childTree instanceof JavaParser.TypeContext) {
                type = childTree.getText();
            } else if (childTree instanceof JavaParser.VariableDeclaratorIdContext) {
                varId = childTree.getText();
            }
        }

        if (type.isEmpty() || varId.isEmpty()) {
            return;
        }

        getCalledMethod(type, varId, ancesstorOfTree);
    }

    //Case: call method of a global var, ex: XSS ZK,...
    //step1:get list global var 
    //step2: enterExpression and check case ex Html.setContent()
    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        String type = "";

        String varId = "";
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree childTree = ctx.getChild(i);
            if (childTree instanceof JavaParser.TypeContext) {
                type = childTree.getText();
            } else if (childTree instanceof JavaParser.VariableDeclaratorsContext) {
                //Rule1: variableDeclarators: variableDeclarator (',' variableDeclarator)*;
                //Rule2: variableDeclarator: variableDeclaratorId ('=' variableInitializer)?;
                varId = childTree.getChild(0).getChild(0).getText();
            }
        }
        //if can't find type, return
        if (type.isEmpty() || varId.isEmpty()) {
            return;
        }
        ParserRuleContext parrentOfTree = ctx.getParent();
//        System.out.println("Type: "+ type + "& Id: "+ varId);
        listGlobalVar.add(new VariableDefine(type, varId));
    }

    // Truong hop dac biet getSesssion().createSQL, Html.setContent, Upload
    @Override
    public void enterExpression(JavaParser.ExpressionContext ctx) {
        JavaParser.ExpressionListContext expessList = null;
        List<JavaParser.ExpressionListContext> listExpressList = new ArrayList<>();
        //case ex: html.setContent() and getSession().createSQL(),...
//        if(listGlobalVar.isEmpty()) return;
        if (ctx.getChildCount() < 4) {
            return;
        }
        if (!ctx.getChild(1).getText().equals("(")) {
            return;
        }
        ParserRuleContext parrentOfTree = ctx.getParent();
        while (!(parrentOfTree instanceof JavaParser.MethodDeclarationContext)
                && !(parrentOfTree instanceof JavaParser.ConstructorDeclarationContext)) {
            try {
                parrentOfTree = parrentOfTree.getParent();
                if (parrentOfTree == null) {
                    return;
                }
            } catch (Exception ex) {
                System.out.println("====>" + ex.toString() + " : " + ctx.getText());
                return;
            }
        }
        List<MethodSource> listCallingMethod = new ArrayList<>();
        if (mapJavaVulnToCheck.containsKey(parrentOfTree)) {
            listCallingMethod = mapJavaVulnToCheck.get(parrentOfTree);
        }
        MethodSource isCalledMethod = null;
        for (int i = 0; i < listMethodSources.size(); i++) {
            MethodSource tmpMethodSource = listMethodSources.get(i);
            if (listCallingMethod.contains(tmpMethodSource)) {
                continue;
            }
            //case ex getSession().createSQL
            //this case dont loop scan, only scan once
            if (listSpecialCase.contains(tmpMethodSource.getClassName())) {
//            if(tmpMethodSource.getClassName().equals("getSession()")) {
                if (ctx.getChildCount() < 3) {
                    continue;
                }
                if (!ctx.getChild(1).getText().equals("(")) {
                    continue;
                }

                if (!ctx.getChild(0).getText().equals(tmpMethodSource.getClassName() + "." + tmpMethodSource.getMethodName())) {
                    continue;
                }

                if (ctx.getChild(2) instanceof JavaParser.ExpressionListContext) {
                    expessList = (JavaParser.ExpressionListContext) ctx.getChild(2);
                    listExpressList.add((JavaParser.ExpressionListContext) ctx.getChild(2));
                }
                if (listExpressList.isEmpty()) {
                    continue;
                }
                if (tmpMethodSource.getScanType() == 102) {
                    TraceForCase102 tracerFor102 = new TraceForCase102(parser, parrentOfTree, listExpressList, tmpMethodSource);
                    if (!tracerFor102.isIsVulnMethod()) {
                        continue;
                    }
                }
                isCalledMethod = tmpMethodSource;
                break;
            }
            //case ex em.createNativeQuery(queryString) with em is global param
            if (tmpMethodSource.getScanType() == 102) {
                if (!listGlobalVar.isEmpty()) {
                    for (int j = 0; j < listGlobalVar.size(); j++) {
                        if (!ctx.getChild(0).getText().equals(listGlobalVar.get(j).getName() + "." + tmpMethodSource.getMethodName())) {
                            continue;
                        }
                        if (ctx.getChild(2) instanceof JavaParser.ExpressionListContext) {
                            expessList = (JavaParser.ExpressionListContext) ctx.getChild(2);
                            listExpressList.add((JavaParser.ExpressionListContext) ctx.getChild(2));
                        }
                        if (listExpressList.isEmpty()) {
                            continue;
                        }
                        if (tmpMethodSource.getScanType() == 102) {
                            TraceForCase102 tracerFor102 = new TraceForCase102(parser, parrentOfTree, listExpressList, tmpMethodSource);
                            if (!tracerFor102.isIsVulnMethod()) {
                                continue;
                            }
                        }
                        isCalledMethod = tmpMethodSource;
                        break;
                    }
                }
            }

            //case ex XSS html.setContent(
            if (tmpMethodSource.getScanType() != 102) {
                if (!listGlobalVar.isEmpty()) {
                    for (int j = 0; j < listGlobalVar.size(); j++) {
                        if (!ctx.getChild(0).getText().equals(listGlobalVar.get(j).getName() + "." + tmpMethodSource.getMethodName())) {
                            continue;
                        }
                        isCalledMethod = tmpMethodSource;
                        break;
                    }
                }
            }
        }
        if (isCalledMethod == null) {
            return;
        }
        listCallingMethod.add(isCalledMethod);
        mapJavaVulnToCheck.put(parrentOfTree, listCallingMethod);
    }

    public void getCalledMethod(String type, String varId, ParserRuleContext parrentOfTree) {

        List<MethodSource> listCallingMethod = new ArrayList<>();
        if (mapJavaVulnToCheck.containsKey(parrentOfTree)) {
            listCallingMethod = mapJavaVulnToCheck.get(parrentOfTree);
        }

        MethodSource isCalledMethod = null;

//        for (MethodSource tmpMethodSource : listMethodSources) {
        for (int i = 0; i < listMethodSources.size(); i++) {
            MethodSource tmpMethodSource = listMethodSources.get(i);
//            if(tmpMethodSource.getClassName().equals("getSession()")) continue;
            if (listSpecialCase.contains(tmpMethodSource.getClassName())) {
                continue;
            }
            if (listCallingMethod.contains(tmpMethodSource)) {
                continue;
            }

            //if type is not equall either class name or full internal name
            if ((!type.toString().equals(tmpMethodSource.getPackageName().toString() + "." + tmpMethodSource.getClassName().toString()))
                    && (!type.toString().equals(tmpMethodSource.getClassName().toString()))) {
                continue;
            }

            //check when vuln method of Class was called in body of Method
            List<JavaParser.ExpressionListContext> listExpressList = getCallMethodExec(varId, tmpMethodSource.getMethodName(), parrentOfTree);
            if (listExpressList.isEmpty()) {
                continue;
            }

            //Demo: process for case 102, ex: SQL Injection
            if (tmpMethodSource.getScanType() == 102) {
                TraceForCase102 tracerFor102 = new TraceForCase102(parser, parrentOfTree, listExpressList, tmpMethodSource);
                if (tracerFor102.getNewMethodSource() != null) {
                    listNewMethodSources = new ArrayList<MethodSource>();
                    MethodSource ms = tracerFor102.getNewMethodSource();
                    ms.setPackageName(packageName);
                    listNewMethodSources.add(ms);
                }
                if (!tracerFor102.isIsVulnMethod()) {
//                    if(tracerFor102.getNewMethodSource() != null){
//                        listNewMethodSources = new ArrayList<MethodSource>();
//                        MethodSource ms = tracerFor102.getNewMethodSource();
//                        ms.setPackageName(packageName);
//                        listNewMethodSources.add(ms);
//                    }
                    continue;
                }
            } // BinhHH6 -- Begin:  XML XXE 
            else if (tmpMethodSource.getScanType() == ConstantCode.ScanType.XML) {
                TraceForCase106 tracerFor106 = new TraceForCase106(parser, parrentOfTree, 
                        varId, tmpMethodSource);
                if (!tracerFor106.isIsVulnMethod()) {
                    continue;
                }
            }
            // BinhHH6 -- End

            //case 102 is true or all other case
            isCalledMethod = tmpMethodSource;
            break;

        }
        if (isCalledMethod == null) {
            return;
        }
        listCallingMethod.add(isCalledMethod);
        mapJavaVulnToCheck.put(parrentOfTree, listCallingMethod);
    }

    public Map<ParserRuleContext, List<MethodSource>> getMapJavaVulnToCheck() {
        return mapJavaVulnToCheck;
    }

    public void setMapJavaVulnToCheck(Map<ParserRuleContext, List<MethodSource>> mapJavaVulnToCheck) {
        this.mapJavaVulnToCheck = mapJavaVulnToCheck;
    }

    private List<JavaParser.ExpressionListContext> getCallMethodExec(String varId, String methodName, ParserRuleContext parrentOfTree) {

        ParseTreeWalker walkermethod = new ParseTreeWalker();

        FindExpressionInMethod finderExpressionInMethod = new FindExpressionInMethod(parser, varId, methodName);

        walkermethod.walk(finderExpressionInMethod, parrentOfTree);

        List<JavaParser.ExpressionListContext> listExpressList = finderExpressionInMethod.getListExpressList();

        return listExpressList;
    }

    public List<MethodSource> getListNewMethodSources() {
        return listNewMethodSources;
    }

    public void setListNewMethodSources(List<MethodSource> listNewMethodSources) {
        this.listNewMethodSources = listNewMethodSources;
    }
}
