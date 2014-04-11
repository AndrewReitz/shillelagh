package com.example.shillelagh;

import shillelagh.Id;

public class BaseDbModel {
  @Id long id;

  public long getId() {
    return id;
  }
}
