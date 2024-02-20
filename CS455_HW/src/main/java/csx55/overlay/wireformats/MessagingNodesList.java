package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MessagingNodesList extends Event{

    private ArrayList<MessagingNodeInfo> peerNodesList = new ArrayList<>();
    private byte[] marshalledPeerNodes;
    private int messageType;
    private int numPeerNodes;

    public MessagingNodesList(byte[] marshalledPeerNodes){
        this.marshalledPeerNodes = marshalledPeerNodes;
    }

    public void unmarshallPeerNodes() throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledPeerNodes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        //read in the first two values of marshalled bytes: the message type and the number of peer nodes to connect to
        this.messageType = din.readInt();
        this.numPeerNodes = din.readInt();

        System.out.println("Number of peer nodes this node is responsible for connecting to: " + numPeerNodes);

        //now in a for loop that loops for numPeerNodes times, grab the 
        for(int i = 0 ; i < numPeerNodes; i++){

            int hostNameLength = din.readInt();
            byte[] hostNameBytes = new byte[hostNameLength];
            din.readFully(hostNameBytes);
            String hostName = new String(hostNameBytes);
            
            //read in the port number then the ipaddress and assign them to temp variables
            int portNumber = din.readInt();
            //System.out.println("Peer messaging node portNumber: " + portNumber);

            int IPAddressLength = din.readInt();
            byte[] IPAddressBytes = new byte[IPAddressLength];
            din.readFully(IPAddressBytes);
            String ipAddress = new String(IPAddressBytes);

            System.out.println("Connected to: " + hostName + ":" + portNumber);

            MessagingNodeInfo peerNode = new MessagingNodeInfo(hostName, portNumber, ipAddress);

            peerNodesList.add(peerNode); 
        }
        baInputStream.close();
        din.close();
    }

    @Override
    public byte[] getBytes() throws IOException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBytes'");
    }

    public ArrayList<MessagingNodeInfo> getPeerNodesList(){
        return peerNodesList;
    }

    @Override
    public int getMessageType() {
        return messageType;
    }
    
    
}
