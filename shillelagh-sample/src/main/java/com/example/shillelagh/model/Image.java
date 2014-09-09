package com.example.shillelagh.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class Image implements Serializable {
  private String name;
  private Date created;
  private byte[] data;

  public Image(String name, Date created, byte[] data) {
    this.name = name;
    this.created = created;
    this.data = data;
  }

  public String getName() {
    return name;
  }

  public Date getCreated() {
    return created;
  }

  public byte[] getData() {
    return data;
  }

  @Override public String toString() {
    return "Image{" +
        "name='" + name + '\'' +
        ", created=" + created +
        ", data=" + Arrays.toString(data) +
        '}';
  }
}
