package csx55.overlay.transport;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import csx55.overlay.node.*;

//Represents a thread associated with a TCP server, responsible for handling incoming connection requests and managing communication with clients.
//this needs to be a thread because it needs to be unique per messaging node - stores a uniqe port number per messaging node
public class TCPServerThread implements Runnable{ 

    private int portNumber;
    private ServerSocket server; 

    private Node node; 

    public TCPServerThread(int portNumber, Node node){
        this.portNumber = portNumber;
        this.node = node;
        try{
            server = new ServerSocket(portNumber); 
            this.portNumber = server.getLocalPort();


        } catch (IOException e){
            System.out.println("socket connection failed"); 
            e.printStackTrace(); 
        }
    }

    public int getPortNumber(){
        return portNumber;
    }

    public void spawnReceiverThread(Socket nodeSocket){
        try{
            TCPReceiverThread nodeReceiver = new TCPReceiverThread(nodeSocket, node); 
            Thread receiverThread = new Thread(nodeReceiver);
            receiverThread.start();

        } catch(IOException e){
            //TODO: handle exception
        }
    }

    public void run() {

        while(true){
            try {
                //blocking call that waits for a node to create a socket and connect to this port - when it does, this thread creates a vanilla socket for all data to travel across
                Socket nodeSocket = server.accept();
                
                //now spawn a TCPRecieverThread for that messaging node socket:
                spawnReceiverThread(nodeSocket);

            } catch (IOException e){
                System.out.println("TCPServer thread ERROR 1");
                //TODO: handle exception
            } 
        }
        

    }
}