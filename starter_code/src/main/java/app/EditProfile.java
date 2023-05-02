package app;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class EditProfile implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/EditProfile.html";
 
    @Override
    public void handle(Context context) throws Exception {
       String loginemail = context.sessionAttribute("user_email");
       String update = "";
       if (context.method().equals("POST")) {
         update = updateProfile(loginemail, context.formParam("screenname"), Integer.parseInt(context.formParam("visibility")), context.formParam("location"), context.formParam("status"));
       }
       ArrayList<String> dataresults = getDataForForm(loginemail);

       String html = "<html>\n";
       html = html + "<head>" + "<title>Facebook: Edit Profile</title>\n";
       html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";
       html = html + "<body>\n";

       html = html + "<h1>Edit Profile</h1>\n";
       html = html + "<p>Return to Homepage: \n";
       html = html + "<a href='/main'>Link to Homepage</a>\n";
       html = html + "</p>\n";

       html = html + "<form action='/EditProfile.html' method='post'>\n";
       html = html + "   <div class='form-group'>\n";
       html = html + "<p>Please make your changes as needed</p>\n";
       html = html + "<br>\n";
       html = html + "<label for='screenname'><b>Screen Name:</b></label>\n";
       html = html + "<br>\n";
       html = html + "<input type='text' value='"+ dataresults.get(0) +"' name='screenname' id='screenname' required>\n";
       html = html + "<br>\n";
       html = html + "<label for='status'><b>Status:</b></label>\n";
       html = html + "<br>\n";
       html = html + "<input type='text' value='"+ dataresults.get(1) +"' name='status' id='status' required>\n";
       html = html + "<br>\n";
       html = html + "<label for='visibility'><b>Visibility Level</b></label>\n";
       html = html + "<br>\n";
       html = html + "<select name='visibility' id='visibility'>\n";
       html = html + "<option value='' selected='selected' hidden='hidden'>Choose here</option>";
       html = html + "<option value='0'>Everyone</option>\n";
       html = html + "<option value='1'>Friends-only</option>\n";
       html = html + "<option value='2'>Private</option>\n";
       html = html + "</select>\n";
       html = html + "<br>\n";
       html = html + "<label for='location'><b>Location:</b></label>\n";
       html = html + "<br>\n";
       html = html + "<input type='text' value='"+ dataresults.get(2) +"' name='location' id='location' required>\n";
       html = html + "<br>\n";
       html = html + "<button type='submit' class='btn btn-primary'>Submit</button>\n";
       html = html + "</form>\n";
       html = html + "<hr>\n";
       html = html + "</div>\n";
 
       html += update;
 
       html = html + "</body>" + "</html>\n";
 
       
 
       // DO NOT MODIFY THIS
       // Makes Javalin render the webpage
 
       context.html(html);
    }
 
    public boolean notEmpty(ArrayList a) {
       return !a.isEmpty();
    }
 
    public String updateProfile(String Email, String ScreenName, int Visibility, String Location, String Status) {
       String html = "";
       html = html + "Profile updated!";

       JDBCConnection jdbc = JDBCConnection.getConnection();
       jdbc.UpdateUser(Email, ScreenName, Visibility, Location, Status);
 
       return html;
   }

    public ArrayList<String> getDataForForm(String email) {

        JDBCConnection jdbc = JDBCConnection.getConnection();

        ArrayList<String> results = jdbc.GetDataEdit(email);

        return results;
    }
 
 }