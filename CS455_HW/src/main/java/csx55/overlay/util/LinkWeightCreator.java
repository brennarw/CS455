package csx55.overlay.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import csx55.overlay.wireformats.MessagingNodeInfo;

public class LinkWeightCreator {
    
    private Hashtable<Integer, MessagingNodeInfo> registeredSockets = new Hashtable<Integer, MessagingNodeInfo>(); 
    private ArrayList<List<Integer>> socketLinks = new ArrayList<List<Integer>>();
    private ArrayList<String> linkWeightsList = new ArrayList<String>();

    public LinkWeightCreator(ArrayList<List<Integer>> socketLinks, Hashtable<Integer, MessagingNodeInfo> registeredSockets){
        this.socketLinks = socketLinks;
        this.registeredSockets = registeredSockets;
    }

    public void setLinkWeights(){

        for(int i = 0; i < socketLinks.size(); i++) {
            String remoteHostName = registeredSockets.get(socketLinks.get(i).get(0)).getHostName();
            int remotePort = registeredSockets.get(socketLinks.get(i).get(0)).getPortNumber();

            for(int j = 1; j < socketLinks.get(i).size(); j++){
                if(socketLinks.get(i).get(j) > 0) { //if the portnumber is positive, then that means this host is responsible for connecting to that node, so generate links only for the positive portnumbers
                    //generate the random link weight
                    Random randy = new Random();
                    int random = randy.nextInt(10 - 1 + 1) + 1; //not sure how this works

                    //assign the link weight to a link
                    String linkWeight = remoteHostName +":"+ remotePort + "---" + random + "---" + registeredSockets.get(socketLinks.get(i).get(j)).getHostName() + ":" + registeredSockets.get(socketLinks.get(i).get(j)).getPortNumber();
                    linkWeightsList.add(linkWeight);
                }
            }
        }
    }

    public ArrayList<String> getLinkWeightsList(){
        return linkWeightsList;
    }

}
