package de.gwdg.metadataqa.ddb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteManager {

  Connection connection = null;
  PreparedStatement insertStatement = null;
  PreparedStatement updateStatement = null;
  PreparedStatement selectStatement = null;

  public SqliteManager(String path) {
    // load the sqlite-JDBC driver using the current class loader
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }

    // SQLite connection string
    String url = "jdbc:sqlite:" + path;
    try {
      connection = DriverManager.getConnection(url);
      Statement statement = connection.createStatement();
      statement.setQueryTimeout(30);  // set timeout to 30 sec.
      // statement.executeUpdate("drop table if exists record");
      statement.executeUpdate("CREATE TABLE IF NOT EXISTS record (file STRING, id STRING, xml TEXT)");
      selectStatement = connection.prepareStatement("SELECT COUNT(*) AS count FROM record WHERE file = ? AND id = ?");
      updateStatement = connection.prepareStatement("UPDATE record SET xml = ? WHERE file = ? AND id = ?");
      insertStatement = connection.prepareStatement("INSERT INTO record (file, id, xml) VALUES(?, ?, ?)");
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }

  /**
   * Insert a new row into the warehouses table
   *
   * @param id
   * @param xml
   */
  public void insert(String file, String id, String xml) {
    try {
      selectStatement.setString(1, file);
      selectStatement.setString(2, id);
      ResultSet rs = selectStatement.executeQuery();
      rs.next();
      int count = rs.getInt("count");
      if (count > 0) {
        updateStatement.setString(1, file);
        updateStatement.setString(2, id);
        updateStatement.setString(3, xml);
        updateStatement.executeUpdate();
      } else {
        insertStatement.setString(1, file);
        insertStatement.setString(2, id);
        insertStatement.setString(3, xml);
        insertStatement.executeUpdate();
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
