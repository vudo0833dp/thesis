package algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import model.SuffixModel;

public class SuffixTree {
    private int remain;
    private End end;
    private SuffixNode root;
    private Active active;
    private static String string;
    private static String string1;
    private static String string2;
    public static void main(String args[]) {

    }

    public List<SuffixModel> UkkonenAlgorithm(String s1, String s2) {
        List<SuffixModel> result = new ArrayList<>();
        long lStartTime = System.nanoTime();
        SuffixTree st = new SuffixTree();
        st.build(s1, s2);
        long lEndTime2 = System.nanoTime();
        List<List<Integer>> lcs = st.dfsTraversal();
        long lEndTime3 = System.nanoTime();

        String input = s1 + "$" + s2 + "#";
        String longestComonString = "";

        for (int i = 0; i < lcs.size(); i++) {
            longestComonString = "";
            for (int j = 0; j < lcs.get(i).size(); j++) {
                longestComonString += input.charAt(lcs.get(i).get(j));
            }
            result.add(new SuffixModel(s1.indexOf(longestComonString), s2.indexOf(longestComonString), longestComonString));
        }
        longestComonString = ((lEndTime2 - lStartTime) / 1000) + "#" + ((lEndTime3 - lEndTime2) / 1000);
        result.add(new SuffixModel(0, 0, longestComonString));

        return result;
    }

    public void build(String s1, String s2) {
        string1 = s1 + "$";
        string2 = s2 + "#";
        string = string1 + string2;
        
        remain = 0;
        root = SuffixNode.createNode(1, new End(0));
        active = new Active(root);
        this.end = new End(-1);
        for (int i = 0; i < string.length(); i++) {
            SuffixNode lastCreatNode = null;
            remain++;
            end.end++;
            while (remain > 0) {
                // searching from root
                if (active.activeLength == 0) {
                    // check current character from root is'nt null => Rule 3
                    // R3 : inc ActiveLenght
                    if (active.activeNode.child.get(string.charAt(i)) != null) {
                        // rule 3 and strick 2
                        active.activeEdge = active.activeNode.child.get(string.charAt(i)).start;
                        active.activeLength++;
                        break;
                    } else {
                        // rule 2 : create new leaf node
                        root.child.put(string.charAt(i), SuffixNode.createNode(i, end));
                        remain--;
                        root.child.get(string.charAt(i)).isLeaf = 1;
                    }
                } else {
                    // AL !=0 => in middle.
                    try {
                        char nextChar = nextChar(i);
                        if (nextChar == string.charAt(i)) {
                            // Rule 3 => stop
                            if (lastCreatNode != null) {
                                lastCreatNode.suffixLink = selectChild();
                            }
                            walkDown(i);
                            break;
                        } else {
                            // Rule 2 i
                            SuffixNode node = selectChild();
                            int oldStart = node.start;
                            node.start = node.start + active.activeLength;
                            SuffixNode newNode = SuffixNode.createNode(oldStart,
                                    new End(oldStart + active.activeLength - 1));
                            SuffixNode newLeaf = SuffixNode.createNode(i, end); 
                            newNode.child.put(string.charAt(i), newLeaf);
                            newNode.child.get(string.charAt(i)).isLeaf = 1;
                            newNode.child.put(string.charAt(newNode.start + active.activeLength), node);
                            active.activeNode.child.put(string.charAt(newNode.start),newNode);
                            if (lastCreatNode != null) {
                                lastCreatNode.suffixLink = newNode;
                            }
                            lastCreatNode = newNode;
                            newNode.suffixLink = root;
                            if (active.activeNode != root) {
                                active.activeNode = active.activeNode.suffixLink;
                            } else {
                                active.activeEdge = active.activeEdge + 1;
                                active.activeLength--;
                            }
                            remain--;
                        }
                    } catch (EndOfPathException e) {
                        // this happens when we are looking for new character
                        // from
                        // end of current path edge. Here we already have
                        // internal
                        // node so
                        // we don't have to create new internal node. Just
                        // create a
                        // leaf node from here and move to suffix new link.
                        SuffixNode node = selectChild();
                        node.child.put(string.charAt(i), SuffixNode.createNode(i, end));
                        node.child.get(string.charAt(i)).isLeaf = 1;
                        if (lastCreatNode != null) {
                            lastCreatNode.suffixLink = node;
                        }
                        lastCreatNode = node;
                        // if active node is not root then follow suffix link
                        if (active.activeNode != root) {
                            active.activeNode = active.activeNode.suffixLink;
                        } // if active node is root then increase active index by
                        // one
                        // and decrease active length by 1
                        else {
                            active.activeEdge = active.activeEdge + 1;
                            active.activeLength--;
                        }
                        remain--;
                    }
                }
            }
        }
        boolean[] check = {false, false};
        setCommonNode(root);
    }

