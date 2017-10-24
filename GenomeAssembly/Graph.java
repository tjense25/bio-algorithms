package GenomeAssembly;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by tjense25 on 9/26/17.
 */
public class Graph {
    private static String USAGE = "USAGE: java Graph input_file_name output_file_name option\n " +
            "input_file is the path to the file with given input\n " +
            "output_file is the path to the file where output is to be written";
    List<Object> vertices = new ArrayList<>();
    Map<Object, List<Object>> adjacencyMap;
    Map<Object, List<Object>> unvisitedEdges;

    public class NoEulerianPathException extends Exception {
    }


    public Graph(Map<Object, List<Object>> adjacencyMap) {
        this.adjacencyMap = adjacencyMap;
        for(String vertex : adjacencyMap.keySet()) {
            vertices.add(vertex);
        }
    }



    public static void main(String[] args) {
        if (args.length != 2) System.out.println(USAGE);
        else {
            try {
                Scanner scan = new Scanner(new FileReader(args[0])).useDelimiter("((\\s*->\\s*)|\\s+)");
                Map<String, List<String>> adjacencyMap = new HashMap<>();
                while(scan.hasNext()) {
                    String key = scan.next();
                    String children_string = scan.next();
                    String[] children = children_string.split(",");
                    List<String> children_list = new ArrayList<String>(Arrays.asList(children));
                    adjacencyMap.put(key, children_list);
                }
                scan.close();
                Graph graph = new Graph(adjacencyMap);
                List<String> eulerian_result = new ArrayList<>();
                try {
                    eulerian_result = graph.getEulerianResult();
                }
                catch(NoEulerianPathException ex) {
                    System.out.println("ERROR: Given Graph has No Eulerian Path or Cycle");
                }
                PrintWriter writer = new PrintWriter(new FileWriter(args[1]));
                for(int i = 0; i < eulerian_result.size(); i++) {
                    writer.print(eulerian_result.get(i));
                    if(i != eulerian_result.size() - 1) writer.print("->");
                }
                writer.close();

            } catch (IOException e) {
                System.out.println("ERROR: NOT A VALID FILE NAME");
            }
        }
    }

    public List<String> getEulerianResult() throws NoEulerianPathException {
        List<String> unbalanced_vertices = findUnbalancedVertices();
        if(unbalanced_vertices.size() == 0) return getEulerianCycle();
        if(unbalanced_vertices.size() == 2) return getEulerianPath(unbalanced_vertices);
        else throw new NoEulerianPathException();
    }

    private List<String> getEulerianCycle() throws NoEulerianPathException {
        unvisitedEdges = adjacencyMap;
        List<String> cycle = new ArrayList<>();
        randomlyWalk(vertices.get(0), cycle);
        while(unvisitedEdges.size() != 0) {
            String new_start = null;
            for(String vertex : unvisitedEdges.keySet()) {
                if(cycle.contains(vertex)) new_start = vertex;
            }
            cycle = traverseCycle(new_start, cycle);
            randomlyWalk(new_start, cycle);
        }
        return cycle;
    }

    private List<String> getEulerianPath(List<String> unbalanced_vertices) throws NoEulerianPathException{
        List<String> path;
        List<String> new_edges;
        if(adjacencyMap.containsKey(unbalanced_vertices.get(0))) {
            new_edges = adjacencyMap.get(unbalanced_vertices.get(0));
            new_edges.add(unbalanced_vertices.get(1));
            adjacencyMap.replace(unbalanced_vertices.get(0), new_edges);
        }
        else {
            new_edges = new ArrayList<>();
            new_edges.add(unbalanced_vertices.get(1));
            adjacencyMap.put(unbalanced_vertices.get(0), new_edges);
        }
        List<String> cycle = getEulerianCycle();
        path = convertCycleToPath(unbalanced_vertices.get(1), unbalanced_vertices.get(0), cycle);
        adjacencyMap.remove(unbalanced_vertices.get(0));
        return path;
    }

    private List<String> convertCycleToPath(String starting_vertex, String ending_vertex, List<String> cycle) {
        List<String> path = new ArrayList<>();
        int start_index = 0;
        for(int i = 0; i < cycle.size() - 1; i++) {
            if(cycle.get(i).equals(ending_vertex) && cycle.get(i + 1).equals(starting_vertex)) {
                start_index = i + 1;
                break;
            }
        }
        for(int i = start_index; i < cycle.size(); i++) {
            path.add(cycle.get(i));
        }
        for(int i = 1; i < start_index; i++) {
            path.add(cycle.get(i));
        }
        return path;
    }

    private List<String> findUnbalancedVertices(){
        List<String> unbalanced_vertices = new ArrayList<>();
        Map<String, Integer> countMap = createCountMap();
        for(String vertex : countMap.keySet()) {
            if(countMap.get(vertex) != 0) unbalanced_vertices.add(vertex);
        }
        Collections.sort(unbalanced_vertices, (String s1, String s2) -> countMap.get(s1).compareTo(countMap.get(s2)));
        return unbalanced_vertices;
    }

    private Map<String, Integer> createCountMap() {
        Map<String, Integer> countMap = new HashMap<>();
        for(String vertex : adjacencyMap.keySet()) {
            List<String> edges = adjacencyMap.get(vertex);
            updateCount(vertex, countMap, edges.size());
            for(String adjacent_vertex : edges) {
                updateCount(adjacent_vertex, countMap, -1);
            }
        }
        return countMap;
    }

    private void updateCount(String vertex, Map<String, Integer> countMap, int amount) {
        if(countMap.containsKey(vertex)) {
            int value = countMap.get(vertex);
            value += amount;
            countMap.replace(vertex, value);
        }
        else countMap.put(vertex, amount);
    }

    private void randomlyWalk(String current_vertex, List<String> cycle) {
        if(!unvisitedEdges.containsKey(current_vertex)) {
            cycle.add(current_vertex);
            return;
        }
        cycle.add(current_vertex);
        List<String> edges = unvisitedEdges.get(current_vertex);
        String new_vertex = null;
        if(edges.size() == 1) {
            new_vertex = edges.get(0);
            unvisitedEdges.remove(current_vertex);
            randomlyWalk(new_vertex, cycle);
        }
        else {
            Random rand = new Random();
            int ran_pos = rand.nextInt(edges.size());
            new_vertex = edges.get(ran_pos);
            unvisitedEdges.get(current_vertex).remove(ran_pos);
            randomlyWalk(new_vertex, cycle);
        }
    }

    private List<String> traverseCycle(String start_vertex, List<String> cycle) {
        List<String> new_cycle = new ArrayList<>();
        int start_index = cycle.indexOf(start_vertex);
        for(int i = start_index; i < cycle.size(); i++) {
            new_cycle.add(cycle.get(i));
        }
        for(int i = 1; i < start_index; i++) {
            new_cycle.add(cycle.get(i));
        }
        return new_cycle;
    }

}
