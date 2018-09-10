package com.pan.algorithm.binarytree;

import java.util.Scanner;

public class CalcuateHeight {

	public static int getHeight(Node root){
	    //Write your code here
		int height = 0;
		if (root==null){
			height = -1;
		}else{
			height = 1 + Math.max(getHeight(root.left), getHeight(root.right));
		}
		return height;
	}


    public static Node insert(Node root,int data){
        if(root==null){
            return new Node(data);
        }
        else{
            Node cur;
            if(data<=root.data){
                cur=insert(root.left,data);
                root.left=cur;
            }
            else{
                cur=insert(root.right,data);
                root.right=cur;
            }
            return root;
        }
    }
	public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        int numbercount =sc.nextInt();// the first input is the count of number
        Node root=null;
        while(numbercount -- >0){
            int data=sc.nextInt();
            root=insert(root,data);
        }
        int height=getHeight(root);
        System.out.println(height);
    }
}
