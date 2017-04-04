/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.geom.Ellipse2D;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import model.TimeSeriesModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYDrawableAnnotation;
import org.jfree.chart.annotations.XYPointerAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;

/**
 *
 * @author Vudodp
 */
public final class ChartApplication extends JFrame{
    
    private final TimeSeriesModel timeSeries_1;
    private final TimeSeriesModel timeSeries_2;
    
    // List file
    private List<Float> dataPointTimeSeries_1, dataPointTimeSeries_2;
    // component in chart
    private JPanel subPanel;
    private JLabel labelNameSeries_1, labelInfor_1 ;
    private JLabel labelNameSeries_2, labelInfor_2;

    private JPanel generalPanel;
    private JLabel totalLabel;
    
    public ChartApplication(TimeSeriesModel timeSeries_1, TimeSeriesModel timeSeries_2, 
            List<Float> dataPointTimeSeries_1, List<Float> dataPointTimeSeries_2) {
        super("Chart Drawing");
        
        this.timeSeries_1 = timeSeries_1;
        this.timeSeries_2 = timeSeries_2;
        this.dataPointTimeSeries_1 = dataPointTimeSeries_1;
        this.dataPointTimeSeries_2 = dataPointTimeSeries_2;
        
        initUI();
        //drawOriginalTimeSeries();
    }
    
    
    public void initUI(){
        JPanel chartPanel = createChartPanel();
        setSize(new Dimension(850, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        
        generalPanel        = new JPanel(new GridLayout(2, 1));
        subPanel            = new JPanel(new GridLayout(2, 2));
        labelNameSeries_1   = new JLabel();
        labelNameSeries_2   = new JLabel();
        labelInfor_1        = new JLabel();
        labelInfor_2        = new JLabel();
        totalLabel          = new JLabel();
        
        // Create empty border for PANEL
        generalPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5)); 
        // Create black line border for LABEL
        generalPanel.setBorder(BorderFactory.createLineBorder(Color.black)); 
        labelNameSeries_1.setBorder(BorderFactory.createLineBorder(Color.black));
        labelNameSeries_2.setBorder(BorderFactory.createLineBorder(Color.black));
        labelInfor_1.setBorder(BorderFactory.createLineBorder(Color.black));
        labelInfor_2.setBorder(BorderFactory.createLineBorder(Color.black));
        totalLabel.setBorder(BorderFactory.createLineBorder(Color.black));
        
        String nameSeries_1 = timeSeries_1.getNameSeries().replace(".txt", "") + " ("+ timeSeries_1.getNumberDataPoints()+" data points)";
        String nameSeries_2 = timeSeries_2.getNameSeries().replace(".txt", "") + " ("+ timeSeries_2.getNumberDataPoints()+" data points)";
        String infor_1 = "Segment starts at index "+(timeSeries_1.getStart() + 1) +
                ", ends at index "+ (timeSeries_1.getEnd() + 1)+".";
        String infor_2 = "Segment starts at index "+(timeSeries_2.getStart() + 1) +
                ", ends at index "+ (timeSeries_2.getEnd() + 1)+".";
        
        labelNameSeries_1.setText("<html><font color = red>"+nameSeries_1+"</font></html>");
        labelNameSeries_2.setText("<html><font color = blue>"+nameSeries_2+"</font></html>");
        
        labelNameSeries_1.setHorizontalAlignment(SwingConstants.CENTER);
        labelNameSeries_1.setVerticalAlignment(SwingConstants.CENTER);
        labelNameSeries_2.setHorizontalAlignment(SwingConstants.CENTER);
        labelNameSeries_2.setVerticalAlignment(SwingConstants.CENTER);
        
        labelInfor_1.setText("<html><font color = red>"+infor_1+"</font></html>");
        labelInfor_2.setText("<html><font color = blue>"+infor_2+"</font></html>");
        totalLabel.setText("<html><font size = '9'>Total data points in segment: "+(timeSeries_1.getEnd() - timeSeries_1.getStart())+"</font></html>");
        totalLabel.setHorizontalAlignment(SwingConstants.CENTER);
        totalLabel.setVerticalAlignment(SwingConstants.CENTER);
       
        // adding component
        subPanel.add(labelNameSeries_1);
        subPanel.add(labelNameSeries_2);
        subPanel.add(labelInfor_1);
        subPanel.add(labelInfor_2);
        generalPanel.add(subPanel);
        generalPanel.add(totalLabel);
        add(generalPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
    }
    
    private JPanel createChartPanel() {
        // creates a line chart object
        // returns the chart panel
        String chartTitle = "The most correlated pair of segments in the two Time Series";
        String xAxisLabel = "Time";
        String yAxisLabel = "Value";
        
        XYDataset dataset = createDataset(timeSeries_1, timeSeries_2, dataPointTimeSeries_1, dataPointTimeSeries_2);
        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
            xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, false, false, false);
        
        chart.getXYPlot().setBackgroundPaint(Color.WHITE);
        XYItemRenderer renderer = chart.getXYPlot().getRenderer();
        // Segment is correlated
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setSeriesPaint(1, Color.BLACK);
        
        // Two original time series data
        renderer.setSeriesPaint(2, Color.RED);
        renderer.setSeriesPaint(3, Color.BLUE);
        // 
        renderer.setSeriesStroke(4, new BasicStroke(20));
        renderer.setSeriesStroke(5, new BasicStroke(20));
        renderer.setSeriesPaint(4, new Color(179, 255, 198));
        renderer.setSeriesPaint(5, new Color(179, 255, 198));
        // Add domain of pair of correlated segment
//        final Marker originalStart_1= new ValueMarker(timeSeries_1.getStart()+1);
//        originalStart_1.setPaint(Color.BLACK);
//        chart.getXYPlot().addDomainMarker(originalStart_1);
//        
//        final Marker originalEnd_1 = new ValueMarker(timeSeries_1.getEnd()+1);
//        originalEnd_1.setPaint(Color.BLACK);
//        originalEnd_1.setStroke(new BasicStroke(1.0f));
//        chart.getXYPlot().addDomainMarker(originalEnd_1);
        
        
        return new ChartPanel(chart);
    }
 
