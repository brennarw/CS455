package csx55.overlay.util;

import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.io.IOException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;

import csx55.overlay.transport.*;
import csx55.overlay.wireformats.MessagingNodeInfo;

public class OverlayCreator {
    
    private Hashtable<Integer, MessagingNodeInfo> registeredSockets;
    private ArrayList<List<Integer>> socketLinks;

    private byte[] marshalledPeerNodes;
    private byte[] peerNodeBytes;
    private final int messageType = 4;

    public OverlayCreator(Hashtable<Integer, MessagingNodeInfo> registeredSockets, ArrayList<List<Integer>> socketLinks) {
        this.registeredSockets = registeredSockets;
        this.socketLinks = socketLinks;
    }

    private int connectionResponsibilites(List<Integer> messagingNodeConnections) {
        int connections = 0; 
        for(int i = 1; i < messagingNodeConnections.size(); i++){
            if(messagingNodeConnections.get(i) > 0) { //if the port number is positive, then it is this nodes responsibility to connect to it
                connections++;
            }
        }
        return connections; 
    }

    public void createOverlay () {
        for(int i = 0; i < registeredSockets.size(); i++){

            MessagingNodeInfo baseMessagingNode = registeredSockets.get(socketLinks.get(i).get(0)); //this holds the socket that connects the registry node and this specific messaging node

            int peerNodes = connectionResponsibilites(socketLinks.get(i));

            //String nodeInfoString = "message type: 4 | number of peers: peerNodes | ";

            try{
                //this starts the byte array for the messaging node in this iteration, starts with the message type and the number of links 
                marshalledPeerNodes = getMessageHeaderBytes(peerNodes);

            } catch(IOException e){
                System.out.println("ERROR: in OverlayCreator outer for loop");
                //TODO: handle error message
            }
            //now in this for loop we need to put the <hostname, portnumber> pairs of each messaging node it needs to connect with
            for(int j = 1; j < socketLinks.get(i).size(); j++){ //start this for loop at 1 since the first port number is not a peer messaging node

                if(socketLinks.get(i).get(j) > 0){ //this means that this messaging node is responsible for connect to that port
                    //find the object for each messagingNode
                    int portNumber = socketLinks.get(i).get(j);
                    MessagingNodeInfo peerMessagingNode = registeredSockets.get(portNumber);

                    //nodeInfoString += peerMessagingNode.getIPAddress() + ", " + String.valueOf(peerMessagingNode.getPortNumber()) + " | ";

                    try{
                        peerNodeBytes = peerMessagingNode.getBytes(); //this marshalls the <hostname, portnumber> for this peer node IN THIS ORDER
                    } catch(IOException e){
                        System.out.println("ERROR: in OverlayCreator inner for loop");
                    }

                    //now combine the marshalledPeerNodes array with the peerNodeBytes array
                    int length1 = marshalledPeerNodes.length;
                    int length2 = peerNodeBytes.length; 

                    byte[] temp = new byte[length1 + length2];

                    System.arraycopy(marshalledPeerNodes, 0 , temp, 0 , length1);
                    System.arraycopy(peerNodeBytes, 0, temp, length1, length2);

                    marshalledPeerNodes = temp; //reset marshalledPeerNodes to temp so that it holds the full byte[] array for the next for loop iteration for this messaging node 
                    //System.out.println(marshalledPeerNodes);
                    
                }

            }

            //marshalledPeerNodes now holds the message type, the number of peer messaging nodes this node is responsible for connecting to, and then <portNumber, ipaddress> pairs of each peer messaging node
            //now send marshalledPeerNodes to the correct messaging node using the nodeSocket found in the outer for loop
            try{
                //System.out.println("marshalled bytes: " + marshalledPeerNodes);
                //System.out.println();
                baseMessagingNode.getSender().sendData(marshalledPeerNodes); //sending the list of messaging nodes here!
                
                //for testing cases
                //String message = "this is a message from the registry";
                //baseMessagingNode.getSender().sendData(nodeInfoString.getBytes());

            } catch(IOException e){
                System.out.println(e.getMessage());
                System.out.println("ERROR: in creatOverlay sender");
            }


        }

    }

    public byte[] getMessageHeaderBytes(int numOfPeers) throws IOException{
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeInt(numOfPeers);

        dout.flush();
        
        byte[] marshalledMessageHeader = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledMessageHeader;
    }


    


}
