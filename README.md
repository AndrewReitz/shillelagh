# Shillelagh

![Library Icon](AndroidShillelagh.png)
![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Shillelagh-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1026)

Shillelagh is an sqlite library. It was built to make life easier. The entire library 
was built around simplicity when using sqlite in Android. 

Quick and dirty.
- Create your model objects, the ones you want to persist
- Add the `@Table` annotation to the model class, and make sure you have a field `@Id long id`
- Create your SQLiteOpenHelper and use `Shillelagh.createTable` to make your tables
- Create an instance of `Shillelagh`
- Create and save your objects!

For how to use see the 
[example](//github.com/pieces029/shillelagh/tree/master/shillelagh-sample) or the 
[JavaDocs](//pieces029.github.io/shillelagh). 

# Supported Types

Supported Types (and corresponding primitives)
- Integer
- Double
- Float
- Long
- Short
- String
- Date
- Boolean

## One to Many and One to One

One to one and one to many relationships are supported. You will need to make sure that the child 
objects are annotated like any other tables. For one to many, make sure you use a list, arrays 
are currently not supported.

## Blobs

Byte arrays are supported with nothing out of the ordinary needing to be done. If you would like to 
save another object type as a blob, you will need to tell Shillelagh that it should be serialized
by adding `@Field(isBlob = true)` to the annotation. These objects MUST also implement the 
Serializable interface.

## Other Notes

- Empty constructors must be provided at package protected level or higher. There is the `@OrmOnly`
 annotation provided to indicate to others that the constructor is only visible for Shillelagh
- Inner classes MUST be marked static.
- Don't forget to update your database version if you change your models 
(Also create migration scripts).

# Download

You will need to include the shillelagh jar in your application's runtime and shillelagh-processor 
jar in your application compile time. You can download the jars 
[here](//bintray.com/pieces/maven/Shillelagh/view) or use Maven/Gradle configurations below.

## Maven
```xml
  <dependency>
    <groupId>com.andrewreitz</groupId>
    <artifactId>shillelagh</artifactId>
    <version>0.3.0</version>
  </dependency>
  <dependency>
    <groupId>com.andrewreitz</groupId>
    <artifactId>shillelagh-processor</artifactId>
    <version>0.3.0</version>
    <optional>true</optional>
  </dependency>
```
## Gradle:
```groovy
  compile 'com.andrewreitz:shillelagh:0.3.0'
  provided 'com.andrewreitz:shillelagh:0.3.0'
```

# License

    Copyright 2014 Andrew Reitz
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    under the License is distributed on an "AS IS" BASIS,
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    the License for the specific language governing permissions and
    under the License.
