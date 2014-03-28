package com.example.shillelagh;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table("Test")
public class TestModel extends BaseModel {

    @Id
    int testInt;

    @Field(columnName = "RealTestName")
    String testString;
}
