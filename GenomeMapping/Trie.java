package GenomeMapping;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by tjense25 on 11/7/17.
 */
public class Trie {
    private static String USAGE = "ERROR. USAGE:\n" +
            "java Trie [-option] input_file.txt output_file.txt\n" +
            "OPTIONS:\n" +
            "-t: create a Trie from a list of patterns and returns adjacency list\n" +
            "-m: implements Trie matching for a list of patterns and a given text\n";
    private Node root;
    public int node_count;

    public class Node {
        public char b;
        public boolean valid_pattern;
        private int id;
        public String word;
        public Node[] nodes;

        public Node(char b, int id, String word) {
            this.b = b;
            this.word = word;
            this.id = id;
            this.valid_pattern = false;
            this.nodes = new Node[4];
        }

        public void add(String pattern, int i) {
            if(i == pattern.length() - 1) {
                this.valid_pattern = true;
                return;
            }
            char next_b = pattern.charAt(i + 1);
            int index = Util.BaseToInt(next_b);
            if(nodes[index] == null) {
                nodes[index] = new Node(next_b, node_count++, pattern.substring(0, i + 2));
            }
            nodes[index].add(pattern, i + 1);
        }

        public boolean hasMatch(String text, int i) {
            if(valid_pattern) return true;
            else if(i >= text.length()) return false;
            char next_b = text.charAt(i);
            int index = Util.BaseToInt(next_b);
            if(nodes[index] == null) return false;
            else return nodes[index].hasMatch(text, i + 1);
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < nodes.length; i++) {
                if(nodes[i] == null) continue;
                sb.append(String.format("%d->%d:%s\n",this.id, nodes[i].id, Util.IntToBase(i)));
                sb.append(nodes[i].toString());
            }
            return sb.toString();
        }

    }

    public Trie(List<String> patterns) {
        this.node_count = 0;
        this.root = new Node('^', node_count++, "");
        for(String pattern : patterns) {
            root.add(pattern, -1);
        }
    }

    public List<Integer> getMatches(String text) {
        List<Integer> indices = new ArrayList<>();
        for(int i = 0; i < text.length(); i++) {
            if(root.hasMatch(text.substring(i), 0)) {
                indices.add(i);
            }
        }
        return indices;
    }

    @Override
    public String toString() {
        return root.toString();
    }



    public static void main(String[] args) {
        if (args.length != 3) System.out.println(USAGE);
        else {
            try {
                if(args[0].equals("-t")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    List<String> patterns = new ArrayList<>();
                    while(scan.hasNext()) {
                        patterns.add(scan.next());
                    }
                    Trie trie = new Trie(patterns);
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    out.print(trie.toString());
                    scan.close();
                    out.close();
                }
                else if(args[0].equals("-m")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    String text;
                    List<String> patterns = new ArrayList<>();
                    text = scan.next();
                    while(scan.hasNext()) {
                        patterns.add(scan.next());
                    }
                    Trie trie = new Trie(patterns);
                    List<Integer> indices = trie.getMatches(text);
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    for(int i: indices) {
                        out.print(String.format("%d ", i));
                    }
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
