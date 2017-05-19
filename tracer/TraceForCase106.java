/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.tracer;

import com.viettel.security.antlr.JavaParser;
import com.viettel.security.config.ModuleConfig;
import com.viettel.security.constant.ConstantCode;
import com.viettel.security.finder.FindExpressionInMethod;
import com.viettel.security.finder.FindExpressionOfParam;
import com.viettel.security.finder.FindFormalInputOfMethod;
import com.viettel.security.finder.FindLocalVarDifineInMethod;
import com.viettel.security.object.MethodInfor;
import com.viettel.security.object.MethodSource;
import com.viettel.security.object.VariableDefine;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 *
 * @author binhhh6
 */
public class TraceForCase106 {

    private JavaParser parser;
    private ParserRuleContext treeContext;
    private String varId;
    private MethodSource tmpMethodSource;
    List<JavaParser.ExpressionContext> listExcepExpressList = new ArrayList<>();
    List<MapFuctionValue> listMapFunctionValue = new ArrayList<MapFuctionValue>();
    List<VariableDefine> listFormalInput = new ArrayList<VariableDefine>();
    MethodInfor methodInfo;

    MethodSource newMethodSource;

    boolean isVulnMethod = false;

    public TraceForCase106(JavaParser parser, ParserRuleContext treeContext,
            String varId,
            MethodSource tmpMethodSource) {
        this.parser = parser;
        this.treeContext = treeContext;
        this.varId = varId;
        this.tmpMethodSource = tmpMethodSource;

        //get param input of method
        ParseTreeWalker walkermethod = new ParseTreeWalker();
        FindFormalInputOfMethod findFormalInputMethod = new FindFormalInputOfMethod(parser);
        walkermethod.walk(findFormalInputMethod, treeContext);

        this.listFormalInput = findFormalInputMethod.getListVariable();
        this.methodInfo = findFormalInputMethod.getMethodInfor();
        this.newMethodSource = null;

        processTrace106();
    }

    private void processTrace106() {
        String classXML = tmpMethodSource.getClassName();
        if (classXML.equals("SAXBuilder")) {
            processTrace106SAXBuilder();
        } else if (classXML.equals("XMLInputFactory")) {
            processTrace106XMLInputFactory();
        } else if (classXML.equals("DocumentBuilder") || classXML.equals("SAXParser")) {
            processTrace106DocumentBuilder();
        }
    }

    private void processTrace106DocumentBuilder() {
        ParseTreeWalker walkermethod = new ParseTreeWalker();
        FindExpressionOfParam findExpressOfParam = new FindExpressionOfParam(parser, varId, listExcepExpressList);
        walkermethod.walk(findExpressOfParam, this.treeContext);

        List<JavaParser.ExpressionContext> listExpressOfVar = findExpressOfParam.getListExpressOfVar();
        listExcepExpressList = findExpressOfParam.getListExcepExpressList();

        if (listExpressOfVar.isEmpty()) {
            this.isVulnMethod = true;
        } else {
            for (JavaParser.ExpressionContext expressOfVar : listExpressOfVar) {
                TraceExpressionToParam traceExpressionToParam = new TraceExpressionToParam(expressOfVar);

                List<String> listParam = traceExpressionToParam.getListParam();
                if (listParam.size() != 2) {
                    this.isVulnMethod = true;
                } else {
                    if (listParam.get(1).equals("newDocumentBuilder")
                            || listParam.get(1).equals("newSAXParser")) {
                        processTrace106DocumentBuilder_Timothy(walkermethod, listParam.get(0),
                                "setXIncludeAware", "false", "true");
                        processTrace106DocumentBuilder_Timothy(walkermethod, listParam.get(0),
                                "setExpandEntityReferences", "false", "true");
                        processTrace106DocumentBuilder_FEATURE(walkermethod, listParam.get(0));
                    }
                }
//                if (expressOfVar.getText().endsWith(".newDocumentBuilder()")) {
//                    String varId1 = expressOfVar.getText()
//                            .substring(0, expressOfVar.getText().indexOf(".newDocumentBuilder()"));
//                    if (varId1.equals("DocumentBuilderFactory.newInstance()")) {
//                        this.isVulnMethod = true;
//                    } else {
//                        processTrace106DocumentBuilder_Timothy(walkermethod, varId1,
//                                "setXIncludeAware", "false", "true");
//                        processTrace106DocumentBuilder_Timothy(walkermethod, varId1,
//                                "setExpandEntityReferences", "false", "true");
//                        processTrace106DocumentBuilder_FEATURE(walkermethod, varId1);
//                    }
//                }
            }
        }
    }

