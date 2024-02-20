package csx55.overlay.wireformats;

import java.io.IOException;

public abstract class Event {
    //this is an interface with the getType() and getBytes() defined

    //every event should implement this interface and then when returning an object from eventFactory, make it an event object: event = new Register(...)
    //then once you want the information you can cast it back to a register object!

    //each event should be handled in the node classes - registry or messaging node class

    public abstract byte[] getBytes() throws IOException;
    public abstract int getMessageType();


}
