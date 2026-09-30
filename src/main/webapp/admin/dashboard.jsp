<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<title>FloodPath | Admin Control Center</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="app-page" data-context="${pageContext.request.contextPath}">
<jsp:include page="/WEB-INF/nav.jsp">
<jsp:param name="active" value="dashboard"/>
</jsp:include>

<main class="app-wrap">
<div class="eyebrow admin-eyebrow">OPERATIONS CENTER</div>
<h1 class="page-title">FloodPath Control Center</h1>
<p class="lead">Monitor reports, road conditions and simulated rainfall from one place.</p>

<section class="stat-row">
<div class="stat-box">
<span>ALL REPORTS</span>
<b>${reports.size()}</b>
</div>
<div class="stat-box">
<span>AWAITING REVIEW</span>
<b>${pendingCount}</b>
</div>
<div class="stat-box">
<span>VERIFIED</span>
<b>${verifiedCount}</b>
</div>
<div class="stat-box">
<span>LIVE REPORTS</span>
<b>${liveCount}</b>
</div>
<div class="stat-box">
<span>AFFECTED ROADS</span>
<b>${affectedCount}</b>
</div>
<div class="stat-box">
<span>CLOSED ROADS</span>
<b>${closedCount}</b>
</div>
</section>

<section class="quick-grid admin-cards">
<a class="dashboard-card" href="${pageContext.request.contextPath}/admin/reports">
<span class="card-icon">✓</span>
<b>Review Reports</b>
<p>Verify or reject community flood reports and inspect photos.</p>
</a>
<a class="dashboard-card" href="${pageContext.request.contextPath}/admin/roads">
<span class="card-icon">⌁</span>
<b>Road Monitor</b>
<p>View the live condition of every road in the network.</p>
</a>
<a class="dashboard-card" href="${pageContext.request.contextPath}/admin/simulation">
<span class="card-icon">☁</span>
<b>Rainfall Simulation</b>
<p>Change rainfall and water levels for the project demonstration.</p>
</a>
</section>

<section class="network-panel">
<div class="section-head">
<div>
<div class="eyebrow">NETWORK OVERVIEW</div>
<h2>Live road map</h2>
</div>
<a class="text-link" href="${pageContext.request.contextPath}/admin/roads">View road monitor →</a>
</div>

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