<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>FloodPath | Safe Route</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="app-page" data-context="${pageContext.request.contextPath}">

    <jsp:include page="/WEB-INF/nav.jsp">
        <jsp:param name="active" value="route"/>
    </jsp:include>

    <main class="route-layout">

        <aside class="route-controls">

            <div class="eyebrow">FLOOD-AWARE ROUTING</div>

            <h1>Find a safer route</h1>

            <p class="muted">
                Affected roads carry a higher route cost. Closed roads are excluded.
            </p>

            <label>Start</label>

            <select id="from">
                <option value="A">Ukkadam · A</option>
                <option value="B">Market · B</option>
                <option value="D">Lake Area · D</option>
                <option value="E">Station · E</option>
            </select>

            <label>Destination</label>

            <select id="to">
                <option value="F">Gandhipuram · F</option>
                <option value="C">City Center · C</option>
                <option value="E">Station · E</option>
            </select>

            <button class="btn full" onclick="findRoute()">
                Calculate route
            </button>

            <div id="routeResult" class="result">
                Choose a start and destination.
            </div>

            <div class="live-note">
                <span id="socketDot" class="socket-dot"></span>
                <span id="socketText">Connecting to live updates…</span>
            </div>

        </aside>

        <section class="map-shell">

            <div class="map-toolbar">
                <b>FLOODPATH NETWORK MAP</b>
                <span id="lastUpdate">Waiting for live data</span>
            </div>

            <svg id="cityMap" viewBox="0 0 900 560">

                <rect
                    width="900"
                    height="560"
                    class="map-bg"
                />

                <path
                    class="river"
                    d="M700 -20 C610 80 780 160 650 260 C560 330 690 400 590 590"
                />

                <g class="road" data-road="1">
                    <path d="M80 120 L320 180"/>
                </g>

                <g class="road" data-road="2">
                    <path d="M320 180 L520 120"/>
                </g>

                <g class="road" data-road="3">
                    <path d="M80 120 L220 390"/>
                </g>

                <g class="road" data-road="4">
                    <path d="M220 390 L480 420"/>
                </g>

                <g class="road" data-road="5">
                    <path d="M480 420 L520 120"/>
                </g>

                <g class="road" data-road="6">
                    <path d="M320 180 L480 420"/>
                </g>

                <g class="road" data-road="7">
                    <path d="M520 120 L780 170"/>
                </g>

                <g class="road" data-road="8">
                    <path d="M480 420 L780 170"/>
                </g>

                <circle
                    cx="80"
                    cy="120"
                    r="12"
                    class="node"
                />
                <text x="45" y="95">Ukkadam</text>

                <circle
                    cx="320"
                    cy="180"
                    r="12"
                    class="node"
                />
                <text x="285" y="155">Market</text>

                <circle
                    cx="220"
                    cy="390"
                    r="12"
                    class="node"
                />
                <text x="180" y="425">Lake Area</text>

                <circle
                    cx="480"
                    cy="420"
                    r="12"
                    class="node"
                />
                <text x="445" y="455">Station</text>

                <circle
                    cx="520"
                    cy="120"
                    r="12"
                    class="node"
                />
                <text x="490" y="95">Center</text>

                <circle
                    cx="780"
                    cy="170"
                    r="12"
                    class="node"
                />
                <text x="745" y="145">Gandhipuram</text>

                <g id="routeLayer"></g>

            </svg>

            <div class="legend">
                <span>
                    <i class="normal"></i>
                    Clear
                </span>

                <span>
                    <i class="minor"></i>
                    Watch
                </span>

                <span>
                    <i class="moderate"></i>
                    Waterlogged
                </span>

                <span>
                    <i class="severe"></i>
                    Severe
                </span>

                <span>
                    <i class="closed"></i>
                    Closed
                </span>
            </div>

        </section>

    </main>

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>

    <script>
        window.addEventListener('load', () => {
            loadRoads();
            connectSocket();
        });
    </script>

</body>
</html>