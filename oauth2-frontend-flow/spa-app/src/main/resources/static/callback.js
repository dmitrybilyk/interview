let lastTokens = null;

function onCallbackLoaded() {
    const params = new URLSearchParams(window.location.search);
    const code = params.get('code');
    const state = params.get('state');
    const expectedState = sessionStorage.getItem('pkce_state');
    const stateOk = state && state === expectedState;

    logStep(
        '2. Keycloak редіректнув браузер сюди з authorization code',
        '(браузер сам прийшов на GET /callback.html?code=...&state=... — це фінал кроку 3 з BFF, тільки тут це кінцева точка SPA, а не сервера)',
        `code=${code}\nstate=${state}  ${stateOk ? '(співпав ✅)' : '(!!! НЕ співпадає)'}\n${window.location.search}`
    );

    document.getElementById('code-value').textContent = code || '(немає — щось пішло не так)';
    window.__code = code;
}

async function exchangeCode() {
    const code = window.__code;
    const verifier = sessionStorage.getItem('pkce_code_verifier');

    const body = new URLSearchParams({
        grant_type: 'authorization_code',
        client_id: CONFIG.clientId,
        code: code,
        redirect_uri: CONFIG.redirectUri,
        code_verifier: verifier,
    });

    const response = await fetch(CONFIG.tokenEndpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: body.toString(),
    });
    const rawText = await response.text();

    logStep(
        '3. Браузер сам (fetch, БЕЗ client_secret!) обміняв code на токени',
        `POST ${CONFIG.tokenEndpoint}\n${body.toString()}`,
        rawText
    );

    lastTokens = JSON.parse(rawText);
    document.getElementById('call-api-btn').disabled = false;
}

async function callApi() {
    const response = await fetch(`${CONFIG.resourceServerUrl}/api/protected-data`, {
        headers: { Authorization: `Bearer ${lastTokens.access_token}` },
    });
    const rawText = await response.text();

    logStep(
        '4. Браузер напряму викликав Resource Server з Authorization: Bearer',
        `GET ${CONFIG.resourceServerUrl}/api/protected-data\nAuthorization: Bearer ${lastTokens.access_token.slice(0, 20)}...(masked)`,
        rawText
    );
}
