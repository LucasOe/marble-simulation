package app;

public class Vector {

    double x;
    double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector addVector(Vector vector) {
        return new Vector(x + vector.getX(), y + vector.getY());
    }

    public Vector multiply(double factor) {
        return new Vector(x * factor, y * factor);
    }

    public Vector normalize() {
        double length = Math.sqrt(x * x + y * y);
        return new Vector(x / length, y / length);
    }

    public Vector flip() {
        return new Vector(-x, -y);
    }

}
