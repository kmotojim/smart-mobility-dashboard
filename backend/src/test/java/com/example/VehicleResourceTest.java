package com.example;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * VehicleResource REST APIの統合テスト
 */
@QuarkusTest
class VehicleResourceTest {
    
    @BeforeEach
    void setUp() {
        // 各テスト前に状態をリセット
        given()
            .when()
            .post("/api/vehicle/reset")
            .then()
            .statusCode(200);
    }
    
    @Test
    @DisplayName("GET /api/vehicle/state - 初期状態を取得できる")
    void shouldGetInitialState() {
        given()
            .when()
            .get("/api/vehicle/state")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("speed", equalTo(0))
            .body("gear", equalTo("P"))
            .body("engineWarning", equalTo(false))
            .body("seatbeltWarning", equalTo(true));
    }
    
    @Test
    @DisplayName("POST /api/vehicle/accelerate - Dレンジで加速できる")
    void shouldAccelerateInDriveGear() {
        // Dレンジに変更
        given()
            .when()
            .post("/api/vehicle/gear/D")
            .then()
            .statusCode(200);
        
        // 加速
        given()
            .when()
            .post("/api/vehicle/accelerate")
            .then()
            .statusCode(200)
            .body("speed", equalTo(10));
    }
    
    @Test
    @DisplayName("POST /api/vehicle/decelerate - 減速できる")
    void shouldDecelerate() {
        // Dレンジに変更して加速
        given().when().post("/api/vehicle/gear/D");
        given().when().post("/api/vehicle/accelerate");
        given().when().post("/api/vehicle/accelerate");
        
        // 減速
        given()
            .when()
            .post("/api/vehicle/decelerate")
            .then()
            .statusCode(200)
            .body("speed", equalTo(10));
    }
    
    @Test
    @DisplayName("POST /api/vehicle/gear/{gear} - 有効なギアに変更できる")
    void shouldChangeGear() {
        given()
            .when()
            .post("/api/vehicle/gear/D")
            .then()
            .statusCode(200)
            .body("gear", equalTo("D"));
    }
    
    @Test
    @DisplayName("POST /api/vehicle/gear/{gear} - 無効なギアでエラーを返す")
    void shouldReturnErrorForInvalidGear() {
        given()
            .when()
            .post("/api/vehicle/gear/X")
            .then()
            .statusCode(400)
            .body("error", containsString("Invalid gear"));
    }
    
    @Test
    @DisplayName("POST /api/vehicle/seatbelt/{fastened} - シートベルト状態を変更できる")
    void shouldChangeSeatbeltStatus() {
        given()
            .when()
            .post("/api/vehicle/seatbelt/true")
            .then()
            .statusCode(200)
            .body("seatbeltWarning", equalTo(false));
    }
    
    @Test
    @DisplayName("POST /api/vehicle/engine-error/{hasError} - エンジン異常を発生できる")
    void shouldTriggerEngineError() {
        given()
            .when()
            .post("/api/vehicle/engine-error/true")
            .then()
            .statusCode(200)
            .body("engineWarning", equalTo(true));
    }
    
    @Test
    @DisplayName("POST /api/vehicle/reset - 状態をリセットできる")
    void shouldResetState() {
        // 状態を変更
        given().when().post("/api/vehicle/gear/D");
        given().when().post("/api/vehicle/accelerate");
        given().when().post("/api/vehicle/engine-error/true");
        
        // リセット
        given()
            .when()
            .post("/api/vehicle/reset")
            .then()
            .statusCode(200)
            .body("speed", equalTo(0))
            .body("gear", equalTo("P"))
            .body("engineWarning", equalTo(false));
    }
    
    @Test
    @DisplayName("速度超過で警告が発生する - E2E風テスト")
    void shouldTriggerSpeedWarningWhenExceeding120() {
        // Dレンジに変更
        given().when().post("/api/vehicle/gear/D");
        
        // 130km/hまで加速（13回）
        for (int i = 0; i < 13; i++) {
            given().when().post("/api/vehicle/accelerate");
        }
        
        // 状態確認
        given()
            .when()
            .get("/api/vehicle/state")
            .then()
            .statusCode(200)
            .body("speed", equalTo(130))
            .body("speedWarning", equalTo(true));
    }
}
