package ru.ssau.tk._AMEBA_._PESEZ_.functions;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction{
    private  int count;
    private  Node head;
    private void addNode(double x, double y){
        Node newNode = new Node(x, y);
        if(head==null){
            head=newNode;
            head.next=head;
            head.prev=head;
        }
        else{
            Node last = head.prev;
            last.next=newNode;
            newNode.prev=last;
            newNode.next=head;
            head.prev=newNode;
        }
       count++;
    }

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            throw new IllegalArgumentException("Размеры массивов не совпадают");
        }
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Должно быть хотя бы 2 точки");
        }
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                throw new IllegalArgumentException("xValues должны быть упорядочены");
            }
        }
        for (int i = 0; i < xValues.length; i++) {
            addNode(xValues[i], yValues[i]);
        }
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count){
        if (count < 2) {
            throw new IllegalArgumentException("Должно быть хотя бы 2 точки");
        }
        if(xFrom>xTo){
            double temp = xFrom;
            xFrom=xTo;
            xTo=temp;
        }
        double step=(xTo-xFrom)/(count-1);
        for(int i=0; i<count; i++){
            double x=xFrom+i*step;
            addNode(x, source.apply(x));
        }
    }

    @Override
    public int floorIndexOfX(double x) {
        if (x < head.x) return 0;
        if (x > head.prev.x) return count;

        Node current = head;
        int index = 0;
        while (index < count - 1 && current.next.x <= x) {
            current = current.next;
            index++;
        }
        return index;
    }

    private Node floorNodeOfX(double x) {
        if (head == null) return null;
        if (x < head.x) return head;
        if (x > head.prev.x) return head.prev;

        Node current = head;
        while (current.next != head && current.next.x <= x) {
            current = current.next;
        }
        return current;
    }

    @Override
    public double extrapolateLeft(double x) {
        return interpolate(x, head.x, head.next.x, head.y, head.next.y);
    }

    @Override
    public double extrapolateRight(double x) {
        return interpolate(x, head.prev.prev.x, head.prev.x, head.prev.prev.y, head.prev.y);
    }

    @Override
    public double interpolate(double x, int floorIndex) {
        Node left = getNode(floorIndex);
        Node right = left.next;
        return interpolate(x, left.x, right.x, left.y, right.y);
    }

    @Override
    public int getCount() {
        return count;
    }

    private Node getNode(int index){
        if (index<0 || index>=count){
            throw new IndexOutOfBoundsException();
        }
        Node current;
        if (index<count/2){
            current=head;
            for(int i=0;i<index;i++){
                current=current.next;
            }
        }else{
            current=head.prev;
            for(int i=count-1;i>index;i--){
                current=current.prev;
            }
        }
        return current;
    }

    @Override
    public double getX(int index) {
        return getNode(index).x;
    }

    @Override
    public double getY(int index) {
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) {
        getNode(index).y=value;
    }

    @Override
    public int indexOfX(double x) {
        Node current=head;
        for (int i=0; i<count; i++){
            if (current.x==x) return i;
            current=current.next;
        }
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        Node current=head;
        for (int i=0; i<count; i++){
            if (current.y==y) return i;
            current=current.next;
        }
        return -1;
    }

    @Override
    public double leftBound() {
        return head.x;
    }

    @Override
    public double rightBound() {
        return head.prev.x;
    }

    @Override
    public  double apply(double x){
        if (count == 0) {
            throw new IllegalStateException("Список пустой");
        }

        Node floorNode = floorNodeOfX(x);

        if (x < head.x) return extrapolateLeft(x);
        if (x > head.prev.x) return extrapolateRight(x);
        if (x == floorNode.x) return floorNode.y;

        Node nextNode = floorNode.next;
        return interpolate(x, floorNode.x, nextNode.x, floorNode.y, nextNode.y);

    }
}
