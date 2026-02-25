package com.example;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * 車両状態を管理するビジネスロジッククラス
 */
@ApplicationScoped
public class VehicleService {
    
    private static final int SPEED_INCREMENT = 10;  // 加速時の速度増分
    private static final int SPEED_DECREMENT = 10;  // 減速時の速度減分
    private static final int MAX_SPEED = 180;       // 最大速度
    private static final int MIN_SPEED = 0;         // 最小速度
    private static final int SPEED_WARNING_THRESHOLD = 120; // 速度警告閾値
    
    private final VehicleState state;
    
    public VehicleService() {
        this.state = new VehicleState();
    }
    
    /**
     * 現在の車両状態を取得
     */
    public VehicleState getState() {
        return state;
    }
    
    /**
     * 加速処理
     * @return 加速後の状態
     */
    public VehicleState accelerate() {
        // Dレンジでのみ加速可能
        if (state.getGear() == VehicleState.Gear.D) {
            int newSpeed = Math.min(state.getSpeed() + SPEED_INCREMENT, MAX_SPEED);
            state.setSpeed(newSpeed);
        }
        return state;
    }
    
    /**
     * 減速処理
     * @return 減速後の状態
     */
    public VehicleState decelerate() {
        int newSpeed = Math.max(state.getSpeed() - SPEED_DECREMENT, MIN_SPEED);
        state.setSpeed(newSpeed);
        return state;
    }
    
    /**
     * ギア変更
     * @param gear 新しいギア状態
     * @return 変更後の状態
     */
    public VehicleState changeGear(VehicleState.Gear gear) {
        // 速度が0のときのみギア変更可能（安全インターロック）
        if (state.getSpeed() == 0) {
            state.setGear(gear);
        }
        return state;
    }
    
    /**
     * シートベルト装着/解除
     * @param fastened シートベルト装着状態
     * @return 変更後の状態
     */
    public VehicleState setSeatbelt(boolean fastened) {
        state.setSeatbeltWarning(!fastened);
        return state;
    }
    
    /**
     * エンジン異常を発生/解除（テスト用）
     * @param hasError エラー状態
     * @return 変更後の状態
     */
    public VehicleState setEngineError(boolean hasError) {
        state.setEngineWarning(hasError);
        // エンジン異常時は強制減速
        if (hasError && state.getSpeed() > 60) {
            state.setSpeed(60);
        }
        return state;
    }
    
    /**
     * 状態をリセット
     * @return リセット後の状態
     */
    public VehicleState reset() {
        state.setSpeed(0);
        state.setGear(VehicleState.Gear.P);
        state.setEngineWarning(false);
        state.setSeatbeltWarning(true);
        return state;
    }
    
    /**
     * 速度から減速度を計算（ブレーキロジック）
     * @param currentSpeed 現在速度
     * @param brakeForce ブレーキ力 (0.0-1.0)
     * @return 減速度 (km/h/s)
     */
    public static int calculateDeceleration(int currentSpeed, double brakeForce) {
        if (brakeForce < 0.0 || brakeForce > 1.0) {
            throw new IllegalArgumentException("brakeForce must be between 0.0 and 1.0");
        }
        if (currentSpeed < 0) {
            throw new IllegalArgumentException("currentSpeed must be non-negative");
        }
        
        // 基本減速度: 最大10km/h/s、ブレーキ力に比例
        int baseDeceleration = (int) (10 * brakeForce);
        
        // 高速時は減速効率が下がる（空気抵抗の影響）
        if (currentSpeed > 100) {
            baseDeceleration = (int) (baseDeceleration * 0.8);
        }
        
        return baseDeceleration;
    }
}
