package csx55.overlay.transport;

import java.net.Socket;
import java.io.IOException;
import java.net.SocketException;
import java.io.DataInputStream;

import csx55.overlay.wireformats.EventFactory;
import csx55.overlay.wireformats.Event;
import csx55.overlay.node.*;


//this needs to be a thread because it needs to save and read the data it receives, this will be unique per messaging node
public class TCPReceiverThread implements Runnable {
    
    private Socket socket;
    private DataInputStream din; 

    public Node node;

    public TCPReceiverThread(Socket socket, Node node) throws IOException{
        this.socket = socket;
        this.node = node;
        din = new DataInputStream(socket.getInputStream());
    }

    public void handleEvent(Event event){
        node.onEvent(event, socket);
    }

    public void run() { //this is the method that the thread will run
        int dataLength; //need to read in the length of the data before receiving the data

        while(socket != null && !socket.isClosed()){ //this will run indefinitely until the node disconnects from the socket
            try {
                dataLength = din.readInt(); //this is a blocking call 
                
                byte[] data = new byte[dataLength]; //have to know the length of the data your receiving
                din.readFully(data, 0, dataLength); //readFully is VERY important - reads the correct length rather than the max it can read - that would cause out of memory errors

                EventFactory factory = new EventFactory(data);
                handleEvent(factory.unmarshallMessageType()); 


            } catch (SocketException se) {
                System.out.println(se.getMessage());
                System.out.println("TCPReciever thread ERROR 1");
                break;
            } catch (IOException ioe){
                break;
            } 
        } 
                try{
                    socket.close();
                } catch(IOException e) {
                    System.out.println("error in closing socket");
                }

    }
}
