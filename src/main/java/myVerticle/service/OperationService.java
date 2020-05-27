package myVerticle.service;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import myVerticle.service.imp.OperationServiceImpl;

public interface OperationService {

	static OperationServiceImpl create(Vertx vertx){
		return new OperationServiceImpl(vertx);
	}

	Future<RowSet<Row>> getMessage(int offset);

}
