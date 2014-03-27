package shillelagh.internal;

public class ShillelaghInjector {

    /** Internal class function to get the sql string */
    public static final String CREATE_TABLE_FUNCTION = "getCreateTableSql";

    /** Used as a template to create a new table */
    private static final String CREATE_TABLE_DEFAULT = "CREATE TABLE %s " +
            "(%s INTEGER PRIMARY KEY AUTOINCREMENT, %s);";
}
