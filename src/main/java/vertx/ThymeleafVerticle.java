package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

public class ThymeleafVerticle extends AbstractVerticle {

    Router router;

    ThymeleafTemplateEngine templateEngine;



    @Override
    public void start(Promise<Void> startPromise){
        router = Router.router(vertx);

        templateEngine = ThymeleafTemplateEngine.create(vertx);

        JsonObject obj = new JsonObject();
        obj.put("name","Hello Vertx");

        router.route("/").handler(req ->{
            templateEngine.render(obj, "templates/index.html"
                ,bufferAsyncResult -> {
                    if (bufferAsyncResult.succeeded()){
                        req.response()
                            .putHeader("content-type", "text/html")
                            .end(bufferAsyncResult.result());
                    }else{
                        bufferAsyncResult.failed();
                    }
            });

        });

        vertx.createHttpServer().requestHandler(router).listen(8080, http->{
            if(http.succeeded()){
                startPromise.complete();
                System.out.println("HTTP server started on port 8888");
            }else {
                http.failed();
            }
        });
    }
}