    private XYDataset createDataset(TimeSeriesModel ts1, TimeSeriesModel ts2, List<Float> dataPointTS1, List<Float> dataPointTS2) {
        // creates an XY dataset...
        // returns the dataset
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        // Data series of all data point in time series
        XYSeries series1 = new XYSeries(ts1.getNameSeries().replace(".txt", ""));
        XYSeries series2 = new XYSeries(ts2.getNameSeries().replace(".txt", ""));
        for(int i = 0; i < dataPointTS1.size(); i++){
            series1.add((i+1), dataPointTS1.get(i));
        }
        for(int i = 0; i < dataPointTS2.size(); i++){
            series2.add((i+1), dataPointTS2.get(i));
        }
        
        // This is pair of correlated segments
        XYSeries series3 = new XYSeries("");
        XYSeries series4 = new XYSeries("");
        for(int i = ts1.getStart(); i < ts1.getEnd(); i++){
            series3.add((i+1), dataPointTS1.get(i));
        }
        for(int i = ts2.getStart(); i < ts2.getEnd(); i++){
            series4.add((i+1), dataPointTS2.get(i));
        }
        
        // This data series is used to high light the correlated segment between two time series
        XYSeries series5 = new XYSeries("");
        XYSeries series6 = new XYSeries("");
        for(int i = ts1.getStart(); i < ts1.getEnd(); i++){
            series5.add((i+1), dataPointTS1.get(i));
        }
        for(int i = ts2.getStart(); i < ts2.getEnd(); i++){
            series6.add((i+1), dataPointTS2.get(i));
        }
        
        dataset.addSeries(series3);
        dataset.addSeries(series4);
        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series5);
        dataset.addSeries(series6);
        
        return dataset;
    }
 
    /**
     *
     */
    public void runChart() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ChartApplication(timeSeries_1, timeSeries_2, dataPointTimeSeries_1, dataPointTimeSeries_2).setVisible(true);
            }
        });
    }
   
}
