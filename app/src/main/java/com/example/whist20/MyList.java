package com.example.whist20;

public class MyList {
    Node head;
    int size;

    public MyList(){
        this.size = 0;
        this.head= new Node();
    }
    public void addNode(Object obj){
        this.head.addValue(obj);
        this.size ++;
    }
    public Object getByIndex(int index){
        return head.getByIndex(index);
    }
    public void removeByIndex(int index){
        if(index == 0){
            this.head = this.head.next;
            this.size--;
            return;
        }
        int counter =0;
        Node iterator1 = this.head;
        Node iterator2 = this.head.next;
        while (iterator2.next != null && counter < (index-1)){
            iterator2 = iterator2.next;
            iterator1 = iterator1.next;
            counter ++;
        }
        iterator1.next = iterator2.next;
        this.size--;
    }
    public void removeByValue(Object obj){
        if(this.head.obj.equals(obj)){
            this.head = this.head.next;
            this.size--;
            return;
        }
        Node iterator1 = this.head;
        Node iterator2 = this.head.next;
        while (iterator2.next != null){
            if(iterator2.obj.equals(obj)) break;
            iterator2 = iterator2.next;
            iterator1 = iterator1.next;
        }
        iterator1.next = iterator2.next;
        this.size--;
    }
}