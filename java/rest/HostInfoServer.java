package pl.edu.agh.kt.rest;

import java.io.IOException;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.kt.Flows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class HostInfoServer extends ServerResource{
	protected static Logger log = LoggerFactory.getLogger(LabRestServer.class);

	@Get("json")
	public String handleGet() throws JsonProcessingException {
		log.info("handleGetHostInfo");
		return serialize(Flows.hosts);
	}

	@Post("json")
	public String handlePost(String text) throws JsonProcessingException,
			IOException {
		log.info("handlePostHostInfo");
		HostInfo hosts = new HostInfo();
		hosts = deserialize(text, HostInfo.class);
		Flows.hosts = hosts;
		return serialize(hosts);
	}

	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	public static String serialize(HostInfo h) throws JsonProcessingException {
		return mapper.writeValueAsString(h);
	}

	public static HostInfo deserialize(String text, Class<HostInfo> clazz)
			throws IOException {
		return mapper.readValue(text, clazz);
	}
}
