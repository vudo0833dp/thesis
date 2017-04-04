/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import algorithms.DynamicProgrammingA;
import algorithms.SkewAlgorithm;
import algorithms.SuffixTree;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import model.SuffixModel;
import model.TimeSeriesModel;

/**
 *
 * @author Vudodp
 */
public class SAX {
    
    public static final int STANDARD_DEVIATION = 1; // Do lech chuan
    private SkewAlgorithm sa;
    private SuffixTree st;
    
    public void buildSAX_Symbols(int algorithms, File []list_file, int numberOfPAA){
        
        // First, read data from txt file.
        List<Float> firstTimeSeries = readData(list_file[0]);
        List<Float> secondTimeSeries = readData(list_file[1]);
        
        if(numberOfPAA > firstTimeSeries.size()){
            JOptionPane.showMessageDialog(null, "PAA number = "+numberOfPAA +" > Number of Data point = "+ firstTimeSeries.size()+" !!!");
        } else {
            
            // Normalize data
            float []normalizedFirst_TS = normalize(firstTimeSeries, numberOfPAA);
            float []normalizedSecond_TS = normalize(secondTimeSeries, numberOfPAA);

            // Convert normalized data into PAA Coefficene, dimentionality reduction
            float []paa1 = changeToPAA(normalizedFirst_TS, numberOfPAA);
            float []paa2 = changeToPAA(normalizedSecond_TS, numberOfPAA);

            // Symbolize PAA coefficent
            String first = symbolizePPA(paa1);
            String second = symbolizePPA(paa2);

            // Apply algorithms to these symbols

            switch(algorithms){
                case 1: // Suffix tree
                    writeData(first, second, "suffix_tree");
                    st = new SuffixTree(); 
                    List<SuffixModel> result = st.UkkonenAlgorithm(first, second);
                    writeLCSIntoFile(result, "suffix_tree", "Suffix Tree.");
                    System.out.println(result.get(0).getSuffix()+"   "+result.get(1).getSuffix());
                    
                    if(result.size() > 1){
                        // Retrieve back in time series
                        TimeSeriesModel timeseries_1 = retrieveBackToTimeSeries(list_file[0].getName(), firstTimeSeries, 
                                        result.get(0), firstTimeSeries.size()/numberOfPAA, 1);

                        TimeSeriesModel timeseries_2 = retrieveBackToTimeSeries(list_file[1].getName(), secondTimeSeries, 
                                        result.get(0), secondTimeSeries.size()/numberOfPAA, 2);

                        ChartApplication ca_suffix_tree = new ChartApplication(timeseries_1, timeseries_2, firstTimeSeries,secondTimeSeries);
                        ca_suffix_tree.runChart();
                        String mess = "SUCCESSFULLY! \n \n Go to the following path to see results in Desktop: \n /output";
                        JOptionPane.showMessageDialog(null, mess);
                    } else {
                        JOptionPane.showMessageDialog(null, "There is no correlation between tow Time Series data.");
                    }

                    break;
                case 2: // Suffix Array
                    writeData(first, second, "suffix_array" );
                    sa = new SkewAlgorithm();
                    try{
                        List<SuffixModel> listLCS = sa.skew(first, second);
                        writeLCSIntoFile(listLCS, "suffix_array", "Suffix Array.");
                        if(listLCS.size() > 1){
                            // Retrieve back in time series
                            TimeSeriesModel ts1 = retrieveBackToTimeSeries(list_file[0].getName(), firstTimeSeries, 
                                            listLCS.get(0), firstTimeSeries.size()/numberOfPAA, 1);

                            TimeSeriesModel ts2 = retrieveBackToTimeSeries(list_file[1].getName(), secondTimeSeries, 
                                            listLCS.get(0), secondTimeSeries.size()/numberOfPAA, 2);

                            ChartApplication ca1 = new ChartApplication(ts1, ts2, firstTimeSeries,secondTimeSeries);
                            ca1.runChart();
                            String mess = "SUCCESSFULLY! \n \n Go to the following path to see results: \n D:/output";
                            JOptionPane.showMessageDialog(null, mess);
                        } else {
                            JOptionPane.showMessageDialog(null, "There is no correlation between tow Time Series data.");
                        }
                    } catch (ArrayIndexOutOfBoundsException e){
                        JOptionPane.showMessageDialog(null, "In some situations of using suffix array, number of PAA is greater than 250 is not allowed. "
                                + "\nIf you get this message, please kindly choose number of PAA less than 250!");
                    }
                    
                    break;
                case 3: // Dynamic programming
                    writeData(first, second, "dynamic_programming" );
                    DynamicProgrammingA da = new DynamicProgrammingA();
                    List<SuffixModel> idx = da.find(first, second);
                    writeLCSIntoFile(idx, "dynamic_programming", "Dynamic Programming.");
                    if(idx.size() > 1){
                        // Retrieve back to real time series data
                        TimeSeriesModel ts_1 = retrieveBackToTimeSeries(list_file[0].getName(), firstTimeSeries, 
                                        idx.get(0), firstTimeSeries.size()/numberOfPAA, 1);

                        TimeSeriesModel ts_2 = retrieveBackToTimeSeries(list_file[1].getName(), secondTimeSeries, 
                                        idx.get(0), secondTimeSeries.size()/numberOfPAA, 2);


                        ChartApplication ca = new ChartApplication(ts_1, ts_2, firstTimeSeries,secondTimeSeries);
                        ca.runChart();
                        String mess = "SUCCESSFULLY! \n \n Go to the following path to see results: \n D:/output";
                        JOptionPane.showMessageDialog(null, mess);
                    } else {
                        JOptionPane.showMessageDialog(null, "There is no correlation between tow Time Series data.");
                    }
                    break;
                default:
                    break;
            }
            // a
            
            System.err.println();
            System.err.println("First: "+first);
            System.err.println("Second: "+second);
        }
    }
    
