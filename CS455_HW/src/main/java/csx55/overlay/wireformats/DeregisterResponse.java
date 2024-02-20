package csx55.overlay.wireformats;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.BufferedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;


public class DeregisterResponse extends Event{
    
    private int messageType;
    private byte statusCode;

    private byte[] marshalledBytes;

    public DeregisterResponse(byte statusCode) {
        this.statusCode = statusCode;
        this.messageType = 10;
    }

    public DeregisterResponse(byte[] marshalledBytes) throws IOException{
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readInt();
        this.statusCode = din.readByte();

        baInputStream.close();
        din.close();
    }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeByte(statusCode);

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

    public byte getStatusCode(){
        return statusCode; 
    }

}
