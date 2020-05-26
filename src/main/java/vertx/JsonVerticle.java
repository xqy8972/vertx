package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

public class JsonVerticle extends AbstractVerticle {

    public void start(Promise<Void> startPromise){

        vertx.createHttpServer().requestHandler(req->{
            req.response()
                .putHeader("content-type","application/json")
                .end(new JsonObject().put("Hello,","Vert.x").toString());
        }).listen(8080, http->{
            if(http.succeeded()){
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            }else {
                startPromise.fail(http.cause());
            }
        });

    }

}
