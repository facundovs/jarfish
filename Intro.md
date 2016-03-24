The inspiration for Jarfish was jarscan by Geoff Yaworski.

There is another project named [jarscan](http://jarscan.dev.java.net/) (not the same one as above, I think) that is a lot like JarFish.


# Introduction #

Jarfish scans
  * a Java archive file
  * a path containing archives [e.g.](Intro#Find_on_a_Path.md)
  * or file containing a list of archive files

for classes.




It can do a couple of things.
  * Find classes [e.g.](Intro#Find_on_a_Path.md)
  * Find classes that are duplicated among a set of jars [e.g.](Intro#Find_Duplicates.md)
  * Find classes that are duplicated and have different versions [e.g.](Intro#Find_Mixed.md)
  * Print information about class files it finds [e.g.](Intro#Extended_Info.md)
  * Find occurences of Strings in class files [e.g.](Intro#Finding_Strings.md)
  * List all classes found



Actions

Find

The **find** action searches a file, path or list of files for a class.

Usage is `find <ClassName> <path>`

  * `<ClassName>` is case sensitive
  * `<path>` can be a file, path, or a file containing a list of files.

### Find on a Path ###
```
reassembler$ java -jar jarfish.jar find TestCase ~/.m2
......................

junit/framework/TestCase.class {} (/Users/reassembler/.m2/repository/junit/junit/3.8.1/junit-3.8.1.jar)
junit/framework/TestCase.class {} (/Users/reassembler/.m2/repository/junit/junit/3.8.2/junit-3.8.2.jar)


```


The dots, ` .................... `, indicate that jarfish is processing archives.

In this case two matching classes were found in two jar files.

## Find Duplicates ##
The **dupes** action finds the same class in different jar files.

```
reassembler$ java -jar jarfish-1.0-rc6.jar dupes ../src/test/resources/
.......
=====
org/reassembler/gepo/Gepo$1.class [2]                            
    /Users/reassembler/jarjar/target/../src/test/resources/dupe-jars/gepo-1.2.2.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.2.1.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.3-b1.jar [2]
org/reassembler/gepo/Gepo$Default.class
    /Users/reassembler/jarjar/target/../src/test/resources/dupe-jars/gepo-1.2.2.jar
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.2.1.jar
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.3-b1.jar
org/reassembler/gepo/Gepo.class [2]
    /Users/reassembler/jarjar/target/../src/test/resources/dupe-jars/gepo-1.2.2.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.2.1.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.3-b1.jar [2]

```

In this case, jarfish found three classes that were duplicated across multiple jars. The format of the listing is:
```
Duplicate Class Name
    archive file found in 1
    archive file found in 2
    etc.
```


This line `org/reassembler/gepo/Gepo.class [2] ` indicates that two(2) different versions of the class were found.

The different versions of the class are tagged with incrementing ids that serve only to indicate which versions are the same and which are different.

So in the example below two(2) versions of the class Gepo were found in three(3) different archives. Classes with the same version tag are the same.
```
org/reassembler/gepo/Gepo.class [2]
    /Users/reassembler/jarjar/target/../src/test/resources/dupe-jars/gepo-1.2.2.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.2.1.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.3-b1.jar [2]
```

#### Extended Info ####
Once you find duplicate classes you might want to run another **find** with extended class info turned on.

```
reassembler$ java -jar jarfish-1.0-rc7.jar find -x Gepo  ../src/test/resources
.......
org/reassembler/gepo/Gepo.class {size=7456, time=Tue Dec 30 09:18:58 EST 2008, classMeta=java version:1.0/1.1, class version:45.3} (/Users/reassembler/jarjar/target/../src/test/resources/dupe-jars/gepo-1.2.2.jar)
org/reassembler/gepo/Gepo.class {size=7456, time=Tue Dec 30 09:18:58 EST 2008, classMeta=java version:1.0/1.1, class version:45.3} (/Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.2.1.jar)
org/reassembler/gepo/Gepo.class {size=13454, time=Thu Jun 04 15:24:36 EDT 2009, classMeta=java version:1.0/1.1, class version:45.3} (/Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.3-b1.jar)

```


#### Find Mixed ####
If you're just looking for different versions of the same class amongst a bunch of jars, you can skip the **dupes** action and use **mixed** instead.

The **mixed** action only prints classes that have duplicates with different versions.

```
reassembler$ java -jar jarfish-1.0-rc6.jar mixed  ../src/test/resources/
.......
=====org/reassembler/gepo/Gepo$1.class [2]
    /Users/reassembler/jarjar/target/../src/test/resources/dupe-jars/gepo-1.2.2.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.2.1.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.3-b1.jar [2]
org/reassembler/gepo/Gepo.class [2]
    /Users/reassembler/jarjar/target/../src/test/resources/dupe-jars/gepo-1.2.2.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.2.1.jar [1]
    /Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.3-b1.jar [2]
```

#### Finding Strings ####
Jarfish can search class files for occurences of String objects. The Strings must be used as literals in the class to be found. Jarfish will not find Strings that have been localized.

Here I am searching for the string "problem". Jarfish found the string twice and printed the results.

When finding strings, you can make the search case-insensitive by passing the **-i** switch.

```
reassembler$ java -jar jarfish-1.0-rc6.jar findString -i problem ../src/test/resources/
query: problem
.......
/Users/reassembler/jarjar/target/../src/test/resources/dupe-jars/gepo-1.2.2.jar
  org/reassembler/gepo/Gepo.class
    problem loading url: 
/Users/reassembler/jarjar/target/../src/test/resources/jars/gepo-1.2.1.jar
  org/reassembler/gepo/Gepo.class
    problem loading url: 
```