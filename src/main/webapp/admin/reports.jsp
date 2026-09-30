<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>FloodPath | Report Review</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body class="app-page" data-context="${pageContext.request.contextPath}">
    <jsp:include page="/WEB-INF/nav.jsp">
        <jsp:param name="active" value="reports"/>
    </jsp:include>

    <main class="app-wrap">
        <div class="eyebrow admin-eyebrow">REPORT REVIEW</div>
        <h1 class="page-title">Community flood reports</h1>
        <p class="lead">Each report is stored in MySQL with a 2-hour live window. Admins can verify, reject, or close a report early.</p>

        <section class="network-panel">
            <c:choose>
                <c:when test="${empty reports}">
                    <p class="muted">No flood reports have been submitted.</p>
                </c:when>
                <c:otherwise>
                    <div class="table-scroll">
                        <table>
                            <thead>
                                <tr>
                                    <th>Report</th>
                                    <th>Photo</th>
                                    <th>Reported by</th>
                                    <th>Road</th>
                                    <th>Water</th>
                                    <th>Condition</th>
                                    <th>Start</th>
                                    <th>End</th>
                                    <th>Status</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="r" items="${reports}">
                                    <tr>
                                        <td>#${r.id}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${r.hasImage}">
                                                    <a href="${pageContext.request.contextPath}/api/image?f=${r.imageFile}" target="_blank">
                                                        <img class="thumb" src="${pageContext.request.contextPath}/api/image?f=${r.imageFile}" alt="Report photo">
                                                    </a>
                                                </c:when>
                                                <c:otherwise>—</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td><c:out value="${r.userName}"/></td>
                                        <td><b><c:out value="${r.roadName}"/></b></td>
                                        <td>${r.waterLevelCm} cm</td>
                                        <td><c:out value="${r.roadCondition}"/> · ${r.severity}/4</td>
                                        <td>${r.startsAt}</td>
                                        <td>${r.endsAt}</td>
                                        <td>
                                            <span class="status-pill ${r.status}">${r.status}</span>
                                            <c:if test="${r.live}">
                                                <div class="live-badge">LIVE</div>
                                            </c:if>
                                        </td>
                                        <td>
                                            <form class="action-row" method="post" action="${pageContext.request.contextPath}/admin/action">
                                                <input type="hidden" name="id" value="${r.id}">
                                                <button class="mini ok" name="action" value="verify">Verify</button>
                                                <button class="mini bad" name="action" value="reject">Reject</button>
                                                <c:if test="${r.live}">
                                                    <button class="mini" name="action" value="close">Close</button>
                                                </c:if>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </section>
    </main>
</body>
</html>