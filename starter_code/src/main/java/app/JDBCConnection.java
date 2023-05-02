package app;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database. Allows SQL
 * queries to be used with the SQLLite Databse in Java.
 * 
 * This is an example JDBC Connection that has a single query for the Movies
 * Database This is similar to the project workshop JDBC examples.
 *
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Halil Ali, 2021. email halil.ali@rmit.edu.au
 */
public class JDBCConnection {

   // the default oracle account uses the the read only MOVIES database
   // once you create a set of tables in your own account, update this to your RMIT
   // Oracle account details
   private static final String DATABASE_USERNAME = "romovies";
   private static final String DATABASE_PASSWORD = "movies";

   private static final String DATABASE_URL = "********";
   private static JDBCConnection jdbc = null;
   private static Connection connection;

   /**
   * Singleton function to return single copy of this class to other classes
   **/
   public static JDBCConnection getConnection(){

      //check that ssh session is still open (if not reopen)
      SSHTunnel.getSession();

      //check that JDBCconnection is available (if not establish)
      if(jdbc==null){
         jdbc = new JDBCConnection();
      }
      return jdbc;
   }

   /**
   * Hidden constructor to establish Database connection (once)
   **/
   private JDBCConnection() {
      System.out.println("Created JDBC Connection Object");
      
      try {
         // Connect to JDBC data base
         connection = DriverManager.getConnection(DATABASE_URL, App.database_username, App.database_password);
      } catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }
   }

   /**
   * Closes the database connection - called only when server shutdown
   **/
   public static void closeConnection(){
      try {
         if (connection != null) {
            connection.close();
            System.out.println("Database Connection closed");
         }
      } catch (SQLException e) {
         // connection close failed.
         System.err.println(e.getMessage());
      }
   }

   /**
    * Get all of the Movies in the database
    */
   public ArrayList<String> getMovies() {
      ArrayList<String> movies = new ArrayList<String>();

      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // The Query
         String query = "SELECT *"   + "\n" +
                        "FROM movie" ;

         // Get Result
         ResultSet results = statement.executeQuery(query);

         // Process all of the results
         // The "results" variable is similar to an array
         // We can iterate through all of the database query results
         while (results.next()) {
            // We can lookup a column of the a single record in the
            // result using the column name
            // BUT, we must be careful of the column type!
            // int id = results.getInt("mvnumb");
            String movieName = results.getString("mvtitle");
            // int year = results.getInt("yrmde");
            // String type = results.getString("mvtype");

            // For now we will just store the movieName and ignore the id
            movies.add(movieName);
         }

         // Close the statement because we are done with it
         statement.close();
      } catch (SQLException e) {
         // If there is an error, lets just print the error
         System.err.println(e.getMessage());
      }

      // Finally we return all of the movies
      return movies;
   }

   /**
    * Get all the movies in the database by a given type. Note this takes a string
    * of the type as an argument! This has been implemented for you as an example.
    * HINT: you can use this to find all of the horror movies!
    */
   public ArrayList<String> getMoviesByType(String movieType) {
      ArrayList<String> movies = new ArrayList<String>();

      // Setup the variable for the JDBC connection
      //Connection connection = null;

      try {
         // Prepare a new SQL Query & Set a timeout
         Statement statement = connection.createStatement();
         statement.setQueryTimeout(30);

         // The Query
         String query = "SELECT *"                                        + "\n" +
                        "FROM movie"                                      + "\n" +
                        "WHERE LOWER(mvtype) = LOWER('" + movieType + "')";
         System.out.println(query);

         // Get Result
         ResultSet results = statement.executeQuery(query);

         // Process all of the results
         while (results.next()) {
            String movieName = results.getString("mvtitle");
            movies.add(movieName);
         }

         // Close the statement because we are done with it
         statement.close();
      } catch (SQLException e) {
         // If there is an error, lets just pring the error
         System.err.println(e.getMessage());
      }

      // Finally we return all of the movies
      return movies;
   }

   public Map<String, Object> getPost(String user, int postId) {
      Map<String, Object> res = new HashMap<String, Object>();
      ArrayList<Map<String, Object>> replies = new ArrayList<Map<String, Object>>();
      res.put("replies", replies);
      boolean found = false;
      
      try {
         PreparedStatement statement = connection.prepareStatement(
            "SELECT PostID, PostBody, ScreenName, ParentID, " +
            "(SELECT COUNT(*) FROM PostLike WHERE Post.PostID = PostLike.PostID) AS PostLikes, " +
            "(SELECT COUNT(*) FROM PostLike WHERE Post.PostID = PostLike.PostID AND AuthorID = ?) AS UserLike " +
            "FROM Post JOIN Memb ON AuthorID = Email " +
            "WHERE PostID = ? OR ParentID = ? " +
            "ORDER BY Timestamp ASC");
         statement.setQueryTimeout(30);
         statement.setString(1, user);
         statement.setInt(2, postId);
         statement.setInt(3, postId);
         ResultSet results = statement.executeQuery();
         while (results.next()) {
            Map<String, Object> post;
            if (results.getInt("PostID") == postId) {
               post = res;
               found = true;
            } else {
               post = new HashMap<String, Object>();
               replies.add(post);
            }
            post.put("postId", results.getInt("PostID"));
            post.put("postBody", results.getString("PostBody"));
            post.put("authorName", results.getString("ScreenName"));
            post.put("isTopLevel", results.getObject("ParentID") == null);
            post.put("postLikes", results.getInt("PostLikes"));
            post.put("userLikes", results.getInt("UserLike") == 1);
         }
         statement.close();
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
      
      if (found) {
         return res;
      } else {
         return null;
      }
   }

   public ArrayList<Map<String, Object>> getFeed(String user) {
      ArrayList<Map<String, Object>> feed = new ArrayList<>();
      Map<BigDecimal, ArrayList<Map<String, Object>>> replies = new HashMap<>();
      
      try {
         PreparedStatement statement = connection.prepareStatement(
            "SELECT PostID, PostBody, ScreenName, ParentID, " +
                "(SELECT COUNT(*) FROM PostLike WHERE Post.PostID = PostLike.PostID) AS PostLikes, " +
                "(SELECT COUNT(*) FROM PostLike WHERE Post.PostID = PostLike.PostID AND AuthorID = ?) AS UserLike " +
            "FROM Post JOIN Memb ON AuthorID = Email " +
            "WHERE (ParentID IS NULL AND (AuthorID = ? " +
                    "OR EXISTS(SELECT * FROM Friendship " +
                        "WHERE (MemberA = ? AND MemberB = AuthorID) " +
                        "OR (MemberA = AuthorID AND MemberB = ?)))) " +
                "OR (ParentID IN (SELECT PostID FROM Post WHERE ParentID IS NULL AND (AuthorID = ? " +
                    "OR EXISTS(SELECT * FROM Friendship " +
                        "WHERE (MemberA = ? AND MemberB = AuthorID) " +
                        "OR (MemberA = AuthorID AND MemberB = ?))))) " +
            "ORDER BY Timestamp ASC");
         statement.setQueryTimeout(30);
         for (int i = 1; i <= statement.getParameterMetaData().getParameterCount(); i++) {
            statement.setString(i, user);
         }
         ResultSet results = statement.executeQuery();
         while (results.next()) {
            Map<String, Object> post = new HashMap<>();
            BigDecimal parent = results.getBigDecimal("ParentID");
            if (parent == null) {
               feed.add(0, post);
               ArrayList<Map<String, Object>> postReplies = new ArrayList<>();
               post.put("replies", postReplies);
               replies.put(results.getBigDecimal("PostID"), postReplies);
            } else {
               replies.get(parent).add(post);
            }
            post.put("postId", results.getInt("PostID"));
            post.put("postBody", results.getString("PostBody"));
            post.put("authorName", results.getString("ScreenName"));
            post.put("isTopLevel", results.getObject("ParentID") == null);
            post.put("postLikes", results.getInt("PostLikes"));
            post.put("userLikes", results.getInt("UserLike") == 1);
         }
         return feed;
      } catch (SQLException e) {
         System.err.println(e.getMessage());
         return null;
      }
   }

   public void likePost(String user, int postId) {
      try {
         PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO PostLike (PostID, MemberID) VALUES (?, ?)");
         statement.setQueryTimeout(30);
         statement.setInt(1, postId);
         statement.setString(2, user);
         statement.executeQuery();
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
   }
   
   public void unlikePost(String user, int postId) {
      try {
         PreparedStatement statement = connection.prepareStatement(
            "DELETE FROM PostLike WHERE PostID = ? AND MemberID = ?");
         statement.setQueryTimeout(30);
         statement.setInt(1, postId);
         statement.setString(2, user);
         statement.executeQuery();
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
   }
   
   public void createPost(String user, String body) {
      try {
         PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO Post (AuthorID, PostBody, Timestamp) " +
            "VALUES (?, ?, SYSDATE)");
         statement.setQueryTimeout(30);
         statement.setString(1, user);
         statement.setString(2, body);
         statement.executeQuery();
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
   }
   
   public void createReply(String user, int parentId, String body) {
      try {
         PreparedStatement statement = connection.prepareStatement(
            "INSERT INTO Post (AuthorID, PostBody, ParentID, Timestamp) " +
            "VALUES (?, ?, ?, SYSDATE)");
         statement.setQueryTimeout(30);
         statement.setString(1, user);
         statement.setString(2, body);
         statement.setInt(3, parentId);
         statement.executeQuery();
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
   }

   private String hashPassword(String password) {
      String hash = null;
      try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            hash = Base64.getEncoder().encodeToString(md.digest());
      } catch (NoSuchAlgorithmException ex) {}
      return hash;
   }

   public boolean RegisterUser(String Email, String FullName, String ScreenName, String DateOfBirth, String Gender, String Password, int Visibility, String Location, String Status) {
      try {
         // The Query
         String query = "INSERT INTO Memb (Email, FullName, ScreenName, DateOfBirth, Gender, Password, Visibility, Loc, Status)"  + "\n" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

         PreparedStatement statement = connection.prepareStatement(query);
         statement.setQueryTimeout(30);

         statement.setString(1, Email);
         statement.setString(2, FullName);
         statement.setString(3, ScreenName);
         statement.setDate(4, Date.valueOf(DateOfBirth));
         statement.setString(5, Gender);
         statement.setString(6, hashPassword(Password));
         statement.setInt(7, Visibility);
         statement.setString(8, Location);
         statement.setString(9, Status);

         statement.executeQuery();

         statement.close();
         return true;
      } catch (SQLException e) {
         System.err.println(e.getMessage());
         return false;
      }
   }
   
   public ArrayList<String> LoginUser(String Email, String Password) {
      ArrayList<String> login = new ArrayList<String>();

      try {
         String query = "SELECT * FROM Memb where email = ? and password = ?";

         PreparedStatement statement = connection.prepareStatement(query);

         statement.setQueryTimeout(30);
         
         statement.setString(1, Email);
         statement.setString(2, hashPassword(Password));

         ResultSet results = statement.executeQuery();

         while (results.next()) {
            login.add(results.getString("screenname"));
            login.add(results.getString("email"));
         }

         statement.close();
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
      return login;
   }

   public ArrayList<String> GetDataEdit(String Email) {
      ArrayList<String> data = new ArrayList<String>();

      try {

         String query = "SELECT * FROM Memb where email = ?";
         
         PreparedStatement statement = connection.prepareStatement(query);

         statement.setQueryTimeout(30);
         
         statement.setString(1, Email);

         ResultSet results = statement.executeQuery();

         while (results.next()) {
            data.add(results.getString("screenname"));
            data.add(results.getString("status"));
            data.add(results.getString("loc"));
            data.add(String.valueOf(results.getInt("visibility")));
         }

         statement.close();
         
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }

      return data;
   }

   public void UpdateUser(String Email, String ScreenName, int Visibility, String Location, String Status) {
      try {
         String query = "UPDATE Memb"  + "\n" +
                        "SET ScreenName = ?, Status = ?, Loc = ?, Visibility = ?" + "\n" +
                        "WHERE Email = ?";
         System.out.println(query);

         PreparedStatement statement = connection.prepareStatement(query);

         statement.setQueryTimeout(30);

         statement.setString(1, ScreenName);
         statement.setString(2, Status);
         statement.setString(3, Location);
         statement.setInt(4, Visibility);
         statement.setString(5, Email);

         statement.executeQuery();
         
         statement.close();
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
   }

   public void DeleteUser(String Email) {
      try {
         // I've tried to search for multiple queries without statements but it seems to be weird when deleting or inserting.

         String query1 = "DELETE from POSTLIKE where MEMBERID = ?";
         String query2 = "DELETE from POSTLIKE where POSTID = " + 
         "(SELECT PL.POSTID from POSTLIKE PL, POST P " +
         "WHERE PL.POSTID = P.POSTID and P.AUTHORID = ?)";
         String query3 = "DELETE FROM POST WHERE AUTHORID = ?";
         String query4 = "DELETE FROM FRIENDSHIP WHERE MEMBERA = ? or MEMBERB = ?";
         String query5 = "DELETE FROM FRIENDREQUEST WHERE REQUESTOR = ? or REQUESTEE = ?";
         String query6 = "DELETE FROM MEMB WHERE EMAIL = ?";

         PreparedStatement s1 = connection.prepareStatement(query1);
         PreparedStatement s2 = connection.prepareStatement(query2);
         PreparedStatement s3 = connection.prepareStatement(query3);
         PreparedStatement s4 = connection.prepareStatement(query4);
         PreparedStatement s5 = connection.prepareStatement(query5);
         PreparedStatement s6 = connection.prepareStatement(query6);

         s1.setQueryTimeout(30);
         s2.setQueryTimeout(30);
         s3.setQueryTimeout(30);
         s4.setQueryTimeout(30);
         s5.setQueryTimeout(30);
         s6.setQueryTimeout(30);

         s1.setString(1, Email);
         s2.setString(1, Email);
         s3.setString(1, Email);
         s4.setString(1, Email);
         s4.setString(2, Email);
         s5.setString(1, Email);
         s5.setString(2, Email);
         s6.setString(1, Email);

         s1.executeQuery();
         s2.executeQuery();
         s3.executeQuery();
         s4.executeQuery();
         s5.executeQuery();
         s6.executeQuery();

         s1.close();
         s2.close();
         s3.close();
         s4.close();
         s5.close();
         s6.close();
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
   }

   public ArrayList<String> getFriendRequests(String useremail) {
      ArrayList<String> searchresults = new ArrayList<String>();

      try {
         String query = "SELECT SCREENNAME, EMAIL, LOC, GENDER " +
                        "FROM FriendRequest JOIN Memb ON Requestor = Email " +
                        "WHERE Requestee = ? " +
                        "ORDER BY StartDate DESC";

         PreparedStatement stmt = connection.prepareStatement(query);
         stmt.setQueryTimeout(30);
         stmt.setString(1, useremail);
         ResultSet results = stmt.executeQuery();
         while (results.next()) {
            searchresults.add(results.getString("screenname"));
            searchresults.add(results.getString("email"));
            searchresults.add(results.getString("loc"));
            searchresults.add(results.getString("gender"));
         }



      } catch (SQLException e) {
         System.err.println(e.getMessage());         
      }
      return searchresults;
   }

   public ArrayList<String> SearchForUsers(String useremail, String input) {
      ArrayList<String> searchresults = new ArrayList<String>();

      try {
         String query = "SELECT SCREENNAME, EMAIL, LOC, GENDER \n" +
                        "FROM MEMB\n" +
                        "WHERE UPPER(SCREENNAME) LIKE UPPER(?) AND EMAIL != ?";

         PreparedStatement stmt = connection.prepareStatement(query);
         stmt.setQueryTimeout(30);
         stmt.setString(1,"%"+input+"%");
         stmt.setString(2, useremail);
         ResultSet results = stmt.executeQuery();
         while (results.next()) {
            searchresults.add(results.getString("screenname"));
            searchresults.add(results.getString("email"));
            searchresults.add(results.getString("loc"));
            searchresults.add(results.getString("gender"));
         }



      } catch (SQLException e) {
         System.err.println(e.getMessage());         
      }
      return searchresults;
   }

   public void SendFriendRequest(String Useremail, String Requesteemail) {
      try {
         String qcheck = "SELECT COUNT(*) ReverseExists FROM FriendRequest WHERE Requestor = ? AND Requestee = ?";
         
         PreparedStatement s = connection.prepareStatement(qcheck);
         s.setQueryTimeout(30);
         s.setString(1, Requesteemail);
         s.setString(2, Useremail);
         ResultSet results = s.executeQuery();
         if (!results.next()) {
            return;
         }
         if (results.getInt("ReverseExists") == 1) {
            String qerase = "DELETE FROM FriendRequest WHERE (Requestor = ? AND Requestee = ?) " +
                            "OR (Requestor = ? AND Requestee = ?)";
            s = connection.prepareStatement(qerase);
            s.setQueryTimeout(30);
            s.setString(1, Requesteemail);
            s.setString(2, Useremail);
            s.setString(3, Useremail);
            s.setString(4, Requesteemail);
            s.executeQuery();
            
            String qinsert = "INSERT INTO Friendship (MemberA, MemberB, StartDate) " +
                              "VALUES (?, ?, SYSDATE)";
            s = connection.prepareStatement(qinsert);
            s.setQueryTimeout(30);
            s.setString(1, Useremail);
            s.setString(2, Requesteemail);
            s.executeQuery();
         } else {
         
            // I've tried to search for multiple queries without statements but it seems to be weird when deleting or inserting.

            String query1 = "INSERT INTO FRIENDREQUEST (REQUESTOR, REQUESTEE, STARTDATE)\n" +
                            "VALUES (?, ?, ?)";

            PreparedStatement s1 = connection.prepareStatement(query1);

            s1.setQueryTimeout(30);

            s1.setString(1, Useremail);
            s1.setString(2, Requesteemail);
            s1.setDate(3, new Date(System.currentTimeMillis()));
            
            s1.executeQuery();


            s1.close();
         }
      } catch (SQLException e) {
         System.err.println(e.getMessage());
      }
   }
}
