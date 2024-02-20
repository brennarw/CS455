package csx55.overlay.wireformats;
import java.io.IOException;

public class EventFactory {
    //this should be a singleton instance
    private byte[] marshalledBytes;
    private int messageType; 

    //this class's constructor should take in a byte array and reads the first 4 bytes to see what event it is
    //then in a switch statement, call a specific constructor of another event class by passing in the byte array to be unmarshalled

    public EventFactory(byte[] marshalledBytes){
        this.marshalledBytes = marshalledBytes;
        messageType = marshalledBytes[3]; //i think this gets the correct message type identifer
    }

    public Event unmarshallMessageType() { 
        switch(messageType){
            case 0: //this is the case that handles when the messaging node wants to register with the registry node
                try{
                    Event register = new Register(marshalledBytes); //unmarshalls the bytes
                    return register;
                    //break;
                } catch (IOException e){
                    System.out.println("error in event factory case 0");;
                }
            case 2:
                try{
                    Event registerResponse = new RegistryResponse(marshalledBytes);
                    return registerResponse;
                } catch (IOException e){
                    //System.out.println(e.getMessage());
                    System.out.println("error in event factory case 2");
                    return null; 
                }
            case 3: //this case handles deregistering the messagingNode
                try{
                    Event deregister = new Deregister(marshalledBytes);
                    return deregister;
                } catch(IOException e){
                    System.out.println("Error in event factory case 3");
                }

            case 4: //this case handles when the messaging node is sent a byte[] of messaging nodes it needs to connect to
                try{
                    MessagingNodesList peerNodesList = new MessagingNodesList(marshalledBytes);
                    peerNodesList.unmarshallPeerNodes();
                    return (Event)peerNodesList;
                } catch(IOException e){
                    //System.out.println(e.getMessage());
                    System.out.println("error in event factory case 4");
                    return null;
                }
            case 5: //this case handles receiving the response from messaging nodes after making their connections
                try{
                    Event peerNodeConnectionResponse = new PeerNodeResponse(marshalledBytes);
                    return peerNodeConnectionResponse;
                } catch(IOException e) {
                    System.out.println("Error in event factory case 5");
                    return null;
                }
            case 6: //this case handles receiving the list of overlay links
                try{
                    Event overlayLinks = new LinkWeights(marshalledBytes);
                    return overlayLinks;
                } catch(IOException e){
                    System.out.println("error in event factory case 6");
                }
            case 7: //this cases handles sending the "task initiate" message to the nodes
                try{
                    Event taskInitiate = new TaskInitiate(marshalledBytes);
                    return taskInitiate;
                } catch(IOException e){
                    System.out.println("error in event factory case 7");
                }
            case 8: //this event handles the messaging nodes sending messages to each other
                try{
                    Event peerNodeCommunication = new PeerNodeCommunication(marshalledBytes);
                    return peerNodeCommunication;
                } catch(IOException e){
                    System.out.println("error in event factory case 8; ");
                }
                return null;
            case 9: //this case handles the pull-traffic-summary response
                try{
                    Event taskSummary = new TaskSummaryResponse(marshalledBytes);
                    return taskSummary;
                } catch(IOException e){
                    System.out.println("error in event factory case 9");
                }
            case 10: //this case handles exiting the overlay
                try{
                    Event deregisterResponse = new DeregisterResponse(marshalledBytes);
                    return deregisterResponse;
                } catch(IOException e) {
                    System.out.println("error in event factory case 10");
                }
                return null; 
            default:
                System.out.println("hit the default case in event factory");
                return null;
        }
        
    }
}
