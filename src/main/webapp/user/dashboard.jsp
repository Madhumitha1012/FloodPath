<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>FloodPath | User Dashboard</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="app-page" data-context="${pageContext.request.contextPath}">

    <jsp:include page="/WEB-INF/nav.jsp">
        <jsp:param name="active" value="dashboard"/>
    </jsp:include>

    <main class="app-wrap">
        <div class="eyebrow">USER DASHBOARD</div>

        <h1 class="page-title">
            Welcome, <c:out value="${sessionScope.user.name}"/>
        </h1>

        <p class="lead">
            Stay informed before the road changes. Report what you see and plan around current water conditions.
        </p>

        <section class="quick-grid">
            <a class="dashboard-card" href="${pageContext.request.contextPath}/user/report.jsp">
                <span class="card-icon">+</span>
                <b>Report Flood</b>
                <p>Send water level, road condition and optional photo evidence.</p>
            </a>

            <a class="dashboard-card" href="${pageContext.request.contextPath}/user/route.jsp">
                <span class="card-icon">↗</span>
                <b>Safe Route</b>
                <p>Compare the current network and calculate a flood-aware route.</p>
            </a>

            <a class="dashboard-card" href="${pageContext.request.contextPath}/user/history">
                <span class="card-icon">◷</span>
                <b>My History</b>
                <p>Track submitted reports and their verification status.</p>
            </a>
        </section>

        <section class="network-panel">
            <div class="section-head">
                <div>
                    <div class="eyebrow">LIVE NETWORK</div>
                    <h2>Road conditions right now</h2>
                </div>

                <a class="text-link" href="${pageContext.request.contextPath}/user/route.jsp">
                    Open map →
                </a>
            </div>

            <div id="roadStatusGrid" class="road-status-grid">
            </div>

            <div class="live-note">
                <span id="socketDot" class="socket-dot"></span>
                <span id="socketText">Connecting to live updates…</span>
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