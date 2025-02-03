export const NAVER_CLIENT_ID = "m1ehrdynlqBavqqPCVab";
export const NAVER_REDIRECT_URI =
  "http://localhost:3000/login/oauth2/code/naver";
export const NAVER_AUTH_URL = (state) =>
  `https://nid.naver.com/oauth2.0/authorize?response_type=code&client_id=${NAVER_CLIENT_ID}&redirect_uri=${encodeURIComponent(
    NAVER_REDIRECT_URI
  )}&state=${state}`;
export const CLIENT_SECRET = "J3c36ztXGC";
