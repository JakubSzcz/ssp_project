package pl.edu.agh.kt.rest;

import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import net.floodlightcontroller.restserver.RestletRoutable;

public class RestLab implements RestletRoutable {
	@Override
	public Restlet getRestlet(Context context) {
		Router router = new Router(context);
		router.attach("/timeout", LabRestServer.class);
		router.attach("/hosts", HostInfoServer.class);
		router.attach("/paths", PathsServer.class);
		return router;
	}

	@Override
	public String basePath() {
		return "/sdnlab";
	}
}
