package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VehicleStateの単体テスト
 */
class VehicleStateTest {
    
    private VehicleState vehicleState;
    
    @BeforeEach
    void setUp() {
        vehicleState = new VehicleState();
    }
    
    @Nested
    @DisplayName("初期状態テスト")
    class InitialStateTests {
        
        @Test
        @DisplayName("速度の初期値は0")
        void initialSpeedShouldBeZero() {
            assertEquals(0, vehicleState.getSpeed());
        }
        
        @Test
        @DisplayName("ギアの初期値はP（パーキング）")
        void initialGearShouldBeP() {
            assertEquals(VehicleState.Gear.P, vehicleState.getGear());
        }
        
        @Test
        @DisplayName("エンジン警告の初期値はfalse")
        void initialEngineWarningShouldBeFalse() {
            assertFalse(vehicleState.isEngineWarning());
        }
        
        @Test
        @DisplayName("シートベルト警告の初期値はtrue（未装着）")
        void initialSeatbeltWarningShouldBeTrue() {
            assertTrue(vehicleState.isSeatbeltWarning());
        }
        
        @Test
        @DisplayName("速度警告の初期値はfalse")
        void initialSpeedWarningShouldBeFalse() {
            assertFalse(vehicleState.isSpeedWarning());
        }
    }
    
    @Nested
    @DisplayName("速度setter/getterテスト")
    class SpeedTests {
        
        @Test
        @DisplayName("通常の速度設定")
        void shouldSetNormalSpeed() {
            vehicleState.setSpeed(50);
            assertEquals(50, vehicleState.getSpeed());
        }
        
        @Test
        @DisplayName("速度0を設定")
        void shouldSetZeroSpeed() {
            vehicleState.setSpeed(0);
            assertEquals(0, vehicleState.getSpeed());
        }
        
        @Test
        @DisplayName("最大速度180を設定")
        void shouldSetMaxSpeed() {
            vehicleState.setSpeed(180);
            assertEquals(180, vehicleState.getSpeed());
        }
        
        @Test
        @DisplayName("負の速度は0に制限される")
        void negativeSpeeedShouldBeClampedToZero() {
            vehicleState.setSpeed(-10);
            assertEquals(0, vehicleState.getSpeed());
        }
        
        @Test
        @DisplayName("180を超える速度は180に制限される")
        void speedAbove180ShouldBeClampedTo180() {
            vehicleState.setSpeed(200);
            assertEquals(180, vehicleState.getSpeed());
        }
        
        @Test
        @DisplayName("120km/h以下では速度警告なし")
        void speedAt120ShouldNotTriggerWarning() {
            vehicleState.setSpeed(120);
            assertFalse(vehicleState.isSpeedWarning());
        }
        
        @Test
        @DisplayName("121km/h以上で速度警告発生")
        void speedAt121ShouldTriggerWarning() {
            vehicleState.setSpeed(121);
            assertTrue(vehicleState.isSpeedWarning());
        }
        
        @Test
        @DisplayName("速度警告後に120km/h以下に減速すると警告解除")
        void speedWarningClearsWhenDeceleratingBelow120() {
            vehicleState.setSpeed(130);
            assertTrue(vehicleState.isSpeedWarning());
            
            vehicleState.setSpeed(100);
            assertFalse(vehicleState.isSpeedWarning());
        }
    }
    
    @Nested
    @DisplayName("ギアsetter/getterテスト")
    class GearTests {
        
        @Test
        @DisplayName("Dレンジに変更")
        void shouldSetGearToD() {
            vehicleState.setGear(VehicleState.Gear.D);
            assertEquals(VehicleState.Gear.D, vehicleState.getGear());
        }
        
        @Test
        @DisplayName("Rレンジに変更")
        void shouldSetGearToR() {
            vehicleState.setGear(VehicleState.Gear.R);
            assertEquals(VehicleState.Gear.R, vehicleState.getGear());
        }
        
        @Test
        @DisplayName("Nレンジに変更")
        void shouldSetGearToN() {
            vehicleState.setGear(VehicleState.Gear.N);
            assertEquals(VehicleState.Gear.N, vehicleState.getGear());
        }
        
