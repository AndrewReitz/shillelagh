package shillelagh.internal;

import shillelagh.SqliteType;

class TableColumn {

    private SqliteType type;
    private String columnName;

    TableColumn(String columnName, SqliteType type) {
        this.columnName = columnName;
        this.type = type;
    }

    @Override public String toString() {
        return columnName + " " + type.toString();
    }
}
