package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin by writing the raw HTML into a Java
 * String object
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2021. email: halil.ali@rmit.edu.au
 */
public class Page3 implements Handler {

   // URL of this page relative to http://localhost:7000/
   public static final String URL = "/page3.html";

   // Name of the Thymeleaf HTML template page in the resources folder
   private static final String TEMPLATE = ("page3.html");

   @Override
   public void handle(Context context) throws Exception {
      // The model of data to provide to Thymeleaf.
      // In this example the model will be filled with:
      // - Title to give to the h1 tag
      // - Array list of all movies for the UL element
      Map<String, Object> model = new HashMap<String, Object>();

      // Add in title for the h1 tag to the model
      model.put("title", new String("Page 3: Movies by Type (Thymeleaf)"));

      // Add into the model the list of types to give to the dropdown
      ArrayList<String> types = new ArrayList<String>();
      types.add("HORROR");
      types.add("DRAMA");
      types.add("COMEDY");
      model.put("types", types);

      // Look up from JDBC
      JDBCConnection jdbc = JDBCConnection.getConnection();

      /*
       * Get the Form Data from the drop down list Need to be Careful!! If the form is
       * not filled in, then the form will return null!
       */
      String movietype_drop = context.formParam("movietype_drop");
      if (movietype_drop == null) {
         // If NULL, nothing to show, therefore we make some "no results" HTML
         // Also store empty array list for completness
         model.put("title_drop", new String("No Results to show for dropbox"));
         ArrayList<String> movies = new ArrayList<String>();
         model.put("movies_drop", movies);
      } else {
         // If NOT NULL, then lookup the movie by type!
         model.put("title_drop", new String(movietype_drop + " Movies"));
         ArrayList<String> movies = jdbc.getMoviesByType(movietype_drop);
         model.put("movies_drop", movies);
      }

      String movietype_textbox = context.formParam("movietype_textbox");
      if (movietype_textbox == null || movietype_textbox == "") {
         // If NULL, nothing to show, therefore we make some "no results" HTML
         // Also store empty array list for completness
         model.put("title_text", new String("No Results to show for textbox"));
         ArrayList<String> movies = new ArrayList<String>();
         model.put("movies_text", movies);
      } else {
         // If NOT NULL, then lookup the movie by type!
         model.put("title_text", new String(movietype_textbox + " Movies"));
         ArrayList<String> movies = jdbc.getMoviesByType(movietype_textbox);
         model.put("movies_text", movies);
      }

      // DO NOT MODIFY THIS
      // Makes Javalin render the webpage using Thymeleaf
      context.render(TEMPLATE, model);
   }

}