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
    private int row;
    private int column;
    private int result;

    public Task(int[][] matrixOne, int[][] matrixTwo, int[][] finalMatrix, int row, int column){
        this.matrixOne = matrixOne;
        this.matrixTwo = matrixTwo;
        this.finalMatrix = finalMatrix; 
        this.row = row;
        this.column = column; 
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult(){
        return result;
    }

    public void setFinalMatrix(){
        finalMatrix[row][column] = result;
    }

    public int getRow(){
        return row;
    }

    public int getColumn(){
        return column;
    }

    public int[][] getMatrixOne(){
        return matrixOne;
    }
    
    public int[][] getMatrixTwo(){
        return matrixTwo;
    }

    
    
}