    private void processTrace106DocumentBuilder_FEATURE(ParseTreeWalker walkermethod,
            String varId) {
        if (this.isVulnMethod) {
            return;
        }
        FindExpressionInMethod finderExpressionInMethod
                = new FindExpressionInMethod(parser, varId, "setFeature");
        walkermethod.walk(finderExpressionInMethod, this.treeContext);
        List<JavaParser.ExpressionListContext> listExpressList
                = finderExpressionInMethod.getListExpressList();
        if (listExpressList.isEmpty()) {
            this.isVulnMethod = true;
        } else {
            for (JavaParser.ExpressionListContext express : listExpressList) {
                if (express.getChildCount() != 3) {
                    continue;
                }
//                String value = express.getText();
//                System.out.println("=========value: " + value);
//                JavaParser.ExpressionContext expression
//                        = (JavaParser.ExpressionContext) express.getChild(0);
                String varNameFEATURE = express.getChild(0).getText();
                String varNameBoolean = express.getChild(2).getText();

                // Truong hop khai bao luon -- Begin
                if (checkPassDocumentBuilder_FEATURE(varNameFEATURE, varNameBoolean)) {
                    this.isVulnMethod = false;
                    return;
                } else {
                    this.isVulnMethod = true;
                }
                // Truong hop khai bao luon -- End

                FindLocalVarDifineInMethod findLocalVarDifineInMethod = new FindLocalVarDifineInMethod(parser, varNameFEATURE);
                walkermethod.walk(findLocalVarDifineInMethod, treeContext);
                List<VariableDefine> listVariableDeclareFEATURE = findLocalVarDifineInMethod.getListVariable();

                findLocalVarDifineInMethod = new FindLocalVarDifineInMethod(parser, varNameBoolean);
                walkermethod.walk(findLocalVarDifineInMethod, treeContext);
                List<VariableDefine> listVariableDeclareBoolean = findLocalVarDifineInMethod.getListVariable();

                if (listVariableDeclareFEATURE.size() > 0) {
                    // Truong hop khai bao luon trong ham
                    for (VariableDefine variableDefineFEATURE : listVariableDeclareFEATURE) {
                        String valueFEATURE = variableDefineFEATURE.getValue();
                        if (listVariableDeclareBoolean == null || listVariableDeclareBoolean.isEmpty()) {
                            if (checkPassDocumentBuilder_FEATURE(valueFEATURE, varNameBoolean)) {
                                this.isVulnMethod = false;
                                return;
                            } else {
                                this.isVulnMethod = true;
                            }
                        } else {
                            for (VariableDefine variableDefineBoolean : listVariableDeclareBoolean) {
                                String valueBoolean = variableDefineBoolean.getValue();
                                if (checkPassDocumentBuilder_FEATURE(valueFEATURE, valueBoolean)) {
                                    this.isVulnMethod = false;
                                    return;
                                } else {
                                    this.isVulnMethod = true;
                                }
                            }
                        }
                    }
                } else {
                    // Truong hop khai bao cho khac
                    // case param in method input
//                    int count = 0;
//                    for (VariableDefine variableDefine : listFormalInput) {
//                        if (variableDefine.getName().equals(varNameFEATURE)) {
//                            //get infor method
//                            if (!variableDefine.getType().contains("String")) {
//                                isVulnMethod = true;
////                                break;
//                            } else {
//                                int[] array = {count};
//                                this.methodInfo.setInjectableArgs(array);
//                                this.methodInfo.setScanType(tmpMethodSource.getScanType());
//                                List<Integer> list = new ArrayList<Integer>();
//                                list.add(ConstantCode.BaselineCode.XML);
//                                this.methodInfo.setListBaselineItem(list);
//                                newMethodSource = methodInforToMethodSource(this.methodInfo);
////                            isVulnMethod = true;
////                            break;
//                            }
//                        }
//                        count++;
//                    }
                }

            }
        }
    }

