# Minesweeper

## Description
This is a simple minesweeper game by default, but with extra features. You can play just like the original one, every gameplay related thing in the original can also be found in this version.

## Extra Features
The differences from the original minesweeper game are the following:
* Log in with your Facebook account and your scores will be saved online.
* Browse other player's scores and compare them to yours.
* Your scores are also saved locally, in an XML file.
* New graphics, 4 different colors to customize the game to your liking.

## Usage

### Prerequisites
You will need at least [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) and [Maven](https://maven.apache.org/) on your computer to be able to run the game.

### Offline mode
If you don't want your scores to be saved and just want to play a quick game, you can play the game offline. To do this, simply use 
`mvn exec:java` in the minesweeper-client folder.

Alternatively, you can do `mvn package` in the root folder, and launch the executable jar created in the client folder.

### Online mode

To use the full functionality of the game, you will need online mode. You have two options:


#### 1. Someone has already started a server and you just want to use it
If you have the **IP** and **port** of an already working server, you just need to put them in the [project.properties](/minesweeper-client/src/main/resources/project.properties) file.  
You need to change the following 2 lines:  
* server.address=&#8727;your server IP&#8727;
* server.port=&#8727;your server port&#8727;

The default port is 8888, which is already included. Feel free to change it when needed.


When you are done you can start the game the same way as the offline mode, so please refer above.


If you want to start your own server, please see the instructions below.

#### 2. Starting your own server
Firstly, this project uses login through the [Facebook Graph API](https://developers.facebook.com/docs/graph-api) so you will need to create a new Facebook App, add the Facebook Login product to it, and don't forget to set it public.

You'll need the client ID and app secret from your newly created Facebook App, and put them in the [server.properties](/minesweeper-server/src/main/resources/server.properties) file. Here, you will also need to specify the port where the server listens for requests.  
So you must change these lines:  
* port=&#8727;your server port&#8727;
* clientid=&#8727;your client ID&#8727;
* appsecret=&#8727;your app secret&#8727;

**Rembember to delete these if you fork or use this project. NEVER share your app secret.**


If everything was ok, you should be able to start your server now. You can connect using the instructions above.  
To start the server use one of the following methods:
* Use `mvn exec:java@run-server` within the server directory.
* Build the whole project (from the root folder) with `mvn package`, and execute the created jar file in the server folder.
