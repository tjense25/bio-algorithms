package GenomeAssembly;

import javafx.util.Pair;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Created by tjense25 on 9/26/17.
 */
public class OverlapGraph {
    private static String USAGE = "USAGE: java OverlapGraph input_file_name output_file_name\n " +
            "input_file is the path to the file with given input\n " +
            "output_file is the path to the file where output is to be written";
    private int k = 0;
    private List<Pair<String, String>> adjacencyList = new ArrayList<>();

    public OverlapGraph(List<String> patterns) {
        if(patterns.size() > 0) this.k = patterns.get(0).length();
        constructOverlapGraph(patterns);
    }

    public static void main(String[] args) {
        if (args.length != 2) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0]));
                List<String> patterns = new ArrayList<>();
                while(scan.hasNext()) {
                    patterns.add(scan.next());
                }
                scan.close();
                OverlapGraph overlapGraph = new OverlapGraph(patterns);
                PrintWriter writer = new PrintWriter(new FileWriter(args[1]));
                writer.print(overlapGraph.toString());
                writer.close();

            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME");
            }
        }
    }


    private void constructOverlapGraph(List<String> patterns) {
        for(int i = 0; i < patterns.size(); i++) {
            for(int j = i + 1; j < patterns.size(); j++) {
                if(Util.getSuffix(patterns.get(i)).equals(Util.getPrefix(patterns.get(j)))) {
                    adjacencyList.add(new Pair<>(patterns.get(i), patterns.get(j)));
                }
                /*if(Util.getSuffix(patterns.get(j)).equals(Util.getPrefix(patterns.get(i)))) {
                    adjacencyList.add(new Pair<>(patterns.get(j), patterns.get(i)));
                }*/
            }
        }
        adjacencyList.sort(new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> o1, Pair<String, String> o2) {
                if(o1.getKey().equals(o2.getKey())) return o2.getValue().compareTo(o2.getValue());
                else return o1.getKey().compareTo(o2.getKey());
            }
        });
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < adjacencyList.size(); i++) {
            Pair<String, String> edge = adjacencyList.get(i);
            builder.append(edge.getKey() + " -> " + edge.getValue() + "\n");
        }
        return builder.toString();
    }
}
