package MyFirstPackage;

public class MySecondClass {
    private int a, b;

    public MySecondClass(int a, int b) {
        this.a = a;
        this.b = b;
    }

    public void setA(int to) {
        a = to;
    }
    public int getA() {
        return a;
    }

    public void setB(int to) {
        b = to;
    }
    public int getB() {
        return b;
    }

    public int get() {
        return a - b;
    }
}
