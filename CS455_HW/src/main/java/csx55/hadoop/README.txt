NameNode: jackson
DataNodes: nashville, olympia, jefferson-city

how to start hadoop cluster:
    $HADOOP_HOME/sbin/start-dfs.sh

how to stop hadoop cluster:
    $HADOOP_HOME/sbin/stop-dfs.sh

how to list directories in the hadoop cluster:
    $HADOOP_HOME/bin/hadoop fs -ls / 
    (list specific directories after the dash if you want to see specific files)

how to make a specific directory called "wc":
    $HADOOP_HOME/bin/hadoop fs -mkdir /wc

how to put a text file in the "wc" directory:
    $HADOOP_HOME/bin/hadoop fs -put youTXTFilename /wc

how to execute the jar (run the mappers and reducers on the textfile):
    $HADOOP_HOME/bin/hadoop jar youJarFilepath WordCount /wc/output
    (the output will be put in the "output" file)
    (WordCount is where you main entry point is)