<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>FloodPath | Road Monitor</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="app-page" data-context="${pageContext.request.contextPath}">
    <jsp:include page="/WEB-INF/nav.jsp">
        <jsp:param name="active" value="roads"/>
    </jsp:include>

    <main class="app-wrap">
        <div class="eyebrow admin-eyebrow">ROAD MONITOR</div>
        <h1 class="page-title">Current road conditions</h1>
        <p class="lead">A clearer view of how water is affecting each connection in the demo network.</p>

        <section class="road-status-grid admin-road-grid">
            <c:forEach var="rd" items="${roads}">
                <article class="road-status-card">
                    <div>
                        <b><c:out value="${rd.name}"/></b>
                        <span>${rd.start} ↔ ${rd.end}</span>
                    </div>
                    <span class="condition-label S${rd.severity}">
                        <c:choose>
                            <c:when test="${rd.severity == 0}">CLEAR</c:when>
                            <c:when test="${rd.severity == 1}">WATCH</c:when>
                            <c:when test="${rd.severity == 2}">WATERLOGGED</c:when>
                            <c:when test="${rd.severity == 3}">SEVERE</c:when>
                            <c:otherwise>CLOSED</c:otherwise>
                        </c:choose>
                    </span>
                    <div class="road-meta">
                        <span>Impact level ${rd.severity}/4</span>
                        <span>${rd.distanceKm} km link</span>
                    </div>
                </article>
            </c:forEach>
        </section>

        <section class="network-panel">
            <h2>Live map</h2>
            <svg class="admin-map" viewBox="0 0 900 420">
                <rect width="900" height="420" class="map-bg"/>
                <path class="river" d="M700 -20 C610 80 780 160 650 260 C560 330 690 400 590 440"/>
                <g class="road" data-road="1">
                    <path d="M80 90 L320 150"/>
                </g>
                <g class="road" data-road="2">
                    <path d="M320 150 L520 90"/>
                </g>
                <g class="road" data-road="3">
                    <path d="M80 90 L220 320"/>
                </g>
                <g class="road" data-road="4">
                    <path d="M220 320 L480 350"/>
                </g>
                <g class="road" data-road="5">
                    <path d="M480 350 L520 90"/>
                </g>
                <g class="road" data-road="6">
                    <path d="M320 150 L480 350"/>
                </g>
                <g class="road" data-road="7">
                    <path d="M520 90 L780 140"/>
                </g>
                <g class="road" data-road="8">
                    <path d="M480 350 L780 140"/>
                </g>
            </svg>
        </section>
    </main>

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>
    <script>
        window.addEventListener('load',()=>{loadRoads();connectSocket();});
    </script>
</body>
</html>