package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Main page for logged in users
 */
public class MainPage implements Handler {

   @Override
   public void handle(Context context) throws Exception {
      Map<String, Object> model = new HashMap<String, Object>();

      JDBCConnection jdbc = JDBCConnection.getConnection();
      
      String user = context.sessionAttribute("user_email");
      if (user == null) {
         context.status(401);
      } else {
         if (context.method().equals("POST")) {
            jdbc.createPost(user, context.formParam("body"));
         }
         
         ArrayList<String> userData = jdbc.GetDataEdit(user);
         ArrayList<Map<String, Object>> posts = jdbc.getFeed(user);

         model.put("screenName", userData.get(0));
         model.put("posts", posts);

         context.render("MainPage.html", model);
      }
   }

}