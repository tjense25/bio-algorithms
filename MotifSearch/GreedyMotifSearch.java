package MotifSearch;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * Created by tjense25 on 9/14/17.
 */
public class GreedyMotifSearch {
    private static String USAGE = "USAGE: java Main input_file\n input_file is the path to a file with given input";
    private String[] dna;
    private int k;
    private int t;

    public GreedyMotifSearch(String[] dna, int k, int t) {
        this.dna = dna;
        this.k = k;
        this.t = t;
    }

    public static void main(String[] args) {
        if(args.length != 1) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0]));
                int k = scan.nextInt();
                int t = scan.nextInt();
                String[] dna = new String[t];
                for(int i = 0; i < t; i++) {
                    dna[i] = scan.next();
                }
                GreedyMotifSearch main = new GreedyMotifSearch(dna, k, t);
                List<String> motifs = main.runGreedyMotifSearch();
                for(int i = 0; i < motifs.size(); i++) {
                    System.out.println(motifs.get(i));
                }

            }
            catch(FileNotFoundException e) {
                System.out.println(USAGE);
            }

        }
    }

    public List<String> runGreedyMotifSearch() {
        List<String> best_motifs = new ArrayList<>();
        for(int i = 0; i < dna.length; i++) {
            best_motifs.add(dna[i].substring(0, k));
        }
        int best_score = scoreMatrix(best_motifs);
        for(int i = 0; i < dna[0].length() - k + 1; i++) {
            List<String> current_motifs = new ArrayList<>();
            current_motifs.add(dna[0].substring(i, i + k));
            for(int j = 1; j < t; j++) {
                double[][] profile_matrix = createProfileMatrix(current_motifs);
                MotifFinder finder = new MotifFinder(dna[j], k, profile_matrix);
                current_motifs.add(finder.findMotif());
            }
            int current_score = scoreMatrix(current_motifs);
            if(current_score < best_score) {
                best_motifs = current_motifs;
                best_score = current_score;
            }

        }
        return best_motifs;
    }

    public int scoreMatrix(List<String> motifs) {
        int[][] count_matrix = createCountMatrix(motifs);
        int score = 0;
        for(int i = 0; i < k; i++) {
            int max = count_matrix[0][i];
            for(int j = 1; j < 4; j++) {
                max = Math.max(max, count_matrix[j][i]);
            }
            score += t - max;
        }

        return score;
    }

    public double[][] createProfileMatrix(List<String> motifs) {
        int[][] count_matrix = createCountMatrix(motifs);
        double[][] profile_matrix = new double[4][k];
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < k; j++) {
                profile_matrix[i][j] = count_matrix[i][j]/(t + 4.0);
            }
        }
        return profile_matrix;
    }

    private int[][] createCountMatrix(List<String> motifs) {
        int[][] count_matrix = new int[4][k];
        for(int i = 0; i < k; i++) {
            int A_count = 0;
            int C_count = 0;
            int G_count = 0;
            int T_count = 0;
            for(int j = 0; j < motifs.size(); j++) {
                switch(motifs.get(j).charAt(i)) {
                    case 'A' : A_count++; break;
                    case 'C' : C_count++; break;
                    case 'G' : G_count++; break;
                    case 'T' : T_count++; break;
                    default : break;
                }
            }
            count_matrix[0][i] = A_count + 1;
            count_matrix[1][i] = C_count + 1;
            count_matrix[2][i] = G_count + 1;
            count_matrix[3][i] = T_count + 1;
        }
        return count_matrix;
    }


}

