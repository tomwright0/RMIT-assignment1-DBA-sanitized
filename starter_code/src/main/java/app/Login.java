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
public class Login implements Handler {

   // URL of this page relative to http://localhost:7000/
   public static final String URL = "/Login.html";

   @Override
   public void handle(Context context) throws Exception {
      String html = "<html>\n";

      html = html + "<head>" + "<title>Facebook: Login</title>\n";

      html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";

      html = html + "<body>\n";
      html = html + "<h1>Login to Facebook</h1>\n";
      html = html + "<p>Return to Homepage: \n";
      html = html + "<a href='/'>Link to Homepage</a>\n";
      html = html + "</p>\n";

      html = html + "<form action='/Login.html' method='post'>\n";
      html = html + "   <div class='form-group'>\n";
      html = html + "<p>Please enter your details to login.</p>\n";
      html = html + "<label for='email'><b>Email:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='text' placeholder='Enter Email' name='email' id='email' required>\n";
      html = html + "<br>\n";
      html = html + "<label for='psw'><b>Password:</b></label>\n";
      html = html + "<br>\n";
      html = html + "<input type='password' placeholder='Enter Password' name='psw' id='psw' required>\n";
      html = html + "<br>\n";
      html = html + "<button type='submit' class='btn btn-primary'>Submit</button>\n";
      html = html + "</form>\n";
      html = html + "<hr>\n";
      html = html + "</div>\n";

      html = html + "</body>" + "</html>\n";

      if (context.method().equals("POST")) {
         context.sessionAttribute("user_email", context.formParam("email"));
         html = html + confirmLogin(context, context.formParam("email"), context.formParam("psw"));

      }


      context.html(html);
   }

   public boolean notEmpty(ArrayList a) {
      return !a.isEmpty();
   }


   public String confirmLogin(Context context, String email, String password) {
      String html = "";

      JDBCConnection jdbc = JDBCConnection.getConnection();
      ArrayList<String> screensession = jdbc.LoginUser(email, password);
      
      if (notEmpty(screensession)) {
         html = html + "<head>\n";
         html = html + "</head>\n";
         html = html + "<body>\n";
         html = html + "<p> Login confirmed! </p>\n";
         html = html + "<p> Hello " + screensession.get(0) + "!</p>\n";
         html = html + "</body>\n";
         context.redirect("/main");
      } else {
         html = html + "<head>\n";
         html = html + "</head>\n";
         html = html + "<body>\n";
         html = html + "<p> Login failed! </p>\n";
         html = html + "</body>\n";
      }
   
      return html;
   }

}
