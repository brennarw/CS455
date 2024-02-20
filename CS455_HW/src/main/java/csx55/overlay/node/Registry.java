package csx55.overlay.node;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner; 
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import csx55.overlay.transport.*;
import csx55.overlay.util.LinkWeightCreator;
import csx55.overlay.util.OverlayCreator;
import csx55.overlay.wireformats.*;

public class Registry extends Node{

    private TCPServerThread server; 

    private Hashtable<Integer, MessagingNodeInfo> registeredSockets = new Hashtable<Integer, MessagingNodeInfo>(); 
    private ArrayList<List<Integer>> socketLinks = new ArrayList<List<Integer>>();
    private ArrayList<String> linkWeightsList = new ArrayList<String>();
    private ArrayList<TaskSummaryResponse> taskSummaryResponses = new ArrayList<>();
    private int portNum; 

    private int taskCompleteCounter = 0;
    private int taskSummaryCounter = 0;

    private boolean overlaySetup = false; 
    private static boolean exit = false;

    private int totalMessagesSent = 0;
    private int totalMessagesReceived = 0;
    private long totalMessagesSentSummation = 0;
    private long totalMessagesReceivedSummation = 0;


    public Registry(int portNumber){
        this.portNum = portNumber;
    }

    private void spawnServerThread(){
        server = new TCPServerThread(portNum, this); 
        Thread serverThread = new Thread(server);
        serverThread.start();
    }

    public synchronized byte checkAndRegister(Socket socket, String hostName, int portNumber, String IPAddress){
        byte statusCode = -1; //0 is failure, 1 is success
        if(!socket.getInetAddress().getHostAddress().equals(IPAddress)){
            statusCode = 0; //failure becase the IP addresses don't match
            return statusCode;
        }
        else if(registeredSockets.isEmpty() || !registeredSockets.containsKey(portNumber)){ //if the hashmap is empty or doesn't contain the key then add the node to the hashmap
            //make the messagingNodeInfo object:
            try{
                TCPSender sender = new TCPSender(socket);
                MessagingNodeInfo messagingNodeInfo = new MessagingNodeInfo(socket, hostName, IPAddress , portNumber, sender);

                //add this object to the hashtable with the port number as the key, then get the length of the hashtable
                registeredSockets.put(portNumber, messagingNodeInfo);
                System.out.println(hostName + " is registered.");
                statusCode = 2; //registration is successful

            } catch(IOException e){
                System.out.println("ERROR: in registry checkAndRegister");
            }
            return statusCode;

        } else{
            statusCode = 1; //failure because it was already registered 
            return statusCode;
        }
    }

    public byte deregisterNode(int portNumber) {
        byte statusCode; 
        //if overlay is setup then the node can't deregister
        if(overlaySetup == true){
            statusCode = 0;
            return statusCode;
        }
        //remove the portNumber from the registeredSockets hashtable
        if(registeredSockets.containsKey(portNumber)){
            String key = registeredSockets.get(portNumber).getHostName() + ":" + portNumber;
            System.out.println();
            System.out.println(key + " deregistered");
            registeredSockets.remove(portNumber);
            statusCode = 1; //deregistration was successful
            return statusCode;
        } 
        else {
            statusCode = 0;
            return statusCode;
        }

    }

    public void populateLinkTable(){
        if(socketLinks.size() != 0){ //this is in case the user tries to send the command line "setup-overlay" multiple times without killing the terminal first or exiting the overlay
            socketLinks.clear();
        }
        if(registeredSockets.size() == 0){
            System.out.println("ERROR: no messaging nodes have been registered");
        } else{
            for(Map.Entry<Integer, MessagingNodeInfo> entry : registeredSockets.entrySet()){
                ArrayList<Integer> links = new ArrayList<>();
                links.add(entry.getKey()); //the port number at index 0 is the portNumber of the base messaging node
                socketLinks.add(links); //adds an arrayList to an arrayList
            }
        }
    }

