package csx55.threads;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class MatrixThreads{

    private int threadPoolSize;
    private int matrixDimension;
    private Random randy; 
    //private Queue<Task> taskQueue; 
    private ThreadPool threadPool;
    private double timeToComputeX;
    private double timeToComputeY;
    private double timeToComputeZ;

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
        //this.taskQueue = new LinkedList<>();
        this.randy = new Random(seed);

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
    }

    public void createThreadPool(int threadPoolSize){
        //start up threadPoolSize number of threads
        threadPool = new ThreadPool(threadPoolSize);
    }

    public int populateMatrix(int[][] matrix) {
        //matrix size = matrixDimension x matrixDimension
        int sum = 0; 

        //populating matrix 
        for(int row = 0; row < matrixDimension; row++) {
            for(int column = 0; column < matrixDimension; column++){
                int randomElement = 1000 - randy.nextInt(2000);
                matrix[row][column] = randomElement;
                sum += randomElement;
            }
        }
        return sum;
    }

    public long sumMatrix(int[][] matrix){
        long sum = 0;
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[i].length; j++){
                sum += matrix[i][j];
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

    // public int findAvailableThread(){ //this needs to be synchronized or something - may cause concurrency issues
    //     return -1; //this means no thread is available for a task
    // }

    // public void assignTasks(){
    //     while(taskQueue.peek() != null){

    //     }
    // }

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
        System.out.println();

        //create thread pool and worker threads - note the worker threads aren't started right away
        matrix.createThreadPool(threadPoolSizeArg);


        //make the first task - multiply matrix A and B to get X
        Task taskOne = new Task(matrix.matrixA, matrix.matrixB, matrix.matrixX);
        //assign this task to the thread pool
        matrix.threadPool.setTask(taskOne);
        //time the first task
        double startTime = System.currentTimeMillis();
        matrix.threadPool.unleashWorkerThreads();
        double endTime = System.currentTimeMillis();
        matrix.timeToComputeX = (endTime - startTime)/1000;
        //print out the matrix X and the time it took to populate it
        long matrixXSum = matrix.sumMatrix(matrix.matrixX);
        System.out.println("Calculation of matrix X (product of A and B) complete - sum of the elements in X is: " + matrixXSum);
        System.out.println("Time to Compute matrix X: " + matrix.timeToComputeX + " s");
        System.out.println();


        //make the second task - multiply matrix C and D to get Y
        Task taskTwo = new Task(matrix.matrixC, matrix.matrixD, matrix.matrixY);
        //assign this task to the thread pool
        matrix.threadPool.setTask(taskTwo);
        //time the second task
        startTime = System.currentTimeMillis();
        matrix.threadPool.unleashWorkerThreads();
        endTime = System.currentTimeMillis();
        matrix.timeToComputeY = (endTime - startTime)/1000;
        //print out the time and sum for this task
        long matrixYSum = matrix.sumMatrix(matrix.matrixY);
        System.out.println("Calculation of matrix Y (product of C and D) complete - sum of the elements in Y is: " + matrixYSum);
        System.out.println("Time to Compute matrix Y: " + matrix.timeToComputeY + " s");
        System.out.println();


        //make the third task - multiply X and Y to get Z
        Task taskThree = new Task(matrix.matrixX, matrix.matrixY, matrix.matrixZ);
        //assign this task to the thread pool
        matrix.threadPool.setTask(taskThree);
        //time the third task
        startTime = System.currentTimeMillis();
        matrix.threadPool.unleashWorkerThreads();
        endTime = System.currentTimeMillis();
        matrix.timeToComputeZ = (endTime - startTime)/1000;
        //print out the time and sum for this task
        long matrixZSum = matrix.sumMatrix(matrix.matrixZ);
        System.out.println("Calculation of matrix Z (product of X and Y) complete - sum of the elements in Z is: " + matrixZSum);
        System.out.println("Time to Compute matrix Z: " + matrix.timeToComputeZ + " s");
        System.out.println();

        double totalProgramTime = matrix.timeToComputeX + matrix.timeToComputeY + matrix.timeToComputeZ;
        System.out.printf("Cumulative time to compute matrixes X, Y, and Z using a thread pool of size = %d is %.3f s %n", matrix.threadPoolSize, totalProgramTime);

        //kill threads
        matrix.threadPool.setProgramFinished(true);

        System.exit(0);

    }
}