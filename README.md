# CChat
CChat simple chat server network which allows students at small universities to discuss current events and opportunities with other students in their major.

## Files and Directories
* README.md                  
  * This document
* GroupRouters.txt           
  * Stores the encrypted passwords and IP addresses used to run a chat room, passwords and IPs can change at any time.
* src/                      
  * Files which run the chat room network
* Central Server.java        
  * Central server uses GroupRouters.txt to ensure the client enters valid chat room information (group name and password)
* ChatServer.java            
  * Client is connected to ChatServer via GroupRouter, ChatServer is in charge of running the actual chatroom.
* ChatRoomGUI.java           
  * When a client is started on the network an interactive GUI will display the chat room application
* ChatServerProcesses.java   
  * This handles threading for the chat servers
* Client.java                
  * Implementation of the client
* Connection.java           
  * Creates a thread which allows servers to read and write to and from their listening sockets
* Crypto.java                
  * Implements a caesar cypher.
* Encryption.java            
  * Allows us to encrypt new IP addresses and passwords for GroupRouters.txt
* GroupRouter.java          
  * Reroutes client to ChatServer and keeps track of number of clients on each ChatServer
* GroupRouterID.java         
  * Defines the components of a GroupRouter object
* GroupRoutersTable.java   
  * Uses a hashmap to store GroupRouter information (key is group name, value is GroupRouterID object)
* ReadClient.java            
  * Continuously listens for messages from other clients
* Server.java               
  * Abstract class which is used by all server classes to open listening sockets and create separate threads for reading and writing messages
* Welcome.java             
  * This file is also apart of the GUI implementation
* WriteClient.java          
  * Thread which allows clients to write to each other
* WrongInfo.java             
  * Displays GUI if incorrect information is entered


##To test, run (in this order):
1. java Encryption <group name> <group router address> <group router port> <new password>
  1. Note: Can be run any number of times to update Group Router information
2. java CentralServer <central server address> <central server port>
3. java GroupRouter <group router address> <group router port>
  3. Note: any number of Group Routers can be started as long as the address and port matches that in GroupRouters.txt and a Group Router for that chat is not already running
4. java ChatServer <chat server address> <chat server port> <group router address <group router port>
  4. Note: up to 5 Chat Servers can be connected to a specific Group Router
5. java Client

In order to run the network correctly, it is necessary to hard code the CentralServer IP address in the Client file, when run the CentralServer should not change