    public int establishLinks(int linkNumber){
        int numberOfLinks = 0; //keeps track of how many links have been established
        int round = 1; //keeps track of what round its on - how many indices it needs to advance
        int statusCode;
        if(registeredSockets.size() <= linkNumber){
            System.out.println("ERROR: not enough registered nodes to create overlay.");
            statusCode = 0; //failure
            return statusCode;
        } else{
            int oddCase = Integer.MIN_VALUE;
            if(linkNumber % 2 == 1){
                oddCase = linkNumber;
                linkNumber = linkNumber - 1;
            }
            while(numberOfLinks < (linkNumber * registeredSockets.size())) { //while the number of links < the max amount of links
                for(int i = 0; i < socketLinks.size(); i++){
                    //check if the arrayList has a size of linkNumber + 1
                    if(socketLinks.get(i).size() - 1 >= linkNumber){
                        continue;
                    }
                    else{
                        int currentPort = socketLinks.get(i).get(0);
                        int linkPort = socketLinks.get((i + round) % socketLinks.size()).get(0); //gets the first index, which is the portNumber for that socket, of the socket that i wants to link to
                        socketLinks.get(i).add(linkPort); //add the positive value of this portNumber to i 
                        socketLinks.get((i + round) % socketLinks.size()).add(-1 * currentPort); //add the negative portNumber of i to i+round so that we know only one connection needs to take place
                        numberOfLinks += 2;
                    }
                }
                round++;
        
            }
            //handle the case when linkNumber is odd:
            if(oddCase > linkNumber){
                for(int i = 0; i < socketLinks.size(); i++){
                    if(socketLinks.get(i).size() - 1 >= oddCase){
                        continue;
                    }
                    else{
                        int currentPort = socketLinks.get(i).get(0);
                        int linkPort = socketLinks.get((i + (socketLinks.size()/2)) % socketLinks.size()).get(0);
                        socketLinks.get(i).add(linkPort);
                        socketLinks.get((i + socketLinks.size()/2) % socketLinks.size()).add(-1 * currentPort);
                    }
                }
            }
            statusCode = 1; //success!
            return statusCode;
        }
    }

    public void listRegisteredSockets() {
        if(registeredSockets.isEmpty()){
            System.out.println("--No nodes have registered yet--");
        }
        else{
            for(Map.Entry<Integer, MessagingNodeInfo> node : registeredSockets.entrySet()){
                InetAddress address = node.getValue().getSocket().getInetAddress();
                String hostName = address.getHostName(); 
                System.out.println(hostName + " | " + node.getKey());
                System.out.println();
            }
        }
    }

    public void sendToAllNodes(byte[] marshalledLinks){
        for(Map.Entry<Integer, MessagingNodeInfo> node : registeredSockets.entrySet()){
            try{
                node.getValue().getSender().sendData(marshalledLinks);
            } catch(IOException e){
                System.out.println(e.getMessage());
                System.out.println("error in sending link weights");
            }
        }
    }

    public void printLinkWeightsList(){
        if(linkWeightsList.isEmpty()){
            System.out.println("--Overlay hasn't been set up yet--");
        }
        else{
            for(String link : linkWeightsList) {
                System.out.println(link);
            }
        }
    }

    public void sendPullTrafficSummaryRequest(){
        try{
            byte statusCode = 0; //non relevant for this response but necessary for this class
            String message = "PULL_TRAFFIC_SUMMARY";
            RegistryResponse request = new RegistryResponse(statusCode, message);
            sendToAllNodes(request.getBytes());
        } catch(IOException e){
            System.out.println("problem sending out traffic summary request");
        }
    }

    public void handleTaskSummary(TaskSummaryResponse taskSummary){
        taskSummaryResponses.add(taskSummary);

        totalMessagesSent += taskSummary.getMessagesSent();
        totalMessagesSentSummation += taskSummary.getMessagesSentSummation();
        totalMessagesReceived += taskSummary.getMessagesReceived();
        totalMessagesReceivedSummation += taskSummary.getMessagesReceivedSummation();
    }

