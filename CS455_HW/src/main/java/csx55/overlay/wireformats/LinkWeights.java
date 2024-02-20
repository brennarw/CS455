package csx55.overlay.wireformats;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LinkWeights extends Event{

    // private Hashtable<Integer, MessagingNodeInfo> registeredSockets = new Hashtable<Integer, MessagingNodeInfo>(); 
    // private ArrayList<List<Integer>> socketLinks = new ArrayList<List<Integer>>();
    private ArrayList<String> linkWeightsList = new ArrayList<String>();
    private int numLinks;

    private final int messageType;
    private String linkWeightString = "";

    private byte[] marshalledBytes;

    public LinkWeights(ArrayList<String> linkWeightsList){
        this.linkWeightsList = linkWeightsList;
        this.numLinks = linkWeightsList.size();
        this.messageType = 6;
    }

    public LinkWeights(byte[] marshalledBytes) throws IOException {
        ByteArrayInputStream baInputStream = new ByteArrayInputStream(marshalledBytes);
        DataInputStream din = new DataInputStream(new BufferedInputStream(baInputStream));

        this.messageType = din.readInt();
        this.numLinks = din.readInt();

        int linkString = din.readInt();
        byte[] linkStringBytes = new byte[linkString];
        din.readFully(linkStringBytes);
        this.linkWeightString = new String(linkStringBytes);

        baInputStream.close();
        din.close();

        convertToArray();
    }

    public void convertToArray(){
        String[] linkArray = linkWeightString.split("\\|");
        for(int i = 0; i < linkArray.length; i++){
            linkWeightsList.add(linkArray[i]);
        }
    }

    public void convertToString(){
        for(int i = 0; i < linkWeightsList.size(); i++){
            linkWeightString += linkWeightsList.get(i) + "|";
        }
    }

    @Override
    public byte[] getBytes() throws IOException {
        convertToString(); //appends the message type on the front of it

        ByteArrayOutputStream baOutputStream = new ByteArrayOutputStream();
        DataOutputStream dout = new DataOutputStream(new BufferedOutputStream(baOutputStream));

        dout.writeInt(messageType);
        dout.writeInt(numLinks);
        
        byte[] linkStringBytes = linkWeightString.getBytes();
        int linkStringLength = linkStringBytes.length;
        dout.writeInt(linkStringLength);
        dout.write(linkStringBytes);


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

    public ArrayList<String> getLinkWeightList(){
        return linkWeightsList;
    }

    public String getLinkString(){
        return linkWeightString;
    }

    public int getNumLinks(){
        return numLinks;
    }

   




    
}