    public MethodSource methodInforToMethodSource(MethodInfor infor) {
        //do something for validation????
        return new MethodSource(infor.getPackageName(), infor.getClassName(), infor.getMethodName(), infor.getOutputType(), infor.getInjectableArgs(), infor.getScanType(), infor.getListArgs(), infor.getListBaselineItem());
    }

    private boolean checkPassDocumentBuilder_FEATURE(String valueFEATURE, String valueBoolean) {
        if (valueFEATURE.equalsIgnoreCase("\"http://apache.org/xml/features/disallow-doctype-decl\"")
                && valueBoolean.equals("true")) {
            return true;
        } else if ((valueFEATURE.equalsIgnoreCase("\"http://xml.org/sax/features/external-general-entities\"")
                && valueBoolean.equals("false"))
                || (valueFEATURE.equalsIgnoreCase("\"http://xml.org/sax/features/external-parameter-entities\"")
                && valueBoolean.equals("false"))) {
            return true;
        } else {
            return false;
        }
    }

    private void processTrace106DocumentBuilder_Timothy(ParseTreeWalker walkermethod,
            String varId, String key, String valuePass, String valueFalse) {
        if (this.isVulnMethod) {
            return;
        }
        FindExpressionInMethod finderExpressionInMethod
                = new FindExpressionInMethod(parser, varId, key);
        walkermethod.walk(finderExpressionInMethod, this.treeContext);
        List<JavaParser.ExpressionListContext> listExpressList
                = finderExpressionInMethod.getListExpressList();
        if (listExpressList.isEmpty()) {
            this.isVulnMethod = true;
        } else {
            for (JavaParser.ExpressionListContext express : listExpressList) {
                String value = express.getText();
                if (value.equals(valuePass)) {
                    this.isVulnMethod = false;
                } else if (value.equals(valueFalse)) {
                    this.isVulnMethod = true;
                } else {
                    // TODO xu ly truong hop truyen bien vao
                    this.isVulnMethod = true;
                }
            }
        }
    }

    private void processTrace106XMLInputFactory() {
        if (this.isVulnMethod) {
            return;
        }
        ParseTreeWalker walkermethod = new ParseTreeWalker();
        FindExpressionInMethod finderExpressionInMethod
                = new FindExpressionInMethod(parser, varId, "setProperty");
        walkermethod.walk(finderExpressionInMethod, this.treeContext);
        List<JavaParser.ExpressionListContext> listExpressList
                = finderExpressionInMethod.getListExpressList();
        if (listExpressList.isEmpty()) {
            this.isVulnMethod = true;
        } else {
            boolean hasIS_SUPPORTING_EXTERNAL_ENTITIES = false;
            boolean hasSUPPORT_DTD = false;
            boolean passSUPPORTING_EXTERNAL_ENTITIES = false;
            boolean passSUPPORT_DTD = false;
            for (JavaParser.ExpressionListContext express : listExpressList) {
                int count = express.getChildCount();
                if (count != 3) {
                    continue;
                }
                JavaParser.ExpressionContext expression1 = (JavaParser.ExpressionContext) express.getChild(0);
                String value1 = expression1.getText();
                JavaParser.ExpressionContext expression2 = (JavaParser.ExpressionContext) express.getChild(2);
                String value2 = expression2.getText();
//                    System.out.println("value: " + value);
                if (value1.equals("XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES")) {
                    hasIS_SUPPORTING_EXTERNAL_ENTITIES = true;
                    if (hasIS_SUPPORTING_EXTERNAL_ENTITIES) {
                        if (value2 != null) {
                            if (value2.equals("false")) {
                                passSUPPORTING_EXTERNAL_ENTITIES = true;
                            } else if (value2.equals("true")) {
                                passSUPPORTING_EXTERNAL_ENTITIES = false;
                            } else {
                                // TODO xu ly truong hop truyen vao la bien
                                passSUPPORTING_EXTERNAL_ENTITIES = false;
                            }
                        } else {
                            passSUPPORTING_EXTERNAL_ENTITIES = false;
                        }
                    }
                } else if (value1.equals("XMLInputFactory.SUPPORT_DTD")) {
                    hasSUPPORT_DTD = true;
                    if (hasSUPPORT_DTD) {
                        if (value2 != null) {
                            if (value2.equals("false")) {
                                passSUPPORT_DTD = true;
                            } else if (value2.equals("true")) {
                                passSUPPORT_DTD = false;
                            } else {
                                // TODO xu ly truong hop truyen vao la bien
                                passSUPPORT_DTD = false;
                            }
                        } else {
                            passSUPPORT_DTD = false;
                        }
                    }
                }
            }
            if (hasIS_SUPPORTING_EXTERNAL_ENTITIES && hasSUPPORT_DTD
                    && passSUPPORTING_EXTERNAL_ENTITIES && passSUPPORT_DTD) {
                this.isVulnMethod = false;
            } else {
                this.isVulnMethod = true;
            }

        }
    }

