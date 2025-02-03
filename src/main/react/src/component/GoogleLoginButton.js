// GoogleLoginButton.js
import React from "react";
import { GoogleOAuthProvider, GoogleLogin } from "@react-oauth/google";
import OAuth2Button from "../styles/OAuth2Button";

const GoogleLoginButton = ({ onSuccess, onError }) => {
  return (
    <GoogleOAuthProvider clientId="711497532760-gt04q7avm9nqsr0ocmjrbnap2vnk8r4i.apps.googleusercontent.com">
      <GoogleLogin
        onSuccess={onSuccess}
        onError={onError}
        theme="filled_blue"
        width="100%"
        render={(renderProps) => (
          <OAuth2Button
            onClick={renderProps.onClick}
            style={{ backgroundColor: "#4285F4" }} // 구글 색상
          >
            구글 로그인
          </OAuth2Button>
        )}
      />
    </GoogleOAuthProvider>
  );
};

export default GoogleLoginButton;
