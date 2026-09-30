<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>FloodPath | My History</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="app-page" data-context="${pageContext.request.contextPath}">

    <jsp:include page="/WEB-INF/nav.jsp">
        <jsp:param name="active" value="history"/>
    </jsp:include>

    <main class="app-wrap">

        <div class="eyebrow">REPORT HISTORY</div>

        <h1 class="page-title">
            Your flood reports
        </h1>

        <section class="network-panel">

            <c:choose>

                <c:when test="${empty reports}">
                    <p class="muted">
                        No reports yet.
                        <a class="text-link" href="${pageContext.request.contextPath}/user/report.jsp">
                            Submit your first report →
                        </a>
                    </p>
                </c:when>

                <c:otherwise>

                    <div class="table-scroll">

                        <table>
                            <thead>
                                <tr>
                                    <th>Road</th>
                                    <th>Water</th>
                                    <th>Condition</th>
                                    <th>Photo</th>
                                    <th>Status</th>
                                    <th>Live start</th>
                                    <th>Live end</th>
                                </tr>
                            </thead>

                            <tbody>

                                <c:forEach var="r" items="${reports}">
                                    <tr>

                                        <td>
                                            <b>
                                                <c:out value="${r.roadName}"/>
                                            </b>
                                        </td>

                                        <td>
                                            ${r.waterLevelCm} cm
                                        </td>

                                        <td>
                                            <c:out value="${r.roadCondition}"/>
                                        </td>

                                        <td>
                                            <c:choose>

                                                <c:when test="${r.hasImage}">
                                                    <img
                                                        class="thumb"
                                                        src="${pageContext.request.contextPath}/api/image?f=${r.imageFile}"
                                                        alt="Flood report photo"
                                                    >
                                                </c:when>

                                                <c:otherwise>
                                                    —
                                                </c:otherwise>

                                            </c:choose>
                                        </td>

                                        <td>
                                            <span class="status-pill ${r.status}">
                                                ${r.status}
                                            </span>

                                            <c:if test="${r.live}">
                                                <div class="live-badge">
                                                    LIVE
                                                </div>
                                            </c:if>
                                        </td>

                                        <td>
                                            ${r.startsAt}
                                        </td>

                                        <td>
                                            ${r.endsAt}
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