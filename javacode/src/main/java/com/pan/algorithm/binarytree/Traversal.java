package com.pan.algorithm.binarytree;

import java.util.LinkedList;

public class Traversal {
	// traversal algorithm
	// https://www.hackerrank.com/challenges/30-binary-trees/tutorial
	
	// level-order traversal
	static void levelOrder(Node root){
		StringBuilder sb = new StringBuilder("");
		
		LinkedList<Node> list = new LinkedList<Node>();
		list.add(root);
		while (list.size()>0){
			Node node = list.remove(0);
			sb.append(node.data).append(" ");
			System.out.println("size:" +list.size());
			if (node.left!=null){
				list.add(node.left);
			}
			if (node.right!=null){
				list.add(node.right);
			}
		}
		System.out.println(sb.toString().trim());
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
    	int[] aa = {3,5,4,7,2,1};
        Node root=null;
        for (int data: aa ){
            
            root=insert(root,data);
        }
        levelOrder(root);
    }
}
