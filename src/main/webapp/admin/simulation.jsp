<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>FloodPath | Rainfall Simulation</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="app-page" data-context="${pageContext.request.contextPath}">
    <jsp:include page="/WEB-INF/nav.jsp">
        <jsp:param name="active" value="simulation"/>
    </jsp:include>

    <main class="simulation-layout">
        <section class="form-card">
            <div class="eyebrow admin-eyebrow">DEMO CONTROL</div>
            <h1>Rainfall simulation</h1>
            <p class="muted">Select one road and simulate its water depth. The flood engine converts the depth into a road status and broadcasts the change live.</p>

            <label>Road to simulate</label>
            <select id="simRoad" class="field">
                <option value="1">Lake View Road · A ↔ B</option>
                <option value="2">Market Road · B ↔ C</option>
                <option value="3">Canal Road · A ↔ D</option>
                <option value="4" selected>Ring Road · D ↔ E</option>
                <option value="5">Station Road · E ↔ C</option>
                <option value="6">Bridge Road · B ↔ E</option>
                <option value="7">Temple Road · C ↔ F</option>
                <option value="8">School Road · E ↔ F</option>
            </select>

            <label>Rain intensity <strong id="rainValue">80%</strong></label>
            <input type="range" id="rain" min="0" max="100" value="80" oninput="rainValue.textContent=this.value+'%'"/>

            <label>Water depth <strong id="waterValue">80 cm</strong></label>
            <input type="range" id="water" min="0" max="100" value="80" oninput="waterValue.textContent=this.value+' cm'"/>

            <button class="btn full" onclick="simulateFlood()">Apply live conditions</button>
            <div id="simMessage" class="result"></div>
        </section>

        <section class="network-panel">
            <div class="eyebrow">ROAD STATUS RULES</div>
            <h2>Water depth → flood condition</h2>

            <div class="threshold-list">
                <div>
                    <b>0–10 cm</b>
                    <span>CLEAR</span>
                </div>
                <div>
                    <b>11–25 cm</b>
                    <span>WATCH</span>
                </div>
                <div>
                    <b>26–50 cm</b>
                    <span>WATERLOGGED</span>
                </div>
                <div>
                    <b>51–79 cm</b>
                    <span>SEVERE</span>
                </div>
                <div>
                    <b>80+ cm</b>
                    <span>CLOSED</span>
                </div>
            </div>

            <p class="muted small">Example: choose <b>Ring Road D ↔ E</b> and set <b>80 cm</b>. It becomes <b>Severity 4/4 · CLOSED</b>, and the route engine will avoid that road.</p>

            <div class="eyebrow">LIVE RESPONSE</div>
            <h2>What changes?</h2>

            <div class="timeline-steps">
                <div>
                    <b>01</b>
                    <span>Rain increases</span>
                </div>
                <div>
                    <b>02</b>
                    <span>Water depth rises</span>
                </div>
                <div>
                    <b>03</b>
                    <span>Road severity changes</span>
                </div>
                <div>
                    <b>04</b>
                    <span>WebSocket broadcasts</span>
                </div>
                <div>
                    <b>05</b>
                    <span>Routes recalculate</span>
                </div>
            </div>

            <div class="live-note">
                <span id="socketDot" class="socket-dot"></span>
                <span id="socketText">Connecting to live updates…</span>
            </div>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
    <script>
        window.addEventListener('load',()=>{loadRoads();connectSocket();});
    </script>
</body>
</html>