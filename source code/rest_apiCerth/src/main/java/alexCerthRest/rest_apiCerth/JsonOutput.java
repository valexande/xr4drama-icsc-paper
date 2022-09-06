
package alexCerthRest.rest_apiCerth;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;



@Path("/secured")
public class JsonOutput {
	
@Path("/requestKnowledge")
@GET
@Produces(MediaType.APPLICATION_JSON)
 public String getObservation() {
	
	return GraphDB.getObservation();
}
}
