public class Circle implements MyShapeInterface, DecorationInterface {
    double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public void dwaw(String color) {
        System.out.println("Drawing a circle with color: " + color);
    }

    @Override
    public void addInfo(String info) {
        System.out.println("Circle info: " + info);
    }

    @Override
    public double calculateArea() {
        return Math.PI * Math.pow(radius, 2);
    }

    @Override
    public double calculatePerimeter() {
        return 2 * Math.PI * radius;
    }
}
