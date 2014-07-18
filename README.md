# Shillelagh

![Library Icon](AndroidShillelagh.png)

** Currently a work in progress. Please submit any feature requests **

Shillelagh is an sqlite library. It was build to make life easier. The entire library 
was build around simplicity when using sqlite. 

Quick and dirty.
- Create your model objects, the ones you want to persist
- Add the `@Table` annotation to the model class, and make sure you have a field `long id`
- Create your SQLiteOpenHelper and use `Shillelagh.createTable` to make your tables
- Create an instance of `Shillelagh`
- Create and save your objects!

For how to use see the [example](https://github.com/pieces029/shillelagh/tree/master/shillelagh-sample). 

Supported Types (and corresponding primitives)
- Integer
- Double
- Float
- Long
- Short
- String
- Date
- Boolean

Must Provide Empty Constructors. Can be package protected
Don't forget to update your database version if you change your models (Also create migration scripts)

## TODO
- Relationship Support
- Constraint Support
- More Unit Tests
- Documentation
- Pull processor out into separate container += refactor writing of internal code

# Download

Download [the latest JAR](http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=com.andrewreitz&a=shillelagh&v=LATEST) or grab via Maven:
```xml
  <dependency>
    <groupId>com.andrewreitz</groupId>
    <artifactId>shillelagh</artifactId>
    <version>0.1.0</version>
  </dependency>
```
or Gradle:
```groovy
  compile 'com.andrewreitz:shillelagh:0.1.0'
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
