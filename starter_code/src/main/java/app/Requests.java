package app;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.client.api.Request;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class Requests implements Handler {
    JDBCConnection jdbc = JDBCConnection.getConnection(); 
    public static final String URL = "/Requests.html";

    @Override
    public void handle(Context context) throws Exception {
       String loginemail = context.sessionAttribute("user_email");

       String html = "<html>\n";
       html = html + "<head>" + "<title>Facebook: Friend Requests</title>\n";
       html = html + "<link rel='stylesheet' type='text/css' href='common.css'>\n";
       html = html + "</head>\n";
       html = html + "<body>\n";
       
       if (context.method().equals("POST") && context.formParam("requestee") != null) {
              html += SendRequest(loginemail, context.formParam("requestee"));
       }
       
       ArrayList<String> pending = jdbc.getFriendRequests(loginemail);
         for (int i = 0; i < pending.size(); i = i + 4) {
             html = html + "<p> Screename: " + pending.get(i) + "</p>\n";
             html = html + "<p> Location: " + pending.get(i+2) + "</p>\n";
             html = html + "<p> Gender: " + pending.get(i+3) + "</p>\n";
             html = html + "<form action='/Requests.html' method= 'post'> \n";
             html = html + "<input type='hidden' id='requestee' name='requestee' value='"+ pending.get(i+1) + "'>\n";
             html = html + "<button type='submit'>Accept Request</button>\n";
             html = html + "</form>\n";

         }
       
       html = html + "<form action='/Requests.html' method='post'>\n";
       html = html + "<div class='form-group'>\n";
       html = html + "<label for='screenname'><b>Enter your friends screen name here.</b></label>\n";
       html = html + "<input type='text' placeholder='Screen name' name='screenname' id='screenname' required>\n";
       html = html + "<br>\n";
       html = html + "<button type='submit'>Submit</button>\n";
       html = html + "</form>\n";
       html = html + "</div>\n";
       html = html + "</body>" + "</html>\n";
       System.out.println(context.formParam("screenname"));
       
     if (context.method().equals("POST") && context.formParam("screenname") != null) {
         ArrayList<String> searchresults = jdbc.SearchForUsers(loginemail, context.formParam("screenname"));

         for (int i = 0; i < searchresults.size(); i = i + 4) {
             html = html + "<p> Screename: " + searchresults.get(i) + "</p>\n";
             html = html + "<p> Location: " + searchresults.get(i+2) + "</p>\n";
             html = html + "<p> Gender: " + searchresults.get(i+3) + "</p>\n";
             html = html + "<form action='/Requests.html' method= 'post'> \n";
             html = html + "<input type='hidden' id='requestee' name='requestee' value='"+ searchresults.get(i+1) + "'>\n";
             html = html + "<button type='submit'>Send Request</button>\n";
             html = html + "</form>\n";
             System.out.println(context.formParam("requestee"));

         }
     }

       
       

 
       // DO NOT MODIFY THIS
       // Makes Javalin render the webpage
 
       context.html(html);
    }
 
    public boolean notEmpty(ArrayList a) {
       return !a.isEmpty();
    }
 
    public String SendRequest(String useremail, String requesteeemail) {
       String html = "";
       html = html + "Done!";

       JDBCConnection jdbc = JDBCConnection.getConnection();
       jdbc.SendFriendRequest(useremail, requesteeemail);
 
       return html;
   }
    
}