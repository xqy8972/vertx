package vertx;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.thymeleaf.ThymeleafTemplateEngine;

public class StaticFileVerticle extends AbstractVerticle {

    Router router;

    ThymeleafTemplateEngine templateEngine;

    final InternalLogger logger = Log4JLoggerFactory.getInstance(StaticFileVerticle.class);

    @Override
    public void start(Promise<Void> startPromise){
        router = Router.router(vertx);

        templateEngine = ThymeleafTemplateEngine.create(vertx);

        //整合静态文件
        //router.route("/static/*").handler(StaticHandler.create());

        //整合静态文件，自定义路径
        router.route("/*").handler(StaticHandler.create());

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
