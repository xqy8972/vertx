package vertx;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

public class ConfigVerticale extends AbstractVerticle {

    Router router;

    //1.配置连接参数
    MySQLConnectOptions connectOptions;

    //2.配置连接池
    PoolOptions poolOptions = new PoolOptions()
        .setMaxSize(5);

    //3.创建 client Pool
    MySQLPool client;

    @Override
    public void start(Promise<Void> startPromise){

        //整合引入配置文件功能
        ConfigRetriever retriever = ConfigRetriever.create(vertx);

        retriever.getConfig(ar->{
            //获取配置文件成功，向下执行
            if(ar.succeeded()){

                JsonObject config = ar.result();

                connectOptions = new MySQLConnectOptions()
                    .setPort(Integer.parseInt(config.getValue("port").toString()))
                    .setHost(config.getString("host"))
                    .setDatabase(config.getString("community"))
                    .setUser(config.getString("user"))
                    .setPassword(config.getString("password"));
                router = Router.router(vertx);

                client = MySQLPool.pool(vertx,connectOptions, poolOptions);

                router.route("/test/mysql").handler(req->{

                    client.getConnection(ar1->{
                        //连接成功
                        if(ar1.succeeded()){
                            System.out.println("Connected");
                            //获取连接对象
                            SqlConnection conn = ar1.result();
                            //获取参数
                            int page = Integer.parseInt(req.request().getParam("page"));

                            conn
                                .preparedQuery("SELECT * FROM users limit 10, offset = ?")
                                .execute(Tuple.of(page),ar2->{
                                    conn.close();
                                    if(ar2.succeeded()){
                                        req.response()
                                            .putHeader("context-type","application/json")
                                            .end(ar2.result().toString());
                                    }
                                });

                        }else{
                            System.out.println("Could not connect: " + ar1.cause().getMessage());
                        }
                    });
                });

                vertx.createHttpServer().requestHandler(router).listen(8080,http->{
                    if (http.succeeded()){
                        startPromise.complete();
                        System.out.println("HTTP server started on port 8080");
                    }else{
                        http.failed();
                    }
                });
            }else{
                JsonObject config = ar.result();
            }
        });


    }

}