    private void processTrace106SAXBuilder() {
        if (this.isVulnMethod) {
            return;
        }
        setMapFunctionValueSAXBuilder();
        for (MapFuctionValue mapEntry : this.listMapFunctionValue) {
            ParseTreeWalker walkermethod = new ParseTreeWalker();
            FindExpressionInMethod finderExpressionInMethod
                    = new FindExpressionInMethod(parser, varId, mapEntry.key);
            walkermethod.walk(finderExpressionInMethod, this.treeContext);
            List<JavaParser.ExpressionListContext> listExpressList
                    = finderExpressionInMethod.getListExpressList();
            if (listExpressList.isEmpty()) {
                this.isVulnMethod = true;
            } else {
                for (JavaParser.ExpressionListContext express : listExpressList) {
                    String value = express.getText();
                    if (value.equals(mapEntry.valuePass)) {
                        this.isVulnMethod = false;
                    } else if (value.equals(mapEntry.valueFail)) {
                        this.isVulnMethod = true;
                    } else {
                        // TODO xu ly bien truyen vao
                        this.isVulnMethod = true;
                    }
                }
            }
        }
    }

    private void setMapFunctionValueSAXBuilder() {
        //get list method in config.properties
        ModuleConfig config = new ModuleConfig();
        List<String> listMethodInConfig = config.readConfig("SAXBuilder_config_value");
        for (String tempConfig : listMethodInConfig) {
            String[] tempList = tempConfig.split("--");
            List<String> tempList1 = Arrays.asList(tempList);
            if (tempList1.size() == 2) {
                MapFuctionValue tempMap = new MapFuctionValue(tempList1.get(0), tempList1.get(1), "");
                this.listMapFunctionValue.add(tempMap);
            } else if (tempList1.size() >= 3) {
                MapFuctionValue tempMap = new MapFuctionValue(tempList1.get(0), tempList1.get(1), tempList1.get(2));
                this.listMapFunctionValue.add(tempMap);
            }
        }
    }

    public class MapFuctionValue {

        public String key;
        public String valuePass;
        public String valueFail;

        public MapFuctionValue(String key, String valuePass, String valueFail) {
            this.key = key != null ? key : "";
            this.valuePass = valuePass != null ? valuePass : "";
            this.valueFail = valueFail != null ? valueFail : "";
        }
    }

    public JavaParser getParser() {
        return parser;
    }

    public void setParser(JavaParser parser) {
        this.parser = parser;
    }

    public ParserRuleContext getTreeContext() {
        return treeContext;
    }

    public void setTreeContext(ParserRuleContext treeContext) {
        this.treeContext = treeContext;
    }

    public MethodSource getTmpMethodSource() {
        return tmpMethodSource;
    }

    public void setTmpMethodSource(MethodSource tmpMethodSource) {
        this.tmpMethodSource = tmpMethodSource;
    }

    public boolean isIsVulnMethod() {
        return isVulnMethod;
    }

    public void setIsVulnMethod(boolean isVulnMethod) {
        this.isVulnMethod = isVulnMethod;
    }

    public String getVarId() {
        return varId;
    }

    public void setVarId(String varId) {
        this.varId = varId;
    }

    public static void main(String[] args) {
        //get list method in config.properties
        ModuleConfig config = new ModuleConfig();
        List<String> listMethodInConfig = config.readConfig("SAXBuilder_config_value");

    }
}
