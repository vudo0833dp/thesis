/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import model.SuffixModel;

/**
 *
 * @author Vudodp
 */
public class SkewAlgorithm {
    
    private static final int K_RANGE = 256;
    
    /**
     *
     * @param a
     * @param b
     * @return
     */
    public List<SuffixModel> skew(String a, String b) {

        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(a).append("#").append(b);
        String str = strBuilder.toString();
        int[] iOfPreTriple = new int[1];
        iOfPreTriple[0] = 0;

        String temp = new StringBuilder().append(str).append("###").toString();
        String[] arrTriple = new String[str.length()];
        String[] substrings = new String[str.length()];
        for (int i = 0; i < temp.length() - 3; i++) {
            arrTriple[i] = temp.substring(i, i + 3);
            substrings[i] = str.substring(i, str.length());
        }

        long lStartTime = System.nanoTime();
        int[] finalArray = buildSuffixArray(arrTriple, iOfPreTriple);
        List<SuffixModel> listLCS = findMaxLCP(substrings, a.length(), finalArray);
        long lEndTime = System.nanoTime();
        long dif = lEndTime - lStartTime;

//        for (int i = 0; i < substrings.length; i++) {
//            System.err.println("i = " + finalArray[i] + ", SA = " + substrings[finalArray[i] - 1]);
//        }

        String commonSubstring = "Time is: " + (dif / 1000) + " microseconds.";
        listLCS.add(new SuffixModel(0, 0, commonSubstring));
        return listLCS;
    }

    /**
     * This is main method is used for creating suffix array.
     *
     * @param aTriple
     * @param iOfPreTriple
     * @return
     */
    public int[] buildSuffixArray(String[] aTriple, int[] iOfPreTriple) {
        // Length of aTriple = length of String

        int lens = aTriple.length, n = lens - (lens / 3);
        int[] i_OfPreTriple = new int[n];
        int[] tempTriple = new int[n];  // Array of indexes at position i mod 3 != 0 (1 4...2 5...)
        char[] lex = new char[n];       // Array of lexicalgraphical name: a b c....
        int[] iver = new int[lens + 1]; // this array is an inverse array of tempTriple

        computeTriple(lens, i_OfPreTriple, tempTriple, iver);
        radixSort(tempTriple, aTriple, 3);
        assignLexicographicalName(aTriple, tempTriple, lex);

        if (iOfPreTriple.length == 1 && isDuplicate(lex) == false) {
            int[] s0 = new int[lens / 3];
            buildS0(lens, s0, tempTriple);
            if (s0.length > 1) {
                count_sort_original(s0, aTriple, 0);
            }
            return mergeArray(aTriple, iOfPreTriple, s0, tempTriple);
        } else if (isDuplicate(lex) == false) {
            int[] s0 = new int[lens / 3];
            buildS0(lens, s0, tempTriple);
            if (s0.length > 1) {
                count_sort_original(s0, aTriple, 0);
            }
            int[] tempArray = mergeArray(aTriple, iOfPreTriple, s0, tempTriple);
            return tempArray;
        } else {
            char[] atemp = new char[tempTriple.length];
            for (int x = 0; x < tempTriple.length; x++) {
                atemp[iver[tempTriple[x]]] = lex[x];
            }
            // Array atemp is an array char of String 's' in every recursion, s+="###";
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(atemp);
            stringBuilder.append("###");
            String s_triple = stringBuilder.toString();
            String[] a_Triple = new String[atemp.length];
            for (int i = 0; i < s_triple.length() - 3; i++) {
                a_Triple[i] = s_triple.substring(i, i + 3);
            }
            int[] s0 = new int[lens / 3];
            buildS0(lens, s0, buildSuffixArray(a_Triple, i_OfPreTriple));
            if (s0.length > 1) {
                count_sort_original(s0, aTriple, 0);
            }
            return mergeArray(aTriple, iOfPreTriple, s0, buildSuffixArray(a_Triple, i_OfPreTriple));
        }
    }

    /**
     * This method is used to make array of triple in order: 1 4 7 ... 2 5 8 ...
     *
     * @param lens
     * @param i_OfPreTrip
     * @param triple
     * @param iv
     */
    public void computeTriple(int lens, int[] i_OfPreTrip, int[] triple, int[] iv) {

        int k1_max, k2_max, i = 0, j = 0;

        if (lens % 3 == 0) {
            k1_max = lens - 2;
            k2_max = lens - 1;
        } else if (lens % 3 == 2) {
            k1_max = lens - 1;
            k2_max = lens;
        } else {
            k1_max = lens;
            k2_max = lens - 2;
        }
        int bound = k1_max;
        while (i < bound) {
            iv[(i + 1)] = j;
            triple[j] = (i + 1);
            i_OfPreTrip[j] = (i + 1);
            j++;
            if (i == k1_max - 1) {
                bound = k2_max;
                i = 1;
            } else {
                i += 3;
            }
        }
    }

