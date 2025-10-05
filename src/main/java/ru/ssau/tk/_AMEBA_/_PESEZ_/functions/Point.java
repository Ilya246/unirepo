package ru.ssau.tk._AMEBA_._PESEZ_.functions;

public class Point {
    double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Point p) && x == p.x && y == p.y;
    }
}
