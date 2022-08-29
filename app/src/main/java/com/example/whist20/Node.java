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

    public Object findByIndex(int index) {
        Node iterator = this;
        int counter = 0;
        while (iterator.next != null && counter < index) {
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

    public void removeByValue(Object obj) {
        Node head = this;
        if (head.obj.equals(obj)) {
            head = head.next;
            if (head == null) head = new Node();
            return;
        }
        boolean tmp = false;
        Node iterator1 = head;
        Node iterator2 = head.next;
        while (iterator2 != null) {
            if (iterator2.obj.equals(obj)){
                tmp = true;
                break;
            }
            iterator2 = iterator2.next;
            iterator1 = iterator1.next;
        }
        if(tmp) {
            iterator1.next = iterator2.next;
        }
    }
}
