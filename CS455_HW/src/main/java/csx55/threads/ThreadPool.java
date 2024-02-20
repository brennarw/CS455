package csx55.threads;

public class ThreadPool implements Runnable{

    private boolean readyForTask;
    private int threadNumber;

    public ThreadPool(int threadNumber){
        this.threadNumber = threadNumber;
        readyForTask = true;
    }

    public int oneCellMatrixMultiplication(Task task){
        return 0; 
    }
    
    public void run(){
        System.out.println("Thread [" + threadNumber + "] started.");
        while(true) {
            //handle each task here
        }
    }
    
}
