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
public class TimeSeriesModel {
    private String nameSeries;
    private int numberDataPoints;
    private int start;
    private int end;
    private float []arrayTimeSeries;

    public TimeSeriesModel(String nameSeries, int numberDataPoints, int start, int end, float[] arrayTimeSeries) {
        this.nameSeries = nameSeries;
        this.numberDataPoints = numberDataPoints;
        this.start = start;
        this.end = end;
        this.arrayTimeSeries = arrayTimeSeries;
    }

    public String getNameSeries() {
        return nameSeries;
    }

    public void setNameSeries(String nameSeries) {
        this.nameSeries = nameSeries;
    }

    public int getNumberDataPoints() {
        return numberDataPoints;
    }

    public void setNumberDataPoints(int numberDataPoints) {
        this.numberDataPoints = numberDataPoints;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public float[] getArrayTimeSeries() {
        return arrayTimeSeries;
    }

    public void setArrayTimeSeries(float[] arrayTimeSeries) {
        this.arrayTimeSeries = arrayTimeSeries;
    }
    
    
    
}