        @Test
        @DisplayName("Pレンジに変更")
        void shouldSetGearToP() {
            vehicleState.setGear(VehicleState.Gear.D);
            vehicleState.setGear(VehicleState.Gear.P);
            assertEquals(VehicleState.Gear.P, vehicleState.getGear());
        }
    }
    
    @Nested
    @DisplayName("エンジン警告setter/getterテスト")
    class EngineWarningTests {
        
        @Test
        @DisplayName("エンジン警告をtrueに設定")
        void shouldSetEngineWarningTrue() {
            vehicleState.setEngineWarning(true);
            assertTrue(vehicleState.isEngineWarning());
        }
        
        @Test
        @DisplayName("エンジン警告をfalseに設定")
        void shouldSetEngineWarningFalse() {
            vehicleState.setEngineWarning(true);
            vehicleState.setEngineWarning(false);
            assertFalse(vehicleState.isEngineWarning());
        }
    }
    
    @Nested
    @DisplayName("シートベルト警告setter/getterテスト")
    class SeatbeltWarningTests {
        
        @Test
        @DisplayName("シートベルト警告をtrueに設定")
        void shouldSetSeatbeltWarningTrue() {
            vehicleState.setSeatbeltWarning(false);
            vehicleState.setSeatbeltWarning(true);
            assertTrue(vehicleState.isSeatbeltWarning());
        }
        
        @Test
        @DisplayName("シートベルト警告をfalseに設定（装着状態）")
        void shouldSetSeatbeltWarningFalse() {
            vehicleState.setSeatbeltWarning(false);
            assertFalse(vehicleState.isSeatbeltWarning());
        }
    }
    
    @Nested
    @DisplayName("速度警告setter/getterテスト")
    class SpeedWarningTests {
        
        @Test
        @DisplayName("速度警告を直接trueに設定")
        void shouldSetSpeedWarningTrue() {
            vehicleState.setSpeedWarning(true);
            assertTrue(vehicleState.isSpeedWarning());
        }
        
        @Test
        @DisplayName("速度警告を直接falseに設定")
        void shouldSetSpeedWarningFalse() {
            vehicleState.setSpeedWarning(true);
            vehicleState.setSpeedWarning(false);
            assertFalse(vehicleState.isSpeedWarning());
        }
    }
    
    @Nested
    @DisplayName("Gear列挙型テスト")
    class GearEnumTests {
        
        @Test
        @DisplayName("全ギア値が存在する")
        void allGearValuesShouldExist() {
            VehicleState.Gear[] gears = VehicleState.Gear.values();
            assertEquals(4, gears.length);
        }
        
        @Test
        @DisplayName("ギアを文字列から取得")
        void shouldGetGearFromString() {
            assertEquals(VehicleState.Gear.P, VehicleState.Gear.valueOf("P"));
            assertEquals(VehicleState.Gear.R, VehicleState.Gear.valueOf("R"));
            assertEquals(VehicleState.Gear.N, VehicleState.Gear.valueOf("N"));
            assertEquals(VehicleState.Gear.D, VehicleState.Gear.valueOf("D"));
        }
        
        @Test
        @DisplayName("無効なギア文字列で例外発生")
        void shouldThrowExceptionForInvalidGear() {
            assertThrows(IllegalArgumentException.class, () -> {
                VehicleState.Gear.valueOf("X");
            });
        }
    }
    
    @Nested
    @DisplayName("境界値テスト")
    class BoundaryTests {
        
        @Test
        @DisplayName("速度境界: -1は0に制限")
        void speedNegativeOneShouldBeZero() {
            vehicleState.setSpeed(-1);
            assertEquals(0, vehicleState.getSpeed());
        }
        
        @Test
        @DisplayName("速度境界: 181は180に制限")
        void speed181ShouldBe180() {
            vehicleState.setSpeed(181);
            assertEquals(180, vehicleState.getSpeed());
        }
        
        @Test
        @DisplayName("速度境界: Integer.MAX_VALUEは180に制限")
        void speedMaxIntShouldBe180() {
            vehicleState.setSpeed(Integer.MAX_VALUE);
            assertEquals(180, vehicleState.getSpeed());
        }
        
        @Test
        @DisplayName("速度境界: Integer.MIN_VALUEは0に制限")
        void speedMinIntShouldBeZero() {
            vehicleState.setSpeed(Integer.MIN_VALUE);
            assertEquals(0, vehicleState.getSpeed());
        }
    }
}
