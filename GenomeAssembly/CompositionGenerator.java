package GenomeAssembly;

import MotifSearch.GibbsSampler;

import java.io.*;
import java.util.*;

/**
 * Created by tjense25 on 9/26/17.
 */
public class CompositionGenerator {
    private static String USAGE = "USAGE: java CompositionGenerator input_file_name output_file_name\n " +
            "input_file is the path to the file with given input\n " +
            "output_file is the path to the file where output is to be written";
    private int k;
    private String text;

    public static void main(String[] args) {
        if (args.length != 2) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0]));
                int k = scan.nextInt();
                String text = scan.next();
                scan.close();
                CompositionGenerator generator = new CompositionGenerator(k, text);
                List<String> compositions = generator.generateKmerComposition();
                PrintWriter writer = new PrintWriter(new FileWriter(args[1]));
                for (int i = 0; i < compositions.size(); i++) {
                    writer.println(compositions.get(i));
                }
                writer.close();

            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME");
            }
        }
    }

    public CompositionGenerator(int k, String text) {
        this.k = k;
        this.text = text;
    }

    public List<String> generateKmerComposition() {
        List<String> compositions = new ArrayList<>();
        for(int i = 0; i < text.length() - k + 1; i++) {
            compositions.add(text.substring(i, i + k));
        }
        Collections.sort(compositions, Comparator.naturalOrder());
        return compositions;
    }


}
