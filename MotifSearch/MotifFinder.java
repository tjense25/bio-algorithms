package MotifSearch;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by tjense25 on 9/14/17.
 */
public class MotifFinder {
    private static String USAGE = "USAGE: java Main input_file\n input_file is the path to a file with given input";
    private String dnaText;
    private int k;
    private double[][] profile_matrix;

    public MotifFinder(String dnaText, int k, double[][] profile_matrix) {
        this.dnaText = dnaText;
        this.k = k;
        this.profile_matrix = profile_matrix;
    }

    public static void main(String[] args) {
        if(args.length != 1) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0]));
                String dnaText = scan.next();
                int k = scan.nextInt();
                double[][] profile_matrix = new double[4][k];
                for(int i = 0; i < 4; i++) {
                    for(int j = 0; j < k; j++) {
                        profile_matrix[i][j] = scan.nextDouble();
                    }
                }
                MotifFinder motifFinder= new MotifFinder(dnaText, k, profile_matrix);
                System.out.println(motifFinder.findMotif());
            }
            catch(FileNotFoundException e) {
                System.out.println(USAGE);
            }

        }
    }

    public String findMotif() {
        String motif = dnaText.substring(0, k);
        double best_p = 0;
        for(int i = 0; i < dnaText.length() - k + 1; i++) {
            String kmer = dnaText.substring(i, i + k);
            double kmer_p = 1;
            for(int j = 0; j < k; j++) {
                char base = kmer.charAt(j);
                kmer_p *= profile_matrix[GenomeMapping.Util.BaseToInt(base)][j];
            }
            if(kmer_p > best_p) {
                best_p = kmer_p;
                motif = kmer;
            }
        }
        return motif;
    }
}
