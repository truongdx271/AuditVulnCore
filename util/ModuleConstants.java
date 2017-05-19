/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.util;

/**
 *
 * @author longnt39
 */
public class ModuleConstants {
    public static String CONSTRUCTOR_NAME = "<init>";
    
    //Java type by ASM
    public static String BOOLEAN    = "Z";
    public static String CHAR       = "C";
    public static String BYTE       = "B";
    public static String SHORT      = "S";
    public static String INT        = "I";
    public static String FLOAT      = "F";
    public static String LONG       = "J";
    public static String DOUBLE     = "D";
    public static String OBJECT     = "L";
    public static String ARRAY      = "[";
    
    
    //Type Listener when scan for called vuln class in method body
    public static int SEARCH_CLASS_NAME = 1;
    public static int SEARCH_INTERNAL_NAME = 2;
}
