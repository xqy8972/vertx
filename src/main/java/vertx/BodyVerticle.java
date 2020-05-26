package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLPool;

public class BodyVerticle extends AbstractVerticle{

    Router router;


    @Override
    public void start(Promise<Void> startPromise){

        router = Router.router(vertx);

        //开启获取Body参数
        router.route().handler(BodyHandler.create());

        router.route("/test/form").handler(req->{
            //获取form-data格式参数
            String page = req.request().getFormAttribute("page");
            System.out.println(page);
            req.response()
                .putHeader("content-type","text/plain")
                .end(new JsonObject().put("Hello","Vertx").toString());
        });

        router.route("/test/json").handler(req->{
            //获取json格式参数
            JsonObject page = req.getBodyAsJson();
            System.out.println(page.toString());
            req.response()
                .putHeader("content-type","application/json")
                .end(new JsonObject().put("Hello","Vertx").toString());
        });

        vertx.createHttpServer().requestHandler(router).listen(8080, http->{
            if (http.succeeded()){
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            }else{
                http.failed();
            }
        });
    }
}
