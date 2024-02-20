package csx55.overlay.wireformats;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import csx55.overlay.transport.TCPSender;

public class MessagingNodeInfo {

    private Socket socket;
    private String ipAddress;
    private int portNumber;
    private String hostName;
    private TCPSender sender;

    public MessagingNodeInfo(Socket socket, String hostName, String ipAddress, int portNumber, TCPSender sender){
        this.socket = socket;
        this.hostName = hostName;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.sender = sender; 
    }

    public MessagingNodeInfo(String hostName, int portNumber, String ipAddress){
        this.hostName = hostName;
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
    }

    public Socket getSocket(){
        return socket;
    }

    public String getHostName(){
        return hostName;
    }

    public String getIPAddress(){
        return ipAddress;
    }

    public int getPortNumber(){
        return portNumber;
    }

    public TCPSender getSender(){
        return sender;
    }

    public byte[] getBytes() throws IOException { //returns the byte array for the ipAddress
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        byte[] hostNameBytes = hostName.getBytes();
        int hostNameLength = hostNameBytes.length;
        dout.writeInt(hostNameLength);
        dout.write(hostNameBytes);

        dout.writeInt(portNumber);

        byte[] ipAddressBytes = ipAddress.getBytes();
        int ipAddressLength = ipAddressBytes.length;
        dout.writeInt(ipAddressLength);
        dout.write(ipAddressBytes);

        dout.flush();
        byte[] ipMarshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();

        return ipMarshalledBytes;
    }


    
}
