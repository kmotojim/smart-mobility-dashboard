package com.example.steps;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.ja.ならば;
import io.cucumber.java.ja.もし;
import io.cucumber.java.ja.前提;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ダッシュボード画面のCucumberステップ定義
 */
public class DashboardSteps {
    
    private static final String BASE_URL = System.getProperty("app.url", "http://localhost:8080");
    
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    
    @Before
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true));
        // ビューポートサイズを大きく設定して、全コントロールが表示されるようにする
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1920, 1080));
        page = context.newPage();
    }
    
    @After
    public void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
    
    // === Given ステップ ===
    
    @前提("ダッシュボードが表示されている")
    public void ダッシュボードが表示されている() {
        page.navigate(BASE_URL);
        page.waitForSelector("#speedometer");
    }
    
    @前提("状態がリセットされている")
    public void 状態がリセットされている() {
        page.click("#btn-reset");
        page.waitForTimeout(500); // 状態更新を待つ
    }
    
    @前提("現在の速度が {int} km\\/h である")
    public void 現在の速度がKmhである(int expectedSpeed) {
        String speed = page.textContent("#speed-value");
        assertEquals(String.valueOf(expectedSpeed), speed);
    }
    
    @前提("現在のギアが {string} である")
    public void 現在のギアがである(String gear) {
        if (!gear.equals("P")) {
            page.click(String.format(".gear-btn[data-gear='%s']", gear));
            page.waitForTimeout(300);
        }
        Locator activeGear = page.locator(".gear-item.active");
        assertEquals(gear, activeGear.getAttribute("data-gear"));
    }
    
    @前提("シートベルト警告灯が点灯している")
    public void シートベルト警告灯が点灯している() {
        Locator warning = page.locator("#seatbelt-warning");
        assertTrue(warning.getAttribute("class").contains("active"));
    }
    
    @前提("速度が {int} km\\/h に設定されている")
    public void 速度が設定されている(int speed) {
        // 指定された速度になるまで加速
        int times = speed / 10;
        for (int i = 0; i < times; i++) {
            page.click("#btn-accelerate");
            page.waitForTimeout(200);
        }
    }
    
    @前提("エンジン異常状態である")
    public void エンジン異常状態である() {
        Locator toggle = page.locator("#engine-error-toggle");
        if (!toggle.isChecked()) {
            // CSSカスタムトグルスイッチのため、親のラベルをクリック
            page.locator("label:has(#engine-error-toggle)").click();
        }
        page.waitForTimeout(300);
    }
    
    // === When ステップ ===
    
    @もし("加速ボタンを {int} 回押す")
    public void 加速ボタンを回押す(int times) {
        for (int i = 0; i < times; i++) {
            page.click("#btn-accelerate");
            page.waitForTimeout(200);
        }
    }
    
    @もし("減速ボタンを {int} 回押す")
    public void 減速ボタンを回押す(int times) {
        for (int i = 0; i < times; i++) {
            page.click("#btn-decelerate");
            page.waitForTimeout(200);
        }
    }
    
    @もし("ギアを {string} に変更する")
    public void ギアをに変更する(String gear) {
        page.click(String.format(".gear-btn[data-gear='%s']", gear));
        page.waitForTimeout(300);
    }
    
    @もし("ギアを {string} に変更しようとする")
    public void ギアをに変更しようとする(String gear) {
        // 速度が0以上の場合、ボタンはdisabledになっている可能性がある
        Locator gearBtn = page.locator(String.format(".gear-btn[data-gear='%s']", gear));
        gearBtn.click(new Locator.ClickOptions().setForce(true));
        page.waitForTimeout(300);
    }
    
    @もし("シートベルトを装着する")
    public void シートベルトを装着する() {
        Locator toggle = page.locator("#seatbelt-toggle");
        if (!toggle.isChecked()) {
            // CSSカスタムトグルスイッチのため、親のラベルをクリック
            page.locator("label:has(#seatbelt-toggle)").click();
        }
        page.waitForTimeout(300);
    }
    
    @もし("エンジン異常スイッチをONにする")
    public void エンジン異常スイッチをONにする() {
        Locator toggle = page.locator("#engine-error-toggle");
        if (!toggle.isChecked()) {
            // CSSカスタムトグルスイッチのため、親のラベルをクリック
            page.locator("label:has(#engine-error-toggle)").click();
        }
        page.waitForTimeout(300);
    }
    
    @もし("リセットボタンを押す")
    public void リセットボタンを押す() {
        page.click("#btn-reset");
        page.waitForTimeout(500);
    }
    
    // === Then ステップ ===
    
    @ならば("速度が {int} km\\/h になる")
    public void 速度がKmhになる(int expectedSpeed) {
        page.waitForTimeout(300);
        String speed = page.textContent("#speed-value");
        assertEquals(String.valueOf(expectedSpeed), speed);
    }
    
    @ならば("ギアインジケーターに {string} が表示される")
    public void ギアインジケーターにが表示される(String gear) {
        Locator activeGear = page.locator(".gear-item.active");
        assertEquals(gear, activeGear.getAttribute("data-gear"));
    }
    
    @ならば("速度警告灯が点灯する")
    public void 速度警告灯が点灯する() {
        Locator warning = page.locator("#speed-warning");
        assertTrue(warning.getAttribute("class").contains("active"),
                "速度警告灯が点灯していません");
    }
    
    @ならば("速度警告灯が消灯している")
    public void 速度警告灯が消灯している() {
        Locator warning = page.locator("#speed-warning");
        assertFalse(warning.getAttribute("class").contains("active"),
                "速度警告灯が点灯しています");
    }
    
    @ならば("エンジン警告灯が点灯する")
    public void エンジン警告灯が点灯する() {
        Locator warning = page.locator("#engine-warning");
        assertTrue(warning.getAttribute("class").contains("active"),
                "エンジン警告灯が点灯していません");
    }
    
    @ならば("エンジン警告灯が消灯している")
    public void エンジン警告灯が消灯している() {
        Locator warning = page.locator("#engine-warning");
        assertFalse(warning.getAttribute("class").contains("active"),
                "エンジン警告灯が点灯しています");
    }
    
    @ならば("シートベルト警告灯が消灯する")
    public void シートベルト警告灯が消灯する() {
        Locator warning = page.locator("#seatbelt-warning");
        assertFalse(warning.getAttribute("class").contains("active"),
                "シートベルト警告灯が点灯しています");
    }
}
