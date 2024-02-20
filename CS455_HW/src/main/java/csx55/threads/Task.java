package csx55.threads;

import java.util.ArrayList;

public class Task {

    private ArrayList<ArrayList<Integer>> matrixOne;
    private ArrayList<ArrayList<Integer>> matrixTwo;
    private int row;
    private int column;

    public Task(ArrayList<ArrayList<Integer>> matrixOne, ArrayList<ArrayList<Integer>> matrixTwo, int row, int column){
        this.matrixOne = matrixOne;
        this.matrixTwo = matrixTwo;
        this.row = row;
        this.column = column; 
    }

    
    
}
