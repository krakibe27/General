package com.practice.doublylinkedlist;

public class DoublyLinkedList {

	Node head;
	Node tail;
	
	class Node {
		int value;
		Node next;
		Node prev;
		
		public Node(int value) {
			this.value = value;
			this.next = null;
			this.prev = null;
		}
		
	}
	
	public void addFirst(int value) {
			Node current = new Node(value);
			current.next = head;
			current.prev = null;
			if(head!=null)
			head.prev = current;
			head = current;
			if(tail==null)
			tail = current;
	}
	
	public void addLast(int value) {
		Node current = new Node(value);
		if(tail!=null)
			tail.next = current;
			current.prev = tail;
			tail = current;
		if(head==null){
			head = current;
		}
		
}
	
	public void printLinkedListForward() {
		System.out.println("Printing Doubly LinkedList (head --> tail) ");
		Node current = head;
		while (current != null) {
			System.out.println(current.value);
			current = current.next;
		}
		System.out.println();
	}
	
	public void printLinkedListBackward() {
		System.out.println("Printing Doubly LinkedList (tail --> head) ");
		Node current = tail;
		while (current != null) {
			System.out.println(current.value);
			current = current.prev;
		}
		System.out.println();
	}
	
	public void deletFirst(){
		if(head!=null){
			head = head.next;
			head.prev = null;
		}
	}
	
	public void deletLast(){
		if(tail!=null){
			tail = tail.prev;
			tail.next = null;
		}
	}
	
	
	public static void main(String[] args) {
		DoublyLinkedList list = new DoublyLinkedList();
		list.addFirst(5);
		list.addFirst(6);
		list.addFirst(7);
		list.addFirst(1);
		list.addFirst(2);
		list.addLast(10);
		list.printLinkedListForward();
		list.printLinkedListBackward();
		System.out.println("ooo");
		list.deletFirst();
		list.deletLast();
		list.printLinkedListForward();
	}
}