    /**
     * This method is used for merging S0 and Array of triples
     *
     * @param t
     * @param iOfPreTriple
     * @param a
     * @param b
     * @return
     */
    public int[] mergeArray(String[] t, int[] iOfPreTriple, int[] a, int[] b) {
        int i = 0, j = 0, k = 0, x = 0, n = a.length + b.length;
        int[] finalArr = new int[n];
        int[] iSA = new int[n + 1];
        boolean isNotDone = true;
        while (x < b.length) {
            iSA[b[x]] = x;
            x++;
        }

        while (isNotDone) {
            if (i > (a.length - 1)) {
                finalArr[k++] = b[j++];
            } else if (j > (b.length - 1)) {
                finalArr[k++] = a[i++];
            } else // Case 1: j % 3 = 1;
            if (b[j] % 3 == 1) {
                // The first component
                char first = t[a[i] - 1].charAt(0), second = t[b[j] - 1].charAt(0);
                if (first != second) {
                    if (first < second) {
                        finalArr[k++] = a[i++];// mean a before b
                    } else {
                        finalArr[k++] = b[j++];
                    }
                } else // Otherwise compare using The second component
                if ((a[i] + 1) > n) {
                    finalArr[k++] = a[i++];
                } else if ((b[j] + 1) > n) {
                    finalArr[k++] = b[j++];
                } else {
                    int iA = iSA[a[i] + 1], iB = iSA[b[j] + 1];
                    if (iA < iB) {
                        finalArr[k++] = a[i++];
                    } else {
                        finalArr[k++] = b[j++];
                    }
                }
            } else if (b[j] % 3 == 2) { // Case 2: j % 3 = 2;
                // The first component
                char first = t[a[i] - 1].charAt(0), second = t[b[j] - 1].charAt(0);
                if (first != second) {
                    if (first < second) {
                        finalArr[k++] = a[i++];// mean a before b
                    } else {
                        finalArr[k++] = b[j++];
                    }
                } else {
                    // The second component
                    char firstA = t[a[i] - 1].charAt(1), secondB = t[b[j] - 1].charAt(1);
                    if (firstA != secondB) {
                        if (firstA < secondB) {
                            finalArr[k++] = a[i++];// mean a before b
                        } else {
                            finalArr[k++] = b[j++];
                        }
                    } else // The third component
                    if ((a[i] + 2) > n) {
                        finalArr[k++] = a[i++];
                    } else if ((b[j] + 2) > n) {
                        finalArr[k++] = b[j++];
                    } else {
                        int iA = iSA[a[i] + 2], iB = iSA[b[j] + 2];
                        if (iA < iB) {
                            finalArr[k++] = a[i++];
                        } else {
                            finalArr[k++] = b[j++];
                        }
                    }
                }
            }
            if ((i > a.length - 1) && j > (b.length - 1)) {
                isNotDone = false;
            }
        }
        replaceIndex(finalArr, iOfPreTriple);
        return finalArr;
    }

    /**
     * This method is used for finding longest common prefix of string 1 and
     * string 2.
     *
     * @param str_1
     * @param str_2
     * @return
     */
    public static String lcp(char[] str_1, char[] str_2) {
        StringBuilder sb = new StringBuilder();
        int n;
        if (str_1.length > str_2.length) {
            n = str_2.length;
        } else {
            n = str_1.length;
        }
        for (int i = 0; i < n; i++) {
            if (str_1[i] == str_2[i]) {
                sb.append(str_1[i]);
            } else {
                break;
            }
        }
        return String.valueOf(sb);
    }

