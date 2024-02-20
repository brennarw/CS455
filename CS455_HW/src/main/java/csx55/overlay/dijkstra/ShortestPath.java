package csx55.overlay.dijkstra;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.PriorityQueue;

public class ShortestPath {

    class TravelNode{
        String hostName;
        ArrayList<TravelNode> neighbors = new ArrayList<>(); //holds neighbor in 0 index, distance in 1 index
        ArrayList<Integer> linkWeights = new ArrayList<>();
        TravelNode previousHost;
        int previousWeight;
        int distance;
        boolean explored;

        public TravelNode(String hostName, int distance){
            this.hostName = hostName;
            this.distance = distance;
            this.explored = false;
            this.previousHost = null;
            this.previousWeight = -1;
        }

        void addNeighbors(TravelNode neighbor){
            neighbors.add(neighbor);
        }
        void addLinkWeights(int weight) {
            linkWeights.add(weight);
        }
        void setDistance(int distance) {
            this.distance = distance;
        }
    }

    private ArrayList<String> linkWeightsList = new ArrayList<String>();
    private Hashtable<String, TravelNode> overlayMap = new Hashtable<>();
    private RoutingCache routingCache;

    public ShortestPath(ArrayList<String> linkWeightsList, RoutingCache routingCache){
        this.linkWeightsList = linkWeightsList;
        this.routingCache = routingCache;
        createTravelNodeObjects();
        addNeighborsToMap();
    }

    public void createTravelNodeObjects() {
        for(int i = 0; i < linkWeightsList.size(); i++){
            String[] connection = linkWeightsList.get(i).split("---");
            if(overlayMap.isEmpty() || !overlayMap.contains(connection[0])){
                TravelNode travelNode = new TravelNode(connection[0], Integer.MAX_VALUE);
                overlayMap.put(connection[0], travelNode);
            }
        }
    }

    public void addNeighborsToMap(){
        for(int i = 0; i < linkWeightsList.size(); i++){
            String[] connection = linkWeightsList.get(i).split("---"); 
            String currentHost = connection[0]; //get the current host name
            for(int j = 0; j < linkWeightsList.size(); j++){ //loop through to find all of this nodes neigbors and wieghts
                String[] nodeLink = linkWeightsList.get(j).split("---");
                if(currentHost.equals(nodeLink[0]) && (overlayMap.get(currentHost).neighbors.isEmpty() || !overlayMap.get(currentHost).neighbors.contains(overlayMap.get(nodeLink[2])))){ //check that this travelNode doesn't already have this TravelNode as a neighbor
                    overlayMap.get(currentHost).addNeighbors(overlayMap.get(nodeLink[2])); //add the neightbor to this TravelNode neighbors arrayList
                    overlayMap.get(currentHost).addLinkWeights(Integer.parseInt(nodeLink[1])); //add the corresponding link weight to this TravelNode neighbor
                }
                else if(currentHost.equals(nodeLink[2]) && (overlayMap.get(currentHost).neighbors.isEmpty() || !overlayMap.get(currentHost).neighbors.contains(overlayMap.get(nodeLink[0])))){
                    overlayMap.get(currentHost).addNeighbors(overlayMap.get(nodeLink[0])); //add the neighbor to this TravelNode neighbors arrayList
                    overlayMap.get(currentHost).addLinkWeights(Integer.parseInt(nodeLink[1])); //add the corresponding link weight to this TravelNode neighbor
                }
            }
        }
    }

    public void printMap(){
        for(Map.Entry<String, TravelNode> node : overlayMap.entrySet()){
            System.out.println(node.getKey() + " neighbors: ");
            for(int i = 0; i < node.getValue().neighbors.size(); i++){
                System.out.println(node.getValue().neighbors.get(i).hostName + " -> " + node.getValue().linkWeights.get(i));
            }
            System.out.println();
        }
    }

    //now implement the shortest path method - should take in a startNode hostName and an endNode hostName

    public String findShortestPath(String startNode, String endNode){

        //clear the distances calculated from past routes in each travelNode and make all nodes explored attribute false
        clearData();

        //get the travelNode of the start node
        TravelNode start = overlayMap.get(startNode);
        start.explored = true;
        start.distance = 0;
        TravelNode end = overlayMap.get(endNode);

        
        //check to see if the path already exists:
        if(routingCache.checkPreviousRoutes(startNode, endNode) == true){
            return routingCache.findExistingRoute(startNode, endNode);
        }
        else{

            TravelNode endPathNode = dijkstraPath(start, end);
            String shortestPath = makePathString(endPathNode);

            //now cache this new path:
            routingCache.addNewRoute(shortestPath);

            return shortestPath;
        }

    }


    public TravelNode dijkstraPath(TravelNode startNode, TravelNode endNode){ //returns the end node with all previous hosts set
        startNode.distance = 0;
        PriorityQueue<TravelNode> pq = new PriorityQueue<>(Comparator.comparingInt(node -> node.distance));
        pq.add(startNode);

        while(!pq.isEmpty() || endNode.explored == false) {
            TravelNode current = pq.poll();
            current.explored = true;

            for(int i = 0; i < current.neighbors.size(); i++){
                if(!current.neighbors.get(i).explored){
                    int nextDistance = current.distance + current.linkWeights.get(i);
                    if(nextDistance < current.neighbors.get(i).distance){
                        current.neighbors.get(i).distance = nextDistance;
                        current.neighbors.get(i).previousHost = current;
                        current.neighbors.get(i).previousWeight = current.linkWeights.get(i);
                        pq.add(current.neighbors.get(i));
                    }
                }
            }
        }
        return endNode;

    }

    public String makePathString(TravelNode endNode){
        String path = "";

        while(endNode != null){
            if(endNode.previousWeight != -1){
                path = String.valueOf(endNode.previousWeight) + "---" + endNode.hostName + "---" + path;
            }
            else{
                path = endNode.hostName + "---" + path;
            }
            endNode = endNode.previousHost;
        }
        //get rid of the last dashes
        path = path.substring(0, path.lastIndexOf("---"));
        return path;
    }

    public void clearData(){

        for(Map.Entry<String, TravelNode> node : overlayMap.entrySet()){
            node.getValue().distance = Integer.MAX_VALUE; //reset the distances of each node for a new route to be calculated
            node.getValue().explored = false;
            node.getValue().previousHost = null;
        }
    }


}
