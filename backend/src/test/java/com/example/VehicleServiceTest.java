package com.example;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VehicleServiceの単体テスト
 */
@QuarkusTest
class VehicleServiceTest {
    
    @Inject
    VehicleService vehicleService;
    
    @BeforeEach
    void setUp() {
        vehicleService.reset();
    }
    
    @Nested
    @DisplayName("加速テスト")
    class AccelerateTests {
        
        @Test
        @DisplayName("Dレンジで加速すると速度が10km/h増加する")
        void shouldIncreaseSpeedBy10WhenInDriveGear() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            
            // When
            VehicleState result = vehicleService.accelerate();
            
            // Then
            assertEquals(10, result.getSpeed());
        }
        
        @Test
        @DisplayName("Dレンジ以外では加速しない")
        void shouldNotAccelerateWhenNotInDriveGear() {
            // Given - Pレンジ（初期状態）
            
            // When
            VehicleState result = vehicleService.accelerate();
            
            // Then
            assertEquals(0, result.getSpeed());
        }
        
        @Test
        @DisplayName("最大速度180km/hを超えない")
        void shouldNotExceedMaxSpeed() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            
            // When - 20回加速（200km/h分）
            for (int i = 0; i < 20; i++) {
                vehicleService.accelerate();
            }
            
            // Then
            assertEquals(180, vehicleService.getState().getSpeed());
        }
    }
    
    @Nested
    @DisplayName("減速テスト")
    class DecelerateTests {
        
        @Test
        @DisplayName("減速すると速度が10km/h減少する")
        void shouldDecreaseSpeedBy10() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            vehicleService.accelerate();
            vehicleService.accelerate();
            vehicleService.accelerate(); // 30km/h
            
            // When
            VehicleState result = vehicleService.decelerate();
            
            // Then
            assertEquals(20, result.getSpeed());
        }
        
        @Test
        @DisplayName("速度は0km/h未満にならない")
        void shouldNotGoBelowZero() {
            // Given - 速度0km/h
            
            // When
            VehicleState result = vehicleService.decelerate();
            
            // Then
            assertEquals(0, result.getSpeed());
        }
    }
    
    @Nested
    @DisplayName("ギア変更テスト")
    class GearChangeTests {
        
        @Test
        @DisplayName("速度0のときギア変更できる")
        void shouldChangeGearWhenSpeedIsZero() {
            // When
            VehicleState result = vehicleService.changeGear(VehicleState.Gear.D);
            
            // Then
            assertEquals(VehicleState.Gear.D, result.getGear());
        }
        
        @Test
        @DisplayName("走行中はギア変更できない（安全インターロック）")
        void shouldNotChangeGearWhenMoving() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            vehicleService.accelerate(); // 10km/h
            
            // When
            VehicleState result = vehicleService.changeGear(VehicleState.Gear.P);
            
            // Then - ギアはDのまま
            assertEquals(VehicleState.Gear.D, result.getGear());
        }
    }
    
    @Nested
    @DisplayName("警告灯テスト")
    class WarningTests {
        
        @Test
        @DisplayName("120km/h超過で速度警告が発生する")
        void shouldTriggerSpeedWarningAbove120() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            
            // When - 130km/hまで加速
            for (int i = 0; i < 13; i++) {
                vehicleService.accelerate();
            }
            
            // Then
            assertTrue(vehicleService.getState().isSpeedWarning());
        }
        
        @Test
        @DisplayName("120km/h以下では速度警告は発生しない")
        void shouldNotTriggerSpeedWarningAtOrBelow120() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            
            // When - 120km/hまで加速
            for (int i = 0; i < 12; i++) {
                vehicleService.accelerate();
            }
            
            // Then
            assertFalse(vehicleService.getState().isSpeedWarning());
        }
        
        @Test
        @DisplayName("エンジン異常時は60km/hに強制減速する")
        void shouldForceDecelerateOnEngineError() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            for (int i = 0; i < 10; i++) {
                vehicleService.accelerate(); // 100km/h
            }
            
            // When
            VehicleState result = vehicleService.setEngineError(true);
            
            // Then
            assertEquals(60, result.getSpeed());
            assertTrue(result.isEngineWarning());
        }
    }
    
    @Nested
    @DisplayName("減速度計算テスト")
    class DecelerationCalculationTests {
        
        @Test
        @DisplayName("ブレーキ力1.0で減速度10km/h/sを返す")
        void shouldReturn10ForFullBrake() {
            // When
            int deceleration = VehicleService.calculateDeceleration(50, 1.0);
            
            // Then
            assertEquals(10, deceleration);
        }
        
        @Test
        @DisplayName("ブレーキ力0.5で減速度5km/h/sを返す")
        void shouldReturn5ForHalfBrake() {
            // When
            int deceleration = VehicleService.calculateDeceleration(50, 0.5);
            
            // Then
            assertEquals(5, deceleration);
        }
        
        @Test
        @DisplayName("100km/h超では減速効率が下がる")
        void shouldReduceEfficiencyAbove100() {
            // When
            int normalDeceleration = VehicleService.calculateDeceleration(50, 1.0);
            int highSpeedDeceleration = VehicleService.calculateDeceleration(120, 1.0);
            
            // Then
            assertTrue(highSpeedDeceleration < normalDeceleration);
        }
        
        @Test
        @DisplayName("ブレーキ力が範囲外の場合は例外をスローする")
        void shouldThrowExceptionForInvalidBrakeForce() {
            assertThrows(IllegalArgumentException.class, () -> {
                VehicleService.calculateDeceleration(50, 1.5);
            });
            
            assertThrows(IllegalArgumentException.class, () -> {
                VehicleService.calculateDeceleration(50, -0.1);
            });
        }
        
        @Test
        @DisplayName("負の速度は例外をスローする")
        void shouldThrowExceptionForNegativeSpeed() {
            assertThrows(IllegalArgumentException.class, () -> {
                VehicleService.calculateDeceleration(-10, 0.5);
            });
        }
    }
    
    @Nested
    @DisplayName("境界値テスト")
    class BoundaryTests {
        
        @Test
        @DisplayName("速度0km/hから減速しても0km/hのまま")
        void speedShouldRemainZeroWhenDeceleratingFromZero() {
            // When
            VehicleState result = vehicleService.decelerate();
            
            // Then
            assertEquals(0, result.getSpeed());
        }
        
        @Test
        @DisplayName("速度180km/hから加速しても180km/hのまま")
        void speedShouldRemain180WhenAcceleratingFromMax() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            for (int i = 0; i < 18; i++) {
                vehicleService.accelerate();
            }
            
            // When
            VehicleState result = vehicleService.accelerate();
            
            // Then
            assertEquals(180, result.getSpeed());
        }
    }
}
