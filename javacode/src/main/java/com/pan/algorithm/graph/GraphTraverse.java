package com.pan.algorithm.graph;

import java.util.*;

// without recursion
public class GraphTraverse {

    public static void main(String[] args) {
        GraphTraverse graphTraverse = new GraphTraverse();
        graphTraverse.addEdge(1, 3);
        graphTraverse.addEdge(1, 6);
        graphTraverse.addEdge(1, 7);
        graphTraverse.addEdge(6, 7);
        graphTraverse.addEdge(6, 8);
        graphTraverse.addEdge(7, 8);
        graphTraverse.addEdge(7, 9);
        System.out.println("dfs");
        List<Integer> res = graphTraverse.dfsTraverse(1);
        for (int i : res){
            System.out.println(i);
        }
        System.out.println("bfs");
        res = graphTraverse.bfsTraverse(1);
        for (int i : res){
            System.out.println(i);
        }
    }

    private Map<Integer, Set<Integer>> graph = new HashMap<>();

    // non-directional
    public void addEdge(int source, int dest) {
        graph.computeIfAbsent(source, p -> new HashSet<>()).add(dest);
        graph.computeIfAbsent(dest, p -> new HashSet<>()).add(source);
    }

    public List<Integer> dfsTraverse(int startNode) {
        List<Integer> res = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        Stack<Integer> stack = new Stack<>();
        stack.push(startNode);
        while (!stack.empty()) {
            int currentNode = stack.pop();
            if (!visited.add(currentNode)){
                continue;
            }
            res.add(currentNode);
            Set<Integer> neighbors = graph.getOrDefault(currentNode, new HashSet<>());
            for (int neighbor : neighbors) {
                if (!visited.contains(neighbor)){
                    stack.push(neighbor);
                }
            }
        }
        return res;
    }

    public List<Integer> bfsTraverse(int startNode) {
        List<Integer> res = new ArrayList<>();

        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        queue.add(startNode);
        visited.add(startNode);
        while (!queue.isEmpty()) {
            int currentNode = queue.poll();
            // we don't need to check visited and return
            res.add(currentNode);

            Set<Integer> neighbors = graph.getOrDefault(currentNode, new HashSet<>());
            for (int neighbor : neighbors) {
                if (!visited.contains(neighbor)){
                    queue.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }

        return res;
    }
}
