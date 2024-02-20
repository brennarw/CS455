package csx55.overlay.wireformats;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;

public class TaskInitiate extends Event{

    private int messageType;
    private int numRounds;
    private String message = "TASK-INITIATE";

    private byte[] marshalledBytes;

    public TaskInitiate(int numRounds){
        this.numRounds = numRounds;
        this.messageType = 7;
    }

    public TaskInitiate(byte[] marshalledBytes) throws IOException{

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readInt();
        this.numRounds = din.readInt();

        baInputStream.close();
        din.close();
 }

    @Override
    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeInt(numRounds);

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

    public int getNumRounds() {
        return numRounds;
    }
    
    public String getMessage(){
        return message;
    }


    
}