    public float[] changeToPAA(float []normalizedTimeSeries, int w){
        // w is number of segment in PAA
        int n = normalizedTimeSeries.length;
        float []paa = new float[w];
        int numberOfElement = n/w; // number of elements in one segment PAA.
        for(int i = 0; i < w; i++){
            float sum = 0.0f;
            for(int j = 0; j < numberOfElement; j++){
                int index = j + numberOfElement*i;
                sum = sum + normalizedTimeSeries[index];
            }
            paa[i] = sum*w/n;
        }
        return paa;
    }
    
    public String symbolizePPA(float []paa){
        StringBuffer str = new StringBuffer();
        
        double[] breakpoints = {-1.64, -1.28, -1.04, -0.84, -0.67, -0.52, -0.39, -0.25, -0.13, 0.0f,
                                0.13, 0.25, 0.39, 0.52, 0.67, 0.84, 1.04, 1.28, 1.64};
        
        char []symbols = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
                          'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't'};
        
        for(int i = 0; i < paa.length; i++){
            for(int j = 0; j < breakpoints.length; j++){
                if( (j == 0) && paa[i] < ((float)breakpoints[0]) ){
                    str.append(symbols[0]);
                    break; 
                } else if( (j == breakpoints.length - 1) && (paa[i] >= ((float)breakpoints[breakpoints.length - 1])) ){
                    str.append(symbols[breakpoints.length]);
                    break;
                } else {
                    if( ((float)breakpoints[j]) <= paa[i] && paa[i] < ((float)breakpoints[j + 1]) ){
                        str.append(symbols[j + 1]);
                        break;
                    }
                }
            }
        }
        return String.valueOf(str);
    }
    
    public float []normalize(List<Float> timeSeries, int w){
        int n = timeSeries.size();
        int length = n - (n%w);
        float []normalizedTimeSeries = new float[length];
        float sum;
        sum = 0.0f;
        for(int i = 0; i < length; i++)
            sum += timeSeries.get(i);
        float meanValue = (sum / length);
        for(int i = 0; i < length; i++){
            normalizedTimeSeries[i] = (timeSeries.get(i) - meanValue)/STANDARD_DEVIATION;
        }        
        return normalizedTimeSeries;
    }
    
