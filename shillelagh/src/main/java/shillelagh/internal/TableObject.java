package shillelagh.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class TableObject {

    /** Used as a template to create a new table */
    private static final String CREATE_TABLE_DEFAULT = "CREATE TABLE %s " +
            "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s);";

    private final String tableName;
    private String idColumnName = "_id"; // default to _id if one isn't provided

    private final List<TableColumn> columns = new LinkedList<>();

    public TableObject(String tableName) {
        this.tableName = tableName;
    }

    public void setIdColumnName(String idColumnName) {
        this.idColumnName = idColumnName;
    }

    public void addColumn(TableColumn column) {

    }

    @Override public String toString() {
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
}
