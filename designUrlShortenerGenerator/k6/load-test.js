import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        exact_requests: {
            executor: 'shared-iterations',
            vus: 50,              // 50 concurrent threads
            iterations: 20000,    // EXACTLY 20,000 total requests
            maxDuration: '2m',    // Safety timeout
        },
    },
};

export default function () {
    const url = 'http://localhost:8082/api/shorten'; // Adjust to your actual endpoint

    // 2. Dynamic Payload: prevents potential optimizations/caching
    const payload = JSON.stringify({
        url: `https://example.com/page/${Math.floor(Math.random() * 1000000)}`
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    // 3. The Request
    const res = http.post(url, payload, params);

    // 4. Validation: Make sure the server is actually responding 200 OK
    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    // A tiny sleep (10ms) allows the OS to switch threads more frequently,
    // which actually INCREASES the chance of a race condition.
    sleep(0.01);
}