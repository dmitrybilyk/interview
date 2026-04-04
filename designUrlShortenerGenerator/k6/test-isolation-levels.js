import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 50,
    iterations: 500, // We want 500 total increments
};

export default function () {
    // Replace 'TESTCODE' with a code already in your DB
    const res = http.post('http://localhost:8082/api/test/increment/TESTCODE');

    check(res, {
        'is status 200': (r) => r.status === 200,
        'is status 500 (Conflict)': (r) => r.status === 500,
    });
}