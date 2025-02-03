import React from "react";
import axios from "axios";
import { Button3 } from "../course/style/style2";

const KakaoPayButton = () => {
  const handleClick = async () => {
    try {
      // 카카오페이에 보낼 데이터
      const data = {
        name: "상품명", // 카카오페이에 보낼 대표 상품명
        totalPrice: 20000, // 총 결제금액
      };

      // 결제 준비를 위한 POST 요청
      const response = await axios.post(
        "http://localhost:8111/order/pay/ready",
        data,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      // 카카오페이 결제 팝업으로 리다이렉트
      if (response.data.next_redirect_pc_url) {
        window.location.href = response.data.next_redirect_pc_url;
      } else {
        console.error("카카오페이 결제 URL을 받을 수 없습니다.");
      }
    } catch (error) {
      console.error("카카오페이 결제 준비 중 오류 발생:", error);
    }
  };

  return (
    <div style={{ textAlign: "center" }}>
      <Button3 onClick={handleClick}>카카오페이 결제</Button3>
    </div>
  );
};

export default KakaoPayButton;
