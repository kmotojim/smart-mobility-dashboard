/**
 * Smart Mobility Dashboard - JavaScript
 */

class Dashboard {
    constructor() {
        this.state = {
            speed: 0,
            gear: 'P',
            engineWarning: false,
            seatbeltWarning: true,
            speedWarning: false
        };
        
        this.canvas = document.getElementById('speedometer');
        this.ctx = this.canvas.getContext('2d');
        
        this.init();
    }
    
    init() {
        this.bindEvents();
        this.fetchState();
        this.drawSpeedometer();
        
        // 定期的に状態を取得
        setInterval(() => this.fetchState(), 1000);
    }
    
    bindEvents() {
        // 加速ボタン
        document.getElementById('btn-accelerate').addEventListener('click', () => {
            this.postAction('/api/vehicle/accelerate');
        });
        
        // 減速ボタン
        document.getElementById('btn-decelerate').addEventListener('click', () => {
            this.postAction('/api/vehicle/decelerate');
        });
        
        // ギアボタン
        document.querySelectorAll('.gear-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const gear = e.target.dataset.gear;
                this.postAction(`/api/vehicle/gear/${gear}`);
            });
        });
        
        // シートベルトトグル
        document.getElementById('seatbelt-toggle').addEventListener('change', (e) => {
            this.postAction(`/api/vehicle/seatbelt/${e.target.checked}`);
        });
        
        // エンジン異常トグル
        document.getElementById('engine-error-toggle').addEventListener('change', (e) => {
            this.postAction(`/api/vehicle/engine-error/${e.target.checked}`);
        });
        
        // リセットボタン
        document.getElementById('btn-reset').addEventListener('click', () => {
            this.postAction('/api/vehicle/reset');
            document.getElementById('seatbelt-toggle').checked = false;
            document.getElementById('engine-error-toggle').checked = false;
        });
    }
    
    async fetchState() {
        try {
            const response = await fetch('/api/vehicle/state');
            if (response.ok) {
                this.state = await response.json();
                this.updateUI();
            }
        } catch (error) {
            console.error('Failed to fetch state:', error);
        }
    }
    
    async postAction(endpoint) {
        try {
            const response = await fetch(endpoint, { method: 'POST' });
            if (response.ok) {
                this.state = await response.json();
                this.updateUI();
            }
        } catch (error) {
            console.error('Failed to post action:', error);
        }
    }
    
    updateUI() {
        // 速度表示
        document.getElementById('speed-value').textContent = this.state.speed;
        
        // スピードメーター再描画
        this.drawSpeedometer();
        
        // ギアインジケーター
        document.querySelectorAll('.gear-item').forEach(item => {
            item.classList.toggle('active', item.dataset.gear === this.state.gear);
        });
        
        // ギアボタン
        document.querySelectorAll('.gear-btn').forEach(btn => {
            btn.classList.toggle('active', btn.dataset.gear === this.state.gear);
            // 速度が0以外の場合はギア変更不可
            btn.disabled = this.state.speed > 0;
        });
        
        // 警告灯
        document.getElementById('engine-warning').classList.toggle('active', this.state.engineWarning);
        document.getElementById('seatbelt-warning').classList.toggle('active', this.state.seatbeltWarning);
        document.getElementById('speed-warning').classList.toggle('active', this.state.speedWarning);
        
        // シートベルト状態
        document.getElementById('seatbelt-toggle').checked = !this.state.seatbeltWarning;
        document.getElementById('seatbelt-status').textContent = 
            this.state.seatbeltWarning ? '未装着' : '装着済';
        
        // エンジン異常状態
        document.getElementById('engine-error-toggle').checked = this.state.engineWarning;
    }
    
    drawSpeedometer() {
        const ctx = this.ctx;
        const centerX = this.canvas.width / 2;
        const centerY = this.canvas.height / 2;
        const radius = 130;
        
        // キャンバスクリア
        ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
        
        // 背景円
        ctx.beginPath();
        ctx.arc(centerX, centerY, radius, 0, Math.PI * 2);
        ctx.fillStyle = '#1a242e';
        ctx.fill();
        
        // 外枠
        ctx.beginPath();
        ctx.arc(centerX, centerY, radius, 0, Math.PI * 2);
        ctx.strokeStyle = '#2a3844';
        ctx.lineWidth = 4;
        ctx.stroke();
        
        // 速度ゲージ（背景）
        const startAngle = Math.PI * 0.75;
        const endAngle = Math.PI * 2.25;
        
        ctx.beginPath();
        ctx.arc(centerX, centerY, radius - 15, startAngle, endAngle);
        ctx.strokeStyle = '#2a3844';
        ctx.lineWidth = 20;
        ctx.lineCap = 'round';
        ctx.stroke();
        
        // 速度ゲージ（値）
        const speedRatio = this.state.speed / 180;
        const speedAngle = startAngle + (endAngle - startAngle) * speedRatio;
        
        // グラデーション色（速度に応じて変化）
        let gaugeColor = '#00b8d4'; // 通常
        if (this.state.speed > 120) {
            gaugeColor = '#ff5252'; // 危険
        } else if (this.state.speed > 80) {
            gaugeColor = '#ff9100'; // 注意
        }
        
        if (this.state.speed > 0) {
            ctx.beginPath();
            ctx.arc(centerX, centerY, radius - 15, startAngle, speedAngle);
            ctx.strokeStyle = gaugeColor;
            ctx.lineWidth = 20;
            ctx.lineCap = 'round';
            ctx.stroke();
            
            // グロー効果
            ctx.shadowColor = gaugeColor;
            ctx.shadowBlur = 15;
            ctx.stroke();
            ctx.shadowBlur = 0;
        }
        
        // 目盛り
        for (let i = 0; i <= 180; i += 20) {
            const ratio = i / 180;
            const angle = startAngle + (endAngle - startAngle) * ratio;
            
            const innerRadius = radius - 40;
            const outerRadius = radius - 45;
            
            const x1 = centerX + Math.cos(angle) * innerRadius;
            const y1 = centerY + Math.sin(angle) * innerRadius;
            const x2 = centerX + Math.cos(angle) * outerRadius;
            const y2 = centerY + Math.sin(angle) * outerRadius;
            
            ctx.beginPath();
            ctx.moveTo(x1, y1);
            ctx.lineTo(x2, y2);
            ctx.strokeStyle = '#8a9199';
            ctx.lineWidth = 2;
            ctx.stroke();
            
            // 数字
            const textRadius = radius - 55;
            const textX = centerX + Math.cos(angle) * textRadius;
            const textY = centerY + Math.sin(angle) * textRadius;
            
            ctx.font = '12px sans-serif';
            ctx.fillStyle = '#8a9199';
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText(i.toString(), textX, textY);
        }
    }
}

// DOMContentLoaded後に初期化
document.addEventListener('DOMContentLoaded', () => {
    new Dashboard();
});
