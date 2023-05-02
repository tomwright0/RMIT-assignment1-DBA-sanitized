package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Partial to render a single post
 */
public class SinglePost implements Handler {

   @Override
   public void handle(Context context) throws Exception {
      Map<String, Object> model = new HashMap<String, Object>();

      String user = context.sessionAttribute("user_email");

      int postId = Integer.parseInt(context.pathParam("id"));
      JDBCConnection jdbc = JDBCConnection.getConnection();
      
      if (user == null) {
         context.status(401);
      } else {
         if (context.method().equals("POST")) {
            switch (context.queryParam("interaction")) {
               case "like":
                  System.out.println("like");
                  jdbc.likePost(user, postId);
                  break;
               case "unlike":
                  System.out.println("unlike");
                  jdbc.unlikePost(user, postId);
                  break;
               case "reply":
                  System.out.println("reply");
                  jdbc.createReply(user, postId, context.body());
                  break;
            }
         }
         Map<String, Object> post = jdbc.getPost(user, postId);
         if (post == null) {
            context.status(404);
         } else {
            model.put("p", post);

            context.render("SinglePost.html", model);
         }
      }
   }

}