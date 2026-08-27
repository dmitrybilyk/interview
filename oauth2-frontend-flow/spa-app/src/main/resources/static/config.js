// All the config a real public client would need — no secret anywhere, because
// a browser-based app cannot keep one safe.
const CONFIG = {
    keycloakBaseUrl: 'http://localhost:8281',
    realm: 'frontend-flow-realm',
    clientId: 'spa-client',
    redirectUri: window.location.origin + '/callback.html',
    resourceServerUrl: 'http://localhost:8289',
};

CONFIG.authorizationEndpoint = `${CONFIG.keycloakBaseUrl}/realms/${CONFIG.realm}/protocol/openid-connect/auth`;
CONFIG.tokenEndpoint = `${CONFIG.keycloakBaseUrl}/realms/${CONFIG.realm}/protocol/openid-connect/token`;
