package GenomeAssembly;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by tjense25 on 9/26/17.
 */
public class StringReconstructor {
    private static String USAGE = "USAGE: java StringReconstructer input_file_name output_file_name\n " +
            "input_file is the path to the file with given input\n " +
            "output_file is the path to the file where output is to be written";
    private int k;
    private List<String> kmers;

    public static void main(String[] args) {
        if (args.length != 2) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0]));
                int k = scan.nextInt();
                List<String> kmers = new ArrayList<>();
                while(scan.hasNext()) {
                    kmers.add(scan.next());
                }
                scan.close();
                StringReconstructor reconstructor = new StringReconstructor(kmers);
                String text = reconstructor.reconstructString();
                PrintWriter writer = new PrintWriter(new FileWriter(args[1]));
                writer.println(text);
                writer.close();

            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME");
            }
        }
    }

    public StringReconstructor(List<String> kmers) {
        this.kmers = kmers;
        k = kmers.get(0).length();
    }


    private String reconstructOrderedString(List<String> patterns) {
        StringBuilder text = new StringBuilder();
        if(patterns.size() != 0) {
            text.append(patterns.get(0));
            for(int i = 1; i < patterns.size(); i++) {
                String current_pattern = patterns.get(i);
                String previous_pattern = patterns.get(i - 1);
                if(Util.getSuffix(previous_pattern).equals(Util.getPrefix(current_pattern))) {
                    text.append(current_pattern.charAt(current_pattern.length() - 1));
                }
            }
        }
        return text.toString();
    }

    public String reconstructString() {
        DeBruijnGraph deBruijnGraph = new DeBruijnGraph(kmers);
        Graph graph = new Graph(deBruijnGraph.getAdjacencyMap());
        try {
            List<String> orderedKmers = graph.getEulerianResult();
            return reconstructOrderedString(orderedKmers);
        } catch(Graph.NoEulerianPathException ex) { System.out.println("ERROR: given Kmers cannot be reconstructed"); }

        return null;
    }
}