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

    public int getLimbLength(int j) {
        return getLimbLength(this.distance_matrix, j);
    }

    private int getLimbLength(int[][] D, int j) {
        int min = Integer.MAX_VALUE;
        for(int i = 0; i < D.length; i++) {
            if(i == j) continue;
            for(int k = 0; k < D.length; k++) {
                if(k == j || k == i) continue;
                int val = (D[i][j] + D[j][k] - D[i][k])/2;
                if(val < min) min = val;
            }
        }
        return min;
    }

    private boolean getPath(int k, int l, List<Integer> path, Map<Integer,Map<Integer, Integer>> ap, Set<Integer> visited) {
        visited.add(k);
        if(k == l) {
            path.add(k);
            return true;
        }
        for(int j : ap.get(k).keySet()) {
            if(visited.contains(j)) continue;
            if(getPath(j, l, path, ap, visited)) {
                path.add(k);
                return true;
            }
        }
        return false;
    }

    private Pair<Integer, Pair<Integer, Integer>> getChangedEdge(int k, int l, int x, Map<Integer, Map<Integer, Integer>> ap) {
        List<Integer> path = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();
        getPath(k, l, path, ap, visited);
        int total = 0;
        for(int i = path.size() - 1; i >= 0; i--) {
            if(total >= x) {
                return new Pair<>(total - x, new Pair<>(path.get(i + 1), path.get(i)));
            }
            else {
                total += ap.get(path.get(i)).get(path.get(i - 1));
            }
        }
        return null;
    }

    public Map<Integer, Map<Integer, Integer>> getAdditivePhylogeny() {
        if(this.tree != null) return tree;
        else this.tree = getAdditivePhylogeny(this.distance_matrix, this.n);
        return tree;
    }

    private void Symmetrize(Map<Integer, Map<Integer, Integer>> map) {
        for(int i : map.keySet()) {
            for(int j : map.get(i).keySet()) {
                Pair<Integer, Integer> edge = new Pair<>(i, map.get(i).get(j));
                addToMap(map, j, edge);
            }
        }
    }

    public Map<Integer, Map<Integer, Integer>> getAdditivePhylogeny(int[][] D, int n) {
        if(n == 2) {
            Map<Integer, Map<Integer, Integer>> additive_phylogeny = new TreeMap<>();
            Map<Integer, Integer> edge = new TreeMap<>();
            edge.put(0, D[1][0]);
            additive_phylogeny.put(1, edge);
            Symmetrize(additive_phylogeny);
            return additive_phylogeny;
        }
        int limbLength = getLimbLength(D, n - 1);
        for(int i = 0; i < n; i++) {
            D[i][n-1] -= limbLength;
            D[n - 1][i] = D[i][n - 1];
        }
        int k = 0;
        int l = 0;
        int[][] new_D = new int[n - 1][n - 1];
        for(int i = 0; i < n - 1; i++) {
            for(int j = 0; j < n - 1; j++) {
                if(i == j) continue;
                new_D[i][j] = D[i][j];
                if(D[i][j] == D[i][n - 1] + D[n -1][j]) {
                    k = i;
                    l = j;
                }
            }
        }
        int x = D[k][n - 1];
        Map<Integer, Map<Integer, Integer>> additive_phylogeny = getAdditivePhylogeny(new_D, n - 1);
        Pair<Integer, Pair<Integer, Integer>> changed_edge = getChangedEdge(k, l, x, additive_phylogeny);
        if(changed_edge.getKey() > 0) { //There is no node X distance from k, so we must make new node
            Pair<Integer, Integer> end_points = changed_edge.getValue();
            int new_inner_node = n + (this.n - 3);
            int old_edge_length = additive_phylogeny.get(end_points.getKey()).get(end_points.getValue());
            additive_phylogeny.get(end_points.getKey()).remove(end_points.getValue());
            additive_phylogeny.get(end_points.getValue()).remove(end_points.getKey());
            additive_phylogeny.get(end_points.getKey()).put(new_inner_node, old_edge_length - changed_edge.getKey());
            Map<Integer, Integer> new_edges = new TreeMap<>();
            new_edges.put(end_points.getValue(), changed_edge.getKey());
            new_edges.put(n - 1, limbLength);
            additive_phylogeny.put(new_inner_node, new_edges);
        }
        else { //There is already an inner node X distance from k, so we simply add new leaf node to this node
            int inner_node = changed_edge.getValue().getValue();
            Map<Integer, Integer> new_edge = new TreeMap<>();
            additive_phylogeny.get(inner_node).put(n - 1, limbLength);
        }
        Symmetrize(additive_phylogeny);
        return additive_phylogeny;
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

    public static void createDistanceMatrix(String[] args) throws IOException{
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

    private static void getLimbLength(String[] args) throws IOException{
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

    private static void createAdditivePhylogeny(String[] args) throws IOException{
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
        Map<Integer, Map<Integer, Integer>> map = ap.getAdditivePhylogeny();
        for(int i : map.keySet()) {
            for(int j : map.get(i).keySet()) {
                out.println(String.format("%d->%d:%d", i, j, map.get(i).get(j)));
            }
        }
        scan.close();
        out.close();
    }

    public static void main(String[] args) {
        if (args.length != 3) System.out.println(USAGE);
        else {
            try {
                if(args[0].equals("-d")) {
                    createDistanceMatrix(args);
                }
                else if(args[0].equals("-l")) {
                    getLimbLength(args);
                }
                else if(args[0].equals("-p")) {
                    createAdditivePhylogeny(args);
                }
                else { System.out.println(USAGE); }
            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME\n" + USAGE);
            }
        }
    }
}
