package csx55.threads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class MatrixThreads{

    private int threadPoolSize;
    private int matrixDimension;
    private int seed;
    private Random randy; 
    private Queue<Task> taskQueue; 
    private ArrayList<Thread> threads; 

    private int[][] matrixA;
    private int[][] matrixB;
    private int[][] matrixC;
    private int[][] matrixD;

    private int matrixASum;
    private int matrixBSum;
    private int matrixCSum;
    private int matrixDSum;

    private int[][] matrixX;
    private int[][] matrixY;

    private int[][] matrixZ;

    public MatrixThreads(int threadPoolSize, int matrixDimension, int seed) {
        this.threadPoolSize = threadPoolSize;
        this.matrixDimension = matrixDimension;
        this.seed = seed;
        this.taskQueue = new LinkedList<>();
        this.threads = new ArrayList<>();


        randy = new Random(seed);
        matrixA = new int[matrixDimension][matrixDimension];
        matrixB = new int[matrixDimension][matrixDimension];
        matrixC = new int[matrixDimension][matrixDimension];
        matrixD = new int[matrixDimension][matrixDimension];

        matrixX = new int[matrixDimension][matrixDimension];
        matrixY = new int[matrixDimension][matrixDimension];

        matrixZ = new int[matrixDimension][matrixDimension];

        matrixASum = populateMatrix(matrixA);
        matrixBSum = populateMatrix(matrixB);
        matrixCSum = populateMatrix(matrixC);
        matrixDSum = populateMatrix(matrixD);

        // System.out.println("Matrix A:");
        // printMatrix(matrixA);
        // System.out.println("Matrix B:");
        // printMatrix(matrixB);
        // System.out.println("Matrix C:");
        // printMatrix(matrixC);
        // System.out.println("Matrix D:");
        // printMatrix(matrixD);
    }

    public void createThreadPool(){
        //start up threadPoolSize number of threads
        
        for(int i = 0; i < threadPoolSize; i++){
            ThreadPool worker = new ThreadPool(i + 1);
            Thread workerThread = new Thread(worker);
            threads.add(workerThread); 
            workerThread.start();
        }
    }

    public int populateMatrix(int[][] matrix) {
        //matrix size = matrixDimension x matrixDimension
        int sum = 0; 

        //populating matrix A
        for(int row = 0; row < matrixDimension; row++) {
            for(int column = 0; column < matrixDimension; column++){
                int randomElement = 1000 - randy.nextInt(2000);
                matrix[row][column] = randomElement;
                sum += randomElement;
            }
        }
        return sum;
    }

    public void printMatrix(int[][] matrix) {
        for(int row = 0; row < matrix.length; row++){
            for(int column = 0; column < matrix[row].length; column++){
                System.out.print(matrix[row][column] + " ");
            }
            System.out.println();
        }
    }

    public int findAvailableThread(){ //this needs to be synchronized or something - may cause concurrency issues
        return -1; //this means no thread is available for a task
    }

    public void createTasks(int[][] matrixOne, int[][] matrixTwo, int[][] finalMatrix){
        for(int column = 0; column < matrixOne.length; column++){
            for(int row = 0; row < matrixTwo.length; row++){
                Task task = new Task(matrixOne, matrixTwo, finalMatrix, row, column);
                taskQueue.add(task);
            }
        }
    }

    public void assignTasks(){
        while(taskQueue.peek() != null){

        }
    }

    public static void main(String[] args) {
        //should take in [threadPoolSize, matrixDimension, seed] in that order
        if(args.length != 3) {
            System.out.println("incorrect arguments");
            System.exit(0);
        }

        int threadPoolSizeArg = Integer.parseInt(args[0]);
        int matrixDimensionArg = Integer.parseInt(args[1]);
        int seedArg = Integer.parseInt(args[2]);

        MatrixThreads matrix = new MatrixThreads(threadPoolSizeArg, matrixDimensionArg, seedArg);

        System.out.println("Dimensionality of the square matrices is: " + matrixDimensionArg);
        System.out.println("The thread pool size has been initialized to: " + threadPoolSizeArg);
        System.out.println();
        System.out.println("Sum of the elements in input matrix A = " + matrix.matrixASum);
        System.out.println("Sum of the elements in input matrix B = " + matrix.matrixBSum);
        System.out.println("Sum of the elements in input matrix C = " + matrix.matrixCSum);
        System.out.println("Sum of the elements in input matrix D = " + matrix.matrixDSum);

        matrix.createThreadPool();

        //build task queue for matrix multiplication between A and B to populate X:
        matrix.createTasks(matrix.matrixA, matrix.matrixB, matrix.matrixX);

        //assign those tasks to the thread pool:

        /*
         * Pool = new ThreadPool(numberOfWorkerThreads);
         * random = new Random(seed);
         * Task task = new Tasl(A,B);
         * pool.setTask(task);
         * //start time
         * pool.unleashThreads();
         * //end time
         */




    }
}