    public void printTaskSummary(){
        System.out.println(" ___________________________________________________________________________________________________________________________________________________________________________");
        System.out.println("|    Nodes     |  Number of Messages Sent  |  Number of Messages Received  |  Summation of Sent Messages  |  Summation of Received Messages  |  Number of Messages Relayed  |");
        System.out.println("|___________________________________________________________________________________________________________________________________________________________________________|");

        for(int i = 0; i < taskSummaryResponses.size(); i++){
            TaskSummaryResponse summary = taskSummaryResponses.get(i);
            System.out.printf("|%14s|%27s|%31s|%30s|%34s|%30s|\n", String.valueOf(summary.getHostName()), String.valueOf(summary.getMessagesSent()), String.valueOf(summary.getMessagesReceived()), String.valueOf(summary.getMessagesSentSummation()), String.valueOf(summary.getMessagesReceivedSummation()), String.valueOf(summary.getMessagesRelayed()));
        }
        System.out.printf("|     SUM      |%27s|%31s|%30s|%34s|                              |\n", String.valueOf(totalMessagesSent), String.valueOf(totalMessagesReceived), String.valueOf(totalMessagesSentSummation), String.valueOf(totalMessagesReceivedSummation));
        System.out.println("|___________________________________________________________________________________________________________________________________________________________________________|");
    }

    public void resetCounters(){
        taskSummaryResponses.clear();
        taskCompleteCounter = 0;
        taskSummaryCounter = 0;
        totalMessagesSent = 0;
        totalMessagesReceived = 0;
        totalMessagesSentSummation = 0;
        totalMessagesReceivedSummation = 0;
    }


    public synchronized void onEvent(Event event, Socket socket){
        int messageType = event.getMessageType();

        switch(messageType){
            case 0: //messaging node wants to register
                try{
                    Register register = (Register) event; //cast the object to the correct type to access the methods unique to that object

                    //TCP connection is already bidirectional, just need to access the specific socket for this node!!
                    byte returnStatusCode = checkAndRegister(socket, register.getHostName(), register.getPortNumber(), register.getIPAddress());
                    
                    String registrationResponse; 
                    TCPSender sender = new TCPSender(socket);
                    RegistryResponse response;

                    if(returnStatusCode == 0){
                        //registration was a failure because the IP addresses don't match
                        registrationResponse = "Registration request FAILED: IP addresses don't match";
                        response = new RegistryResponse(returnStatusCode, registrationResponse);
                        sender.sendData(response.getBytes()); 
                    } else if(returnStatusCode == 1) {
                        //registration was a failure because it was already registered
                        registrationResponse = "Registration request FAILED: this messaging node is already registered.";
                        response = new RegistryResponse(returnStatusCode, registrationResponse);
                        sender.sendData(response.getBytes());
                    } else if(returnStatusCode == 2){
                        //registration was a success
                        registrationResponse = "Registration request SUCCESSFUL. The number of messaging nodes currently constituting the overlay is (" + registeredSockets.size() + ")"; 
                        response = new RegistryResponse(returnStatusCode, registrationResponse);
                        //System.out.println(response);
                        sender.sendData(response.getBytes());
                    }
                    break;

                } catch (IOException io){
                    System.out.println("problem in Registry Node");
                    break;
                }
            case 3:
                try{
                    Deregister deregister = (Deregister)event;
                    byte statusCode = (deregisterNode(deregister.getPortNumber()));
                    DeregisterResponse response = new DeregisterResponse(statusCode);
    
                    TCPSender sender = new TCPSender(socket);

                    sender.sendData(response.getBytes()); //send the response after the 

                } catch(IOException e) {
                    System.out.println("Error in registry onEvent case 3");
                }
                break;
            
            case 5:
                PeerNodeResponse connectionsResponse = (PeerNodeResponse)event;
                System.out.println(connectionsResponse.getPeerNodeResponse());

                if(connectionsResponse.getPeerNodeResponse().contains("TASK_COMPLETE")){
                    taskCompleteCounter++;
                }
                if(taskCompleteCounter == registeredSockets.size()){
                    //if all nodes have finished sending their messages, have the registry node wait for 15 seconds
                    System.out.println("---Waiting for all messages to finish sending...");
                    try{
                        Thread.sleep(15000);
                    } catch(InterruptedException e){
                        System.out.println("problem with registry sleeping");
                    }
                    System.out.println("---Registry waited for 15 seconds: sending out PULL_TRAFFIC_SUMMARY request...");
                    sendPullTrafficSummaryRequest();
                    this.taskCompleteCounter = 0; 
                }
                break;
            case 9:
                TaskSummaryResponse taskSummary = (TaskSummaryResponse)event;
                taskSummaryCounter++;
                handleTaskSummary(taskSummary);

                if(taskSummaryCounter == registeredSockets.size()){
                    printTaskSummary();
                    resetCounters(); //then reset all counters
                }
                break;

            default: 
                System.out.println("hit the default case in Registry onEvent");

        }
    }

