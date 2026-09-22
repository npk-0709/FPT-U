package roadgraph;

import java.util.List;
import java.util.ArrayList;
import geography.GeographicPoint;

public class MapNode implements Comparable<MapNode> {
    private GeographicPoint location;
    private List<MapEdge> edges;

    // Khoảng cách thực tế từ Start -> Node này
    private double distance;
    // Khoảng cách dự đoán (Distance + Heuristic tới Goal) dùng cho A*
    private double actualDistance;

    public MapNode(GeographicPoint location) {
        this.location = location;
        this.edges = new ArrayList<>();
        this.distance = Double.POSITIVE_INFINITY;
        this.actualDistance = Double.POSITIVE_INFINITY;
    }

    public GeographicPoint getLocation() {
        return location;
    }

    public List<MapEdge> getEdges() {
        return edges;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getActualDistance() {
        return actualDistance;
    }

    public void setActualDistance(double actualDistance) {
        this.actualDistance = actualDistance;
    }

    // Ưu tiên sắp xếp node có actualDistance (hoặc distance) nhỏ nhất lên đầu PriorityQueue
    @Override
    public int compareTo(MapNode other) {
        return Double.compare(this.actualDistance, other.actualDistance);
    }
}