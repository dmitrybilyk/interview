function logStep(title, request, response) {
    const panel = document.getElementById('log');
    const entry = document.createElement('div');
    entry.className = 'trace-entry';
    const time = new Date().toISOString().slice(11, 23);
    entry.innerHTML = `
        <div class="trace-time">${time}</div>
        <strong>${title}</strong>
        <div><em>Request:</em></div>
        <pre>${escapeHtml(request)}</pre>
        <div><em>Response:</em></div>
        <pre>${escapeHtml(response)}</pre>
    `;
    panel.appendChild(entry);
    console.log(`[${time}] ${title}`, { request, response });
}

function escapeHtml(str) {
    const div = document.createElement('div');
    div.textContent = str;
    return div.innerHTML;
}
