Threads Homework 2 README

MatrixThreads class
    This class is the entry point for the program. In the main method, it starts by populating the first four matrices: A, B, C, and D.
    While populating the matrices it keeps track of the sum of each matrix to print out for error checking. After each matrix is populated
    it then creates the thread pool by calling the 'createThreadPool()' method with the ThreadPool object. Once everything is set up, it starts
    assigning the thread pool tasks. The first task is multiplying matrix A and matrix B to get matrix X. In this assignment, we are dealing with
    square matrices, so we don't have to worry about row vs column size of A and B. This class times how long it takes to calculate matrix X and prints
    it out, as well as the sum of the items in matrix X. NOTE that we use a seed in our random object so the sum of each matrix should be the same each
    time if the same matrix size and seed is used. Once task one is complete, it assigns task two: multiplying matrix C and D to get matrix Y. Once this 
    task is finished, it assigns the third task: multiply matrix X and Y to get Z. With each task, it prints out the time it took to complete that task 
    and the sum of the calcuated matrix. Finally, it prints out the total time it took to complete all three tasks and then kills the threads in the thread
    pool before exiting the program.

ThreadPool class
    This class handles the worker thread pool. When initialized from the MatrixThreads class, the public constructor is used. This constructor takes in a 
    threadPoolSize and with that, creates threadPoolSize number of worker threads. To create a worker thread, it uses the private constructor that takes in 
    the threadPoolSize as well as a threadNumber that acts as an ID for that thread. Once the worker threads have been created, they haven't been started just yet.
    When the MatrixThread class assigns the first task, it calls 'unleashWorkerThreads' where a check is made to make sure the worker threads have been started.
    If they haven't, then 'start' is called which loops through the workerThreads array and starts them all. Once all threads have been started, the task assigned 
    is completed by using a set of locks. 'unleashWorkerThreads' works hand-in-hand with the 'run' method:
        -lockMainthread is initially set to true, this locks the main thread to allow all worker threads to get the "start line" before releasing them to work on the task.
        -in the run method, lockMainThread is unlocked once the atomic counter is the same size as threadPoolSize, implying that all threads have made it past that first if statement and are at the "start line"
        -once all threads are busy waiting at the start line, lockMainThread is unlocked which then allows the main thread to unlock lockStartLine in unleashWorkerThreads().
        -After the worker threads are released, in unleashWorkerThreads(), the threadCounter is reset to get ready to catch the threads again at the startline and the main thread is again locked in order to force the main thread to wait 
            for the worker threads to finish the assigned task and make it to the "finish line"
        -In the run method, the thread takes into account how many tasks need to completed in total, finds which tasks it is responsible for executing using it's threadNumber ID.
        -once it finds its assigned tasks (calculating one cell of a matrix is considered to be one task), it loops through a for loop and calls matrixMultiplication() to complete each task
        -Once that thread has completed all of its tasks, it then checks the last if statement in run() to see if it is the last thread to finish, then it gets caught at the finish line where it waits for all other threads to finish
        -If that thread is the last thread to finish, it unlocks lockMainThread where in unleashWorkerThreads() the main thread then locks the startLine, resets the threadCounter in order to count which threads have made it back to the 
            start line then unlocks lockFinishLine in order to allow all threads to go back to the startLine. 
        -lockMainThread is then locked again in order to wait for all threads to make it back to the start line
        -once all threads have made it back to the start line, lockMainThread is unlocked where it then locks lockFinishLine in order to get ready for another task to be assigned and leaves
            the worker threads busy waiting at the start line until another task is assigned.
    This process is repeated for each task in order to facilitate concurrent calculation of the product matrices as well as implement load balancing between the worker threads. The threads are then broken
    out of their run method when programFinished is set to true by MatrixThreads.

    NOTE: each lock used must be volatile and static to ensure that only one copy of the lock exists across threads. 
    NOTE: threadCounter must be atomic in order to get the correct count since every thread is incrementing it at the same time

Task class
    This class holds the information needed for each task assigned, as well as the helper method that does the matrix multiplication. This class holds the two matrices used for the calculation as well as the initially empty produc matrix.
    To complete each sub task, AKA calculating the value for each cell of the product matrix, matrixMultiplication is called. This method takes in a cell number, then with this cellNumber and the length of the row and columns of the matrices
    used for the calculation, we are able to determine which row and column this cell is associated with. Once these are found, using a for loop, I compute the dot product for the cell in the product matrix. Once this is complete, setFinalMatrix 
    is called to set the correct cell in the product matrix to the calculated sum of the dot product.