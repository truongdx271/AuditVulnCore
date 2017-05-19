/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.security.object;

/**
 *
 * @author longnt39
 */
public class ErrorBaseline {
    private String bigItemBaseline;
    private String smallItemBaseline;
    private String vulnLine;
    private int lineNumber;
    private String filePath;
    private String levelAlert;
    private String errorDescription;
    private String recommendation;

    public ErrorBaseline(String baselineItem, String vulnLine, int lineNumber, String filePath){
        if (baselineItem.length() != 3){
            return;
        }
        this.bigItemBaseline = baselineItem.substring(0, 1);
        
        String smallItemBaseline = baselineItem.substring(1);
        if (smallItemBaseline.startsWith("0")){
            smallItemBaseline = smallItemBaseline.substring(1);
        }
        
        this.smallItemBaseline = smallItemBaseline;
        
        this.vulnLine = vulnLine;
        this.lineNumber = lineNumber;
        this.filePath = filePath;
    }
    
    public String getBigItemBaseline() {
        return bigItemBaseline;
    }

    public void setBigItemBaseline(String bigItemBaseline) {
        this.bigItemBaseline = bigItemBaseline;
    }

    public String getSmallItemBaseline() {
        return smallItemBaseline;
    }

    public void setSmallItemBaseline(String smallItemBaseline) {
        this.smallItemBaseline = smallItemBaseline;
    }

    public String getVulnLine() {
        return vulnLine;
    }

    public void setVulnLine(String vulnLine) {
        this.vulnLine = vulnLine;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getLevelAlert() {
        return levelAlert;
    }

    public void setLevelAlert(String levelAlert) {
        this.levelAlert = levelAlert;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
    
    
}
