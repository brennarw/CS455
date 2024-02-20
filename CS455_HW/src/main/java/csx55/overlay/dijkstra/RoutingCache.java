package csx55.overlay.dijkstra;

import java.util.ArrayList;

public class RoutingCache {

    private ArrayList<String> cachedRoutes;

    public RoutingCache(){
        cachedRoutes = new ArrayList<>();
    }

    public boolean checkPreviousRoutes(String startHost, String endHost){

        if(cachedRoutes.isEmpty()){
            return false;
        }
        else{
            for(int i = 0; i < cachedRoutes.size(); i++){
                String[] route = cachedRoutes.get(i).split("---");
                if(route[0].equals(startHost) && route[route.length - 1].equals(endHost)){
                    return true;
                }
            }
            return false;
        }
    }

    public String findExistingRoute(String startHost, String endHost){

        if(cachedRoutes.isEmpty()){
            return "Route doesn't exist - cache is empty";
        }
        else{
            for(int i = 0; i < cachedRoutes.size(); i++){
                String[] route = cachedRoutes.get(i).split("---");
                if(route[0].equals(startHost) && route[route.length - 1].equals(endHost)){
                    return cachedRoutes.get(i);
                }
            }
            return "Route doesn't exist.";
        }
    }

    public void addNewRoute(String path){
        cachedRoutes.add(path);
    }

    public void printCachedRoutes() {
        if(cachedRoutes.isEmpty()){
            System.out.println("---No routes have been cached---");
        }
        else{
            for(int i = 0; i < cachedRoutes.size(); i++){
                System.out.println(cachedRoutes.get(i));
            }
        }
    }
    
    
}
