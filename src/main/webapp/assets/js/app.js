const CTX = (document.body && document.body.dataset.context) || '';
let roadsCache = [];

async function loadRoads() {
    try {
        const r = await fetch(`${CTX}/api/floods`);
        const data = await r.json();
        roadsCache = data.roads || [];
        renderRoads();
        updateStats();
    } catch (e) {
        console.error(e);
    }
}

function labelFor(sev) {
    return ['CLEAR', 'WATCH', 'WATERLOGGED', 'SEVERE', 'CLOSED'][Number(sev) || 0] || 'CLEAR';
}

function renderRoads() {
    roadsCache.forEach(r => {
        document.querySelectorAll(`.road[data-road="${r.id}"]`).forEach(el => {
            el.className = `road severity-${r.severity}`;
            el.title = `${r.name}: ${labelFor(r.severity)}`;
        });
    });

    const grid = document.getElementById('roadStatusGrid');

    if (grid) {
        grid.innerHTML = roadsCache.map(r => `
            <article class="road-status-card">
                <div>
                    <b>${esc(r.name)}</b>
                    <span>${esc(r.start)} ↔ ${esc(r.end)}</span>
                </div>
                <span class="condition-label S${r.severity}">
                    ${labelFor(r.severity)}
                </span>
                <div class="road-meta">
                    <span>Impact level ${r.severity}/4</span>
                    <span>Live network</span>
                </div>
            </article>
        `).join('');
    }
}

function updateStats() {
    const affected = roadsCache.filter(r => r.severity > 0).length;
    const closed = roadsCache.filter(r => r.status === 'CLOSED').length;
    const incident = roadsCache.filter(r => r.severity >= 2).length;

    ['affectedCount', 'closedCount', 'incidentCount'].forEach((id, i) => {
        const e = document.getElementById(id);

        if (e) {
            e.textContent = [affected, closed, incident][i];
        }
    });
}

async function findRoute() {
    const from = document.getElementById('from')?.value;
    const to = document.getElementById('to')?.value;

    if (!from || !to) {
        return;
    }

    const res = await fetch(
        `${CTX}/api/route?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`
    );

    const data = await res.json();
    const box = document.getElementById('routeResult');
    const layer = document.getElementById('routeLayer');

    if (layer) {
        layer.innerHTML = '';
    }

    if (!data.found) {
        if (box) {
            box.innerHTML = '<b>No route available.</b><br>All known paths are currently closed.';
        }

        return;
    }

    const risk = data.roads.some(r => r.severity >= 3)
        ? 'High'
        : data.roads.some(r => r.severity > 0)
            ? 'Moderate'
            : 'Low';

    if (box) {
        box.innerHTML = `
            <b>Route found</b><br>
            Distance: ${data.distance.toFixed(2)} km<br>
            Current flood risk: ${risk}<br>
            <small>${data.roads.map(r => esc(r.name)).join(' → ')}</small>
        `;
    }

    drawDemoRoute(data.roads);
}

function drawDemoRoute(roads) {
    const layer = document.getElementById('routeLayer');

    if (!layer) {
        return;
    }

    const points = {
        A: [80, 120],
        B: [320, 180],
        C: [520, 120],
        D: [220, 390],
        E: [480, 420],
        F: [780, 170]
    };

    let d = '';

    roads.forEach((r, i) => {
        if (i === 0) {
            d += `M ${points[r.start][0]} ${points[r.start][1]} `;
        }

        d += `L ${points[r.end][0]} ${points[r.end][1]} `;
    });

    const p = document.createElementNS('http://www.w3.org/2000/svg', 'path');

    p.setAttribute('d', d);
    p.setAttribute('class', 'route-line');

    layer.appendChild(p);
}

const MAX_IMAGE_BYTES = 5 * 1024 * 1024;
const IMAGE_TYPES = [
    'image/jpeg',
    'image/png',
    'image/gif',
    'image/webp'
];

