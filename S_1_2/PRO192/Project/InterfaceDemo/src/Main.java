public class Main {
    public static void main(String[] args) {

        System.out.println("Demo Interface");
        Circle circle = new Circle(5);
        System.out.println("Circle with radius: " + circle.getRadius());
        System.out.println("Area: " + circle.calculateArea());
        System.out.println("Perimeter: " + circle.calculatePerimeter());
        circle.dwaw("Red");
        circle.addInfo("This is a circle with radius: "+ circle.getRadius());
    }
}