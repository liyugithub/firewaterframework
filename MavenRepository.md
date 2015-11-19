# Including Firewater as a Maven dependency #

The latest Firewater SNAPSHOT build is easily accessed using [Maven2](http://maven.apache.org/download.html).  You will need to include a 

&lt;repository&gt;

 tag in your pom.xml file (or your ~/.m2/settings.xml file) as well as a 

&lt;dependency&gt;

 tag for including Firewater:

**Note the latest release is v0.9.3.1 and the latest snapshot is v0.9.4-SNAPSHOT**

```
<project ....>
  <build>
    ...
  </build>

  <repositories>
    <repository>
      <id>google-code-firewater-repo</id>
      <name>Firewater Maven Repo</name>
      <url>http://firewaterframework.googlecode.com/svn/repo</url>
    </repository>
  </repositories>

  <dependencies>
    <dependency>
      <groupId>org.firewaterframework</groupId>
      <artifactId>firewater</artifactId>
      <version>0.9.3.1</version>      
      <!--<version>0.9.4-SNAPSHOT</version>-->
    </dependency>
    ...
  </dependencies>
</project>
```