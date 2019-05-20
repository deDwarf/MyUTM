package core;

import com.google.common.base.CaseFormat;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class GenericCUDService<T> {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Database db;
    private String schemaName;
    private String tableName;
    private Class<T> pojo;

    private String idName;
    private List<Method> pojoGetters;
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
        pojoGetters = new ArrayList<>();

        MapHandler mh = new MapHandler();
        MapListHandler mlh = new MapListHandler();
        Connection c = db.src.getConnection();

        List<Map<String, Object>> res = mlh.handle(c.getMetaData()
                .getColumns(null, schemaName, tableName, null));
        if (res == null || res.size() == 0) {
            throw new NoSuchElementException(tableName);
        }
        this.columnNames = res.stream()
                .map(mapEntry -> ((String)mapEntry.get("COLUMN_NAME")).toUpperCase().trim())
                .collect(Collectors.toSet());

        Map<String, Object> key = mh.handle(c.getMetaData().getPrimaryKeys(null, schemaName, tableName));
        if (key == null || key.size() == 0) {
            throw new NoKeyException(tableName);
        }
        idName = (String)key.get("COLUMN_NAME");

        this.preparedUpdateQuery = String.format("UPDATE %s.%s SET {$fieldName} = ? WHERE %s = ?",
                schemaName, tableName, idName);
        this.preparedDeleteQuery = String.format("DELETE FROM %s.%s WHERE %s = ?",
                schemaName, tableName, idName);
        this.preparedInsertQuery = this.generateInsertStringTemplate(pojo, schemaName + "." + tableName);
    }

    /*
        Errors to consider:
        - No such field name in given table
        - Wrong datatype for given field
        - constraint violation
         */
    public void update(Long id, String fieldName, Object fieldValue) throws SQLException {
        fieldName = transformToColumnName(fieldName);
        if (!columnNames.contains(fieldName.toUpperCase().trim())) {
            throw new NoSuchElementException(fieldName);
        }
        String updateStatement = preparedUpdateQuery.replace("{$fieldName}", fieldName);
        log.info("Running update: <{}>. Params: {}, {}", updateStatement, fieldValue, id);
        db.runner.update(preparedUpdateQuery.replace("{$fieldName}", fieldName), fieldValue, id);
    }

    // constraint violation
    public BigInteger insert(T obj) throws SQLException {
        final ResultSetHandler<BigInteger> h = new ScalarHandler<>();
        Object[] values = new Object[pojoGetters.size()];
        for (int i = 0; i < values.length; i++) {
            try {
                values[i] = pojoGetters.get(i).invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Something went wrong with generated insert statement. ", e);
            }
        }
        log.info("Running insert <{}>. Params: <{}>", preparedInsertQuery, values);
        return db.runner.insert(preparedInsertQuery, h, values);
    }

    // true if deleted anything, false otherwise
    public boolean delete(Long id) throws SQLException {
        log.info("Running delete <{}>. Id: <{}>", preparedDeleteQuery, id);
        int i = db.runner.update(preparedDeleteQuery, id);
        return i == 1;
    }

    private String generateInsertStringTemplate(Class<T> pojo, String fullTableName) {
        Method[] methods = pojo.getDeclaredMethods();
        Arrays.sort(methods, Comparator.comparing(Method::getName));
        StringBuilder bldr = new StringBuilder();
        bldr.append("INSERT INTO ").append(fullTableName).append(" (");
        for (Method m: methods) {
            if (!m.getReturnType().equals(Void.TYPE) && m.getName().startsWith("get") && !m.getName().endsWith("_id")) {
                String assumedColumnName = transformToColumnName(m.getName());
                if (!this.columnNames.contains(assumedColumnName.toUpperCase())) {
                    throw new InvalidPojoException(String.format("%s column does not exist in target column list", assumedColumnName));
                }
                bldr.append(transformToColumnName(m.getName())).append(", ");
                pojoGetters.add(m);
            }
        }
        bldr.replace(bldr.length() - 2, bldr.length(), "") // remove trailing ', '
            .append(") VALUES(")
            .append(String.join(",", Collections.nCopies(pojoGetters.size(), "?")))
            .append(")");

        return bldr.toString();
    }

    private String transformToColumnName(String methodName) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,
                methodName.replaceFirst("^(get|is)", ""));
    }
}

class InvalidPojoException extends RuntimeException {
    public InvalidPojoException(String message) {
        super(message);
    }
}

class NoKeyException extends RuntimeException {
    public final String TABLE_NAME;

    NoKeyException(String tableName) {
        TABLE_NAME = tableName;
    }
}
