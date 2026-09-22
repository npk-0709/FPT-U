package roadgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

import geography.GeographicPoint;
import util.GraphLoader;

public class MapGraph {
    
    private HashMap<GeographicPoint, MapNode> vertices;
    private int numEdges;
    
    public MapGraph() {
        vertices = new HashMap<>();
        numEdges = 0;
    }
    
    public int getNumVertices() {
        return vertices.size();
    }
    
    public Set<GeographicPoint> getVertices() {
        return vertices.keySet();
    }
    
    public int getNumEdges() {
        return numEdges;
    }

    public boolean addVertex(GeographicPoint location) {
        if (location == null || vertices.containsKey(location)) {
            return false;
        }
        vertices.put(location, new MapNode(location));
        return true;
    }
    
    public void addEdge(GeographicPoint from, GeographicPoint to, String roadName,
            String roadType, double length) throws IllegalArgumentException {
        
        if (from == null || to == null || roadName == null || roadType == null) {
            throw new IllegalArgumentException();
        }
        if (length < 0) {
            throw new IllegalArgumentException();
        }
        if (!vertices.containsKey(from) || !vertices.containsKey(to)) {
            throw new IllegalArgumentException();
        }

        MapEdge newEdge = new MapEdge(from, to, roadName, roadType, length);
        vertices.get(from).addEdge(newEdge);
        numEdges++;
    }
    
    public List<GeographicPoint> bfs(GeographicPoint start, GeographicPoint goal) {
        Consumer<GeographicPoint> temp = (x) -> {};
        return bfs(start, goal, temp);
    }
    
    public List<GeographicPoint> bfs(GeographicPoint start, 
                                     GeographicPoint goal, Consumer<GeographicPoint> nodeSearched) {
        
        if (start == null || goal == null) return null;
        if (!vertices.containsKey(start) || !vertices.containsKey(goal)) return null;

        Queue<GeographicPoint> queue = new LinkedList<>();
        HashSet<GeographicPoint> visited = new HashSet<>();
        HashMap<GeographicPoint, GeographicPoint> parentMap = new HashMap<>();

        queue.add(start);
        visited.add(start);
        
        boolean found = false;

        while (!queue.isEmpty()) {
            GeographicPoint curr = queue.poll();
            
            nodeSearched.accept(curr);
            
            if (curr.equals(goal)) {
                found = true;
                break;
            }
            
            for (MapEdge edge : vertices.get(curr).getEdges()) {
                GeographicPoint next = edge.getEnd();
                
                if (!visited.contains(next)) {
                    visited.add(next);
                    parentMap.put(next, curr);
                    queue.add(next);
                }
            }
        }

        if (!found) {
            return null;
        }

        LinkedList<GeographicPoint> path = new LinkedList<>();
        GeographicPoint curr = goal;
        
        while (curr != null) {
            path.addFirst(curr);
            curr = parentMap.get(curr);
        }
        
        return path;
    }
    
    public List<GeographicPoint> dijkstra(GeographicPoint start, GeographicPoint goal) {
        Consumer<GeographicPoint> temp = (x) -> {};
        return dijkstra(start, goal, temp);
    }
    
    public List<GeographicPoint> dijkstra(GeographicPoint start, 
                                          GeographicPoint goal, Consumer<GeographicPoint> nodeSearched) {
        return null;
    }

    public List<GeographicPoint> aStarSearch(GeographicPoint start, GeographicPoint goal) {
        Consumer<GeographicPoint> temp = (x) -> {};
        return aStarSearch(start, goal, temp);
    }
    
    public List<GeographicPoint> aStarSearch(GeographicPoint start, 
                                             GeographicPoint goal, Consumer<GeographicPoint> nodeSearched) {
        return null;
    }

    public static void main(String[] args) {
        System.out.print("Making a new map...");
        MapGraph firstMap = new MapGraph();
        System.out.print("DONE. \nLoading the map...");
        GraphLoader.loadRoadMap("data/testdata/simpletest.map", firstMap);
        System.out.println("DONE.");
    }
}