    public TimeSeriesModel retrieveBackToTimeSeries(String nameSeries, List<Float> timeSeries, SuffixModel sm, int nElements, int type){
        int length = sm.getSuffix().length()*nElements;
        
        int start = 0, end = 0;
        switch(type){
            case 1: //  Type = 1: we calculate start index and end index of first time series. 
                start = sm.getIndexFirstString()*nElements;
                end = (sm.getIndexFirstString()+sm.getSuffix().length())*nElements;
                break;
            case 2: // Type = 2: we calculate start index and end index of second time series. 
                start = sm.getIndexSecondString()*nElements;
                end = (sm.getIndexSecondString()+sm.getSuffix().length())*nElements;
                break;
            default:
                break;
        }
       
        float []data = new float[length];
        int idx = 0;
        
        // Get all data points in section that has the same change with second time series from time to time.
        for(int i = start; i < end; i++){
            data[idx++] = ((float)timeSeries.get(i));
        }
 
        return new TimeSeriesModel(nameSeries, timeSeries.size(), start, end, data);
    }
    
    public List<Float> readData(File file){
        List<Float> timeSeries = new ArrayList<>();
        try{
            try(FileReader fileReader = new FileReader(file)){
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while((line = bufferedReader.readLine()) != null){
                    if(!line.equals("")){
                        timeSeries.add(new Float(line));
                    }
                }
            }
        }catch(IOException | NumberFormatException e){
            JOptionPane.showMessageDialog(null, e);
        }
    return timeSeries;
    }
    
    public void writeData(String data_1, String data_2, String nameAlgorithms){
        try{
          
            String directory = System.getProperty("user.home")+"\\Desktop\\output\\"+nameAlgorithms;
            File fileDirectory = new File(directory);
            if(!fileDirectory.exists()){
                fileDirectory.mkdirs();
            }
            
            String pathFile1 = directory+"\\symbolizeTimeSeries_1.txt";
            String pathFile2 = directory+"\\symbolizeTimeSeries_2.txt";
            File file_1 = new File(pathFile1);
            File file_2 = new File(pathFile2);
            
            if(!file_1.exists()) file_1.createNewFile();
            if(!file_2.exists()) file_2.createNewFile();
            
            FileWriter fw1 = new FileWriter(file_1.getAbsoluteFile());
            BufferedWriter bw1 = new BufferedWriter(fw1);
            bw1.write(data_1);
            bw1.close();
            
            FileWriter fw2 = new FileWriter(file_2.getAbsoluteFile());
            BufferedWriter bw2 = new BufferedWriter(fw2);
            bw2.write(data_2);
            bw2.close();
           
            
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
        
      
    }
    public void writeLCSIntoFile(List<SuffixModel> listLCS, String nameAlgorithms, String name){
        try{
            String path = System.getProperty("user.home")+"\\Desktop\\output\\"+nameAlgorithms+"\\LCS.txt";
            File file = new File(path);
            if(!file.exists()){
                file.createNewFile();
            }
            
            FileWriter fw = new FileWriter(file);
            BufferedWriter bw = new BufferedWriter(fw);
            int n = listLCS.size();
            for(int i = 0; i < n - 1; i++){
                bw.write("LCS "+(i+1)+": "+listLCS.get(i).getSuffix());
                bw.newLine();
            }
            if(nameAlgorithms.equals("suffix_tree")){
                String [] time = listLCS.get(n - 1).getSuffix().split("#");
                bw.write("Time building tree: "+ time[0]);
                bw.newLine();
                bw.write("Time finding LCS: "+ time[1]);
                bw.newLine();
                bw.write("Done by "+name);
                bw.close();
            } else {
                bw.write(listLCS.get(n - 1).getSuffix());
                bw.newLine();
                bw.write("Done by "+name);
                bw.close();
            }
           
        } catch (Exception e){
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
}
