package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

public class RouterVerticle extends AbstractVerticle {

    Router router;

    @Override
    public void start(Promise<Void> startPromise){
        router = Router.router(vertx);

        router.route("/").handler(req->{
            req.response()
                .putHeader("context-type","application/json")
                .end(new JsonObject().put("Hello","Router").toString());
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
