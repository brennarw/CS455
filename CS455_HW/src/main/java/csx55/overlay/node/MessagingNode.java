package csx55.overlay.node; 

import java.io.IOException;
import java.net.Socket;
import java.net.InetAddress;
import java.util.Hashtable;
import java.util.Map;
import java.util.ArrayList;
import java.util.Scanner; 
import java.util.Random;

import csx55.overlay.dijkstra.RoutingCache;
import csx55.overlay.dijkstra.ShortestPath;
import csx55.overlay.transport.*;
import csx55.overlay.wireformats.*;

public class MessagingNode extends Node{
   
     //attributes needed to display from the registry node:
     private int messagesSent = 0;
     private int messagesReceived = 0;
     private long messagesSentSummation = 0;
     private long messagesReceivedSummation = 0;
     private int messagesRelayed = 0; 

     private static boolean exit = false;
     
     //store the sockets to all connecting nodes here
     public Hashtable<String, MessagingNodeInfo> peerNodes = new Hashtable<String, MessagingNodeInfo>(); //the key is in the following format: "hostName:portNumber"

     private ArrayList<String> overlayWeights = new ArrayList<>();
     private ArrayList<String> overlayHostNames = new ArrayList<>();
     private int overlayOwnHostIndex;
     
     private int portNumber;
     private String IPAddress; 
     private String hostName; 
     private boolean finishedMessageRelaying = false;

     private String registryHostName;
     private int registryPortNum; 
     private TCPServerThread server; 
     private Socket socket; //this is the socket that connects the messaging node with the registry node
     private Thread serverThread;
     private TCPSender sender; //this is a sender object just for the register
     private RoutingCache routingCache = new RoutingCache();

     public MessagingNode(String registryHostName, int registryPortNum) {
          this.registryHostName = registryHostName;
          this.registryPortNum = registryPortNum;
     }

     public void createConnection(){
          //spawn a server thread for this messaging node
          server = new TCPServerThread(0, this); //pass in a zero because you want to utilize the wildcard constructor and find an available port number
          serverThread = new Thread(server);
          serverThread.start(); 
          try{

               //access the ip address of the registry host name and convert it to a string
               InetAddress ipAddress = InetAddress.getByName(registryHostName); 
               String registryIP = ipAddress.getHostAddress(); 

               //create a socket to connect with the registry node
               socket = new Socket(registryIP, registryPortNum); 

               //start a reciever thread for this messaging node with the socket that was just created with the registry node
               server.spawnReceiverThread(socket);

               //set portNumber and IP address class variables in order to register the messaging node with the registry node:
               hostName = InetAddress.getLocalHost().getHostName();
               //if the hostname has a dash in the name, it needs to be removed
               fixHostName();

               portNumber = server.getPortNumber();
               InetAddress ip = InetAddress.getLocalHost();
               IPAddress = ip.getHostAddress(); 
               
               System.out.println("HOST: " + hostName + " | " + portNumber);
             
          } catch(IOException io){
               System.out.println("messagingNode creatConnection error");
          }

     }

     public void fixHostName(){
          if(hostName.contains("-")){
               hostName = hostName.replace("-", "");
          }
     }

     public void registerNode(){
          try{
               Register registerNode = new Register(hostName, portNumber, IPAddress);
               sender = new TCPSender(socket);
               sender.sendData(registerNode.getBytes()); 

          } catch(IOException io){
               System.out.println("MessagingNode registerNode error");
          }
     }

     public void setPortNumber(int portNum){
          portNumber = portNum;
     }

     public void populateOverlayList(){
          for(int i = 0; i < overlayWeights.size(); i++){
               String[] connection = overlayWeights.get(i).split("---");
               if(overlayHostNames.isEmpty() || !overlayHostNames.contains(connection[0])){
                    overlayHostNames.add(connection[0]);
                    String key = hostName + ":" + portNumber;
                    if(connection[0].equals(key)){
                         overlayOwnHostIndex = overlayHostNames.size() - 1; 
                    }
               }
          }
     }

     public void printPeerNodes(){
          for(Map.Entry<String, MessagingNodeInfo> node : peerNodes.entrySet()){
               System.out.println(node.getKey());
          }
     }

     public void printLinkWeights(){
          for(String link : overlayWeights){
               System.out.println(link);
          }
     }

     public String findKey(String hostName) {
          for(Map.Entry<String, MessagingNodeInfo> node : peerNodes.entrySet()){
               if(hostName.equals(node.getKey())){
                    return node.getKey();
               }
          }
          return "";
     }

     public int findIndexOf(String[] path) { //this method finds the index of this messaging nodes 
          String key = hostName + ":" + portNumber;
          for(int i = 0; i < path.length; i++){
               if(path[i].equals(key)){
                    return i;
               }
          }
          return -1;

     }

