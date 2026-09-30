<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header class="topbar">
  <a class="brand" href="${pageContext.request.contextPath}/index.jsp"><span>FLOOD</span>PATH</a>
  <nav class="topnav">
    <c:choose>
      <c:when test="${sessionScope.user.admin}">
        <a class="${param.active == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/dashboard">Overview</a>
        <a class="${param.active == 'reports' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/reports">Reports</a>
        <a class="${param.active == 'roads' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/roads">Roads</a>
        <a class="${param.active == 'simulation' ? 'active' : ''}" href="${pageContext.request.contextPath}/admin/simulation">Simulation</a>
      </c:when>
      <c:otherwise>
        <a class="${param.active == 'dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/user/dashboard">Overview</a>
        <a class="${param.active == 'report' ? 'active' : ''}" href="${pageContext.request.contextPath}/user/report.jsp">Report Flood</a>
        <a class="${param.active == 'route' ? 'active' : ''}" href="${pageContext.request.contextPath}/user/route.jsp">Safe Route</a>
        <a class="${param.active == 'history' ? 'active' : ''}" href="${pageContext.request.contextPath}/user/history">My History</a>
      </c:otherwise>
    </c:choose>
  </nav>
  <div class="identity"><span class="identity-name"><c:out value="${sessionScope.user.name}"/></span><span class="identity-role"><c:choose><c:when test="${sessionScope.user.admin}">ADMIN</c:when><c:otherwise>USER</c:otherwise></c:choose></span><a class="logout-btn" href="${pageContext.request.contextPath}/logout">Logout</a></div>
</header>
