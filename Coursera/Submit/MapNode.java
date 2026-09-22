package roadgraph;

import java.util.ArrayList;
import java.util.List;
import geography.GeographicPoint;

public class MapNode {
    private GeographicPoint location;
    private List<MapEdge> edges;

    public MapNode(GeographicPoint location) {
        this.location = location;
        this.edges = new ArrayList<MapEdge>();
    }

    public void addEdge(MapEdge edge) {
        edges.add(edge);
    }

    public List<MapEdge> getEdges() {
        return edges;
    }

    public GeographicPoint getLocation() {
        return location;
    }
}