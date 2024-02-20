package csx55.overlay.wireformats;

import java.net.Socket;
import java.io.IOException;
import csx55.overlay.transport.TCPSender;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

public class Register extends Event{

    private int portNumber;
    private String IPAddress;
    private String hostName;
    private final int messageType; //REGISTER_REQUEST//4th element in byte array

    private byte[] marshalledBytes;

    //Marshalling constructor:
    public Register(String hostName, int portNumber, String IPAddress){
        this.hostName = hostName;
        this.IPAddress = IPAddress;
        this.portNumber = portNumber; 
        this.messageType = 0;
    }

    //unmarshalling constructor:
    public Register(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        
        this.messageType = din.readInt();

        int hostNameLength = din.readInt();
        byte[] hostNameBytes = new byte[hostNameLength];
        din.readFully(hostNameBytes);
        this.hostName = new String(hostNameBytes);
        
        this.portNumber = din.readInt(); 
        
        int IPAddressLength = din.readInt();
        byte[] IPAddressBytes = new byte[IPAddressLength];
        din.readFully(IPAddressBytes); 
        this.IPAddress = new String(IPAddressBytes); 

        baInputStream.close();
        din.close();
    }

    public byte[] getBytes() throws IOException { //abstract method implementation
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));
        
        dout.writeInt(messageType);

        byte[] hostNameBytes = hostName.getBytes();
        int hostNameLength = hostNameBytes.length;
        dout.writeInt(hostNameLength);
        dout.write(hostNameBytes);

        dout.writeInt(portNumber);
        
        byte[] IPAdressBytes = IPAddress.getBytes();
        int IPAddressLength = IPAdressBytes.length;
        dout.writeInt(IPAddressLength);
        dout.write(IPAdressBytes);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray(); 

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    public int getMessageType() { //abstract method implementation
        return messageType;
    }

    public int getPortNumber(){
        return portNumber;
    }

    public String getIPAddress(){
        return IPAddress;
    }

    public String getHostName(){
        return hostName;
    }

    

    




}
