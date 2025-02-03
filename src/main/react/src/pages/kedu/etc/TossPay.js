import React, { useEffect } from "react";
import { loadTossPayments, ANONYMOUS } from "@tosspayments/tosspayments-sdk";
import "./style.css";

const PaymentPage = () => {
  useEffect(() => {
    const amount = {
      currency: "KRW",
      value: 50000,
    };

    const main = async () => {
      try {
        const tossPayments = await loadTossPayments(
          "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm" // 여기에 실제 Toss Payments Client ID를 넣어야 합니다
        );

        const widgets = tossPayments.widgets({
          customerKey: ANONYMOUS,
        });

        // 결제 금액 설정
        await widgets.setAmount(amount);

        // 결제 UI 렌더링
        await Promise.all([
          widgets.renderPaymentMethods({
            selector: "#payment-method",
            variantKey: "DEFAULT",
          }),
          widgets.renderAgreement({
            selector: "#agreement",
            variantKey: "AGREEMENT",
          }),
        ]);

        // 결제 요청 버튼 클릭 이벤트 처리
        const paymentRequestButton = document.getElementById(
          "payment-request-button"
        );
        paymentRequestButton.addEventListener("click", async () => {
          try {
            await widgets.requestPayment({
              orderId: generateRandomString(),
              orderName: "토스 티셔츠 외 2건",
              successUrl: window.location.origin + "/sandbox/success",
              failUrl: window.location.origin + "/sandbox/fail",
              customerEmail: "customer123@gmail.com",
              customerName: "김토스",
              customerMobilePhone: "01012341234",
            });
          } catch (err) {
            console.error("결제 요청 오류:", err);
          }
        });
      } catch (err) {
        console.error("Toss Payments 로딩 오류:", err);
      }
    };

    main();

    // Cleanup (컴포넌트가 언마운트 될 때)
    return () => {
      const paymentRequestButton = document.getElementById(
        "payment-request-button"
      );
      if (paymentRequestButton) {
        paymentRequestButton.removeEventListener("click", () => {});
      }
    };
  }, []);

  const generateRandomString = () => window.btoa(Math.random()).slice(0, 20);

  return (
    <div>
      <div id="payment-method"></div>
      <div id="agreement"></div>
      <button id="payment-request-button">결제 요청</button>
    </div>
  );
};

export default PaymentPage;
