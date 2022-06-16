package com.example.whist20;

public class MyList {
    public Node head;
    public int size;

    public MyList() {
        this.size = 0;
        this.head = new Node();
    }

    public void addNode(Object obj) {
        this.head.addValue(obj);
        this.size++;
    }

    public Object findByIndex(int index) {
        return head.findByIndex(index);
    }

    public void removeByIndex(int index) {
        if (index == 0) {
            this.head = this.head.next;
            if (this.head == null) this.head = new Node();
            this.size--;
            return;
        }
        int counter = 0;
        Node iterator1 = this.head;
        Node iterator2 = this.head.next;
        while (iterator2.next != null && counter < (index - 1)) {
            iterator2 = iterator2.next;
            iterator1 = iterator1.next;
            counter++;
        }
        iterator1.next = iterator2.next;
        this.size--;
    }

    public Object findByValue(Object obj) {
        Node iterator = this.head;
        while (iterator.next != null) {
            if (iterator.obj.equals(obj)) return obj;
            iterator = iterator.next;
        }

        return null;
    }

    public void removeByValue(Object obj) {
        if (this.head.obj.equals(obj)) {
            this.head = this.head.next;
            if (this.head == null) this.head = new Node();
            this.size--;
            return;
        }
        Node iterator1 = this.head;
        Node iterator2 = this.head.next;
        while (iterator2.next != null) {
            if (iterator2.obj.equals(obj)) break;
            iterator2 = iterator2.next;
            iterator1 = iterator1.next;
        }
        iterator1.next = iterator2.next;
        this.size--;
    }
}
