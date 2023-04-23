package com.ngin;

public class Stack {
    private int[] items;
    private int index;

    
    Stack() {
        items = new int[10];
        index = 0;
    }

    Stack(int size) {
        items = new int[size];
        index = 0;
    }

    public int size() {
        return index;
    }

    public void add(int item) {
        if (index < 10) {
            items[index] = item;
            index++;
        }
    }

    public int remove() {
        if (index > 0) {
            index--;
            return items[index];
        } else {
            return -1;
        }
    }

    static void haha() {
        System.out.println("haha");
    }
}
