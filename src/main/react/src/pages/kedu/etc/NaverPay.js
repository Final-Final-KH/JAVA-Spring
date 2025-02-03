import React, { useEffect } from "react";
import { Button2 } from "../course/style/style2";

const NaverPayButton = () => {
  useEffect(() => {
    // 네이버페이 SDK 로드
    if (
      !document.querySelector(
        "script[src='https://nsp.pay.naver.com/sdk/js/naverpay.min.js']"
      )
    ) {
      const script = document.createElement("script");
      script.src = "https://nsp.pay.naver.com/sdk/js/naverpay.min.js";
      script.async = true;
      script.onload = () => {
        if (window.Naver && window.Naver.Pay) {
          // 네이버페이 SDK 초기화
          window.oPay = window.Naver.Pay.create({
            mode: "development", // 개발 모드 (실제 결제 시 production으로 변경)
            clientId: "HN3GGCMDdTgGUfl0kFCo", // clientId
            chainId: "bldVVUtqeEVoS21", // chainId
          });
        } else {
          console.error("네이버페이 SDK 로드에 실패했습니다.");
        }
      };
      document.body.appendChild(script);
    }

    return () => {
      // 컴포넌트가 언마운트 될 때 SDK 스크립트 제거
      const script = document.querySelector(
        "script[src='https://nsp.pay.naver.com/sdk/js/naverpay.min.js']"
      );
      if (script) {
        document.body.removeChild(script);
      }
    };
  }, []);

  const handleClick = () => {
    if (!window.oPay) {
      alert("네이버페이 SDK가 아직 로드되지 않았습니다.");
      return;
    }

    try {
      window.oPay.open({
        merchantPayKey: "20250123R4IOTu", // 결제 키
        productName: "상품명", // 상품명
        productCount: "1", // 상품 수량
        totalPayAmount: "1000", // 총 결제 금액
        taxScopeAmount: "1000", // 과세 금액
        taxExScopeAmount: "0", // 면세 금액
        returnUrl: "https://developers.pay.naver.com/user/sand-box/payment", // 결제 후 리다이렉트 URL
      });
    } catch (error) {
      console.error("네이버페이 결제 호출 중 오류 발생:", error);
      alert("결제 창을 열 수 없습니다. 다시 시도해주세요.");
    }
  };

  return (
    <div>
      <Button2
        id="naverPayBtn"
        value="네이버페이 결제 버튼"
        onClick={handleClick}
      >
        네이버 PAY
      </Button2>
    </div>
  );
};

export default NaverPayButton;
