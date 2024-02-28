package csx55.threads;

import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPool implements Runnable{

    private int threadNumber;
    private int threadPoolSize;
    private Thread[] workerThreads;
    private boolean threadsStarted = false;
    private volatile static boolean lockStartLine = true;
    private volatile static boolean lockFinishLine = true;
    private volatile static boolean lockMainThread = true;
    private volatile static boolean programFinished = false;
    private static AtomicInteger threadCounter = new AtomicInteger(0);
    private static Task task;
    
    private ThreadPool(int threadPoolSize, int threadNumber){
        this.threadNumber = threadNumber;
        this.threadPoolSize = threadPoolSize;
    }
    
    public ThreadPool(int threadPoolSize){
        this.threadPoolSize = threadPoolSize;
        workerThreads = new Thread[threadPoolSize];
        this.threadNumber = -1;
        for(int i = 0; i < threadPoolSize; i++){
            Thread thread = new Thread(new ThreadPool(threadPoolSize, i));
            workerThreads[i] = thread; 
        }
    }

    public void startThreads(){
        for(int i = 0; i < workerThreads.length; i++){
            workerThreads[i].start();
        }
        threadsStarted = true;
    }

    public void unleashWorkerThreads(){
        if(!threadsStarted){
            startThreads();
        }
        //lockMainThread = true;
        while(lockMainThread){} //main thread waits
        lockStartLine = false; //threads do stuff
        threadCounter.set(0); //reset counter for the finish line
        lockMainThread = true; //lock main thread to wait for all threads to get to the finish line
        while(lockMainThread){} //waiting for all threads to reach the finish line
        lockStartLine = true; //threads have left the startline so reset it to catch them again at the start line
        threadCounter.set(0); //reset the start line counter
        lockFinishLine = false; //once all threads have made it to the finish line, release them to go back to the start line
        lockMainThread = true; //lock main thread to wait for them all to get to the start line
        while(lockMainThread){}
        lockFinishLine = true; //reset the finish line lock so that the race can begin again
    }

    public void setTask(Task task){
        ThreadPool.task = task;
    }

    public Task getTask(){
        return task;
    }

    public void setProgramFinished(boolean finished){
        ThreadPool.programFinished = finished;
    }
    
    public void run(){
        //System.out.println("Thread [" + threadNumber + "] started.");

        while(!programFinished) {
            if(threadCounter.incrementAndGet() == threadPoolSize){ //"start line"
              //only the last thread enters into this if statement - it means that all other threads have moved passed this
              lockMainThread = false;
            }
            while(lockStartLine) {
                //busy waiting at the startline
            }
            //handle task here
            int stride = (task.totalNumberOfTasks + (threadPoolSize - 1))/threadPoolSize; //this finds the number of tasks this thread is responsible for doing
            int start = threadNumber * stride; //finds where their task number starts
            int end = start + stride;
            if(start >= task.totalNumberOfTasks){ //edge case
                start = task.totalNumberOfTasks;
            }
            if(end >= task.totalNumberOfTasks){ //edge case
                end = task.totalNumberOfTasks;
            }
            for(int i = start; i < end; ++i){
                task.matrixMultiplication(i);
            }
            
            if(threadCounter.incrementAndGet() == threadPoolSize) {
                //all threads have finished their tasks
                lockMainThread = false;
            }
            while(lockFinishLine) {
                //busy waiting at the finish line
            }
    
        }
    }
}

