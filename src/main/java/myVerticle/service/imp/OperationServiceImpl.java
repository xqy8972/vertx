package myVerticle.service.imp;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.*;
import myVerticle.service.OperationService;

import java.util.ArrayList;
import java.util.List;

public class OperationServiceImpl  implements OperationService {

	private Vertx vertx;

	//数据库连接配置对象
	MySQLConnectOptions connectOptions;

	//数据库连接池
	MySQLPool client;

	//数据库连接池配置对象
	PoolOptions poolOptions = new PoolOptions().setMaxSize(5);

	//配置文件加载器
	ConfigRetriever retriever;

	public OperationServiceImpl(Vertx vertx){
		this.vertx = vertx;
	}

	@Override
	public Future<RowSet<Row>> getMessage(int offset) {
		Promise<RowSet<Row>> promise = Promise.promise();

		getConfig().compose(config->getConn(config))//获取配置文件->获取连接对象
				.compose(conn->getRows(conn,offset))//执行sql获取结果
				.onSuccess(rows -> {
					List<JsonObject> list = new ArrayList<>();
					rows.forEach(item->{
						JsonObject obj = new JsonObject();
						obj.put("id",obj.getValue("id"));
						obj.put("name",obj.getValue("name"));
						list.add(obj);
					});
					promise.complete(rows);
				});
		return promise.future();
	}


	//1.获取配置文件
	private Future<JsonObject> getConfig(){
		Promise<JsonObject> promise = Promise.promise();
		retriever = ConfigRetriever.create(vertx);
		retriever.getConfig(ar->{
			if(ar.succeeded()){
				JsonObject config = ar.result();
				promise.complete(config);
			}else{
				ar.failed();
			}
		});
		return promise.future();
	}

	//2.获取conn连接对象
	private Future<SqlConnection> getConn(JsonObject config){
		Promise<SqlConnection> promise = Promise.promise();

		connectOptions = new MySQLConnectOptions()
				.setPort(Integer.parseInt(config.getValue("port").toString()))
				.setHost(config.getString("host"))
				.setDatabase(config.getString("database"))
				.setUser(config.getString("user"))
				.setPassword(config.getString("password"));

		client = MySQLPool.pool(vertx,connectOptions, poolOptions);

		client.getConnection(ar->{
			if(ar.succeeded()){
				SqlConnection conn = ar.result();
				promise.complete(conn);
			}else{
				ar.failed();
			}
		});
		return promise.future();
	}

	//3.查询数据库返回结果
	private Future<RowSet<Row>> getRows(SqlConnection conn, Integer offset){
		Promise<RowSet<Row>> promise = Promise.promise();

		conn.preparedQuery("SELECT id,name FROM users limit 10 offset = ?")
				.execute(Tuple.of(offset), ar->{
					conn.close();
					if(ar.succeeded()){
						promise.complete(ar.result());
					}else{
						promise.fail(ar.cause());
					}
				});

		return promise.future();
	}
}






