function previewImage(input) {
    const file = input.files && input.files[0];
    const wrap = document.getElementById('imagePreview');
    const img = document.getElementById('previewImg');
    const box = document.getElementById('reportMessage');

    if (!file) {
        if (wrap) {
            wrap.hidden = true;
        }

        return;
    }

    if (!IMAGE_TYPES.includes(file.type)) {
        box.textContent = 'Choose a JPG, PNG, GIF or WEBP image.';
        clearImage();
        return;
    }

    if (file.size > MAX_IMAGE_BYTES) {
        box.textContent = 'Image is larger than 5 MB.';
        clearImage();
        return;
    }

    box.textContent = '';

    if (img.src) {
        URL.revokeObjectURL(img.src);
    }

    img.src = URL.createObjectURL(file);
    wrap.hidden = false;
}

function clearImage() {
    const input = document.getElementById('image');
    const wrap = document.getElementById('imagePreview');
    const img = document.getElementById('previewImg');

    if (input) {
        input.value = '';
    }

    if (img && img.src) {
        URL.revokeObjectURL(img.src);
        img.removeAttribute('src');
    }

    if (wrap) {
        wrap.hidden = true;
    }
}

async function submitReport() {
    const box = document.getElementById('reportMessage');
    const btn = document.getElementById('submitBtn');
    const form = new FormData();

    form.append('roadId', document.getElementById('roadId').value);
    form.append('waterLevel', document.getElementById('waterLevel').value);
    form.append('severity', document.getElementById('severity').value);
    form.append('condition', document.getElementById('condition').value);
    form.append('description', document.getElementById('description').value);

    const file = document.getElementById('image')?.files[0];

    if (file) {
        form.append('image', file);
    }

    btn.disabled = true;
    box.textContent = 'Submitting…';

    try {
        const res = await fetch(`${CTX}/api/report`, {
            method: 'POST',
            body: form
        });

        if (res.status === 401) {
            location.href = `${CTX}/login`;
            return;
        }

        const data = await res.json().catch(() => ({}));

        if (res.ok && data.ok) {
            box.textContent = `Report submitted successfully. ID: ${data.reportId}${data.hasImage ? ' · Photo attached' : ''}. Live users will be notified.`;

            document.getElementById('description').value = '';
            clearImage();
        } else {
            box.textContent = data.error || 'Could not submit the report.';
        }
    } catch (e) {
        box.textContent = 'Network error. Please try again.';
    } finally {
        btn.disabled = false;
    }
}

async function simulateFlood() {
    const roadId = document.getElementById('simRoad')?.value;
    const water = document.getElementById('water').value;
    const rain = document.getElementById('rain').value;

    const params = new URLSearchParams({
        roadId,
        waterLevel: water,
        rainIntensity: rain
    });

    const box = document.getElementById('simMessage');

    try {
        const res = await fetch(`${CTX}/api/simulate`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params
        });

        const data = await res.json().catch(() => ({}));

        if (res.ok && data.ok) {
            box.textContent = `${data.status} · Severity ${data.severity}/4 · Water ${water} cm · Road updated live`;
            await loadRoads();
        } else {
            box.textContent = data.error || 'Simulation failed.';
        }
    } catch (e) {
        box.textContent = 'Network error. Please try again.';
    }
}

function connectSocket() {
    const dot = document.getElementById('socketDot');
    const text = document.getElementById('socketText');

    if (!window.WebSocket || !dot) {
        return;
    }

    const protocol = location.protocol === 'https:' ? 'wss' : 'ws';
    const ws = new WebSocket(`${protocol}://${location.host}${CTX}/ws/flood`);

    ws.onopen = () => {
        dot.classList.add('live');

        if (text) {
            text.textContent = 'Live updates connected';
        }
    };

    ws.onclose = () => {
        dot.classList.remove('live');

        if (text) {
            text.textContent = 'Live updates disconnected';
        }
    };

    ws.onmessage = async e => {
        try {
            const data = JSON.parse(e.data);
            const lu = document.getElementById('lastUpdate');

            if (lu) {
                lu.textContent = 'Updated just now';
            }

            await loadRoads();

            if (
                data.type === 'ROAD_UPDATED' ||
                data.type === 'SIMULATION_UPDATED' ||
                data.type === 'REPORT_REVIEWED'
            ) {
                const box = document.getElementById('routeResult');

                if (box) {
                    box.innerHTML = '<b>Live road update received.</b><br>Recalculate the route to use the latest conditions.';
                }
            }
        } catch (err) {
            console.error(err);
        }
    };
}

function esc(s) {
    return String(s ?? '').replace(
        /[&<>"']/g,
        c => ({
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        }[c])
    );
}