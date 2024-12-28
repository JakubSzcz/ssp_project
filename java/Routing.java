package pl.edu.agh.kt;

import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.routing.RouteId;
import net.floodlightcontroller.topology.NodePortTuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.U64;
import org.sdnplatform.sync.internal.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Routing {
	private IRoutingService routingService;
	protected static final Logger logger = LoggerFactory
			.getLogger(Routing.class);

	public Routing(IRoutingService routingService) {
		super();
		this.routingService = routingService;
	}

	public Route calculateSpfTree(DatapathId srcSwitch, DatapathId dstSwitch) {
		// TODO lab9
		logger.info("Calculating SPF tree");
		Route route = calculateSpf(srcSwitch, dstSwitch);
		return route;
	}

	public Route calculateSpf(DatapathId src, DatapathId dst) {
		//logger.info("SRC = {}", src);
		//logger.info("DST = {}", dst);
		Route route = routingService.getRoute(src, dst, U64.of(0));
		return route;
	}
	
	public List<List<DatapathId>> calculateAllPaths(DatapathId src, DatapathId dst){
		List<List<DatapathId>> paths = calculateAllPaths(src, dst, new ArrayList<DatapathId>());
		//logger.info("path = {}", paths);
		return paths;
	}
	
	
	public List<List<DatapathId>> calculateAllPaths(DatapathId src, DatapathId dst, List<DatapathId> exclude){
		List<List<DatapathId>> toReturn = new ArrayList<List<DatapathId>>();
		//logger.info("src = {}", src);
		for (DatapathId nei : Flows.adjacencyMap.get(src)){
			if (exclude.contains(nei)){
				continue;
			}
			//logger.info("src = {}, nei = {}", src, nei);
			if (nei.equals(dst)){
				toReturn.add(Arrays.asList(src, nei));
			}else{
				exclude.add(src);
				List<List<DatapathId>> temp = calculateAllPaths(nei, dst, exclude);
				List<List<DatapathId>> temp2 = new ArrayList<List<DatapathId>>();
				for (List<DatapathId> elem : temp){
					//logger.info("elem = {}", elem);
					temp2.add(new ArrayList<DatapathId>());
					temp2.get(temp2.size() - 1).add(src);
					temp2.get(temp2.size() - 1).addAll(elem);
					//logger.info("elem2 = {}", elem);
				}
				toReturn.addAll(temp2);
			}
		}
		//logger.info("ret = {}", toReturn);
		return toReturn;
	}
	
	public List<Pair<List<DatapathId>, Integer>> getKPaths(DatapathId src, DatapathId dst, int k){
		List<List<DatapathId>> paths = calculateAllPaths(src, dst);
		List<Pair<List<DatapathId>, Integer>> toReturn = new ArrayList<Pair<List<DatapathId>, Integer>>();
		for (List<DatapathId> elem : paths){
			toReturn.add(new Pair(elem, elem.size()));
		}
		List<Pair<List<DatapathId>, Integer>> toReturn2 = new ArrayList<Pair<List<DatapathId>, Integer>>();
		for (int i = 0; i < k; i++){
			Pair<List<DatapathId>, Integer> maxElem = toReturn.get(0);
			for (Pair<List<DatapathId>, Integer> elem : toReturn){
				if (elem.getValue() < maxElem.getValue()){
					maxElem = elem;
				}
			}
			toReturn2.add(maxElem);
			toReturn.remove(maxElem);
		}
		return toReturn2;
	}
}
