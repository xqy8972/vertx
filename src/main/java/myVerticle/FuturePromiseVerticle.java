package myVerticle;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import myVerticle.service.OperationService;

import java.util.ArrayList;
import java.util.List;

public class FuturePromiseVerticle extends AbstractVerticle {

    Router router;

    public void start(Promise<Void> startPromise){

        //router绑定vertx
        router = Router.router(vertx);

        //创建handler对象
        VerticleHandler handler = new VerticleHandler(OperationService.create(vertx));

        //绑定路径，和handler方法
        router.route("/test/list").handler(handler::getMessage);

        //创建httpServer监听请求
        vertx.createHttpServer().requestHandler(router)
                .exceptionHandler(t->{
                    t.printStackTrace();
                })
                .listen(8080,http->{

            if (http.succeeded()){
                startPromise.complete();
                System.out.println("HTTP server started on port 8080");
            }else{
                http.failed();
            }
        });

    }

}


















