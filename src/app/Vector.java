package app;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

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

    public Vector rotateVector() {
        return new Vector(y, -x);
    }

    public Vector normalize() {
        return new Vector(x / getVectorLength(), y / getVectorLength());
    }

    public Vector flip() {
        return new Vector(-x, -y);
    }

    public double getVectorLength() {
        return Math.sqrt(x * x + y * y);
    }

    public double dotProduct(Vector vector) {
        return x * vector.getX() + y * vector.getY();
    }

    public String toString() {
        DecimalFormat df = new DecimalFormat("0.000", new DecimalFormatSymbols(Locale.ENGLISH));
        return "(x: " + df.format(x) + ", y: " + df.format(y) + ")";
    }

}
