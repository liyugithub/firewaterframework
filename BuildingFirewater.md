#How to build Firewater from sources.

### Download Maven2 ###
From http://maven.apache.org/download.html

### Install Maven2 ###
Follow the instructions in the Maven install guide.

### Edit your PATH variable to include the 'mvn' program ###
On Windows, you will right click on 'My Computer' and hit the 'Advanced' button where you should find a way of finding and editing the 'PATH' Environment Variable.

### Check Firewater out of source control ###
see http://code.google.com/p/firewaterframework/source

or just type:
```
svn checkout http://firewaterframework.googlecode.com/svn/trunk/ firewaterframework-read-only
```

### Build it! ###
  * go to the firewaterframework directory in a command tool (Xterm, cygwin, dos prompt)
  * type `mvn clean install`

The first time you build the framework, it will take a while - maven is downloading all dependent JAR files and their dependencies, as well as a bunch of plugins used by Firewater.  Don't worry, the next time you build it will be fast.

You should find a JAR file in the 'target' directory.

### Build the Example ###
```
  cd example
  mvn clean package jetty:run
```

Here, we CD into the firewaterframework/example directory and execute the maven2 command to build and package a WAR file containing our simple Flickr tagging API.  The 'jetty:run' part of the command will actually run the application on port 8080 of your machine.  Once you see that Jetty has been started, fire up a web browser such as Firefox, and go to:

http://localhost:8080/example/ws/photos

You should see a list of our photos in XML format.

Also, try this URL:

http://localhost:8080/example/ws?_method=OPTIONS

This will tell you all of the URLs defined by this API and all of their corresponding METHODs and parameters.