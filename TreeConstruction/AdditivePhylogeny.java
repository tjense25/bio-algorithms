package TreeConstruction;

import javafx.util.Pair;

import java.io.*;
import java.util.*;

/**
 * Created by tjense25 on 10/24/17.
 */
public class AdditivePhylogeny {
    private static String USAGE = "java AdditivePhylogeny [-option] infile.txt outfile.txt";
    private int n;
    private int[][] distance_matrix;
    private Set<Integer> leaf_nodes;
    private Map<Integer, Map<Integer, Integer>> tree;

    public AdditivePhylogeny(int n, Map<Integer, Map<Integer, Integer>> tree) {
        this.n = n;
        this.tree = tree;
        this.leaf_nodes = new HashSet<>();
    }

    public AdditivePhylogeny(int n, int[][] distance_matrix) {
        this. n = n;
        this.distance_matrix = distance_matrix;
    }


    private void findLeafNodes() {
        for(int i : tree.keySet()) {
            if(tree.get(i).size() == 1) leaf_nodes.add(i);
        }
    }

    private void fillDistanceMatrix(int i, int j, int total, Set<Integer> visited) {
        visited.add(j);
        if(i == j) {
            distance_matrix[i][j] = 0;
        }
        else if(leaf_nodes.contains(j)) {
            distance_matrix[i][j] = total;
            distance_matrix[j][i] = total;
            return;
        }
        for(int k : tree.get(j).keySet()) {
            if(visited.contains(k)) continue;
            fillDistanceMatrix(i, k, total + tree.get(j).get(k), visited);
        }
    }

    public int[][] getDistanceMatrix() {
        if(distance_matrix != null) return distance_matrix;
        findLeafNodes();
        distance_matrix = new int[n][n];
        Set<Pair<Integer, Integer>> nodepairs = new HashSet<>();
        for(int i : leaf_nodes) {
            Set<Integer> visited = new HashSet<>();
            fillDistanceMatrix(i, i, 0, visited);
        }
        return distance_matrix;
    }

    private int getLimbLength(int j) {
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < n; i++) {
            if(i == j) continue;
            for(int k = 0; k < n; k++) {
                if(k == j || k == i) continue;
                int val = (distance_matrix[i][j] + distance_matrix[j][k] - distance_matrix[i][k])/2;
                if(val < min) min = val;
            }
        }
        return min;
    }

    public Map<Integer, Map<Integer, Integer>> getAdditivePhylogeny(int[][] D, int n) {
        if(n == 2) {
            Map<Integer, Map<Integer, Integer>> additive_phylogeny = new TreeMap<>();
            Map<Integer, Integer> edge1 = new TreeMap<>();
            edge1.put(0, D[0][1]);
            additive_phylogeny.put(0, edge1);
            return additive_phylogeny;
        }
        int limbLength = getLimbLength(n - 1);
        for(int i = 0; i < n; i++) {
            D[i][n-1] -= limbLength;
            D[n - 1][i] = D[i][n - 1];
        }
        int k = 0;
        int l = 0;
        int[][] new_D = new int[n - 1][n - 1];
        for(int i = 0; i < n - 1; i++) {
            for(int j = 0; j < n - 1; j++) {
                new_D[i][j] = D[i][j];
                if(D[i][j] == D[i][n - 1] + D[n -1][j]) {
                    k = i;
                    l = j;
                }
            }
        }
        int x = D[k][n - 1];
        int i = k;
        Map<Integer, Map<Integer, Integer>> additive_phylogeny = getAdditivePhylogeny(new_D, n - 1);
        int old_length = additive_phylogeny.get(k).get(l);
            Map<Integer, Integer> old_edge = additive_phylogeny.get(i);
            old_edge.replace(l)
            Map<Integer, Integer> new_edge = new TreeMap<>();
            new_edge.put(n + 2, x);
            additive_phylogeny.replace(i, new_edge);
            additive_phylogeny.put(n + 2, old_edge);




    }


    private static void addToMap(Map<Integer,Map<Integer, Integer>> adjacencyMap, int node, Pair<Integer, Integer> new_edge) {
        if(adjacencyMap.containsKey(node)) {
            adjacencyMap.get(node).put(new_edge.getKey(), new_edge.getValue());
        }
        else {
            Map<Integer, Integer> edges = new HashMap<>();
            edges.put(new_edge.getKey(), new_edge.getValue());
            adjacencyMap.put(node, edges);
        }
    }

    public static void main(String[] args) {
        if (args.length != 3) System.out.println(USAGE);
        else {
            try {
                if(args[0].equals("-d")) {
                    Scanner scan = new Scanner(new FileReader(args[1])).useDelimiter("((\\s*(->|:)\\s*)|\\s+)");
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    int n = scan.nextInt();
                    Map<Integer, Map<Integer, Integer>> adjacencyMap = new HashMap<>();
                    while(scan.hasNext()) {
                        int node = scan.nextInt();
                        int connected_node = scan.nextInt();
                        int weight = scan.nextInt();
                        addToMap(adjacencyMap, node, new Pair<>(connected_node, weight));
                    }
                    AdditivePhylogeny ap = new AdditivePhylogeny(n, adjacencyMap);
                    int[][] result = ap.getDistanceMatrix();
                    for(int i = 0; i < n; i++) {
                        for(int j = 0; j < n; j++) {
                            out.print(Integer.toString(result[i][j]) + ' ');
                        }
                        out.print('\n');
                    }
                    scan.close();
                    out.close();
                }
                else if(args[0].equals("-l")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    int n = scan.nextInt();
                    int j = scan.nextInt();
                    int[][] distance_matrix = new int[n][n];
                    for(int i = 0; i < n; i++) {
                        for(int k = 0; k < n; k++) {
                            distance_matrix[i][k] = scan.nextInt();
                        }
                    }
                    AdditivePhylogeny ap = new AdditivePhylogeny(n, distance_matrix);
                    out.println(ap.getLimbLength(j));
                    scan.close();
                    out.close();
                }
                else if(args[0].equals("-p")) {
                    Scanner scan = new Scanner(new FileReader(args[1]));
                    PrintWriter out = new PrintWriter(new FileWriter(args[2]));
                    int n = scan.nextInt();
                    int[][] distance_matrix = new int[n][n];
                    for(int i = 0; i < n; i++) {
                        for(int k = 0; k < n; k++) {
                            distance_matrix[i][k] = scan.nextInt();
                        }
                    }
                    AdditivePhylogeny ap = new AdditivePhylogeny(n, distance_matrix);
                }


            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME");
            }
        }
    }
}
