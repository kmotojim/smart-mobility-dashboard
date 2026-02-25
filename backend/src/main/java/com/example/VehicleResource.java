package com.example;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * 車両状態を操作するREST APIエンドポイント
 */
@Path("/api/vehicle")
@Produces(MediaType.APPLICATION_JSON)
public class VehicleResource {
    
    @Inject
    VehicleService vehicleService;
    
    /**
     * 現在の車両状態を取得
     */
    @GET
    @Path("/state")
    public VehicleState getState() {
        return vehicleService.getState();
    }
    
    /**
     * 加速
     */
    @POST
    @Path("/accelerate")
    public VehicleState accelerate() {
        return vehicleService.accelerate();
    }
    
    /**
     * 減速
     */
    @POST
    @Path("/decelerate")
    public VehicleState decelerate() {
        return vehicleService.decelerate();
    }
    
    /**
     * ギア変更
     */
    @POST
    @Path("/gear/{gear}")
    public Response changeGear(@PathParam("gear") String gearStr) {
        try {
            VehicleState.Gear gear = VehicleState.Gear.valueOf(gearStr.toUpperCase());
            VehicleState state = vehicleService.changeGear(gear);
            return Response.ok(state).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid gear: " + gearStr + ". Valid values: P, R, N, D\"}")
                    .build();
        }
    }
    
    /**
     * シートベルト状態変更
     */
    @POST
    @Path("/seatbelt/{fastened}")
    public VehicleState setSeatbelt(@PathParam("fastened") boolean fastened) {
        return vehicleService.setSeatbelt(fastened);
    }
    
    /**
     * エンジン異常発生/解除（テスト用）
     */
    @POST
    @Path("/engine-error/{hasError}")
    public VehicleState setEngineError(@PathParam("hasError") boolean hasError) {
        return vehicleService.setEngineError(hasError);
    }
    
    /**
     * 状態リセット
     */
    @POST
    @Path("/reset")
    public VehicleState reset() {
        return vehicleService.reset();
    }
}
