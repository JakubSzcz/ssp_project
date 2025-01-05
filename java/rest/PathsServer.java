package pl.edu.agh.kt.rest;

import java.io.IOException;

import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.kt.Flows;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class PathsServer extends ServerResource {
	protected static Logger log = LoggerFactory.getLogger(LabRestServer.class);
	
	
	@Post("json")
	public String handlePost(String text) throws JsonProcessingException,
			IOException {
		log.info("handlePostPathsInfo");
		PathsInfo paths = new PathsInfo();
		paths = deserialize(text, PathsInfo.class);
		Flows.paths = paths;
		return serialize(paths);
	}

	
	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	}

	public static String serialize(PathsInfo p) throws JsonProcessingException {
		return mapper.writeValueAsString(p);
	}

	public static PathsInfo deserialize(String text, Class<PathsInfo> clazz)
			throws IOException {
		return mapper.readValue(text, clazz);
	}
}
