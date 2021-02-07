package com.pan.datastructure;


public class ReverseLinkedList {

    Node head;
    void printList(Node node)
    {
        while (node != null) {
            System.out.print(node.data + " ");
            node = node.next;
        }
    }

    // Driver Code
    public static void main(String[] args)
    {
        ReverseLinkedList list = new ReverseLinkedList();
        list.head = list.new Node(85);
        list.head.next = list.new Node(15);
        list.head.next.next = list.new Node(4);
        list.head.next.next.next = list.new Node(20);

        System.out.println("Given Linked list");
        list.printList(list.head);
        list.head = list.reverse(list.head);
        System.out.println("");
        System.out.println("Reversed linked list ");
        list.printList(list.head);
    }

    public class Node {

        int data;
        Node next;

        Node(int d)
        {
            data = d;
            next = null;
        }
    }
    // https://www.geeksforgeeks.org/reverse-a-linked-list/
    Node reverse(Node head) {


        Node prev = null;
        Node curr = head;
        Node next = null;
        while (curr != null){
            next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        head = prev;

        return head;
    }
}
