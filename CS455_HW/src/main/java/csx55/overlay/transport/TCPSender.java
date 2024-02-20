package csx55.overlay.transport;

import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;

//this is not a thread because it doesnt need to be unique per messaging node
//this is a generic class that just sends any given data over any given socket
public class TCPSender {
    
    private Socket socket;
    private DataOutputStream dout;

    public TCPSender(Socket socket) throws IOException {
        this.socket = socket;
        dout = new DataOutputStream(socket.getOutputStream());
    }

    //we have to marshall the integer message into a byte array?
    public synchronized void sendData(byte[] dataToSend) throws IOException {
        int dataLength = dataToSend.length; //this holds the length of the data you want to send, this will be send first to the receiver
        dout.writeInt(dataLength); //always write the length of the data first!
        dout.write(dataToSend, 0, dataLength); 
        dout.flush();
    }
    
}
