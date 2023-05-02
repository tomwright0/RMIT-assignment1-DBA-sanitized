package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class Delete implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/Delete.html";
 
    @Override
    public void handle(Context context) throws Exception {
       // Create a simple HTML webpage in a String
       String loginemail = context.sessionAttribute("user_email");

       String html = "<html>\n";
       html = html + "<head>" + "<title>Facebook: Delete Account</title>\n";
       html = html + "<link rel='stylesheet' type='text/css' href='common.css'/></head>\n";

       html = html + "<body>\n";
       html = html + "<h1>Delete Account</h1>\n";
       html = html + "<p>Return to Homepage: \n";
       html = html + "<a href='/main'>Link to Homepage</a>\n";
       html = html + "</p>\n";

       html = html + "<form action='/Delete.html' method='post'>\n";
       html = html + "   <div class='form-group'>\n";
       html = html + "<p>Are you sure you wish to delete your account?</p>\n";
       html = html + "<br>\n";
       html = html + "<button type='submit' class='btn btn-primary'>DELETE</button>\n";
       html = html + "</form>\n";

       html = html + "<hr>\n";
       html = html + "</div>\n";
       html = html + "</body>" + "</html>\n";
 
       if (context.method().equals("POST")) {
         html = html + deleteProfile(loginemail);
       }
 
       // DO NOT MODIFY THIS
       // Makes Javalin render the webpage
 
       context.html(html);
    }
 
    public boolean notEmpty(ArrayList a) {
       return !a.isEmpty();
    }
 
    public String deleteProfile(String Email) {
       String html = "";
       html = html + "Profile deleted!";

       JDBCConnection jdbc = JDBCConnection.getConnection();
       jdbc.DeleteUser(Email);
 
       return html;
   }
 
 }
