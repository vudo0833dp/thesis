package algorithms;

import java.util.ArrayList;
import java.util.List;
import model.SuffixModel;


public class DynamicProgrammingA {
	
    public List<SuffixModel> find(String a, String b){
        long lStartTime = System.nanoTime();
        List<SuffixModel> listLCS = new ArrayList<>();
	int [][]LCS = new int [a.length() + 1][b.length() + 1];
	
	for(int i = 0; i <= a.length(); i++){
            for(int j = 0; j <= b.length(); j++){
		LCS[i][j] = 0;
            }
	}
		
	//fill the rest of the matrix
	for(int i = 1; i <= a.length(); i++){
            for(int j = 1; j <= b.length(); j++){
        	if(a.charAt(i - 1) == b.charAt(j - 1)){
                    LCS[i][j] =  LCS[i - 1][j - 1] + 1;
        	}else{
                    LCS[i][j] = 0;					
        	}
            }
	}
        
	int result = -1;
	
	for(int i = 0; i <= a.length(); i++){
            for(int j = 0; j <= b.length(); j++){
                SuffixModel suffixModel = new SuffixModel();
                if(result < LCS[i][j] && LCS[i][j] > 0){
                    result = LCS[i][j];
                    String lcs = a.substring(i - result, i);
                    //SuffixModel suffixModel = new SuffixModel(i - result, j - result, lcs);
                    suffixModel.setIndexFirstString(i - result);
                    suffixModel.setIndexSecondString(j - result);
                    suffixModel.setSuffix(lcs);
                    if(!listLCS.isEmpty()) listLCS.clear();
                    listLCS.add(suffixModel);
                    
                } else {
                    if(result == LCS[i][j] && LCS[i][j] > 0){
                        result = LCS[i][j];
                        String lcs = a.substring(i - result, i);
                        suffixModel.setIndexFirstString(i - result);
                        suffixModel.setIndexSecondString(j - result);
                        suffixModel.setSuffix(lcs);
                        if(isEqual(suffixModel, listLCS) == false){
                            listLCS.add(suffixModel);
                        }
                    }
                }
            }
	}	

        long lEndTime = System.nanoTime();
        long difference = lEndTime - lStartTime;
        // the last element of List LCS is time.
        listLCS.add(new SuffixModel(0, 0, "Time is: "+(difference/1000) + " microseconds."));
        return listLCS;
    }
    
    public static boolean isEqual(SuffixModel suffix, List<SuffixModel> listLCS){

        for(int i = 0; i < listLCS.size(); i++){
            
            SuffixModel sm = listLCS.get(i);
            if(isEqually(suffix.getSuffix(), sm.getSuffix()) == true){
                return true;
            }
            
        }
        return false;
    }
    
    public static boolean isEqually(String first, String second){
        for(int i = 0; i < first.length(); i++){
            if(first.charAt(i) != second.charAt(i)){
                return false;
            }
        }
        return true;
    }
    
}
