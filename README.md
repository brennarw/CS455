# Distributing Systems Homework 1 & 2

## Overlay Assignment: 

The objective of this assignment is to get you familiar with coding in a distributed setting where you need to manage the underlying communications between nodes. Upon completion of this assignment you will have a set of reusable classes that you will be able to draw upon. As part of this assignment you will be: (1) constructing a logical overlay over a distributed set of nodes, and then (2) computing shortest paths using Dijkstra’s algorithm to route packets in the system.
The overlay will contain at least 10 messaging nodes, and each messaging node will be connected to CR (default of 4) other messaging nodes. Each link that connects two messaging nodes within the overlay has a weight associated with it. Links are bidirectional i.e. if messaging node A established a connection to messaging node B, then messaging node B must use that link to communicate with A.
Once the overlay has been setup, messaging nodes in the system will select a node at random and send that node (also known as the sink node) a message. Rather than send this message directly to the sink node, the source node will use the overlay for communications. This is done by computing the shortest route (based on the weights assigned during overlay construction) between the source node and the sink node. Depending on the overlay and link weights, there may be zero or more intermediate messaging nodes that packets between a particular source and sink must pass through. Such intermediate nodes are said to relay the packets. The assignment requires you to verify correctness of packet exchanges between the source and sinks by ensuring that: (1) the number of messages that you send and receive within the system match, and (2) these messages have been not corrupted in transit to the intended recipient. Message exchanges and connection setups/terminations happen continuously in the system.
All communications in this assignment are based on TCP. The assignment must be implemented in Java and you cannot use any external jar files. You must develop all functionality yourself. This assignment may be modified to clarify any questions (and the version number incremented), but the crux of the assignment and the distribution of points will not change.

## Threading Assignment:

In this assignment you will be designing a thread pool that manages a set of matrix multiplications. The matrix multiplications will be expressed as a set of tasks that are managed by a thread pool; a given task is performed by a single thread within the pool.
Given a set of 4 input matrices, you will be computing two intermediate matrices en route to computing the final product matrix. Here is a compact representation for the goals of this assignment.
* There are 4 input matrices: A, B, C, and D
* You will be computing 2 intermediate matrices: X and Y. where X=AB and Y=CD
* The final product matrix Z is the product of the two intermediate matrices. Z=XY
