package pl.edu.agh.kt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.projectfloodlight.openflow.types.IPv4Address;
import org.sdnplatform.sync.internal.util.Pair;

import pl.edu.agh.kt.rest.LinkPortsInfo;
import pl.edu.agh.kt.rest.PathInfo;

import net.floodlightcontroller.topology.NodePortTuple;

public class WRRLoadBalancer {
	/* {							-> Map of src and dst pairs
	 * 	(src1, dst1) : [			-> List of paths
	 * 		{
	 * 			weight : 3,			-> info about path weight
	 * 			count : 1			-> info about returned paths
	 * 			index : 1
	 * 		},
	 * 		{
	 * 			weight : 2,
	 * 			count : 0
	 * 			index : 0
	 * 		}
	 * 	],
	 *  (src2, dst2) : [...],
	 *  (src3, dst3) : [...]
	 * }
	 */
	private static Map<List<IPv4Address>, List<Map<String, Integer>>> robinInfo = new HashMap<>();
	
	public static List<LinkPortsInfo> getBestPath(IPv4Address src, IPv4Address dst){
		PathInfo paths = Flows.paths.getPaths(src, dst);
		
		if (paths == null){
			return null;
		}
		final List<List<LinkPortsInfo>> originalPaths = paths.getPaths();
		List<Integer> originalIndices = new ArrayList<>();
	    for (int i = 0; i < originalPaths.size(); i++) {
	        originalIndices.add(i);
	    }
	    // Sort paths from shortest to longest using Collections.sort
	    List<Integer> sortedIndices = new ArrayList<>(originalIndices);
	    Collections.sort(sortedIndices, new Comparator<Integer>() {
	        @Override
	        public int compare(Integer i1, Integer i2) {
	            return Integer.compare(originalPaths.get(i1).size(), originalPaths.get(i2).size());
	        }
	    });
	    
	    return originalPaths.get(sortedIndices.get(0));
	}
	
	public static List<LinkPortsInfo> getPath(IPv4Address src, IPv4Address dst){
		PathInfo paths = Flows.paths.getPaths(src, dst);
		
		if (paths == null){
			return null;
		}
		final List<List<LinkPortsInfo>> originalPaths = paths.getPaths();
		
		if (!robinInfo.containsKey(Arrays.asList(src, dst))){
			List<Map<String, Integer>> info = new ArrayList<>();
			
		    List<Integer> originalIndices = new ArrayList<>();
		    for (int i = 0; i < originalPaths.size(); i++) {
		        originalIndices.add(i);
		    }
			
			// Sort paths from shortest to longest using Collections.sort
		    List<Integer> sortedIndices = new ArrayList<>(originalIndices);
		    Collections.sort(sortedIndices, new Comparator<Integer>() {
		        @Override
		        public int compare(Integer i1, Integer i2) {
		            return Integer.compare(originalPaths.get(i1).size(), originalPaths.get(i2).size());
		        }
		    });
		    
		    int weight = sortedIndices.size();
		    for (Integer sortedIndex : sortedIndices) {
		        Map<String, Integer> pInfo = new HashMap<>();
		        pInfo.put("weight", weight--);        // Decrement weight
		        pInfo.put("count", 0);               // Initialize count
		        pInfo.put("index", sortedIndex);     // Original index
		        info.add(pInfo);
		    }
			robinInfo.put(Arrays.asList(src, dst), info);
		}
		
		List<Map<String, Integer>> rInfo = robinInfo.get(Arrays.asList(src, dst));
		
		for (Map<String, Integer> path : rInfo){
			if (!path.get("count").equals(path.get("weight"))){
				path.put("count", path.get("count") + 1);
				return originalPaths.get(path.get("index"));
			}
		}
		
		for (Map<String, Integer> path : rInfo){
			path.put("count", 0);
		}
		Map<String, Integer> path = rInfo.get(0);
		path.put("count", 1);
		return originalPaths.get(path.get("index"));
	}
}
