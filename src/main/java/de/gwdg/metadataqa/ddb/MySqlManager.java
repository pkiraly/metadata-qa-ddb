package de.gwdg.metadataqa.ddb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlManager {

  Connection connection = null;
  PreparedStatement insertRecordStatement = null;
  PreparedStatement updateRecordStatement = null;
  PreparedStatement selectRecordStatement = null;
  PreparedStatement getFileCountBySchemaRecordStatement = null;
  PreparedStatement getRecordCountBySchemaRecordStatement = null;

  PreparedStatement selectFileRecordStatement = null;
  PreparedStatement updateFileRecordStatement = null;
  PreparedStatement insertFileRecordStatement = null;

  public MySqlManager(String host, String port, String path, String user, String password) {
    // load the sqlite-JDBC driver using the current class loader
    try {
      Class.forName("com.mysql.cj.jdbc.Driver"); // com.mysql.jdbc.Driver
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    String url = String.format("jdbc:mysql://%s:%s/%s", host, port, path);
    try {
      connection = DriverManager.getConnection(url, user, password);
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.
      // statement.executeUpdate("drop table if exists record");
      selectRecordStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM record WHERE file = ? AND recordId = ?");
      updateRecordStatement = connection.prepareStatement("UPDATE record SET xml = ? WHERE file = ? AND recordId = ?");
      insertRecordStatement = connection.prepareStatement("INSERT INTO record (file, recordId, xml) VALUES(?, ?, ?)");
      getFileCountBySchemaRecordStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM file WHERE metadata_schema = ?");
      getRecordCountBySchemaRecordStatement = connection.prepareStatement(
        "SELECT count(*) AS count " +
          "FROM file " +
          "JOIN file_record USING (file) " +
          "WHERE metadata_schema = ?"
      );

      selectFileRecordStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM file_record WHERE file = ? AND recordId = ?");
      //updateFileRecordStatement = connection.prepareStatement("UPDATE file_record SET xml = ? WHERE file = ? AND id = ?");
      insertFileRecordStatement = connection.prepareStatement("INSERT INTO file_record (file, recordId) VALUES(?, ?)");
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Insert a new row into the warehouses table
   *
   * @param id
   * @param xml
   */
  public void insertRecord(String file, String id, String xml) {
    try {
      selectRecordStatement.setString(1, file);
      selectRecordStatement.setString(2, id);
      ResultSet rs = selectRecordStatement.executeQuery();
      rs.next();
      int count = rs.getInt("count");
      if (count > 0) {
        updateRecordStatement.setString(1, file);
        updateRecordStatement.setString(2, id);
        updateRecordStatement.setString(3, xml);
        updateRecordStatement.executeUpdate();
      } else {
        insertRecordStatement.setString(1, file);
        insertRecordStatement.setString(2, id);
        insertRecordStatement.setString(3, xml);
        insertRecordStatement.executeUpdate();
      }
    } catch (SQLException e) {
      System.err.println(e.getMessage());
    }
  }

  /**
   * Insert a new row into the warehouses table
   *
   * @param file
   * @param recordId
   */
  public void insertFileRecord(String file, String recordId) {
    try {
      selectFileRecordStatement.setString(1, file);
      selectFileRecordStatement.setString(2, recordId);
      ResultSet rs = selectFileRecordStatement.executeQuery();
      rs.next();
      int count = rs.getInt("count");
      if (count > 0) {
        // updateFileRecordStatement.setString(1, file);
        // updateFileRecordStatement.setString(2, recordId);
        // updateFileRecordStatement.executeUpdate();
      } else {
        insertFileRecordStatement.setString(1, file);
        insertFileRecordStatement.setString(2, recordId);
        insertFileRecordStatement.executeUpdate();
      }
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
  }

  public int getFileCountBySchema(String schemaName) {
    int fileCount = 0;
    try {
      getFileCountBySchemaRecordStatement.setString(1, schemaName);
      ResultSet rs = getFileCountBySchemaRecordStatement.executeQuery();
      rs.next();
      fileCount = rs.getInt("count");
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return fileCount;
  }

  public int getRecordCountBySchema(String schemaName) {
    int recordCount = 0;
    try {
      getRecordCountBySchemaRecordStatement.setString(1, schemaName);
      ResultSet rs = getRecordCountBySchemaRecordStatement.executeQuery();
      rs.next();
      recordCount = rs.getInt("count");
    } catch (SQLException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
    }
    return recordCount;
  }
}
