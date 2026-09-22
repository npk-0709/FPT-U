package graph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class CapGraph implements Graph {

    private HashMap<Integer, HashSet<Integer>> graph;

    public CapGraph() {
        this.graph = new HashMap<>();
    }

    @Override
    public void addVertex(int num) {
        if (!graph.containsKey(num)) {
            graph.put(num, new HashSet<>());
        }
    }

    @Override
    public void addEdge(int from, int to) {
        if (graph.containsKey(from) && graph.containsKey(to)) {
            graph.get(from).add(to);
        }
    }

    @Override
    public Graph getEgonet(int center) {
        CapGraph egonet = new CapGraph();
        if (!graph.containsKey(center)) {
            return egonet;
        }

        egonet.addVertex(center);
        HashSet<Integer> neighbors = graph.get(center);
        for (int neighbor : neighbors) {
            egonet.addVertex(neighbor);
        }

        for (int node : egonet.graph.keySet()) {
            for (int neighbor : this.graph.get(node)) {
                if (egonet.graph.containsKey(neighbor)) {
                    egonet.addEdge(node, neighbor);
                }
            }
        }

        return egonet;
    }

    @Override
    public List<Graph> getSCCs() {
        List<Graph> sccs = new ArrayList<>();
        Stack<Integer> finished = new Stack<>();
        HashSet<Integer> visited = new HashSet<>();

        for (int vertex : graph.keySet()) {
            if (!visited.contains(vertex)) {
                dfs(vertex, visited, finished);
            }
        }

        CapGraph transposed = getTranspose();
        visited.clear();

        while (!finished.isEmpty()) {
            int vertex = finished.pop();
            if (!visited.contains(vertex)) {
                HashSet<Integer> sccNodes = new HashSet<>();
                transposed.dfsSCC(vertex, visited, sccNodes);

                CapGraph sccGraph = new CapGraph();
                for (int node : sccNodes) {
                    sccGraph.addVertex(node);
                }
                for (int node : sccNodes) {
                    for (int neighbor : this.graph.get(node)) {
                        if (sccNodes.contains(neighbor)) {
                            sccGraph.addEdge(node, neighbor);
                        }
                    }
                }
                sccs.add(sccGraph);
            }
        }
        return sccs;
    }

    @Override
    public HashMap<Integer, HashSet<Integer>> exportGraph() {
        HashMap<Integer, HashSet<Integer>> export = new HashMap<>();
        for (int vertex : graph.keySet()) {
            export.put(vertex, new HashSet<>(graph.get(vertex)));
        }
        return export;
    }

    private void dfs(int vertex, HashSet<Integer> visited, Stack<Integer> finished) {
        visited.add(vertex);
        for (int neighbor : graph.get(vertex)) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited, finished);
            }
        }
        finished.push(vertex);
    }

    private void dfsSCC(int vertex, HashSet<Integer> visited, HashSet<Integer> sccNodes) {
        visited.add(vertex);
        sccNodes.add(vertex);
        for (int neighbor : graph.get(vertex)) {
            if (!visited.contains(neighbor)) {
                dfsSCC(neighbor, visited, sccNodes);
            }
        }
    }

    private CapGraph getTranspose() {
        CapGraph transposed = new CapGraph();
        for (int vertex : graph.keySet()) {
            transposed.addVertex(vertex);
        }
        for (int vertex : graph.keySet()) {
            for (int neighbor : graph.get(vertex)) {
                transposed.addEdge(neighbor, vertex);
            }
        }
        return transposed;
    }
}