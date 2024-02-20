package csx55.overlay.wireformats;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

public class PeerNodeCommunication extends Event{
    
    private String path;
    private int message;
    private int messageType;

    private byte[] marshalledBytes;

    public PeerNodeCommunication(String path, int message){
        this.path = path;
        this.message = message;
        this.messageType = 8;
    }

    public PeerNodeCommunication(byte[] marshalledBytes) throws IOException{

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readInt();
        this.message = din.readInt();

        int pathLength = din.readInt();
        byte[] pathBytes = new byte[pathLength];
        din.readFully(pathBytes);
        this.path = new String(pathBytes);

        baInputStream.close();
        din.close();
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeInt(message);

        byte[] pathBytes = path.getBytes();
        int pathLength = pathBytes.length;
        dout.writeInt(pathLength);
        dout.write(pathBytes);

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

    public int getMessage() {
        return message;
    }

    public String getPath(){
        return path;
    }


}
