async function beginLogin() {
    const verifier = generateCodeVerifier();
    const challenge = await generateCodeChallenge(verifier);
    const state = generateState();

    // sessionStorage survives the full-page redirect to Keycloak and back.
    sessionStorage.setItem('pkce_code_verifier', verifier);
    sessionStorage.setItem('pkce_state', state);

    const url = new URL(CONFIG.authorizationEndpoint);
    url.searchParams.set('client_id', CONFIG.clientId);
    url.searchParams.set('redirect_uri', CONFIG.redirectUri);
    url.searchParams.set('response_type', 'code');
    url.searchParams.set('scope', 'openid profile email');
    url.searchParams.set('state', state);
    url.searchParams.set('code_challenge', challenge);
    url.searchParams.set('code_challenge_method', 'S256');

    logStep(
        '1. JS у браузері згенерував PKCE-пару і Authorization URL',
        `code_verifier (секрет, лишається тільки в цьому браузері):\n${verifier}\n\ncode_challenge (S256, публічний):\n${challenge}`,
        url.toString()
    );

    document.getElementById('go-link').href = url.toString();
    document.getElementById('go-link').style.display = 'inline-block';
}
