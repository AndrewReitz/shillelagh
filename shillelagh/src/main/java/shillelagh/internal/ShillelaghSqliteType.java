package shillelagh.internal;

import shillelagh.SqliteType;

class ShillelaghSqliteType {

    private SqliteType type;
    private String columnName;

    ShillelaghSqliteType(String columnName, SqliteType type) {
        this.columnName = columnName;
        this.type = type;
    }

    @Override public String toString() {
        return columnName + " " + type.toString();
    }
}