    public void onCommandLine(int[] instruction){
        switch(instruction[0]){
            case 1: //this case handles listing all registered messaging nodes
                listRegisteredSockets();
                break;
            case 3: //this case handles setting up the overlay
                if(registeredSockets.isEmpty()){
                    System.out.println("--No nodes have registered yet, can't set up an overlay--");
                }
                else{

                    //first populate the socketLinks arraylist
                    populateLinkTable();
                    
                    //second fill the socketLinks arrayList with the links each node is responsible for connecting to
                    int statusCode = establishLinks(instruction[1]); //the element at index 1 in this case is the number of links that the user establishes
                    
                    if(statusCode == 1) {
                        //make an overlayCreator object and send both lists to that object
                        OverlayCreator overlayCreator = new OverlayCreator(registeredSockets, socketLinks);
                        //call the method that will start up the overlay creation
                        overlayCreator.createOverlay();
                        
                        LinkWeightCreator linkWeights = new LinkWeightCreator(socketLinks, registeredSockets);
                        linkWeights.setLinkWeights();
                        linkWeightsList = linkWeights.getLinkWeightsList(); //this sets the class variable
                        
                        overlaySetup = true;
                        break;
                    } else{
                        break;
                    }
                }
            case 2: //list the link weights 
                printLinkWeightsList();
                break;
            case 4: //send overlay link weights
                try{

                    LinkWeights overlayWeights = new LinkWeights(linkWeightsList);
                    overlayWeights.convertToString();

                    sendToAllNodes(overlayWeights.getBytes());


                } catch(IOException e){
                    System.out.println("error in registry onCommand case 4");
                }
                break;
            case 5: //this is task initiate = start #ofRounds
                try{
                    TaskInitiate startTask = new TaskInitiate(instruction[1]);
                    sendToAllNodes(startTask.getBytes());

                } catch(IOException e){
                    System.out.println("error in registry onCommand case 5");
                }
                break;
            case 7: //handles exiting the overlay
                //clear all lists
                socketLinks.clear();
                linkWeightsList.clear();
                taskSummaryResponses.clear();

                //reset all counters
                taskCompleteCounter = 0;
                taskSummaryCounter = 0;
                totalMessagesSent = 0;
                totalMessagesReceived = 0;
                totalMessagesSentSummation = 0;
                totalMessagesReceivedSummation = 0;

                //send the message to the messaging nodes
                byte exitCode = 1; //successful
                String message = "EXITED OVERLAY";
                RegistryResponse exitResponse = new RegistryResponse(exitCode, message);
                try{
                    sendToAllNodes(exitResponse.getBytes());
                } catch(IOException e){
                    System.out.println("error in registry onCommanLine case 7");
                }
                overlaySetup = false; 
                System.out.println("---Overlay has been deconstructed---");
                break;
            case 8: //handles exiting
                exit = true; //this breaks out of the while loop in main
                System.out.println("Exiting process...");
                break;
            default:
                System.out.println("Command line doesn't exist");
                break;

        }
    }

    public static void main(String[] args){
        if(args.length != 1){
            System.out.println("---Missing Argumments---");
            System.exit(0); 
        }
        else{
            
                int portNumber = Integer.parseInt(args[0]); 

                Registry registry = new Registry(portNumber);
                registry.spawnServerThread();

                Scanner scnr = new Scanner(System.in);
            
                while(exit == false){
                    String commandLine = scnr.nextLine(); //this is a blocking call 
                    CommandLineFactory commandLineFactory = new CommandLineFactory(commandLine);
                    int[] commandNumber = commandLineFactory.findCommandEvent();
                    registry.onCommandLine(commandNumber);
                }
                scnr.close();
                System.exit(0);
        }
    }
}
