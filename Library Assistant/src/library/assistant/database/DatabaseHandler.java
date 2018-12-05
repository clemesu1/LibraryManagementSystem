package library.assistant.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import library.assistant.ui.listbook.BookListController.Book;
import library.assistant.ui.listmember.MemberListController;

public final class DatabaseHandler {
    private static DatabaseHandler handler = null;
    
    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn = null;
    private static Statement stmt = null;
    
    private DatabaseHandler() {
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
    }
    
    public static DatabaseHandler getInstance() {
        if(handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }
    public static Connection getConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (Exception e) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    void createConnection() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn = DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Cannot load database", "Database Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    void setupBookTable() {
        String TABLE_NAME = "BOOK";
        try {
            stmt = conn.createStatement();
            
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "     id varchar(200) primary key,\n"
                        + "     title varchar(200),\n"
                        + "     author varchar(200),\n"
                        + "     publisher varchar(100),\n"
                        + "     isAvail boolean default true"
                        + " )");      
            }
        } catch (Exception e) {
        } finally {
        }
    }
    void setupMemberTable() {
        String TABLE_NAME = "MEMBER";
        try {
            stmt = conn.createStatement();
            
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "     id varchar(200) primary key,\n"
                        + "     name varchar(200),\n"
                        + "     username varchar(200),\n"
                        + "     password varchar(200),\n"
                        + "     phone varchar(20),\n"
                        + "     email varchar(100),\n"
                        + "     isAdmin boolean default false\n"
                        + " )");      
            }
        } catch (Exception e) {
        } finally {
        }
    }
    void setupIssueTable() {
        String TABLE_NAME = "ISSUE";
        try {
            stmt = conn.createStatement();
            
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            
            if (tables.next()) {
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go!");
            } else {
                stmt.execute("CREATE TABLE " + TABLE_NAME + "("
                        + "     bookID varchar(200) primary key,\n"
                        + "     memberID varchar(200),\n"
                        + "     issueTime timestamp default CURRENT_TIMESTAMP,\n"
                        + "     renew_count integer default 0,\n"
                        + "     FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                        + "     FOREIGN KEY (memberID) REFERENCES MEMBER(id)\n"
                        + " )");      
            }
        } catch (Exception e) {
        } finally {
        }
    }
    public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        } catch (Exception ex) {
            return null;
        } finally {
        }
        return result;
    }
    
    public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.execute(qu);
            return true;
        } catch (Exception ex) {
            return false;
        } finally {
        }
    }
    
    public boolean deleteBook(Book book) {
        try {
            String deleteStatement = "DELETE FROM BOOK WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, book.getId());
            int res = stmt.executeUpdate();
            
        } catch (Exception ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean isBookAlreadyIssued(Book book) {
         try {
            String checkStatement = "SELECT COUNT(*) FROM ISSUE WHERE bookid = ?";
            PreparedStatement stmt = conn.prepareStatement(checkStatement);
            stmt.setString(1, book.getId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count>0);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateBook(Book book) {
        try {
            String update = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PUBLISHER = ? WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getPublisher());
            stmt.setString(4, book.getId());
            int res = stmt.executeUpdate();
            return (res>0);
        } catch (Exception ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean ifMemberHasAnyBooks(MemberListController.Member member) {
        try {
            String checkStatement = "SELECT COUNT(*) FROM ISSUE WHERE memberid = ?";
            PreparedStatement stmt = conn.prepareStatement(checkStatement);
            stmt.setString(1, member.getId());
            ResultSet rs = stmt.executeQuery();
            if(rs.next()) {
                int count = rs.getInt(1);
                System.out.println(count);
                return (count>0);
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteMember(MemberListController.Member member) {
         try {
            String deleteStatement = "DELETE FROM MEMBER WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(deleteStatement);
            stmt.setString(1, member.getId());
            int res = stmt.executeUpdate();
            if(res == 1) {
                return true;
            }
        } catch (Exception ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateMember(MemberListController.Member member) {
        try {
            String update = "UPDATE MEMBER SET NAME = ?, EMAIL = ?, PHONE = ? WHERE ID = ?";
            PreparedStatement stmt = conn.prepareStatement(update);
            stmt.setString(1, member.getName());
            stmt.setString(2, member.getEmail());
            stmt.setString(3, member.getPhone());
            stmt.setString(4, member.getId());
            int res = stmt.executeUpdate();
            return (res>0);
        } catch (Exception ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
