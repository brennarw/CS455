package csx55.threads;

public class Task {

    /*
     * Matrix A, B, C;
     * int howManyDotProducts;
     * 
     * public Task(Matrix a, Matrix b){
     *     A = a;
     *     B = b;
     *     C = new Matrix(row);
     *     howManyDotProducts = a.row * a.row;
     * }
     * 
     * public void doDotProduct(int i){
     *      int row = i / A.row;
     *      int column = i % A.row;
     *      //do dot product
     * }
     */

    private int[][] matrixOne; //only the column/row will be passed from this matrix
    private int[][] matrixTwo; //only the column/row will be passed from this matrix
    private int[][] finalMatrix;
    public int totalNumberOfTasks; 

    public Task(int[][] matrixOne, int[][] matrixTwo, int[][] finalMatrix){
        this.matrixOne = matrixOne;
        this.matrixTwo = matrixTwo;
        this.finalMatrix = finalMatrix; 
        this.totalNumberOfTasks = matrixOne.length * matrixTwo.length; 
    }

    public void matrixMultiplication(int cellNumber){
        int row = cellNumber / matrixOne.length; //square matrix so can use the same length for both calculations - this one is "row"
        int column = cellNumber % matrixOne.length; //this one is "column"
        int taskResult = 0;

        for(int i = 0; i < matrixOne.length; ++i){
            taskResult += matrixOne[row][i] * matrixTwo[i][column];
        }

        ///now set the final result in the matrix
        setFinalMatrix(row, column, taskResult);
    }

    public void setFinalMatrix(int row, int column, int taskResult){
        finalMatrix[row][column] = taskResult;
    }

    
    
}