     public void sendOverRoute(String route, int message){
          //split the path up into an array list and then start sending the message
          String[] pathStrings = route.split("---");

          int hostIndex = findIndexOf(pathStrings);
          if(hostIndex != -1){
               if(hostIndex + 1 == pathStrings.length){ //then it is the receiveing node, not a relaying node
                    messagesReceived++;
                    messagesReceivedSummation += message;
               }
               else{ //then it is a relaying messaging 

                    int nextNodeIndex = hostIndex + 2;

                    MessagingNodeInfo nextNode = peerNodes.get(pathStrings[nextNodeIndex]);
                    PeerNodeCommunication pathRelay = new PeerNodeCommunication(route, message);
                    try{
                         nextNode.getSender().sendData(pathRelay.getBytes());
                    } catch(IOException e){
                         System.out.println("error in relaying messages");
                    }
                    messagesRelayed++;
               }
          }
     }

     public void sendMessages(int numRounds){
          Random randy = new Random();
          ShortestPath routing = new ShortestPath(overlayWeights, routingCache);
          //routing.printMap();
          //loop through numRounds times and send messages each time
          for(int i = 0; i < numRounds; i++){
               for(int j = 0; j < 5; j++){
                    //find a random messaging node to send a message to - excluding itself
                    int randomMessagingNodeIndex = randy.nextInt(overlayHostNames.size());
                    String startNode = hostName + ":" + portNumber;
                    int message = randy.nextInt(); //generates a number between [-2147483648, 2147483647]
                    if(randomMessagingNodeIndex != overlayOwnHostIndex){ //index is valid
                         String endNode = overlayHostNames.get(randomMessagingNodeIndex);
                         String path = routing.findShortestPath(startNode, endNode);
     
                         //increment counters:
                         messagesSent++;
                         messagesSentSummation += message;
     
                         sendOverRoute(path, message);
                    }
                    else{
                         randomMessagingNodeIndex = (randomMessagingNodeIndex + 1) % overlayHostNames.size(); //add one if the index matches the index of this messaging node
                         String endNode = overlayHostNames.get(randomMessagingNodeIndex);
                         String path = routing.findShortestPath(startNode, endNode);
     
                         //increment counters
                         messagesSent++;
                         messagesSentSummation += message;
     
                         sendOverRoute(path, message);
                    }
               }
          }

          finishedMessageRelaying = true; 
     }

     public void resetCounters(){
          messagesSent = 0;
          messagesReceived = 0;
          messagesSentSummation = 0;
          messagesReceivedSummation = 0;
          messagesRelayed = 0; 
          finishedMessageRelaying = false;
     }

