<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>FloodPath | Create your FloodPath account</title>
    <link rel="stylesheet" href="assets/css/style.css">
</head>

<body class="auth-page" data-context="${pageContext.request.contextPath}">

    <a class="brand auth-brand" href="index.jsp">
        <span>FLOOD</span>PATH
    </a>

    <main class="auth-shell">

        <section class="auth-panel">

            <div class="eyebrow">FLOODPATH</div>

            <h1>Create your FloodPath account</h1>

            <p class="muted">
                Join the community and help keep local roads informed.
            </p>

            <form method="post" action="register">

                <label>Full name</label>

                <input
                    class="field"
                    name="name"
                    type="text"
                    value="${requestScope.name}"
                    required
                >

                <label>Email address</label>

                <input
                    class="field"
                    name="email"
                    type="email"
                    value="${requestScope.email}"
                    required
                >

                <label>Password</label>

                <input
                    class="field"
                    name="password"
                    type="password"
                    required
                >

                <label>Confirm password</label>

                <input
                    class="field"
                    name="confirm"
                    type="password"
                    required
                >

                <c:if test="${not empty error}">
                    <div class="alert error">
                        <c:out value="${error}"/>
                    </div>
                </c:if>

                <button class="btn full" type="submit">
                    Create account
                </button>

            </form>

            <p class="auth-foot">
                <a href="login">Back to sign in</a>
            </p>

        </section>

    </main>

</body>
</html>