import React from "react";
import styled from "styled-components";

const ModalStyle = styled.div`
  .modal {
    display: none;
    position: fixed;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 99;
    background-color: rgba(0, 0, 0, 0.6);
  }

  .openModal {
    display: flex;
    align-items: center;
    justify-content: center;
    /* 팝업이 열릴때 스르륵 열리는 효과 */
    animation: modal-bg-show 0.8s;
  }

  section {
    width: 90%;
    max-width: 450px;
    margin: 0 auto;
    border-radius: 0.3rem;
    background-color: #fff;
    /* 팝업이 열릴때 스르륵 열리는 효과 */
    animation: modal-show 0.3s;
    overflow: hidden;
    position: relative; /* 닫기 버튼을 우측 상단에 위치시키기 위해 필요 */

    header {
      position: relative;
      padding: 16px 64px 16px 16px;
      background-color: #f1f1f1;
      font-weight: 700;
      button {
        position: absolute;
        top: 10px;
        right: 10px;
        width: 24px;
        height: 24px;
        font-size: 18px;
        font-weight: 700;
        text-align: center;
        color: #999;
        background-color: transparent;
        border: none;
        cursor: pointer;
        transition: color 0.3s;
      }

      button:hover {
        color: #333; /* hover 시 색상 변경 */
      }
    }

    main {
      padding: 16px;
      border-bottom: 1px solid #dee2e6;
      border-top: 1px solid #dee2e6;
    }

    footer {
      padding: 12px 16px;
      text-align: right;
      button {
        padding: 6px 12px;
        color: #fff;
        background-color: #6c757d;
        border-radius: 5px;
        font-size: 13px;
      }
    }
  }

  @keyframes modal-show {
    from {
      opacity: 0;
      margin-top: -50px;
    }
    to {
      opacity: 1;
      margin-top: 0;
    }
  }
  @keyframes modal-bg-show {
    from {
      opacity: 0;
    }
    to {
      opacity: 1;
    }
  }
`;

const Button = styled.button`
  outline: none;
  cursor: pointer;
  margin-right: 10px;
  border: 0;
  width: 60px;
`;

const Modal03 = (props) => {
  const { open, confirm, close, type, header, children } = props;

  return (
    <ModalStyle>
      <div className={open ? "openModal modal" : "modal"}>
        {open && (
          <section>
            <header>{header}</header>
            <main>{children}</main>
            <footer>
              {type && <Button onClick={confirm}>확인</Button>}
              {close && <Button onClick={close}>닫기</Button>}
            </footer>
          </section>
        )}
      </div>
    </ModalStyle>
  );
};

export default Modal03;
