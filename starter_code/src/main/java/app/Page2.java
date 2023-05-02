package app;

import java.util.ArrayList;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 * @author Halil Ali, 2021. email: halil.ali@rmit.edu.au
 */
public class Page2 implements Handler {

    // URL of this page relative to http://localhost:7000/
    public static final String URL = "/page2.html";

    @Override
    public void handle(Context context) throws Exception {
        // Create a simple HTML webpage in a String
        String html = "<html>\n";

        // Add some Header information
        html = html + "<head>" + 
               "<title>Page 2: Movies by Type (simple HTML)</title>\n";

        // Add some CSS (external file)
        html = html + "<link rel='stylesheet' type='text/css' href='common.css' />\n";

        // Add the body
        html = html + "<body>\n";

        // Add HTML for the movies list
        html = html + "<h1>Page 2: Movies by Type (simple HTML)</h1>\n";

        /* Add HTML for the web form
         * We are giving two ways here
         *  - one for a text box
         *  - one for a drop down
         * 
         * Whitespace is used to help us understand the HTML!
         * 
         * IMPORTANT! the action speicifes the URL for POST!
         */
        html = html + "<form action='/page2.html' method='post'>\n";
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='movietype_drop'>Select the type Movie Type (Dropdown):</label>\n";
        html = html + "      <select id='movietype_drop' name='movietype_drop'>\n";
        html = html + "         <option>HORROR</option>\n";
        html = html + "         <option>DRAMA</option>\n";
        html = html + "         <option>COMEDY</option>\n";
        html = html + "      </select>\n";
        html = html + "   </div>\n";
        html = html + "   <div class='form-group'>\n";
        html = html + "      <label for='movietype_textbox'>Select the type Movie Type (Textbox)</label>\n";
        html = html + "      <input class='form-control' id='movietype_textbox' name='movietype_textbox'>\n";
        html = html + "   </div>\n";
        html = html + "   <button type='submit' class='btn btn-primary'>Submit</button>\n";
        html = html + "</form>\n";

        /* Get the Form Data
         *  from the drop down list
         * Need to be Careful!!
         *  If the form is not filled in, then the form will return null!
        */
        String movietype_drop = context.formParam("movietype_drop");
        if (movietype_drop == null) {
            // If NULL, nothing to show, therefore we make some "no results" HTML
            html = html + "<h2><i>No Results to show for dropbox</i></h2>";
        } else {
            // If NOT NULL, then lookup the movie by type!
            html = html + outputMovies(movietype_drop);
        }

        String movietype_textbox = context.formParam("movietype_textbox");
        if (movietype_textbox == null || movietype_textbox == "") {
            // If NULL, nothing to show, therefore we make some "no results" HTML
            html = html + "<h2><i>No Results to show for textbox</i></h2>\n";
        } else {
            // If NOT NULL, then lookup the movie by type!
            html = html + outputMovies(movietype_textbox);
        }

        // Add HTML for link back to the homepage
        html = html + "<p>Return to Homepage: \n";
        html = html + "<a href='/'>Link to Homepage</a>\n";
        html = html + "</p>\n";

        // Finish the HTML webpage
        html = html + "</body>" + "</html>\n";

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage
        context.html(html);
    }

    public String outputMovies(String type) {
        String html = "";
        html = html + "<h2>" + type + " Movies</h2>";

        // Look up movies from JDBC
        JDBCConnection jdbc = JDBCConnection.getConnection();
        ArrayList<String> movies = jdbc.getMoviesByType(type);
        
        // Add HTML for the movies list
        html = html + "<ul>";
        for (String movie : movies) {
            html = html + "<li>" + movie + "</li>";
        }
        html = html + "</ul>";

        return html;
    }

}