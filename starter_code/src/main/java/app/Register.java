package app;

import java.util.ArrayList;
import java.util.List;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Temporary HTML as an example page.
 * 
 * Based on the Project Workshop code examples.
 * This page currently:
 *  - Provides a link back to the index page
 *  - Displays the list of movies from the Movies Database using the JDBCConnection
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2021. email: halil.ali@rmit.edu.au
 */
public class Register implements Handler {

   // URL of this page relative to http://localhost:7000/
   public static final String URL = "/Register.html";

   @Override
   public void handle(Context context) throws Exception {
      String html = "<html>\n";

      html = html + "<head>" + "<title>Facebook: Register</title>\n";
      html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";
      html = html + "</head>\n";
      
      html = html + "<body>\n";
      html = html + "<h1>Register to Facebook</h1>\n";
      html = html + "<p>Return to Homepage: \n";
      html = html + "<a href='/'>Link to Homepage</a>\n";
      html = html + "</p>\n";

      html = html + "<form action='/Register.html' method='post'>\n";
      html = html + "   <div class='form-group'>\n";
      html = html + "<p>Please fill in this form to create an account.</p>\n";
      html = html + "<label for='email'><b>Email:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='text' placeholder='Enter Email' name='email' id='email' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='screenname'><b>Screen Name:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='text' placeholder='Enter Screen Name' name='screenname' id='screenname' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='firstname'><b>First Name:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='text' placeholder='Enter First Name' name='firstname' id='firstname' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='lastname'><b>Last Name:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='text' placeholder='Enter Last Name' name='lastname' id='lastname' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='status'><b>Profile Status:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='text' placeholder='Enter Profile Status' name='status' id='status' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='dob'><b>Date of Birth:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='date' placeholder='Date of Birth' name='dob' id='dob' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='gender'><b>Gender:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='text' placeholder='Male/Female/Other' name='gender' id='gender' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='psw'><b>Password:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='password' placeholder='Enter Password' name='psw' id='psw' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='psw-repeat'><b>Repeat Password:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='password' placeholder='Repeat Password' name='psw-repeat' id='psw-repeat' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='visibility'><b>Visibility Level</b></label>\n";
      html = html + "<br>\n";
      html = html + "<select name='visibility' id='visibility'>\n";
      html = html + "<option value='0'>Everyone</option>\n";
      html = html + "<option value='1'>Friends-only</option>\n";
      html = html + "<option value='2'>Private</option>\n";
      html = html + "</select>\n";
      html = html + "<br>\n";
      html = html + "<label for='location'><b>Location:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='text' placeholder='City/Country' name='location' id='location' required>\n";
      html = html + "<br>\n";
      html = html + "<button type='submit' class='btn btn-primary'>Submit</button>\n";
      html = html + "</form>\n";

      html = html + "<hr>\n";
      html = html + "</div>\n";
      html = html + "</body>" + "</html>\n";

      if (context.method().equals("POST")) {
        if (confirmRegister(context.formParam("email"), context.formParam("screenname"), context.formParam("firstname"), context.formParam("lastname"), context.formParam("dob"), context.formParam("gender"), context.formParam("psw"), Integer.parseInt(context.formParam("visibility")), context.formParam("location"), context.formParam("status"))) {
            context.sessionAttribute("user_email", context.formParam("email"));
            context.redirect("/main");
        } else {
            html += "Registration failed\n";
        }
      }

      context.html(html);
   }

   public boolean notEmpty(ArrayList a) {
      return !a.isEmpty();
   }

   public boolean confirmRegister(String email, String screenname, String firstname, String lastname, String dob, String gender, String password, int visibility, String location, String status) {

      JDBCConnection jdbc = JDBCConnection.getConnection();
      String fullname = "" + firstname + " " + lastname + "";
      return jdbc.RegisterUser(email, fullname, screenname, dob, gender, password, visibility, location, status);
  }

}
