package com.pan.algorithm.binarytree;

import java.util.ArrayList;
import java.util.List;
//check if it's a binary tree or not
public class ValidateTree {
	
	class Node {

	    public int data;
	    public Node left;
	    public Node right;
	    
	    public Node(int data){
	        this.data=data;
	        left=right=null;
	    }
	}

    // check if it's a binary tree, input param is the root of the tree
    boolean checkBST(Node root) {
        //System.out.println("data:"+ root.data);
        if (root.left!= null){
            //System.out.println("data left:"+ root.left.data);
        }
        if (root.right!= null){
            //System.out.println("data right:"+ root.right.data);
        }
        /**
        boolean res = true;
        if (validateNode(root) == false){
            return false;
        }
        if (root.left != null){
            
            if (checkBST(root.left) == false){
                return false;
            }
        }
        if (root.right != null){
            if (checkBST(root.right) == false){
                return false;
            }
        }
        return res;
        */
        return validateNode(root,Integer.MIN_VALUE,Integer.MAX_VALUE);
    }
    boolean validateNode(Node node){
        boolean res = true;
        if (node.left != null){
            List<Node> nodeList = new ArrayList<Node>();
            addToList(node.left,nodeList);
            for (Node tempnode:nodeList){
                if (tempnode.data >= node.data){
                    res = false;
                }
            }
            
        }
        if (node.right != null){
            List<Node> nodeList = new ArrayList<Node>();
            addToList(node.right,nodeList);
            for (Node tempnode:nodeList){
                if (tempnode.data <= node.data){
                    res = false;
                }
            }
            
        }
        return res;
    }
    void addToList(Node node,List<Node> nodeList){
        nodeList.add(node);
        if (node.left != null){
            addToList(node.left,nodeList);
        }
        if (node.right != null){
            addToList(node.right,nodeList);
        }
    }

    boolean validateNode(Node node, int min, int max){
        
        if (node == null){
            return true;
        }
        if (node.data >= max || node.data <= min){
            return false;
        }
        return validateNode(node.left,min,node.data) && validateNode(node.right,node.data,max);
    }
}
