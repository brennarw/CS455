package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RegistryResponse extends Event{
    private byte statusCode;
    private String response;
    private int messageType = 2;

    private byte[] marshalledBytes;
    
    
    public RegistryResponse(byte statusCode, String response) {
        this.statusCode = statusCode;
        this.response = response;
    }

    public RegistryResponse(byte[] marshalledBytes) throws IOException {

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readInt();

        this.statusCode = din.readByte();

        int responseLength = din.readInt();
        byte[] responseBytes = new byte[responseLength];
        din.readFully(responseBytes);

        this.response = new String(responseBytes); 

        baInputStream.close();
        din.close();
    }


    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);

        dout.writeByte(statusCode);

        byte[] responseBytes = response.getBytes();
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

    public byte getStatusCode() {
        return statusCode;
    }

    public String getResponseString() {
        return response;
    }

}
