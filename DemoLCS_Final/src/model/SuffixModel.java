/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Vudodp
 */
public class SuffixModel {
    
    private int indexFirstString;
    private int indexSecondString;
    private String suffix;

    public SuffixModel(){
        this.indexFirstString = 0;
        this.indexSecondString = 0;
        this.suffix = null;
    }
    
    public SuffixModel(int indexFirstString, int indexSecondString, String suffix){
        this.indexFirstString = indexFirstString;
        this.indexSecondString = indexSecondString;
        this.suffix = suffix;
    }

    public int getIndexFirstString() {
        return indexFirstString;
    }

    public void setIndexFirstString(int indexFirstString) {
        this.indexFirstString = indexFirstString;
    }

    public int getIndexSecondString() {
        return indexSecondString;
    }

    public void setIndexSecondString(int indexSecondString) {
        this.indexSecondString = indexSecondString;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    
}