    private SuffixNode selectChild() {
        return active.activeNode.child.get(string.charAt(active.activeEdge));
    }

    private SuffixNode selectChildIndex(int i) {
        return active.activeNode.child.get(string.charAt(i));
    }

    private char nextChar(int index) throws EndOfPathException {
        SuffixNode node = selectChild();
        if (lengthOfEdge(node) >= active.activeLength) {
            return string.charAt(active.activeNode.child.get(string.charAt(active.activeEdge)).start + active.activeLength);
        }
        if ((lengthOfEdge(node) + 1) == active.activeLength) {
            if (node.child.get(string.charAt(index)) != null) {
                return string.charAt(index);
            }
        } else {
            active.activeNode = node;
            active.activeLength = active.activeLength - lengthOfEdge(node) - 1;
            active.activeEdge = active.activeEdge + lengthOfEdge(node) + 1;
            return nextChar(index);
        }
        throw new EndOfPathException();
    }

    private void walkDown(int i) {
        SuffixNode node = selectChild();
        if (lengthOfEdge(node) < active.activeLength) {
            active.activeNode = node;
            active.activeLength = active.activeLength - lengthOfEdge(node);
            active.activeEdge = node.child.get(string.charAt(i)).start;
        } else {
            active.activeLength++;
        }
    }

    private static class EndOfPathException extends Exception {

    }

    private int lengthOfEdge(SuffixNode node) {
        return node.end.end - node.start;
    }

    private int setCommonNode(SuffixNode root) {
        if (root == null) {
            return -1;
        }
        int n = 0;
        SuffixNode node;
	for (Entry<Character, SuffixNode> entry : root.child.entrySet()) {
            node = entry.getValue();
            //if (node != null) {
                n = setCommonNode(node);
                if (n == 1) {
                    root.check[1] = true;
                } else if (n == 0) {
                    root.check[0] = true;
                }
            //}
        }
        if (root.start > string1.length()) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Do a DFS traversal of the tree.
     *
     * @return List<List<Integer>>
     */
    public List<List<Integer>> dfsTraversal() {
        List<Integer> label = new ArrayList<>();
        List<List<Integer>> lcs = new ArrayList<>();
        int length = 0;
        SuffixNode node;
	for (Entry<Character, SuffixNode> entry : root.child.entrySet()) {
            node = entry.getValue();
            //if (node != null) {
                dfsTraversal(node, label, lcs, length);
            //}
        }
        return lcs;
    }

    private List<List<Integer>> dfsTraversal(SuffixNode root, List<Integer> label, List<List<Integer>> lcs, int length) {
        if (root.isLeaf == 1)
            return lcs;
        else {
            //System.out.println(string.charAt(root.start)+" "+string.charAt(root.end.end)+" c["+root.check[0]+","+root.check[1]+"]");
            for (int i = root.start; i <= root.end.end; i++) {
                label.add(i);
            }
            if (lcs.size() > 0) {
                length = lcs.get(0).size();
            }
            if (label.size() >= length) {
                if ((root.check[0]) && (root.check[1])) {
                    if (label.size() > length) 
                        lcs.clear();
                    List<Integer> a = new ArrayList<>();
                    for (int j = 0; j < label.size(); j++) {
                        a.add(label.get(j));
                    }
                    lcs.add(a);
                    length = label.size();
                }
            }
            SuffixNode node;
            for (Entry<Character, SuffixNode> entry : root.child.entrySet()) {
                node = entry.getValue();
                //if (node != null) {
                    dfsTraversal(node, label, lcs, length);
                //}
            }
            for (int i = root.start; i <= root.end.end; i++) {
                label.remove(label.size() - 1);
            }
            return lcs;
        }
    }

}

class SuffixNode {
    private SuffixNode() {
    }
    HashMap <Character,SuffixNode> child = new HashMap();
    int isLeaf = 0;
    int start;
    End end;
    boolean[] check = {false, false};
    SuffixNode suffixLink;
    public static SuffixNode createNode(int start, End end) {
        SuffixNode node = new SuffixNode();
        node.start = start;
        node.end = end;
        return node;
    }
}
    
class Active {
    Active(SuffixNode node) {
        activeLength = 0;
        activeNode = node;
        activeEdge = -1;
    }
    SuffixNode activeNode;
    int activeEdge;
    int activeLength;
}

class End {
    public End(int end) {
        this.end = end;
    }
    int end;
}
