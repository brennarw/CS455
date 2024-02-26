package csx55.threads;

import java.Concurrent.*;

public class ThreadPool implements Runnable{

    private boolean readyForTask;
    private int threadNumber;
    private int threadPoolSize;
    private Thread[] workerThreads;
    private boolean threadsStarted = false;
    private volatile static boolean lock;
    private volatile static boolean unlock;
    private volatile static boolean lockMainThread = true;
    private volatile static boolean programFinished = false;
    private static AtomicInteger counterStartLine = new AtomicInteger(0);
    private static AtomicInteger counterFinishLine = new AtomicInteger(0);
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

    public void unleashWorkerThreads(Task task){
        if(!threadsStarted){
            startThreads();
        }
        //set unlock to true - unlock the waiting threads
        /*
         * counter.set(0);
         * lockMainThread = true;
         * lock = false; //threads do stuff
         * while(lockMainThread){//main waits}
         * lock = true;//threads finished, now reset them
         * counter.set(0);
         * unlock = false; //starting reset...
         * lockMainThread = true;
         * while(lockMainThred){//waiting for reset}
         * lockMainThread = true; 
         * unlock = true; //race can begin again...
         * 
         */

    }

    public int oneCellMatrixMultiplication(Task task){
        int result = 0; 
        for(int i = 0; i < task.getMatrixOne().length; i++){
            result += task.getMatrixOne()[task.getRow()][i] * task.getMatrixTwo()[i][task.getColumn()];
        }
        task.setResult(result);
        task.setFinalMatrix();
        return 0; 
    }

    public boolean getReadyForTask(){
        return readyForTask;
    }

    public void setReadyForTask(boolean isReady){
        readyForTask = isReady; 
    }
    
    public void run(){
        System.out.println("Thread [" + threadNumber + "] started.");
        while(true) {
            //handle each task here
        }

        /* 
         * while(!programFinished) {
         *  if(counter.incrementAndGet() == threadPoolSize){ //"start line"
         *      //only the last thread enters into this if statement - it means that all other threads have moved passed this
         *      lockMainThread = false;
         *  }
         *  while(lock) {
         *      //busy waiting
         *  }
         *  //handle task here
         *  int stride = (tasl.howManyDots + (count - 1))/count; //this finds the number of tasks this thread is responsible for doing
         *  int start = threadNumber * stride; //finds where their task number starts
         *  int end = start + stride;
         *  if(start >= task.howManyDotProducts){ //edge case
         *      start = task.howManyDotProducts;
         *  }
         *  if(end >= task.howManyDotProducts){ //edge case
         *      end = task.howManyDotProducts;
         *  }
         *  for(int i = start; i < end; ++i){
         *      task.doDotProduct(i);
         *  }
         * 
         *  if(counter.incrementAndGet() == threadPoolSize) {
         *      //all threads have finished their tasks
         *      lockMainThread = false;
         *  }
         *  while(unlock) {//busy waiting}
         * }
         */
    }
    
}
