package com.qa.framework.library.database;

import com.library.common.CollectionHelper;
import com.library.common.StringHelper;
import com.qa.framework.config.PropConfig;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * The type Db DBHelper.
 */
public class DBHelper {

    private final static Logger logger = Logger.getLogger(DBHelper.class);

    private static final Map<String, Connection> connContainer = new HashMap<>();
    private static final QueryRunner queryRunner = new QueryRunner();
    private static String poolName;

    static {
        String dbPoolName = PropConfig.getDbPoolName();
        if (dbPoolName == null) {
            String webPath = PropConfig.getWebPath();
            if (StringHelper.startsWithIgnoreCase(webPath, "http://")) {
                if (webPath.substring(7).contains("/")) {
                    poolName = StringHelper.getTokensList(webPath.substring(7), "/").get(0);
                } else {
                    poolName = webPath.substring(7);
                }
            }
        } else {
            poolName = dbPoolName;
        }
    }

    /**
     * 获取数据库连接
     *
     * @param poolName the poolname
     * @return the connection
     */
    public static Connection getConnection(String poolName) {
        Connection conn = connContainer.get(poolName);
        if (conn == null) {
            try {
                conn = DBPoolFactory.getDbConnection(poolName);
            } catch (SQLException e) {
                logger.error("get connection failure", e);
                throw new RuntimeException(e);
            } finally {
                connContainer.put(poolName, conn);
            }
        }
        return conn;
    }

    /**
     * Gets connection.
     *
     * @return the connection
     */
    public static Connection getConnection() {
        return getConnection(poolName);
    }

    /**
     * 开启事务
     */
    public static void beginTransaction(String poolName) {
        Connection conn = getConnection(poolName);
        if (conn != null) {
            try {
                conn.setAutoCommit(false);
            } catch (SQLException e) {
                logger.error("开启事务出错！", e);
                throw new RuntimeException(e);
            } finally {
                connContainer.put(poolName, conn);
            }
        }
    }

    public static void beginTransaction() {
        beginTransaction(poolName);
    }

    /**
     * 提交事务
     */
    public static void commitTransaction(String poolName) {
        Connection conn = getConnection(poolName);
        if (conn != null) {
            try {
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                logger.error("提交事务出错！", e);
                throw new RuntimeException(e);
            }
        }
    }

