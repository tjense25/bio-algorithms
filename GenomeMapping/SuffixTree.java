package GenomeMapping;

import java.io.*;
import java.util.*;

/**
 * Created by tjense25 on 11/7/17.
 */
public class SuffixTree {
    private static String USAGE = "ERROR. USAGE:\n" +
            "java SuffixTree [-option] input_data.txt output_file.txt\n" +
            "Options:\n" +
            "-t Construct the suffix tree of a given text\n" +
            "-f find the longest repeating string in a given text\n";
    public static int NOT_LEAF = -1;
    private Node root;
    private String longest = "";
    private String shortest;


    public class Node {
        public String word;
        public int index;
        public boolean shared_substring;
        int parent;
        public Map<String, Node> edges;

        public Node(int index, String word) {
            this.index = index;
            this.word = word;
            this.edges = new HashMap<>();
        }

        public void addSuffix(String suffix, int index, int i) {
            String edge_to_change = null;
            String new_edge = null;
            for (String edge : edges.keySet()) {
                if (edge.charAt(0) == suffix.charAt(i)) {
                    if (suffix.substring(i).startsWith(edge)) {
                        edges.get(edge).addSuffix(suffix, index, i + edge.length());
                        return;
                    }
                    edge_to_change = edge;
                    new_edge = Util.getSharedPrefix(suffix.substring(i), edge);
                    break;
                }
            }
            if (edge_to_change == null) {
                Node new_node = new Node(index, suffix);
                this.edges.put(suffix.substring(i), new_node);
            } else {
                Node old_node = edges.get(edge_to_change);
                edges.remove(edge_to_change);
                Node new_node = new Node(NOT_LEAF, suffix.substring(0, i + new_edge.length()));
                new_node.edges.put(edge_to_change.substring(new_edge.length()), old_node);
                edges.put(new_edge, new_node);
                edges.get(new_edge).addSuffix(suffix, index, i + new_edge.length());
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String edge : edges.keySet()) {
                sb.append(edge + "\n");
                sb.append(edges.get(edge).toString());
            }
            return sb.toString();
        }

        public String findLargestRepeat() {
            if (edges.size() == 0) return "";
            else if (this.word.length() > longest.length()) longest = this.word;
            for (String edge : edges.keySet()) {
                edges.get(edge).findLargestRepeat();
            }
            return longest;
        }

        public String findLargestSharedRepeat() {
            if (!shared_substring) return "";
            else if(this.word.contains("$")) return "";
            else if (this.word.length() > longest.length()) longest = this.word;
            for (String edge : edges.keySet()) {
                edges.get(edge).findLargestSharedRepeat();
            }
            return longest;
        }

        public String findShortestNonShared() {
           for(String edge : edges.keySet()) {
               if(edge.contains("$") || edge.contains("1") || edge.contains("2")) continue;
               else if(!edges.get(edge).shared_substring) {
                   if(edges.get(edge).parent == 2) continue;
                   else if(edges.get(edge).word.length() < shortest.length()) shortest = edges.get(edge).word;
               }
               else edges.get(edge).findShortestNonShared();
           }
           return shortest;
        }

        public boolean findShared() {
            if (edges.size() == 0) {
                this.shared_substring = false;
                return false;
            }
            for (String edge : edges.keySet()) {
                if (edges.get(edge).findShared()) {
                    this.shared_substring = true;
                }
            }
            if(shared_substring) return true;
            String str = toString();
            if (str.contains("1") && str.contains("2")) {
                this.shared_substring = true;
                return true;
            }
            else {
                if(str.contains("1")) parent = 1;
                else parent = 2;
                this.shared_substring = false;
                return false;
            }
        }
    }

    public SuffixTree(String text) {
        this.root = new Node(NOT_LEAF, "");
        for(int i = 0; i < text.length(); i++) {
            root.addSuffix(text.substring(i), i,0);
        }
    }



    @Override
    public String toString() {
        return root.toString();
    }

    public String findLargestRepeat() {
        return root.findLargestRepeat();
    }

    public void addSuffixes(String text) {
        for(int i = 0; i < text.length(); i++) {
            root.addSuffix(text.substring(i), i, 0);
        }
    }
    public String findLargestSharedSubstring(String text) {
        addSuffixes(text);
        root.findShared();
        return root.findLargestSharedRepeat();
    }

    public String findShortestNonShared(String text) {
        addSuffixes(text);
        root.findShared();
        this.shortest = text;
        return root.findShortestNonShared();
    }


    public static void main(String[] args) {
        if (args.length != 3) System.out.println(USAGE);
        else {
            try {
                if(args[0].equals("-t")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    String text = scan.next();
                    SuffixTree tree = new SuffixTree(text);
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    out.print(tree.toString());
                    scan.close();
                    out.close();
                }
                else if(args[0].equals("-f")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    String text;
                    text = scan.next();
                    text = text + "$";
                    SuffixTree tree = new SuffixTree(text);
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    out.print(tree.findLargestRepeat());
                    scan.close();
                    out.close();
                }
                else if(args[0].equals("-r") || args[0].equals("-n")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    String text1 = scan.next();
                    text1 += "$1";
                    String text2 = scan.next();
                    text2 += "$2";
                    SuffixTree tree = new SuffixTree(text1);
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    if(args[0].equals("-r")) out.print(tree.findLargestSharedSubstring(text2));
                    else out.print(tree.findShortestNonShared(text2));
                    scan.close();
                    out.close();
                }

                else { System.out.println(USAGE); }
            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME\n" + USAGE);
            }
        }
    }
}
