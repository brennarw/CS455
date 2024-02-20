package csx55.overlay.node;

import java.net.Socket;

import csx55.overlay.wireformats.*;


//interface with the onEvent(Event) method
public abstract class Node {
    
    public abstract void onEvent(Event event, Socket socket); 

    //this interface should have an onEvent() method that each node class implements 
        //this method will receive a messageType with the unmarshalled data and then in a switch statement determine with method in Event it should call
}
