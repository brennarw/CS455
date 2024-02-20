package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class TaskSummaryResponse extends Event{

    private final int messageType; 
    private String hostName;
    private String ipAddress;
    private int port;
    private int messagesSent;
    private long messagesSentSummation;
    private int messagesReceived;
    private long messagesReceivedSummation;
    private int messagesRelayed;

    private byte[] marshalledBytes;

    public TaskSummaryResponse(String hostName, String ipAddress, int port, int messagesSent, long messagesSentSummation, int messagesReceived, long messagesReceivedSummation, int messagesRelayed){
        this.messageType = 9;
        this.hostName = hostName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.messagesSent = messagesSent;
        this.messagesSentSummation = messagesSentSummation;
        this.messagesReceived = messagesReceived;
        this.messagesReceivedSummation = messagesReceivedSummation;
        this.messagesRelayed = messagesRelayed;   
    }

    public TaskSummaryResponse(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readInt();

        int hostNameLength = din.readInt();
        byte[] hostNameBytes = new byte[hostNameLength];
        din.readFully(hostNameBytes);
        this.hostName = new String(hostNameBytes);

        int ipAddressLength = din.readInt();
        byte[] ipAddressBytes = new byte[ipAddressLength];
        din.readFully(ipAddressBytes);
        this.ipAddress = new String(ipAddressBytes);

        this.port = din.readInt();

        this.messagesSent = din.readInt();

        this.messagesSentSummation = din.readLong();

        this.messagesReceived = din.readInt();

        this.messagesReceivedSummation = din.readLong();

        this.messagesRelayed = din.readInt();

        baInputStream.close();
        din.close();

    }

    @Override
    public byte[] getBytes() throws IOException {

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);

        byte[] hostNameBytes = hostName.getBytes();
        int hostNameLength = hostNameBytes.length;
        dout.writeInt(hostNameLength);
        dout.write(hostNameBytes);

        byte[] ipAddressBytes = ipAddress.getBytes();
        int ipAddressLength = ipAddressBytes.length;
        dout.writeInt(ipAddressLength);
        dout.write(ipAddressBytes);

        dout.writeInt(port);

        dout.writeInt(messagesSent);

        dout.writeLong(messagesSentSummation);

        dout.writeInt(messagesReceived);

        dout.writeLong(messagesReceivedSummation);

        dout.writeInt(messagesRelayed);

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

    public String getHostName(){
        return hostName;
    }

    public String getIPAddress(){
        return ipAddress;
    }

    public int getPort(){
        return port;
    }

    public int getMessagesSent(){
        return messagesSent;
    }

    public long getMessagesSentSummation(){
        return messagesSentSummation;
    }

    public int getMessagesReceived(){
        return messagesReceived;
    }

    public long getMessagesReceivedSummation(){
        return messagesReceivedSummation;
    }

    public int getMessagesRelayed(){
        return messagesRelayed;
    }

    
}
