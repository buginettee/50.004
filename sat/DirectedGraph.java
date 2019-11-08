package sat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import sat.formula.Literal;


public final class DirectedGraph implements Iterable<Literal> {

    private final Map<Literal, HashSet<Literal>> digraph = new HashMap<Literal, HashSet<Literal>>();

    //Add a new node into the graph
    //input: generic type
    public void addNode(Literal node) {
        if (digraph.containsKey(node)) { //if the graph already contains the node, do nothing
            System.out.println("Node " + node + " is already in the graph");
        } else if (!digraph.containsKey(node)) {  //if the graph does not contain the node, add it in, with an empty hashset
            digraph.put(node, new HashSet<Literal>());
            System.out.println("Node " + node + " has been added");
        }
    }

    //Remove a node from the graph
    //input: T typed node to be removed
    //throw an exception if the node cannot be found and return false
    public void removeNode(Literal node) {
        try {
            if (digraph.containsKey(node)) { //if the node is inside, remove as a destination and as a source
                for (Object nodes : digraph.keySet()) {
                    if (digraph.get(nodes).contains(node)) {
                        digraph.get(nodes).remove(node);
                    }
                }
                digraph.remove(node);
                System.out.println("The " + node + " has been removed from the graph");
            } else if (!digraph.containsKey(node)) {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException removenodeE) { //the node does not exist
            System.out.println("No such node exist in the graph");
        }
    }

    //Add a new edge between 2 nodes into the graph, from source to destination
    //input: source node, destination node
    //output: edge is only added to the source node's set of destination nodes
    public void addEdge(Literal sourcenode, Literal destnode) {
        try {
            if (digraph.containsKey(sourcenode) && digraph.containsKey(destnode)) {
                if (!digraph.get(sourcenode).contains(destnode)) { //if an edge was not previously present
                    digraph.get(sourcenode).add(destnode);
                    System.out.println(sourcenode + " -> " + destnode);
                } else if (digraph.get(sourcenode).contains(destnode)) { //if the edge is already present
                    System.out.println(sourcenode + " -> " + destnode);
                }
            }
            else if (!digraph.containsKey(sourcenode) || !digraph.containsKey(destnode)) {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException addedgeE) {
            System.out.println("Either " + sourcenode + " or " + destnode + " does not exist");
        }
    }


    //Remove an edge from the graph
    //input: source and destination node's connected edge
    //output: edge is removed from the source node's set of destination
    public void removeEdge(Literal sourcenode, Literal destnode) {
        try {
            if (digraph.containsKey(sourcenode) && digraph.containsKey(destnode)) {
                if(digraph.get(sourcenode).contains(destnode)) { //the edge was inside
                    digraph.get(sourcenode).remove(destnode);
                    System.out.println(destnode + " is no longer implied by " + sourcenode);
                } else if (!digraph.get(sourcenode).contains(destnode)){ //the edge has already been removed or does not exist
                    System.out.println(destnode + " is no longer implied by " + sourcenode);
                }
            } else if (!digraph.containsKey(sourcenode) || digraph.containsKey(destnode)) {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException removeedgeE) {
            System.out.println("Either " + sourcenode + " or " + destnode + " does not exist");
        }
    }


    //check if an edge exists between the sourcenode and destnode
    public boolean checkEdge(Literal sourcenode, Literal destnode) {  //need to use system print
        if (!digraph.containsKey(sourcenode) || !digraph.containsKey(destnode)) {
            throw new NoSuchElementException("Either " + sourcenode + " or " + destnode + " does not exist");
        }
        return digraph.get(sourcenode).contains(destnode);
    }

    public Iterator iterator(){
        return digraph.keySet().iterator();
    }


    public DirectedGraph getTranspose() { // you want to transpose an existing graph
        DirectedGraph transposedgraph = new DirectedGraph();
        for (Map.Entry<Literal, HashSet<Literal>> entry : digraph.entrySet()) {
            for( Literal destnodes : entry.getValue()){
                transposedgraph.addNode(destnodes);
                transposedgraph.addNode(entry.getKey());
                transposedgraph.addEdge(destnodes, entry.getKey());
            }
        }
        return transposedgraph;
    }

    public void forwardreach(Literal node, Set<Literal> reached, Stack stack){
        //each node is checked sequentially and pushed into the stack in order
        //every node that the one in focus can reach is added into the array

        reached.add(node); //starting node can reach itself

        HashSet<Literal> iterable = digraph.get(node);
        Iterator<Literal> itr = iterable.iterator();
        while(itr.hasNext()){
            Literal n = itr.next();
            if(!reached.contains(n)){
                forwardreach(n, reached, stack);
            }
        }
        stack.push(node);
    }

    public HashMap<Literal, ArrayList<Literal>> backreach(Literal node, Set<Literal> reached, Literal root, HashMap<Literal, ArrayList<Literal>> SCCs) {
        if(root == null){
            root = node;
            SCCs.put(node, new ArrayList<Literal>());
            SCCs.get(node).add(node);
        }
        else {
            SCCs.get(root).add(node);
        }

        reached.add(node);

        HashSet<Literal> iterable = digraph.get(node);
        Iterator<Literal> itr = iterable.iterator();
        while(itr.hasNext()){
            Literal n = itr.next();
            if(!reached.contains(n)){
                backreach(n, reached, root, SCCs);
            }
        }
        return SCCs;
    }


    public HashMap<Literal, ArrayList<Literal>> printSCCS() {
        Stack stack = new Stack();
        Set<Literal> reached = new HashSet<Literal>();
        HashMap<Literal, ArrayList<Literal>> allSCCs = new HashMap<Literal, ArrayList<Literal>>();


        for (Literal i : digraph.keySet()) {
            if (!reached.contains(i)) {
                forwardreach(i, reached, stack);
            }
        }
        DirectedGraph transposed = getTranspose();

        for (Literal i : digraph.keySet()) {
            reached.clear();
        }
        while (stack.empty() == false) {
            Literal v = (Literal) stack.pop();
            if (!reached.contains(v)) {
                transposed.backreach(v, reached, null, allSCCs);
            }
        }

        for (Map.Entry<Literal, ArrayList<Literal>> entry : allSCCs.entrySet()) {
            for (Literal checknegation : entry.getValue()) {
                try {
                    if (entry.getValue().contains(checknegation.getNegation()))
                        throw new Exception();
                } catch (Exception unsatisfiable) {
                    System.out.println("This problem is unsatisfiable");
                    return allSCCs;
                }
            }
        }
        System.out.println("This problem is satisfiable");
        return allSCCs;
    }

    public int getSize(){
        return digraph.size();
    }

    public void displaygraph(){
        for (Object sourcenodes : digraph.keySet()){
            System.out.println(sourcenodes + "\t" + digraph.get(sourcenodes));
        }
    }
}




