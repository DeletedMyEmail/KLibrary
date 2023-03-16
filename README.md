# KLibrary

## About The Project

<img align="right" src="https://user-images.githubusercontent.com/88390464/200192182-7f87b55a-0197-4b84-8f68-564b83a06920.png" height="200" width="200" alt="logo">

This is just a small collection of classes that will help you deal with <br> 
- [system paths](src/main/java/klibrary/utils/SystemUtils.java)
- [databases](src/main/java/klibrary/utils/SQLUtils.java)
- [sockets](src/main/java/klibrary/net/SocketWrapper.java) and [server sockets](src/main/java/klibrary/net/ServerSocketWrapper.java)
- classes of the North Rhine-Westphalia government
  - [List](src/main/java/klibrary/net/abiturklassen/AbiListWrapper.java)
  - [Graphs](src/main/java/klibrary/utils/abiturklassen/GraphHandler.java)
- [encryption](src/main/java/klibrary/utils/EncryptionUtils.java)
  - OTP
  - RSA
  - AES
  - Hashing

## Getting Started

Add the [JAR](out/artifacts/KLibrary_jar) to your project as shown [here](https://stackoverflow.com/questions/1051640/correct-way-to-add-external-jars-lib-jar-to-an-intellij-idea-project). That's it. Now you're able to import all classes.

Last stable version: **1.3.3**

### Server

Create a new class that inherits from [AbstractServer](src/main/java/klibrary/net/AbstractServer.java) in order to implement its 
**abstract methods**: 

- ```onClientConnect(SocketWrapper pClient)``` - Triggered when a client connects to the server and (if necessary) finished a key handshake for encryption 
- ```onClientDisconnect(SocketWrapper pClient)``` - Triggered when the connection to a client is lost 
- ```onMessage(SocketWrapper pClient, String pMessage)``` - Triggered when the server receives a message from a client

**Constructor** 
- ```AbstractServer(int pPort, boolean pEncryptionRequired)``` - Takes the port to listen on and a boolean which requires encryption if true

Call ```acceptSockets()``` to start listening for sockets.

### Client

Create a new class that inherits from [AbstractClient](src/main/java/klibrary/net/AbstractClient.java) in order to implement its
abstract methods:

- ```onMessageReadError(Exception pException)``` - Triggered if an error occurs while reading a message from the server
- ```onMessageReceived(String pMessage)``` - Triggered when a message sent by the server is received

**Constructor**
- ```AbstractClient(String pIp, int pPort, boolean pEnableEncryption)``` - Takes the ip and port of the server to connect to and a boolean which enables encryption if true

Call ```listenForMessages()``` to start listening for message in another thread.

### SQLUtils

### SortUtils

### SearchUtils

### EncryptionUtils

### SystemUtils
