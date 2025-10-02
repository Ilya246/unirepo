package ru.ssau.tk._AMEBA_._PESEZ_.functions;

import ru.ssau.tk._AMEBA_._PESEZ_.exceptions.InterpolationException;

public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Removable, Insertable{

    static class Node {
        public Node next;
        public Node prev;
        public double x;
        public double y;

        public Node(double x, double y){
            this.x=x;
            this.y=y;
        }
    }

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
        if (xValues.length < 2) {
            throw new IllegalArgumentException("Должно быть хотя бы 2 точки");
        }
        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

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

        if (x < head.x) throw new IllegalArgumentException("x is less than left bound");

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
        if (x < left.x || x > right.x) throw new InterpolationException();
        return interpolate(x, left.x, right.x, left.y, right.y);
    }

    @Override
    public int getCount() {
        return count;
    }

    private Node getNode(int index){
        if (index<0 || index>=count){
            throw new IllegalArgumentException();
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

        Node floorNode = floorNodeOfX(x);

        if (x < head.x) return extrapolateLeft(x);
        if (x > head.prev.x) return extrapolateRight(x);
        if (x == floorNode.x) return floorNode.y;

        Node nextNode = floorNode.next;
        return interpolate(x, floorNode.x, nextNode.x, floorNode.y, nextNode.y);

    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index out of bounds");
        }

        Node toRemove = getNode(index);

        if (count == 1) {
            head = null;
        } else {
            toRemove.prev.next = toRemove.next;
            toRemove.next.prev = toRemove.prev;

            if (toRemove == head) {
                head = toRemove.next;
            }
        }

        count--;
    }

    @Override
    public void insert(double x, double y) {
        if (head == null) {
            addNode(x, y);
            return;
        }

        int idx = indexOfX(x);
        if (idx != -1) {
            setY(idx, y);
            return;
        }

        Node beforeNode;
        if (x < head.x) beforeNode = head.prev;
        else beforeNode = floorNodeOfX(x);

        Node newNode = new Node(x, y);
        newNode.prev = beforeNode;
        newNode.next = beforeNode.next;
        beforeNode.next.prev = newNode;
        beforeNode.next = newNode;

        if (x < head.x) head = newNode;
        count++;
    }
}
