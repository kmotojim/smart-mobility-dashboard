package com.example;

/**
 * 車両状態を表すモデルクラス
 */
public class VehicleState {
    
    /**
     * ギア状態
     */
    public enum Gear {
        P, // パーキング
        R, // リバース
        N, // ニュートラル
        D  // ドライブ
    }
    
    private int speed;           // 現在速度 (0-180 km/h)
    private Gear gear;           // ギア状態
    private boolean engineWarning;     // エンジン警告
    private boolean seatbeltWarning;   // シートベルト警告
    private boolean speedWarning;      // 速度超過警告
    
    public VehicleState() {
        this.speed = 0;
        this.gear = Gear.P;
        this.engineWarning = false;
        this.seatbeltWarning = true;  // 初期状態ではシートベルト未装着
        this.speedWarning = false;
    }
    
    // Getters and Setters
    public int getSpeed() {
        return speed;
    }
    
    public void setSpeed(int speed) {
        this.speed = Math.max(0, Math.min(180, speed)); // 0-180の範囲に制限
        this.speedWarning = this.speed > 120; // 120km/h超過で警告
    }
    
    public Gear getGear() {
        return gear;
    }
    
    public void setGear(Gear gear) {
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
