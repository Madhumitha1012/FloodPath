<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>FloodPath | Every journey. A safer way.</title>
    <link rel="stylesheet" href="assets/css/style.css">
</head>

<body class="landing" data-context="${pageContext.request.contextPath}">

    <header class="landing-nav">

        <a class="brand" href="index.jsp">
            <span>FLOOD</span>PATH
        </a>

        <nav>
            <a href="#how">How it works</a>
            <a href="#features">Features</a>

            <c:choose>

                <c:when test="${not empty sessionScope.user}">
                    <a class="btn outline" href="dashboard">
                        Dashboard
                    </a>
                </c:when>

                <c:otherwise>
                    <a href="login">Login</a>
                    <a class="btn" href="register">Get Started</a>
                </c:otherwise>

            </c:choose>
        </nav>

    </header>

    <main class="hero-new">

        <div class="hero-content">

            <div class="eyebrow">
                REAL-TIME WATERLOGGING & SAFE ROUTES
            </div>

            <h1>
                Every journey.<br>
                <span>A safer way.</span>
            </h1>

            <p>
                FloodPath turns local flood reports into live road intelligence,
                helping people understand changing water conditions and choose a safer route.
            </p>

            <div class="hero-actions">

                <c:choose>

                    <c:when test="${not empty sessionScope.user}">
                        <a class="btn" href="dashboard">
                            Open Dashboard
                        </a>
                    </c:when>

                    <c:otherwise>
                        <a class="btn" href="register">
                            Start your journey
                        </a>

                        <a class="btn outline" href="login">
                            Sign in
                        </a>
                    </c:otherwise>

                </c:choose>

            </div>

            <div class="hero-note">
                <span>●</span>
                Community reports · Live road status · Flood-aware routing
            </div>

        </div>

        <div class="hero-visual">

            <div class="glow"></div>

            <div class="map-card">

                <div class="map-label">
                    FLOODPATH LIVE NETWORK
                </div>

                <svg viewBox="0 0 650 430">

                    <path
                        class="map-water"
                        d="M500 0 C440 80 570 120 470 220 C400 300 500 350 430 440"
                    />

                    <g class="map-road">
                        <path d="M70 80 L250 145 L420 90 L570 160"/>
                        <path d="M70 80 L180 300 L360 350 L570 160"/>
                        <path d="M250 145 L360 350"/>
                        <path d="M420 90 L360 350"/>
                    </g>

                    <g class="map-route">
                        <path d="M70 80 L250 145 L360 350 L570 160"/>
                    </g>

                    <g class="map-node">
                        <circle cx="70" cy="80" r="10"/>
                        <circle cx="250" cy="145" r="10"/>
                        <circle cx="180" cy="300" r="10"/>
                        <circle cx="360" cy="350" r="10"/>
                        <circle cx="420" cy="90" r="10"/>
                        <circle cx="570" cy="160" r="10"/>
                    </g>

                </svg>

                <div class="map-chip chip-one">
                    ● LIVE
                </div>

                <div class="map-chip chip-two">
                    Safer route
                </div>

            </div>

        </div>

    </main>

    <section id="how" class="story-new">

        <div>
            <b>01</b>
            <h2>See what is changing.</h2>
            <p>
                Water levels, road condition and community reports create a clearer picture of local waterlogging.
            </p>
        </div>

        <div>
            <b>02</b>
            <h2>Share what you see.</h2>
            <p>
                A user can report a road with water depth, severity, notes and an optional photo.
            </p>
        </div>

        <div>
            <b>03</b>
            <h2>Adapt the journey.</h2>
            <p>
                The routing engine increases the cost of affected roads and avoids roads marked closed.
            </p>
        </div>

    </section>

    <section id="features" class="feature-strip">

        <div>
            <strong>LIVE UPDATES</strong>
            <span>WebSocket road changes</span>
        </div>

        <div>
            <strong>FLOOD-AWARE</strong>
            <span>Dynamic Dijkstra routing</span>
        </div>

        <div>
            <strong>COMMUNITY DATA</strong>
            <span>Reports with photo evidence</span>
        </div>

        <div>
            <strong>ADMIN CONTROL</strong>
            <span>Rainfall simulation</span>
        </div>

    </section>

    <footer>
        FloodPath · Web Technologies Project · Tomcat 9 · Java 26
    </footer>

    <script src="assets/js/app.js"></script>

</body>
</html>