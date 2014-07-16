package shillelagh.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class TableObject {

  /** Used as a template to create a new table */
  private static final String CREATE_TABLE_DEFAULT = "CREATE TABLE %s " +
          "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s);";

  private final String tableName;
  private String idColumnName;

  private final List<TableColumn> columns = new LinkedList<TableColumn>();

  TableObject(String tableName) {
    this.tableName = tableName;
  }

  void setIdColumnName(String idColumnName) {
    this.idColumnName = idColumnName;
  }

  String getIdColumnName() {
    return idColumnName;
  }

  List<TableColumn> getColumns() {
    return columns;
  }

  void addColumn(TableColumn column) {
    columns.add(column);
  }

  String getTableName() {
    return tableName;
  }

  /** Prints out the table schema */
  String getSchema() {
    StringBuilder sb = new StringBuilder();
    Iterator<TableColumn> iterator = columns.iterator();
    while (iterator.hasNext()) {
      sb.append(iterator.next());
      if (iterator.hasNext()) {
        sb.append(", ");
      }
    }

    return String.format(
            CREATE_TABLE_DEFAULT,
            tableName,
            idColumnName,
            sb.toString()
    );
  }

  @Override public String toString() {
    return getSchema();
  }
}
