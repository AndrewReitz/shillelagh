package com.example.shillelagh;

import shillelagh.Field;
import shillelagh.Table;

@Table("Test")
public class TestModel extends BaseModel {

    int testInt;

    @Field(columnName = "RealTestName")
    String testString;
}