    /**
     * This method is used for finding longest common prefix from suffix array.
     *
     * @param input
     * @param lenA
     * @param suffixArray
     * @return
     */
    public static List<SuffixModel> findMaxLCP(String[] input, int lenA, int[] suffixArray) {
        List<SuffixModel> maxLCP = new ArrayList<>();
        for (int i = 0; i < suffixArray.length - 1; i++) {
            if (isNotBelongToTheSame(lenA, suffixArray[i], suffixArray[i + 1])) {
                String first = input[suffixArray[i] - 1];
                String second = input[suffixArray[i + 1] - 1];
                String lcp_temp = lcp(first.toCharArray(), second.toCharArray());

                if (maxLCP.isEmpty()) {
                    //if(lcp_temp.length() > 1) {
                    if (suffixArray[i] <= lenA) {
                        maxLCP.add(new SuffixModel(suffixArray[i] - 1, suffixArray[i + 1] - 2 - lenA, lcp_temp));
                    } else {
                        maxLCP.add(new SuffixModel(suffixArray[i + 1] - 1, suffixArray[i] - 2 - lenA, lcp_temp));
                    }
                    // }
                } else  {//if(lcp_temp.length() > 1){
               
                    if (lcp_temp.length() > maxLCP.get(0).getSuffix().length()) {
                        maxLCP.clear();
                        if (suffixArray[i] <= lenA) {
                            maxLCP.add(new SuffixModel(suffixArray[i] - 1, suffixArray[i + 1] - 2 - lenA, lcp_temp));
                        } else {
                            maxLCP.add(new SuffixModel(suffixArray[i + 1] - 1, suffixArray[i] - 2 - lenA, lcp_temp));
                        }
                    } else if (lcp_temp.length() == maxLCP.get(0).getSuffix().length()) {
                        if (isDup(lcp_temp.toCharArray(), maxLCP) == false) {
                            if (suffixArray[i] <= lenA) {
                                maxLCP.add(new SuffixModel(suffixArray[i] - 1, suffixArray[i + 1] - 2 - lenA, lcp_temp));
                            } else {
                                maxLCP.add(new SuffixModel(suffixArray[i + 1] - 1, suffixArray[i] - 2 - lenA, lcp_temp));
                            }
                        }
                    } // }
                }
            }
        }
        return maxLCP;
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean isDup(char[] a, List<SuffixModel> b) {

        for (int i = 0; i < b.size(); i++) {
            if (beEqual(a, b.get(i).getSuffix()) == true) {
                return true;
            }
        }
        return false;
    }

    public static boolean beEqual(char[] first, String second) {
        // Return FALSE if not the same

        for (int i = 0; i < first.length; i++) {
            if (first[i] != second.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method is used for checking if two suffixes belong to two different
     * input strings or not.
     *
     * @param lenA
     * @param a
     * @param b
     * @return
     */
    public static boolean isNotBelongToTheSame(int lenA, int a, int b) {
        // Return TRUE if a # b, else FALSE

        if ((a <= lenA) && (b > lenA + 1)) {
            return true;
        } else {
            return ((a > lenA + 1) && (b <= lenA));
        }
    }

    /**
     * Every times in recursion, after we merge S0 and Array of triple with
     * index is sorted in order. We must replace index, so that it can be merged
     * with previous S0.
     *
     * @param finalArray: Array need to be replaced index.
     * @param iOfPreTriple: Every value of this array is index of Previous
     * triple.
     */
    public void replaceIndex(int[] finalArray, int[] iOfPreTriple) {
        if (iOfPreTriple.length > 1) {
            int[] tp = new int[finalArray.length];
            for (int r = 0; r < finalArray.length; r++) {
                tp[r] = finalArray[r] - 1;
            }
            for (int r = 0; r < finalArray.length; r++) {
                finalArray[r] = iOfPreTriple[tp[r]];
            }
        }
    }

    /**
     * This method is used for assign lexicographical name for Array of triples
     *
     * @param a: Array of indexes at position i mod 3 != 0.
     * @param t: Array triples is created from input string.
     * @param lexArr
     */
    public void assignLexicographicalName(String[] t, int[] a, char[] lexArr) {
        int n = a.length;
        int lex = 97;
        lexArr[0] = ((char) lex);
        for (int i = 1; i < n; i++) {
            if (isTheSame(t[a[i - 1] - 1].toCharArray(), t[a[i] - 1].toCharArray(), 3) == true) {
                lexArr[i] = ((char) lex);
            } else {
                lex++;
                lexArr[i] = ((char) lex);
            }
        }
    }

    /**
     * This method is used for check is 2 triples are the same.
     *
     * @param a: The first triple.
     * @param b: The second triple.
     * @param d: Length of triple.
     * @return
     */
    public boolean isTheSame(char[] a, char[] b, int d) {
        for (int i = 0; i < d; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * This method is used for checking if Lexicographical name of triples is
     * duplicate or not.
     *
     * @param s: Array of triple, contains one attribute is lexicographical
     * name.
     * @return
     */
    public static boolean isDuplicate_improve(char[] s) {
        HashMap<Integer, Integer> countDuplicate = new HashMap<>();
        int value;
        for (int i = 0; i < s.length; i++) {
            countDuplicate.put((int)s[i], 0);
        }
        
        for (int i = 0; i < s.length; i++) {
            value = countDuplicate.get((int)s[i]) + 1;
            countDuplicate.put((int)s[i], value);
            if(value > 1){
                return true;
            }
        }
        return false;
    }
    
    public static boolean isDuplicate(char[] s) {
       Set<Character> dupes = new HashSet<>(); 
       for (char i : s) { 
           if (!dupes.add(i)) { 
               return true;
           }
       } 
       return false;
    }
    

    /**
     * This method is used for creating array s0 based on array of triples.
     *
     * @param len: length of input String.
     * @param s0: array s0.
     * @param tempTriple: Array with index of triples is sorted.
     */
    public void buildS0(int len, int[] s0, int[] tempTriple) {
        int x = 0;
        if (len % 3 == 0) {
            s0[x++] = len;
        }
        for (int i = 0; i < tempTriple.length; i++) {
            if ((tempTriple[i] - 1) % 3 == 0 && (tempTriple[i] - 1) > 0) {
                s0[x++] = tempTriple[i] - 1;
            }
        }
    }

    /**
     * This method is used for sorting triples in alphabetical order. Length of
     * triple is 3. So we can sort array triple in linear time.
     *
     * @param a: Array of indexes at position i mod 3 != 0
     * @param t: Array of triples is created from input string (from 0 to n - 1)
     * @param len: Length of triple (length is 3)
     */
    public static void radixSort(int[] a, String[] t, int len) {
        
        int n = a.length;
	int output[] = new int[n];
        char arr[] = new char[n];
	int count[] = new int[K_RANGE];
        
        for (int j = len - 1; j >= 0; j--) {
            
            for(int i = 0; i < K_RANGE; i++){
		count[i] = 0;
            }
            for(int i = 0; i < n; i++){
                arr[i] = t[ a[i] - 1 ].charAt(j);
		count[ arr[i] ]++;
            }
            for(int i = 1; i < K_RANGE; i++){
		count[i] = count[i] + count[i - 1];
            }
            for(int i = n - 1; i >= 0; i--){
		output[ count[ arr[i] ] - 1] = a[i];
		count[ arr[i] ]--;
            }
            for(int i = 0; i < n; i++){
		a[i] = output[i];
            }
        }
    }

    public static void radixSort_improve(int[] a, String[] t, int len) {
        // Not improve about the time, it's about the length of string and the range of characters has to be sorted.
        for (int j = len - 1; j >= 0; j--) {
            counting_sort_improve(a, t, j);
        }
    }
    
    /**
     * This method is used for sorting S0 base on array triples.
     *
     * @param s0: Array S0 need to be sorted.
     * @param t: Array of triple. [0...length - 1] (length of input)
     * @param idx
     */
    public static void counting_sort_improve(int[] s0, String[] t, int idx) {

        int n = s0.length;
        int output[] = new int[n];
        int arr[] = new int[n];
        int previousValue = 0;
        int value;
        
        HashMap<Integer, Integer> count = new HashMap<>();
        
        for (int i = 0; i < n; i++) {
            arr[i] = (int)t[s0[i] - 1].charAt(idx);
            count.put(arr[i], 0);
        }

        for (int i = 0; i < n; i++) {
            value = count.get(arr[i]) + 1;
            count.put(arr[i], value);
        }
        
        TreeMap<Integer, Integer> countSorted = new TreeMap<>(count);
        
        for (Entry<Integer, Integer> key : countSorted.entrySet()) {
            countSorted.put(key.getKey(), key.getValue() + previousValue);
            previousValue = key.getValue();
        }
        for (int i = n - 1; i >= 0; i--) {
            output[countSorted.get(arr[i]) - 1] = s0[i];
            countSorted.put(arr[i], countSorted.get(arr[i]) - 1);
        }
        
        for (int i = 0; i < n; i++) {
            s0[i] = output[i];
        }
    }
    
     public static void count_sort_original(int []s0, String []t, int idx){
        
	int n = s0.length;
	int output[] = new int[n];
        char arr[] = new char[n];
	int count[] = new int[K_RANGE];
	
	for(int i = 0; i < K_RANGE; i++){
            count[i] = 0;
        }
	for(int i = 0; i < n; i++){
            arr[i] = t[ s0[i] - 1 ].charAt(idx);
            count[ arr[i] ]++;
        }
	for(int i = 1; i < K_RANGE; i++){
            count[i] = count[i] + count[i - 1];
        }
	for(int i = n - 1; i >= 0; i--){
            output[ count[ arr[i] ] - 1] = s0[i];
            count[ arr[i] ]--;
	}
	for(int i = 0; i < n; i++){
            s0[i] = output[i];
	}
    }
}
