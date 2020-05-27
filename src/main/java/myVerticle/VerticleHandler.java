package myVerticle;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import myVerticle.service.OperationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class VerticleHandler {

	OperationService service;

	public VerticleHandler(OperationService service){
		this.service = service;
	}

	public void getMessage(RoutingContext routingContext){

		int page = Integer.parseInt(routingContext.request().getParam("page"));

		int offset = (page-1)*10;

		service.getMessage(offset)
				.setHandler(ar->{
					if(ar.succeeded()){
						RowSet<Row> rows = ar.result();
						List<JsonObject> list = new ArrayList<>();
						rows.forEach(item->{
							JsonObject obj = new JsonObject();
							obj.put("id",item.getValue("id"));
							obj.put("name",item.getValue("name"));
							list.add(obj);
						});
						routingContext.response()
								.putHeader("content-type","application/json")
								.end(list.toString());
					}else{
						routingContext.response()
								.putHeader("content-type","text/html")
								.end(ar.cause().toString());
					}
				});



	}


}
