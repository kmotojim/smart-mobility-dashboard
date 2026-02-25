package com.example;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * バックエンドAPIと通信するRESTクライアント
 */
@Path("/api/vehicle")
@RegisterRestClient(configKey = "backend-api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BackendClient {
    
    @GET
    @Path("/state")
    VehicleStateDto getState();
    
    @POST
    @Path("/accelerate")
    VehicleStateDto accelerate();
    
    @POST
    @Path("/decelerate")
    VehicleStateDto decelerate();
    
    @POST
    @Path("/gear/{gear}")
    VehicleStateDto changeGear(@PathParam("gear") String gear);
    
    @POST
    @Path("/seatbelt/{fastened}")
    VehicleStateDto setSeatbelt(@PathParam("fastened") boolean fastened);
    
    @POST
    @Path("/engine-error/{hasError}")
    VehicleStateDto setEngineError(@PathParam("hasError") boolean hasError);
    
    @POST
    @Path("/reset")
    VehicleStateDto reset();
}