    public static void commitTransaction() {
        commitTransaction(poolName);
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction(String poolName) {
        Connection conn = getConnection(poolName);
        if (conn != null) {
            try {
                conn.rollback();
                conn.close();
            } catch (SQLException e) {
                logger.error("回滚事务出错！", e);
                throw new RuntimeException(e);
            } finally {
                connContainer.remove(poolName);
            }
        }
    }


    public static void rollbackTransaction() {
        rollbackTransaction(poolName);
    }

    /**
     * 执行查询语句
     *
     * @param sql    the sql
     * @param params the params
     * @return the list
     */
    public static List<Map<String, Object>> queryRows(String sql, Object... params) {
        List<Map<String, Object>> result;
        try {
            Connection conn = getConnection();
            result = queryRunner.query(conn, sql, new MapListHandler(), params);
        } catch (Exception e) {
            logger.error("execute query failure:"+sql, e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public static List<Map<String, Object>> queryRows(String poolName, String sql, Object... params) {
        List<Map<String, Object>> result;
        try {
            Connection conn = getConnection(poolName);
            result = queryRunner.query(conn, sql, new MapListHandler(), params);
        } catch (Exception e) {
            logger.error("execute query failure:"+sql, e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * Query one row map.
     *
     * @param sql    the sql
     * @param params the params
     * @return the map
     */
    public static Map<String, Object> queryOneRow(String sql, Object... params) {
        List<Map<String, Object>> records = queryRows(sql, params);
        if (records == null || records.size() == 0) {
            return null;
        }
        Random random = new Random();
        return records.get(random.nextInt(records.size()));
    }

    public static Map<String, Object> queryOneRow(String poolName, String sql, Object... params) {
        List<Map<String, Object>> records = queryRows(poolName, sql, params);
        if (records == null || records.size() == 0) {
            return null;
        }
        Random random = new Random();
        return records.get(random.nextInt(records.size()));
    }

    /**
     * Query field string.
     *
     * @param sql        the sql
     * @param columnName the column name
     * @param params     the params
     * @return the string
     */
    public static String queryField(String sql, String columnName, Object... params) {
        Map<String, Object> recordInfo = queryOneRow(sql, params);
        if (recordInfo != null) {
            return recordInfo.get(columnName).toString();
        }
        return null;
    }

    public static String queryField(String poolName, String sql, String columnName, Object... params) {
        Map<String, Object> recordInfo = queryOneRow(poolName, sql, params);
        if (recordInfo != null) {
            return recordInfo.get(columnName).toString();
        }
        return null;
    }

    /**
     * Has record boolean.
     *
     * @param sql    the sql
     * @param params the params
     * @return the boolean
     */
    public static boolean hasRecord(String sql, Object... params) {
        List<Map<String, Object>> records = queryRows(sql, params);
        return records.size() > 0;
    }

    public static boolean hasRecord(String poolName, String sql, Object... params) {
        List<Map<String, Object>> records = queryRows(poolName, sql, params);
        return records.size() > 0;
    }

    /**
     * 执行更新语句（包括：update、insert、delete）
     *
     * @param sql    the sql
     * @param params the params
     * @return the int
     */
    public static int executeUpdate(String sql, Object... params) {
        int rows = 0;
        try {
            Connection conn = getConnection();
            rows = queryRunner.update(conn, sql, params);
        } catch (SQLException e) {
            logger.error("execute update failure:"+sql, e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    public static int executeUpdate(String poolName, String sql, Object... params) {
        int rows = 0;
        try {
            Connection conn = getConnection(poolName);
            rows = queryRunner.update(conn, sql, params);
        } catch (SQLException e) {
            logger.error("execute update failure:"+sql, e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    /**
     * 查询实体列表
     *
     * @param <T>         the type parameter
     * @param entityClass the entity class
     * @param sql         the sql
     * @param params      the params
     * @return the list
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass, String sql, Object... params) {
        List<T> entityList;
        try {
            Connection conn = getConnection();
            entityList = queryRunner.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            logger.error("query entity list failure:"+sql, e);
            throw new RuntimeException(e);
        }
        return entityList;
    }

    public static <T> List<T> queryEntityList(String poolName, Class<T> entityClass, String sql, Object... params) {
        List<T> entityList;
        try {
            Connection conn = getConnection(poolName);
            entityList = queryRunner.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            logger.error("query entity list failure:"+sql, e);
            throw new RuntimeException(e);
        }
        return entityList;
    }

    /**
     * 查询实体
     *
     * @param <T>         the type parameter
     * @param entityClass the entity class
     * @param sql         the sql
     * @param params      the params
     * @return the t
     */
    public static <T> T queryEntity(Class<T> entityClass, String sql, Object... params) {
        T entity;
        try {
            Connection conn = getConnection();
            entity = queryRunner.query(conn, sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            logger.error("query entity failure:"+sql, e);
            throw new RuntimeException(e);
        }
        return entity;
    }

    /**
     * 插入实体
     *
     * @param <T>         the type parameter
     * @param entityClass the entity class
     * @param fieldMap    the field map
     * @return the boolean
     */
    public static <T> boolean insertEntity(Class<T> entityClass, Map<String, Object> fieldMap) {
        if (CollectionHelper.isEmpty(fieldMap)) {
            logger.error("can not insert entity: fieldMap is empty");
            return false;
        }

        String sql = "INSERT INTO " + getTableName(entityClass);
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "), columns.length(), ")");
        values.replace(values.lastIndexOf(", "), values.length(), ")");
        sql += columns + " VALUES " + values;

        Object[] params = fieldMap.values().toArray();

        return executeUpdate(sql, params) == 1;
    }

    /**
     * 更新实体
     *
     * @param <T>         the type parameter
     * @param entityClass the entity class
     * @param id          the id
     * @param fieldMap    the field map
     * @return the boolean
     */
    public static <T> boolean updateEntity(Class<T> entityClass, long id, Map<String, Object> fieldMap) {
        if (CollectionHelper.isEmpty(fieldMap)) {
            logger.error("can not update entity: fieldMap is empty");
            return false;
        }

        String sql = "UPDATE " + getTableName(entityClass) + " SET ";
        StringBuilder columns = new StringBuilder();
        for (String fieldName : fieldMap.keySet()) {
            columns.append(fieldName).append(" = ?, ");
        }
        sql += columns.substring(0, columns.lastIndexOf(", ")) + " WHERE id = ?";

        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);
        Object[] params = paramList.toArray();

        return executeUpdate(sql, params) == 1;
    }

    /**
     * 删除实体
     *
     * @param <T>         the type parameter
     * @param entityClass the entity class
     * @param id          the id
     * @return the boolean
     */
    public static <T> boolean deleteEntity(Class<T> entityClass, long id) {
        String sql = "DELETE FROM " + getTableName(entityClass) + " WHERE id = ?";
        return executeUpdate(sql, id) == 1;
    }

    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }

    /**
     * Query count long.
     *
     * @param sql    the sql
     * @param params the params
     * @return the long
     */
    public long queryCount(String sql, Object... params) {
        long result;
        try {
            Connection conn = getConnection();
            result = queryRunner.query(conn, sql, new ScalarHandler<Long>("count(*)"), params);
        } catch (SQLException e) {
            logger.error("查询出错::"+sql, e);
            throw new RuntimeException(e);
        }
        return result;
    }

}
