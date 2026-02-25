package com.example;

/**
 * 車両状態のDTO（Data Transfer Object）
 */
public class VehicleStateDto {
    
    private int speed;
    private String gear;
    private boolean engineWarning;
    private boolean seatbeltWarning;
    private boolean speedWarning;
    
    public VehicleStateDto() {
    }
    
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    
    public String getGear() {
        return gear;
    }
    
    public void setGear(String gear) {
        this.gear = gear;
    }
    
    public boolean isEngineWarning() {
        return engineWarning;
    }
    
    public void setEngineWarning(boolean engineWarning) {
        this.engineWarning = engineWarning;
    }
    
    public boolean isSeatbeltWarning() {
        return seatbeltWarning;
    }
    
    public void setSeatbeltWarning(boolean seatbeltWarning) {
        this.seatbeltWarning = seatbeltWarning;
    }
    
    public boolean isSpeedWarning() {
        return speedWarning;
    }
    
    public void setSpeedWarning(boolean speedWarning) {
        this.speedWarning = speedWarning;
    }
}
