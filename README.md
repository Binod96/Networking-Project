# Networking-Project
A multithreaded banking system for as many users required(Classes need to be added per user). 
Functions such as Inquire balance, Withdraw, Deposit and Transfer balance between users are employed.
The Server Class is the main server where all the main methods are called.
Locking and Threading techniques are used to open and close the server socket.
The Client class is where Transaction options are defined with the help of catch Exception function.
Each User class is subjected to a RandomGenerator Automated transaction function as for the requirement of this project.
SharedState Class handles the creation/transactions of the users through the lock and release thread system for the multithreaded banking system.
