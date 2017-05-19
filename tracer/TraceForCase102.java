/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.tracer;

import com.viettel.security.antlr.JavaParser;
import com.viettel.security.config.ModuleConfig;
import com.viettel.security.constant.ConstantCode;
import com.viettel.security.finder.FindExpressionOfParam;
import com.viettel.security.finder.FindFormalInputOfMethod;
import com.viettel.security.finder.FindLocalVarDifineInMethod;
import com.viettel.security.object.MethodInfor;
import com.viettel.security.object.MethodSource;
import com.viettel.security.object.VariableDefine;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author Whitehat
 */
public class TraceForCase102 {

    JavaParser parser;
    ParserRuleContext treeContext;
    List<JavaParser.ExpressionListContext> listExpressList;
    List<JavaParser.ExpressionContext> listExcepExpressList = new ArrayList<>();
    MethodSource tmpMethodSource;
    MethodSource newMethodSource;
    MethodInfor methodInfo;

    List<VariableDefine> listFormalInput = new ArrayList<>();

    boolean isVulnMethod = false;
    List<String> listMethodInConfig;

    public TraceForCase102(JavaParser parser, ParserRuleContext treeContext, List<JavaParser.ExpressionListContext> listExpressList, MethodSource tmpMethodSource) {
        this.parser = parser;
        this.treeContext = treeContext;
        this.listExpressList = listExpressList;
        this.tmpMethodSource = tmpMethodSource;
        this.newMethodSource = null;
        //get list method in config.properties
        ModuleConfig config = new ModuleConfig();
        this.listMethodInConfig = config.readConfig("methods_get_value");
        //get param input of method
        ParseTreeWalker walkermethod = new ParseTreeWalker();
        FindFormalInputOfMethod findFormalInputMethod = new FindFormalInputOfMethod(parser);
        walkermethod.walk(findFormalInputMethod, treeContext);

        this.listFormalInput = findFormalInputMethod.getListVariable();
        this.methodInfo = findFormalInputMethod.getMethodInfor();
        processTracer102();
    }

    private void processTracer102() {

        for (JavaParser.ExpressionListContext expressList : listExpressList) {

            //Exception Case: when expressList not equal input of param
            if (tmpMethodSource.getListArgs().size() != expressList.getChildCount()) {
                continue;
            }

            //Array position of method' input param, which user may control
            int[] injectArgs = tmpMethodSource.getInjectableArgs();

            for (int injectPoint : injectArgs) {
                //From list arg input of method, get only param which can injection, follow file config
                try {
                    JavaParser.ExpressionContext expression = (JavaParser.ExpressionContext) expressList.getChild(injectPoint);

                    String typeArg = tmpMethodSource.getListArgs().get(injectPoint);

                    //Demo process only case input be String
                    if (!typeArg.endsWith("Ljava/lang/String")) {
                        continue;
                    }

                    //Find all param in this expression
                    TraceExpressionToParam traceExpressionToParam = new TraceExpressionToParam(expression);

                    List<String> listParam = traceExpressionToParam.getListParam();

                    //trace list param in method
                    processParam(listParam);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void processParam(List<String> listParam) {

        if (isVulnMethod) {
            return;
        }

        for (String param : listParam) {
            ParseTreeWalker walkermethod = new ParseTreeWalker();

            //get local variable declare in method of this param -> can get type of param
            FindLocalVarDifineInMethod findLocalVarDifineInMethod = new FindLocalVarDifineInMethod(parser, param);
            walkermethod.walk(findLocalVarDifineInMethod, treeContext);

            List<VariableDefine> listVariableDeclare = findLocalVarDifineInMethod.getListVariable();

            //case param from user Input
            if (listVariableDeclare.size() > 0) {
                //Demo for get Param from User by HttpServletRequest, ServletRequest
                //@Quangbx: should define method, which get user input, in file config, then get method to MethodSource Objecct
                if (listVariableDeclare.get(0).getType().equals("HttpServletRequest")
                        || listVariableDeclare.get(0).getType().equals("ServletRequest")) {
                    isVulnMethod = true;
                    break;
                }
            }

            //case param in method input
            int count = 0;
            for (VariableDefine variableDefine : listFormalInput) {
                if (variableDefine.getName().equals(param)) {
                    //get infor method
                    if (!variableDefine.getType().contains("String")) {
                        isVulnMethod = false;
                        break;
                    }
                    int[] array = {count};
                    this.methodInfo.setInjectableArgs(array);
                    this.methodInfo.setScanType(tmpMethodSource.getScanType());
                    List<Integer> list = new ArrayList<Integer>();
                    list.add(ConstantCode.BaselineCode.SQL);
                    this.methodInfo.setListBaselineItem(list);
                    newMethodSource = methodInforToMethodSource(this.methodInfo);
//                    
//                    
//                    if(newMethodSource.g  )
                    isVulnMethod = true;
                    break;
                } 
                count++;
            }

            if (isVulnMethod) {
                break;
            }

            FindExpressionOfParam findExpressOfParam = new FindExpressionOfParam(parser, param, listExcepExpressList);
            walkermethod.walk(findExpressOfParam, treeContext);

            List<JavaParser.ExpressionContext> listExpressOfVar = findExpressOfParam.getListExpressOfVar();
            listExcepExpressList = findExpressOfParam.getListExcepExpressList();

            //when param not get from user or from method's input, find all expression of param again to process
            for (JavaParser.ExpressionContext expressOfVar : listExpressOfVar) {
                TraceExpressionToParam traceExpressionToParam = new TraceExpressionToParam(expressOfVar);
//                System.out.println("BBBBB: "+expressOfVar.getText());
                for (String tmpMethodName : listMethodInConfig) {
//                    System.out.println("tmpMethodName: " + tmpMethodName);
//                    System.out.println("expressOfVar.getText(): " + expressOfVar.getText());
//                    System.out.println("");
                    if (expressOfVar.getText().contains(tmpMethodName) || expressOfVar.getText().contains(tmpMethodName)) {
                        isVulnMethod = true;
                        break;
                    }
                }
                List<String> listParamChild = traceExpressionToParam.getListParam();

                if (listParamChild.isEmpty()) {
                    continue;
                }

                //loop trace param until isVulnMethod = true or all listParam are empty
                processParam(listParamChild);
            }
        }

    }

    public boolean isIsVulnMethod() {
        return isVulnMethod;
    }

    public void setIsVulnMethod(boolean isVulnMethod) {
        this.isVulnMethod = isVulnMethod;
    }

    public MethodSource methodInforToMethodSource(MethodInfor infor) {
        //do something for validation????
        return new MethodSource(infor.getPackageName(), infor.getClassName(), infor.getMethodName(), infor.getOutputType(), infor.getInjectableArgs(), infor.getScanType(), infor.getListArgs(), infor.getListBaselineItem());
    }

    public MethodSource getNewMethodSource() {
        return newMethodSource;
    }

    public void setNewMethodSource(MethodSource newMethodSource) {
        this.newMethodSource = newMethodSource;
    }

}
