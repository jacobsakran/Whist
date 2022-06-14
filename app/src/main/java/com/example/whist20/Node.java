package com.example.whist20;

public class Node {
    public Object obj;
    public Node next;

    public Node() {
        this.obj = null;
        this.next = null;
    }

    public Node(Object obj) {
        this.obj = obj;
        this.next = null;
    }
    public Object getByIndex(int index){
        Node iterator = this;
        int counter =0;
        while (iterator.next != null && counter < index){
            iterator = iterator.next;
            counter += 1;
        }
        return iterator.obj;
    }

    public void addValue(Object obj) {
        if (this.obj == null) {
            this.obj = obj;
            return;
        }
        Node iterator = this;
        while (iterator.next != null) iterator = iterator.next;
        iterator.next = new Node(obj);
    }
}
