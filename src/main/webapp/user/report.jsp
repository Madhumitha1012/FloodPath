<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>

<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>FloodPath | Report Flood</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>

<body class="app-page" data-context="${pageContext.request.contextPath}">

    <jsp:include page="/WEB-INF/nav.jsp">
        <jsp:param name="active" value="report"/>
    </jsp:include>

    <main class="form-wrap">

        <section class="form-card wide">

            <div class="eyebrow">COMMUNITY REPORT</div>

            <h1>Report waterlogging</h1>

            <p class="muted">
                Your report helps other travellers understand the road ahead.
            </p>

            <div class="form-grid">

                <div>
                    <label>Road</label>

                    <select id="roadId">
                        <option value="1">Lake View Road</option>
                        <option value="2">Market Road</option>
                        <option value="3">Canal Road</option>
                        <option value="4">Ring Road</option>
                        <option value="5">Station Road</option>
                        <option value="6">Bridge Road</option>
                        <option value="7">Temple Road</option>
                        <option value="8">School Road</option>
                    </select>
                </div>

                <div>
                    <label>Water depth</label>

                    <select id="waterLevel">
                        <option value="10">10 cm · Low</option>
                        <option value="30">30 cm · Medium</option>
                        <option value="60">60 cm · High</option>
                        <option value="100">100 cm · Very high</option>
                    </select>
                </div>

                <div>
                    <label>Severity</label>

                    <select id="severity">
                        <option value="1">1 · Minor</option>
                        <option value="2">2 · Moderate</option>
                        <option value="3">3 · Severe</option>
                        <option value="4">4 · Closed</option>
                    </select>
                </div>

                <div>
                    <label>Road condition</label>

                    <select id="condition">
                        <option>PASSABLE</option>
                        <option>DIFFICULT</option>
                        <option>BLOCKED</option>
                    </select>
                </div>

            </div>

            <label>What did you observe?</label>

            <textarea
                id="description"
                rows="5"
                placeholder="Example: water is covering the left lane and vehicles are moving slowly."
            ></textarea>

            <label>
                Photo evidence
                <small>Optional · JPG, PNG, GIF or WEBP · max 5 MB</small>
            </label>

            <input
                class="field"
                type="file"
                id="image"
                accept="image/jpeg,image/png,image/gif,image/webp"
                onchange="previewImage(this)"
            >

            <div id="imagePreview" class="img-preview" hidden>
                <img id="previewImg" alt="Selected flood report photo">

                <button
                    type="button"
                    class="mini bad"
                    onclick="clearImage()"
                >
                    Remove
                </button>
            </div>

            <button
                class="btn full"
                id="submitBtn"
                onclick="submitReport()"
            >
                Submit flood report
            </button>

            <div id="reportMessage" class="result"></div>

        </section>

    </main>

    <script src="${pageContext.request.contextPath}/assets/js/app.js"></script>

</body>
</html>