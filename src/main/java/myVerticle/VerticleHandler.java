package myVerticle;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import myVerticle.service.OperationService;

import java.util.Objects;

public class VerticleHandler {

	OperationService service;

	public VerticleHandler(OperationService service){
		this.service = service;
	}

	public void getMessage(RoutingContext routingContext){

		int page = Integer.parseInt(routingContext.request().getParam("page"));

		int offset = (page-1)*10;

		try{
			service.getMessage(offset)
					.setHandler(ar->{
						if(ar.succeeded()){
							RowSet<Row> result = ar.result();
							routingContext.response()
									.putHeader("content-type","application/json")
									.end(result.toString());
						}else{
							ar.failed();
						}
					});
		}catch (Exception e){
			e.printStackTrace();
		}


	}


}
