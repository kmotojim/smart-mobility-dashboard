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
        @DisplayName("Rレンジでは加速しない")
        void shouldNotAccelerateInReverseGear() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.R);
            
            // When
            VehicleState result = vehicleService.accelerate();
            
            // Then
            assertEquals(0, result.getSpeed());
        }
        
        @Test
        @DisplayName("Nレンジでは加速しない")
        void shouldNotAccelerateInNeutralGear() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.N);
            
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
        
        @Test
        @DisplayName("連続加速で速度が累積する")
        void shouldAccumulateSpeedOnConsecutiveAccelerations() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            
            // When
            vehicleService.accelerate();
            vehicleService.accelerate();
            VehicleState result = vehicleService.accelerate();
            
            // Then
            assertEquals(30, result.getSpeed());
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
        
        @Test
        @DisplayName("Rレンジに変更できる")
        void shouldChangeToReverseGear() {
            // When
            VehicleState result = vehicleService.changeGear(VehicleState.Gear.R);
            
            // Then
            assertEquals(VehicleState.Gear.R, result.getGear());
        }
        
        @Test
        @DisplayName("Nレンジに変更できる")
        void shouldChangeToNeutralGear() {
            // When
            VehicleState result = vehicleService.changeGear(VehicleState.Gear.N);
            
            // Then
            assertEquals(VehicleState.Gear.N, result.getGear());
        }
    }
    
    @Nested
    @DisplayName("リセットテスト")
    class ResetTests {
        
        @Test
        @DisplayName("リセットで速度が0になる")
        void shouldResetSpeedToZero() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            vehicleService.accelerate();
            
            // When
            VehicleState result = vehicleService.reset();
            
            // Then
            assertEquals(0, result.getSpeed());
        }
        
        @Test
        @DisplayName("リセットでギアがPになる")
        void shouldResetGearToP() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            
            // When
            VehicleState result = vehicleService.reset();
            
            // Then
            assertEquals(VehicleState.Gear.P, result.getGear());
        }
        
        @Test
        @DisplayName("リセットでエンジン警告が解除される")
        void shouldResetEngineWarning() {
            // Given
            vehicleService.setEngineError(true);
            
            // When
            VehicleState result = vehicleService.reset();
            
            // Then
            assertFalse(result.isEngineWarning());
        }
        
        @Test
        @DisplayName("リセットでシートベルト警告が有効になる（未装着状態）")
        void shouldResetSeatbeltWarning() {
            // Given
            vehicleService.setSeatbelt(true);
            
            // When
            VehicleState result = vehicleService.reset();
            
            // Then
            assertTrue(result.isSeatbeltWarning());
        }
    }
    
    @Nested
    @DisplayName("状態取得テスト")
    class GetStateTests {
        
        @Test
        @DisplayName("getStateが現在の状態を返す")
        void shouldReturnCurrentState() {
            // When
            VehicleState state = vehicleService.getState();
            
            // Then
            assertNotNull(state);
            assertEquals(0, state.getSpeed());
            assertEquals(VehicleState.Gear.P, state.getGear());
        }
        
        @Test
        @DisplayName("getStateは同じインスタンスを返す")
        void shouldReturnSameInstance() {
            // When
            VehicleState state1 = vehicleService.getState();
            VehicleState state2 = vehicleService.getState();
            
            // Then
            assertSame(state1, state2);
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
        
        @Test
        @DisplayName("エンジン異常時に60km/h以下なら減速しない")
        void shouldNotForceDecelerateWhenSpeedIsBelow60() {
            // Given
            vehicleService.changeGear(VehicleState.Gear.D);
            for (int i = 0; i < 5; i++) {
                vehicleService.accelerate(); // 50km/h
            }
            
            // When
            VehicleState result = vehicleService.setEngineError(true);
            
            // Then
            assertEquals(50, result.getSpeed());
            assertTrue(result.isEngineWarning());
        }
        
        @Test
        @DisplayName("エンジン異常を解除できる")
        void shouldClearEngineError() {
            // Given
            vehicleService.setEngineError(true);
            
            // When
            VehicleState result = vehicleService.setEngineError(false);
            
            // Then
            assertFalse(result.isEngineWarning());
        }
        
        @Test
        @DisplayName("シートベルト装着で警告が解除される")
        void shouldClearSeatbeltWarningWhenFastened() {
            // Given - 初期状態でシートベルト警告あり
            assertTrue(vehicleService.getState().isSeatbeltWarning());
            
            // When
            VehicleState result = vehicleService.setSeatbelt(true);
            
            // Then
            assertFalse(result.isSeatbeltWarning());
        }
        
        @Test
        @DisplayName("シートベルト解除で警告が発生する")
        void shouldTriggerSeatbeltWarningWhenUnfastened() {
            // Given
            vehicleService.setSeatbelt(true);
            assertFalse(vehicleService.getState().isSeatbeltWarning());
            
            // When
            VehicleState result = vehicleService.setSeatbelt(false);
            
            // Then
            assertTrue(result.isSeatbeltWarning());
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
        @DisplayName("ブレーキ力0.0で減速度0km/h/sを返す")
        void shouldReturn0ForNoBrake() {
            // When
            int deceleration = VehicleService.calculateDeceleration(50, 0.0);
            
            // Then
            assertEquals(0, deceleration);
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
        @DisplayName("100km/hちょうどでは減速効率が下がらない")
        void shouldNotReduceEfficiencyAt100() {
            // When
            int deceleration = VehicleService.calculateDeceleration(100, 1.0);
            
            // Then
            assertEquals(10, deceleration);
        }
        
        @Test
        @DisplayName("101km/hでは減速効率が下がる")
        void shouldReduceEfficiencyAt101() {
            // When
            int deceleration = VehicleService.calculateDeceleration(101, 1.0);
            
            // Then
            assertEquals(8, deceleration);
        }
        
        @Test
        @DisplayName("速度0でも計算できる")
        void shouldCalculateAtZeroSpeed() {
            // When
            int deceleration = VehicleService.calculateDeceleration(0, 1.0);
            
            // Then
            assertEquals(10, deceleration);
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
