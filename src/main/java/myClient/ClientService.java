package myClient;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;

public class ClientService {

	private Vertx vertx;

	public ClientService(Vertx vertx){
		this.vertx = vertx;
	}

	WebClient client;

	public Future<JsonObject> getMessage(String type ){

		client = WebClient.create(vertx);

		Promise<JsonObject> promise = Promise.promise();

		client.get(8080,"/instance","/?type="+type)
				.as(BodyCodec.jsonObject())
				.send(ar->{
					if (ar.succeeded()){
						HttpResponse<JsonObject> result = ar.result();
						JsonObject obj =  result.body();
						promise.complete(obj);
					}else{
						System.out.println(ar.cause());
					}
				});

		return promise.future();

	}
}
