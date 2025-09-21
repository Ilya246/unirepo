class MyFirstClass {
    void main(String[] s) {
        MySecondClass o = new MySecondClass(32, 16);
        System.out.println(o.get());
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                o.setA(i);
                o.setB(j);
                System.out.print(o.get());
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}

class MySecondClass {
    private int a, b;

    public MySecondClass(int a, int b) {
        this.a = a;
        this.b = b;
    }

    void setA(int to) {
        a = to;
    }
    int getA() {
        return a;
    }

    void setB(int to) {
        b = to;
    }
    int getB() {
        return b;
    }

    int get() {
        return a - b;
    }
}
