package de.gwdg.metadataqa.ddb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteManager {

  Connection connection = null;
  PreparedStatement insertStatement = null;

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
      statement.executeUpdate("drop table if exists record");
      statement.executeUpdate("create table record (id string, xml string)");
      insertStatement = connection.prepareStatement("INSERT INTO record (id, xml) VALUES(?, ?)");
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
  public void insert(String id, String xml) {
    try {
      insertStatement.setString(1, id);
      insertStatement.setString(2, xml);
      insertStatement.executeUpdate();
    } catch (SQLException e) {
      System.out.println(e.getMessage());
    }
  }
}
