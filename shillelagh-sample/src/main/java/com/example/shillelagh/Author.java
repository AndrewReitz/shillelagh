package com.example.shillelagh;

import java.sql.Date;

import shillelagh.Field;
import shillelagh.Id;
import shillelagh.Table;

@Table
public class Author extends BaseDbModel {
  @Field String title;
//  @Field Date publishDate;
}
