package csx55.overlay.wireformats;

public class CommandLineFactory{ //this one doesn't extend Event because it doesn't have a getBytes/getMessageType functions
    
    private String commandLineString;

    public CommandLineFactory(String commandLineString){
        this.commandLineString = commandLineString;
    }

    public int[] findCommandEvent(){
        String[] commandLine = commandLineString.split(" ", 2);
        if(commandLine[0].contains("list-messaging")){ //specific to registry node
            int[] returnCommand = {1, -1};
            return returnCommand;
        }
        else if(commandLine[0].equals("list-weights")){ //specific to registry node
            int[] returnCommand = {2, -1};
            return returnCommand;
        }
        else if(commandLine[0].contains("setup-overlay")){ //specific to registry node
            //NOTE: this command takes in a parameter
            if(commandLine.length == 2){
                int[] returnCommand = {3, Integer.parseInt(commandLine[1])};
                return returnCommand;
            } else{
                System.out.println("**Missing an input argument**");
                int[] returnFail = {-1, -1};
                return returnFail; 
            }
        }
        else if(commandLine[0].equals("send-overlay-link-weights")){ //specific to registry node
            int[] returnCommand = {4, -1};
            return returnCommand;
        }
        else if(commandLine[0].contains("start")){ //specific to registry node
            //NOTE this command takes in a parameter
            if(commandLine.length == 2){
                int[] returnCommand = {5, Integer.parseInt(commandLine[1])};
                return returnCommand;
            } else{
                System.out.println("**Missing an input argument**");
                int[] returnFail = {-1, -1};
                return returnFail;
            }
        }
        else if(commandLine[0].equals("print-shortest-path")){ //specific to messaging nodes
            int[] returnCommand = {6, -1};
            return returnCommand;
        }
        else if(commandLine[0].equals("exit-overlay")){ //specific to messaging nodes
            int[] returnCommand = {7, -1};
            return returnCommand;
        }
        else if(commandLine[0].equals("exit")){
            int[] returnCommand = {8, -1};
            return returnCommand;
        }
        else{
            int[] returnCommand = {-1, -1};
            return returnCommand;
        }
        
    }
}
