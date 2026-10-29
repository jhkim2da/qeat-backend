// 손님이 QR로 메뉴판을 여는 요청(GET /api/public/tables/{token}) 부하 테스트
// 실행: k6 run -e RATE=300 loadtest/public-menu.js
import http from 'k6/http';
import { check } from 'k6';

const BASE = __ENV.BASE || 'http://localhost:9090';
const RATE = Number(__ENV.RATE || 300);      // 초당 요청 수
const DURATION = __ENV.DURATION || '60s';

export const options = {
  scenarios: {
    public_menu: {
      executor: 'constant-arrival-rate',
      rate: RATE,
      timeUnit: '1s',
      duration: DURATION,
      preAllocatedVUs: 100,
      maxVUs: 1000,
    },
  },
  summaryTrendStats: ['avg', 'p(50)', 'p(95)', 'p(99)', 'max'],
};

export default function () {
  const n = Math.floor(Math.random() * 100) + 1;
  const token = 'lt' + String(n).padStart(4, '0');
  const res = http.get(`${BASE}/api/public/tables/${token}`);
  check(res, { 'status 200': (r) => r.status === 200 });
}
