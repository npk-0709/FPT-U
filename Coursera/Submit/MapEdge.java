package roadgraph;

import geography.GeographicPoint;

public class MapEdge {
    private GeographicPoint start;
    private GeographicPoint end;
    private String roadName;
    private String roadType;
    private double length;

    public MapEdge(GeographicPoint start, GeographicPoint end, String roadName, String roadType, double length) {
        this.start = start;
        this.end = end;
        this.roadName = roadName;
        this.roadType = roadType;
        this.length = length;
    }

    public GeographicPoint getEnd() {
        return end;
    }
    
    // Bạn có thể thêm các getter khác nếu cần sử dụng cho tuần sau (A* / Dijkstra)
    public double getLength() {
        return length;
    }
}