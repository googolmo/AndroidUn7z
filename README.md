AndroidUn7z
==================

* A simple android ndk library used to simply extract lzma 7z files.<br>

* Some times we need to compress some resources in our applications,in some cases,LZMA 
 get smaller achieves than common zip,so we need extract the resources when the app 
start to run,this library is to do the work.<br>

###1.Introduction
* This is a small free library with simple function to extract the 7z file
* It is a jni call library
* This library is based on LZMA sdk,it does the most job.

###2.Usage

```groovy
compile 'im.amomo.andun7z:library:1.2.0'
```

```java
import im.amomo.andun7z.AndUn7z;

public void extract7zip() {
    AndUn7z.extract7z("a.zip", destinationPath);
    AndUn7z.extract7z(context.getAsset(), "test.7z", destinationPath);
}
```

###3.Notice

Please make sure that your application has permission to write data to external storage

