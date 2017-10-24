package GenomeAssembly;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by tjense25 on 9/27/17.
 */
public class UniversalStringGenerator {
    private static String USAGE = "USAGE: java Graph input_file_name output_file_name option\n " +
            "input_file is the path to the file with given input\n " +
            "output_file is the path to the file where output is to be written";
    private int k;
    private List<String> binary_strings = new ArrayList<>();
    private String universal_string;

    public UniversalStringGenerator(int k) {
        this.k = k;
        generateAllBinaryStrings();
        this.universal_string = generateUniversalString();
    }

    public static void main(String[] args) {
        if (args.length != 2) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0]));
                int k = scan.nextInt();
                scan.close();
                UniversalStringGenerator generator = new UniversalStringGenerator(k);
                PrintWriter writer = new PrintWriter(new FileWriter(args[1]));
                writer.print(generator.getUniversalString());
                writer.close();

            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME");
            }
        }
    }

    private void generateAllBinaryStrings() {
        StringBuilder bin = new StringBuilder();
        for(int i = 0; i < k; i++) {
            bin.append("0");
        }
        buildBinString(bin, k - 1);
    }

    private void buildBinString(StringBuilder bin, int n) {
        if(n < 0) {
            binary_strings.add(bin.toString());
            return;
        }
        bin.replace(n, n + 1, "0");
        buildBinString(bin, n - 1);
        bin.replace(n, n + 1, "1");
        buildBinString(bin, n - 1);
    }

    private String generateUniversalString() {
        StringReconstructor reconstructor = new StringReconstructor(binary_strings);
        String universal_string = reconstructor.reconstructString();
        /*Since  is circular and therefore a cycle, Algorithm it will repeat the starting kmer at the end
        So We delete this last kmer before returning it*/
        return universal_string.substring(0, universal_string.length() - (k - 1));
    }

    public String getUniversalString() {
        return universal_string;
    }

    public List<String> getAllBinaryStrings() {
        return binary_strings;
    }







}
