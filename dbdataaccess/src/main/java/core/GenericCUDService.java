package core;

import com.google.common.base.CaseFormat;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class GenericCUDService<T> {
    private Database db;
    private String schemaName;
    private String tableName;
    private Class<T> pojo;
    private List<Method> pojoGetters;

    private String idName;
    private Set<String> columnNames;

    private String preparedUpdateQuery;
    private String preparedDeleteQuery;
    private String preparedInsertQuery;
    /*
    - No such table in given schema
    - pojo does not match with table
    - table does not have PK defined
     */
    public GenericCUDService(Database db, String schemaName, String tableName, Class<T> pojo) throws SQLException {
        this.db = db;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.pojo = pojo;

        MapHandler mh = new MapHandler();
        MapListHandler mlh = new MapListHandler();
        Connection c = db.src.getConnection();

        List<Map<String, Object>> res = mlh.handle(c.getMetaData()
                .getColumns(null, schemaName, tableName, null));
        if (res == null || res.size() == 0) {
            throw new NoSuchElementException(tableName);
        }
        columnNames = res.stream()
                .map(mapEntry -> ((String)mapEntry.get("COLUMN_NAME")).toUpperCase().trim())
                .collect(Collectors.toSet());

        Map<String, Object> key = mh.handle(c.getMetaData().getPrimaryKeys(null, schemaName, tableName));
        if (key == null || key.size() == 0) {
            throw new NoKeyException(tableName);
        }
        idName = (String)key.get("COLUMN_NAME");

        this.preparedUpdateQuery = String.format("UPDATE %s.%s SET %s = ? WHERE {$fieldName} = ?",
                schemaName, tableName, idName);
        this.preparedDeleteQuery = String.format("DELETE FROM %s.%s WHERE %s = ?",
                schemaName, tableName, idName);
        this.preparedInsertQuery = this.generateInsertStringTemplate(pojo, res, schemaName + "." + tableName);
    }

    /*
        Errors to consider:
        - No such field name in given table
        - Wrong datatype for given field
        - constraint violation
         */
    public void update(Long id, String fieldName, Object fieldValue) {
        try {
            fieldName = transformToColumnName(fieldName);
            if (!columnNames.contains(fieldName.toUpperCase().trim())) {
                throw new NoSuchElementException(fieldName);
            }
            db.runner.update(preparedUpdateQuery.replace("{$fieldName}", fieldName), fieldValue, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // constraint violation
    public Long insert(T obj) throws SQLException {
        final ResultSetHandler<Long> h = new ScalarHandler<>();
        Object[] values = new Object[pojoGetters.size()];
        for (int i = 0; i < values.length; i++) {
            try {
                values[i] = pojoGetters.get(i).invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Something went wrong with generated insert statement. ", e);
            }
        }

        return db.runner.insert(preparedInsertQuery, h, values);
    }

    // true if deleted anything, false otherwise
    public boolean delete(Long id) throws SQLException {
        int i = db.runner.update(preparedDeleteQuery, id);
        return i == 1;
    }

    private String generateInsertStringTemplate(Class<T> pojo, List<Map<String, Object>> mp, String fullTableName) {
        Method[] methods = pojo.getDeclaredMethods();
        Arrays.sort(methods);
        StringBuilder bldr = new StringBuilder();
        bldr.append("INSERT INTO %s (");
        int count = 0;
        pojoGetters = new ArrayList<>();
        for (Method m: methods) {
            if (m.getReturnType().equals(Void.class) && m.getName().startsWith("get") && !m.getName().endsWith("_id")) {
                bldr.append(transformToColumnName(m.getName())).append(", ");
                pojoGetters.add(m);
                count++;
            }
        }
        bldr.append(") (")
            .append(Collections.nCopies(count - 1, "?, "))
            .append("?)");

        return bldr.toString();
    }

    private String transformToColumnName(String methodName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                methodName.replaceFirst("^(get|is)", ""));
    }
}

class NoKeyException extends RuntimeException {
    public final String TABLE_NAME;

    NoKeyException(String tableName) {
        TABLE_NAME = tableName;
    }
}
