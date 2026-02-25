package com.example;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

/**
 * フロントエンドからバックエンドAPIへのプロキシリソース
 */
@Path("/api/vehicle")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VehicleApiResource {
    
    @Inject
    @RestClient
    BackendClient backendClient;
    
    @GET
    @Path("/state")
    public VehicleStateDto getState() {
        return backendClient.getState();
    }
    
    @POST
    @Path("/accelerate")
    public VehicleStateDto accelerate() {
        return backendClient.accelerate();
    }
    
    @POST
    @Path("/decelerate")
    public VehicleStateDto decelerate() {
        return backendClient.decelerate();
    }
    
    @POST
    @Path("/gear/{gear}")
    public VehicleStateDto changeGear(@PathParam("gear") String gear) {
        return backendClient.changeGear(gear);
    }
    
    @POST
    @Path("/seatbelt/{fastened}")
    public VehicleStateDto setSeatbelt(@PathParam("fastened") boolean fastened) {
        return backendClient.setSeatbelt(fastened);
    }
    
    @POST
    @Path("/engine-error/{hasError}")
    public VehicleStateDto setEngineError(@PathParam("hasError") boolean hasError) {
        return backendClient.setEngineError(hasError);
    }
    
    @POST
    @Path("/reset")
    public VehicleStateDto reset() {
        return backendClient.reset();
    }
}
