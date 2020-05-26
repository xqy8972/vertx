package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class UrlParamsVerticle extends AbstractVerticle {

      Router router;

      @Override
      public void start(Promise<Void> startPromise){

          router = Router.router(vertx);
          //1.经典模式
          router.route("/test").handler(req->{
              String page = req.request().getParam("page");
              System.out.println(page);
              req.response()
                  .putHeader("content-type","application/json")
                  .end(new JsonObject().put("Hello","World").toString());
          });

          //2.rest模式
          router.route("/test/:page").handler(req->{
              String page = req.request().getParam("page");
              System.out.println(page);
              req.response()
                  .putHeader("content-type","application/json")
                  .end(new JsonObject().put("Hello","World").toString());
          });

          vertx.createHttpServer().requestHandler(router).listen(8080,http->{
              if (http.succeeded()){
                  startPromise.complete();
                  System.out.println("HTTP server started on port 8080");
              }else{
                  http.failed();
              }

          });

      }
}
