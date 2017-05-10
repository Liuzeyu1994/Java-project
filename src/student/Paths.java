
package student;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.Edge;
import models.Node;
import models.ReturnStage;

/** This class contains Dijkstra's shortest-path algorithm and some other methods. */
public class Paths {

    /** Return the shortest path from start to end, or the empty list if a path
     * does not exist.
     * Note: The empty list is NOT "null"; it is a list with 0 elements. */
    public static List<Node> shortestPath(Node start, Node end, ReturnStage state) {
        /* TODO Read note A7 FAQs on the course piazza for ALL details. */
        Heap<Node> F= new Heap<Node>(); // As in lecture slides
        int max_htl = 2;
        // map contains an entry for each node in S or F. Thus,
        // |map| = |S| + |F|.
        // For each such key-node, the value part contains the shortest known
        // distance to the node and the node's backpointer on that shortest path.
        HashMap<Node, SFdata> map= new HashMap<Node, SFdata>();
        
        for(Node n:state.allNodes()){
        	double[] newWtime_array = {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE};
        	map.put(n, new SFdata(newWtime_array, null, 0));
        	F.add(n, Double.MAX_VALUE);
        }
        F.updatePriority(start, 0);
        double[] init_time = {Double.MAX_VALUE,Double.MAX_VALUE,0.0};	//[corresponds to hostile visited 0,1,2]
        map.put(start, new SFdata(init_time, null, 1));	//time), bk, speed
        
        // invariant: as in lecture slides, together with def of F and map
        while (F.size() != 0) {
            Node f= F.poll();
            //System.out.println("f: "+f + "ID: "+f.getId());
            
            if (f == end){
            	//System.out.println(map.get(end).backPointer);
            	return constructPath(end, map);
            }
            
            
            for (Edge e : f.getExits()) {// for each neighbor w of f
                Node w= e.getOther(f);
                if(!F.isInHeap(w)){continue;}	// w not in heap
                
                //System.out.println("w: "+w+"ID: "+w.getId());

                SFdata wData= map.get(w);
                double curr_speed = map.get(f).speed;
                double next_speed = curr_speed;
                if(w.getId()==34){
            		int temp = 1;
            		temp = temp + 2;
            	}
                /** update the speed and number of hostile planets visited */
                if(w.isHostile()){
                	// hostile
                	if(w.hasSpeedUpgrade()){
                		//has speed upgrade
                		if(curr_speed>=1.2){
                			next_speed = Math.max(1.0, curr_speed-0.2);
                			}
                		next_speed = next_speed + 0.2;
                	}else{
                		if(curr_speed>=1.2){
                			next_speed = Math.max(1.0, curr_speed-0.2);
                		}
                	}
                }else{
                	// non-hostile
                	if(w.hasSpeedUpgrade()){
                		next_speed = next_speed + 0.2;
                	}
                }
                int m = 0;	// check current m for this node
                double[] f_time = map.get(f).time_tot;
                while(m<max_htl && f_time[m]==Double.MAX_VALUE){
                	m = m+1;
                }          
                double fTime= f_time[m];
                double newWtime= fTime + e.length/map.get(f).speed;
                
                //newWtime= fTime + e.length;
                
                int balance = m;	// the remaining number of hostile planet that you can visit
                if(w.isHostile()){balance = balance-1;}
                
                if (balance>=0 && newWtime < wData.time_tot[balance]) { 
                    wData.time_tot[balance]= newWtime;
                    wData.backPointer= f;
                    wData.speed = next_speed;
                    F.updatePriority(w, newWtime);
                }
            }
        }

        // no path from start to end
        System.out.println("No path found");
        return new LinkedList<Node>();
    }


    /** Return the path from the start node to node end.
     *  Precondition: nData contains all the necessary information about
     *  the path. */
    public static List<Node> constructPath(Node end, HashMap<Node, SFdata> nData) {
        LinkedList<Node> path= new LinkedList<Node>();
        Node p= end;
        // invariant: All the nodes from p's successor to the end are in
        //            path, in reverse order.
        while (p != null) {
            path.addFirst(p);
            p= nData.get(p).backPointer;
        }
        return path;
    }

    /** Return the sum of the weights of the edges on path path. */
    public static int pathDistance(List<Node> path) {
        if (path.size() == 0) return 0;
        synchronized(path) {
            Iterator<Node> iter= path.iterator();
            Node p= iter.next();  // First node on path
            int s= 0;
            // invariant: s = sum of weights of edges from start to p
            while (iter.hasNext()) {
                Node q= iter.next();
                s= s + p.getConnect(q).length;
                p= q;
            }
            return s;
        }
    }

    /** An instance contains information about a node: the previous node
     *  on a shortest path from the start node to this node and the distance
     *  of this node from the start node. */
    private static class SFdata {
        private Node backPointer; // backpointer on path from start node to this one
        private double[] time_tot; // total time from start node to this one
        private double speed;
        /** Constructor: an instance with distance d from the start node and
         *  backpointer p.*/
        private SFdata(double[] t, Node p, double s) {
            time_tot= t;     // Distance from start node to this one.
            backPointer= p;  // Backpointer on the path (null if start node)
            speed = s;
        }

        /** return a representation of this instance. */
        public String toString() {
            return "dist " + time_tot + ", bckptr " + backPointer;
        }
    }
}
