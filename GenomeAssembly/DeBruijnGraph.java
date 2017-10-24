package GenomeAssembly;

import javafx.util.Pair;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by tjense25 on 9/26/17.
 */
public class DeBruijnGraph {
    private static String USAGE = "USAGE: java DeBruijnGraph input_file_name output_file_name option\n " +
            "input_file is the path to the file with given input\n " +
            "output_file is the path to the file where output is to be written\n " +
            "Options: \n" +
            "   -k : input data is a list of Kmers";
    private int k = 0;
    private Map<String,List<String>> adjacencyMap = new HashMap<>();

    public DeBruijnGraph(int k, String text) {
        this.k = k;
        CompositionGenerator generator= new CompositionGenerator(k, text);
        constructDeBruijnGraph(generator.generateKmerComposition());
    }

    public DeBruijnGraph(List<String> kmers) {
        if(kmers.size() > 0) k = kmers.get(0).length();
        constructDeBruijnGraph(kmers);
    }

    public static void main(String[] args) {
        if (args.length != 2 && args.length != 3) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0]));
                DeBruijnGraph deBruijnGraph = null;
                if(args.length == 2) {
                    int k = scan.nextInt();
                    String text = scan.next();
                    deBruijnGraph = new DeBruijnGraph(k, text);
                }
                else if(args[2].equals("-k")) {
                    List<String> kmers = new ArrayList<>();
                    while(scan.hasNext()) {
                        kmers.add(scan.next());
                    }
                    deBruijnGraph = new DeBruijnGraph(kmers);
                }
                else {
                    System.out.println(USAGE);
                    return;
                }
                scan.close();
                PrintWriter writer = new PrintWriter(new FileWriter(args[1]));
                writer.print(deBruijnGraph.toString());
                writer.close();

            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME");
            }
        }
    }


    private void constructDeBruijnGraph(List<String> patterns) {
        for(int i = 0; i < patterns.size(); i++) {
            String pattern = patterns.get(i);
            addEdge(Util.getPrefix(pattern), Util.getSuffix(pattern));
        }
    }

    private void addEdge(String parentNode, String childNode) {
        if(adjacencyMap.containsKey(parentNode)) {
            List<String> childrenNodes = adjacencyMap.get(parentNode);
            childrenNodes.add(childNode);
        }
        else {
            List<String> childrenNodes = new ArrayList<>();
            childrenNodes.add(childNode);
            adjacencyMap.put(parentNode, childrenNodes);
        }
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(String parentNode : adjacencyMap.keySet()) {
            builder.append(parentNode + " -> ");
            List<String> childrenNodes = adjacencyMap.get(parentNode);
            for(int i = 0; i < childrenNodes.size(); i++) {
                if(i != 0) builder.append(",");
                builder.append(childrenNodes.get(i));
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    public Map<String,List<String>> getAdjacencyMap() {
        return adjacencyMap;
    }
}