     public synchronized void onEvent(Event event, Socket socket) { //this socket is sent from the TCPReciever, it represents the socket that this event is coming from 
          int messageType = event.getMessageType();

          switch(messageType){
               case 0: //this case handles when a peer node wants to register 
                    try{
                         Register peerNode = (Register)event;

                         //make a MessagingNodeInfo object
                         TCPSender sender = new TCPSender(socket);
                         MessagingNodeInfo peerNodeInfo = new MessagingNodeInfo(socket, peerNode.getHostName(), peerNode.getIPAddress(), peerNode.getPortNumber(), sender);

                         //make the key and add the peer node to the peerNodes hashmap
                         String key = peerNode.getHostName() + ":" + peerNode.getPortNumber();
                         peerNodes.put(key, peerNodeInfo);
                         System.out.println("Accepted connection from: " + key);

                    } catch(IOException e){
                         System.out.println("error in messagingNode onEvent case 0");
                    }
                    break;
               case 2: //this case handles recieving any String message from the registry node 
                    RegistryResponse response = (RegistryResponse)event;
                    System.out.println(response.getResponseString());

                    if(response.getResponseString().equals("PULL_TRAFFIC_SUMMARY")){
                         //send all information from relaying messages to registry node
                         TaskSummaryResponse taskSummary = new TaskSummaryResponse(hostName, IPAddress, portNumber, messagesSent, messagesSentSummation, messagesReceived, messagesReceivedSummation, messagesRelayed);
                         try{
                              sender.sendData(taskSummary.getBytes());
                         } catch(IOException e){
                              System.out.println("error in sending traffic summary");
                         }
                    }
                    resetCounters();
                    break;
               case 4: //this case handles receiving an arraylist of messagingNodeInfo objects it needs to connect with. //add these nodes to the connectedSockets hashtable
                    MessagingNodesList messagingNodesList = (MessagingNodesList)event;
                    ArrayList<MessagingNodeInfo> peerInfo = messagingNodesList.getPeerNodesList();
                    for(int i = 0; i < peerInfo.size(); i++){
                         try{
                              Socket peerSocket = new Socket(peerInfo.get(i).getIPAddress(), peerInfo.get(i).getPortNumber());
                              TCPSender peerNodeSender = new TCPSender(peerSocket);

                              //now create a MessagingNodeInfo object for this peer node and add it to the hashtable
                              MessagingNodeInfo peerNodeInfo = new MessagingNodeInfo(peerSocket, peerInfo.get(i).getHostName(), peerInfo.get(i).getIPAddress(), peerInfo.get(i).getPortNumber(), peerNodeSender);
                              String key = peerInfo.get(i).getHostName() + ":" + peerInfo.get(i).getPortNumber();
                              peerNodes.put(key, peerNodeInfo);

                              //now start a TCPReceiver thread for this peer node
                              server.spawnReceiverThread(peerSocket);

                              //now register with that node
                              Register peerRegistration = new Register(hostName, portNumber, IPAddress);
                              peerNodeSender.sendData(peerRegistration.getBytes());

                              
                              
                         } catch(IOException e){
                              System.out.println(e.getMessage());
                         }    
                    }
                    try{
                         //use the socket passed into onEvent to send a message back to the registry node that all connections were made successfully
                         String returnMessage = "From host [" + hostName + "] : All peer node connections were successful.";
                         PeerNodeResponse registryResponse = new PeerNodeResponse(returnMessage);

                         //sender object for the registry
                         sender.sendData(registryResponse.getBytes());

                    } catch(IOException e){
                         System.out.println("error in messagingNode onEvent case 4");
                    }
                    
                    break;
               case 6: //this receives a list of all links in the overlay
                    LinkWeights overlayLinks = (LinkWeights)event;
                    overlayWeights = overlayLinks.getLinkWeightList();
                    populateOverlayList();

                    System.out.println("OVERLAY LINK WEIGHTS RECEIVED");
                    System.out.println("Number of Links in Overlay: " + overlayLinks.getNumLinks());
                    printLinkWeights();


                    //now send a response back:
                    try{
                         String returnMessage = "From host [" + hostName + "] : Link Weights received.";
                         PeerNodeResponse linkResponse = new PeerNodeResponse(returnMessage);

                         sender.sendData(linkResponse.getBytes());
                         
                    } catch(IOException e){
                         System.out.println("error in messagingNode onEvent case 6");
                    }

                    break;
               case 7: //this recieves the "task initiate" message with the number of rounds
                    TaskInitiate startTask = (TaskInitiate)event; 
                    System.out.println(startTask.getMessage() + ": " + startTask.getNumRounds() + " rounds");

                    //printPeerNodes();
                    
                    sendMessages(startTask.getNumRounds());

                    while(finishedMessageRelaying == false){
                         continue;
                    }
                    //once this node has finished relaying messages, send a response back to the registry node
                    System.out.println("TASK_COMPLETE"); 
                    String message = "From host [" + hostName + "]: TASK_COMPLETE";
                    PeerNodeResponse messageRelaying = new PeerNodeResponse(message);
                    try{
                         sender.sendData(messageRelaying.getBytes());
                    } catch(IOException e){
                         System.out.println("error in messagingNode onEvent case 8");
                    }
                    //resetCounters();
                    break;
               case 8: //this case handles the messaging nodes relaying messages to each other
                    PeerNodeCommunication peerNodeCommunication = (PeerNodeCommunication)event;
                    String route = peerNodeCommunication.getPath();
                    int messageRelayed = peerNodeCommunication.getMessage();
                    sendOverRoute(route, messageRelayed);
                    break;
               case 10: //this case handles deregistering the messaging node
                    DeregisterResponse deregisterResponse = (DeregisterResponse)event;
                    if(deregisterResponse.getStatusCode() == 0){
                         System.out.println("Deregistration FAILED: overlay is set up OR messaging node was never registered");
                    }
                    else if(deregisterResponse.getStatusCode() == 1){
                         System.out.println("Deregistration SUCCESSFUL: messaging node was successfully deregistered. Exiting now...");

                         try{
                              socket.close(); 
                              killThreads();
                              exit = true;
                              System.exit(0);
                         } catch(IOException e){
                              System.out.println("error in messaging node onEvent case 10");
                         }
                    }
                    break;
               default:
                    System.out.println("hit the default case in MessagingNode onEvent");
                    break;

          }
     }

     public void killThreads(){
          serverThread.interrupt();
     }

     public void onCommandLine(int[] instruction){
          switch(instruction[0]){
               case 6: //this case handles "print-shortest-path"
                    routingCache.printCachedRoutes();
                    break;
               case 7: //this case handles exiting the overlay
                    try{
                         Deregister deregister = new Deregister(IPAddress, portNumber);

                         sender.sendData(deregister.getBytes());
                    } catch(IOException e){
                         System.out.println("Error in messaging node onCommandLine case 7");
                    }
                    break;
               default:
                    System.out.println("Command line doesn't exist.");
                    break;
          }
     }

     public static void main(String[] args){
          if(args.length != 2){
               System.exit(0);
          }

          //create an instance of the messaging node
          MessagingNode messageNode = new MessagingNode(args[0], Integer.parseInt(args[1]));
          
          messageNode.createConnection();
          messageNode.registerNode();

          Scanner scnr = new Scanner(System.in);

          while(exit == false){
               String commandLine = scnr.nextLine(); //this is a blocking call
               CommandLineFactory commandFactory = new CommandLineFactory(commandLine);
               int[] commandNumber = commandFactory.findCommandEvent();
               messageNode.onCommandLine(commandNumber); 
          }

          scnr.close();
          System.exit(0);
          
     }
     
}
