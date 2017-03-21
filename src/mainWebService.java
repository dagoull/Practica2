

/**
 * @author Crunchify.com
 */

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.json.JSONException;
import org.json.JSONObject;


import ExpositoTOP.src.top.TOPTW;
import ExpositoTOP.src.top.TOPTWGRASP;
import ExpositoTOP.src.top.TOPTWReader;
import ExpositoTOP.src.top.TOPTWSolution;



@Path("/topservice")
public class mainWebService {

	@GET
	@Produces("application/json")
	public Response getRoutes(@Context ServletContext context)
	{
		 //you can specify in your method argument
		String realPath = context.getRealPath("/");
		System.out.println(realPath);
		 String[] instances = new String[1];
         instances[0] = "c101.txt";
             String INSTANCE = realPath + "/WEB-INF/Instances/TOPTW/"+instances[0];
             TOPTW problem = TOPTWReader.readProblem(INSTANCE);
             TOPTWSolution solution = new TOPTWSolution(problem);
             TOPTWGRASP grasp = new TOPTWGRASP(solution);

             System.out.println(" --> Instance: "+instances[0]);
             grasp.GRASP(5, 3);
             //grasp.GRASP(10000, 5);
             //grasp.GRASP(10000, 7);
             System.out.println("");
             System.out.println(grasp.getBest_solution().getSolutionJSON().toString());
             String result = grasp.getBest_solution().getSolutionJSON().toString();

             return Response.status(200).entity(result).build();

	}

	@Path("{j}")
	@GET
	@Produces("application/json")
	public Response getRoutesfromInput(@Context ServletContext context, @PathParam("j") JSONObject j)
	{
		 //you can specify in your method argument
		JSONObject json = j;
		String realPath = context.getRealPath("/");
		System.out.println(realPath);
		 String[] instances = new String[1];
         instances[0] = "c101.txt";
             String INSTANCE = realPath + "/WEB-INF/Instances/TOPTW/"+instances[0];
             TOPTW problem = TOPTWReader.readProblem(INSTANCE);
             problem.setMaxRoutes(json.getDouble("max number of routes"));
             problem.setMaxTimePerRoute(json.getDouble("max time per route"));
             problem.setVehicles(json.getInt("max number of routes"));
             TOPTWSolution solution = new TOPTWSolution(problem);
             TOPTWGRASP grasp = new TOPTWGRASP(solution);

             System.out.println(" --> Instance: "+instances[0]);
             grasp.GRASP(5, 3);
             //grasp.GRASP(10000, 5);
             //grasp.GRASP(10000, 7);
             System.out.println("");
             System.out.println(grasp.getBest_solution().getSolutionJSON().toString());
             String result = grasp.getBest_solution().getSolutionJSON().toString();

             return Response.status(200).entity(result).build();

	}

}
