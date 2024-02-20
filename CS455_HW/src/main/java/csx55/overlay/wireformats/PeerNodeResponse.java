package csx55.overlay.wireformats;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

public class PeerNodeResponse extends Event{

    private int messageType;
    private String peerNodeResponse;

    byte[] marshalledBytes;

    public PeerNodeResponse(String peerNodeResponse){
        this.peerNodeResponse = peerNodeResponse;
        this.messageType = 5;
    }

    public PeerNodeResponse(byte[] marshalledBytes) throws IOException{

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));
        
        this.messageType = din.readInt();

        int responseLength = din.readInt();
        byte[] responseBytes = new byte[responseLength];
        din.readFully(responseBytes);

        this.peerNodeResponse = new String(responseBytes);

        baInputStream.close();
        din.close();
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);

        byte[] responseBytes = peerNodeResponse.getBytes();
        int responseLength = responseBytes.length;
        dout.writeInt(responseLength);
        dout.write(responseBytes);

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

    public String getPeerNodeResponse(){
        return peerNodeResponse;
    }


    
}
