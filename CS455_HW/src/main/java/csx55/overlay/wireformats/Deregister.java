package csx55.overlay.wireformats;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

public class Deregister extends Event{

    private int messageType;
    private String ipAddress;
    private int portNumber;

    private byte[] marshalledBytes;

    public Deregister(String ipAddress, int portNumber){
        this.ipAddress = ipAddress;
        this.portNumber = portNumber;
        this.messageType = 3;
    }

    public Deregister(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        
        this.messageType = din.readInt();
        this.portNumber = din.readInt();

        int IPAddressLength = din.readInt();
        byte[] IPAddressBytes = new byte[IPAddressLength];
        din.readFully(IPAddressBytes);

        this.ipAddress = new String(IPAddressBytes);

        baInputStream.close();
        din.close();
    }


    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeInt(portNumber);

        byte[] IPAddressBytes = ipAddress.getBytes();
        int ipAddressLength = IPAddressBytes.length;
        dout.writeInt(ipAddressLength);
        dout.write(IPAddressBytes);

        dout.flush();
        marshalledBytes = baOutputStream.toByteArray();

        baOutputStream.close();
        dout.close();
        return marshalledBytes;
    }

    @Override
    public int getMessageType() {
        return messageType;
    }

    public int getPortNumber(){
        return portNumber;
    }

}
