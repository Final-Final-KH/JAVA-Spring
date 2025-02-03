import { ModalStyle, ModalButton } from "./ModalStyle2";
import PaymentPage from "./TossPay";

const Modal = (props) => {
  const { close, open, type } = props;

  return (
    <ModalStyle>
      <div
        className={open ? "modal fade show d-block" : "modal fade"}
        tabindex="-1"
        aria-labelledby="exampleModalLabel"
        aria-hidden="true"
      >
        {open && (
          <section>
            <div className="modal-dialog">
              <div className="modal-content">
                <header className="modal-header">
                  <h2 className="modal-title">구독 결제</h2>
                  <button
                    type="button"
                    className="btn-close"
                    aria-label="Close"
                    onClick={() => close()}
                  />
                </header>
                <main className="modal-body">
                  {/* 여기에 내용물이 비어있는 상태로 둡니다 */}
                  <PaymentPage></PaymentPage>
                </main>
                <footer className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => close()}
                  >
                    취소
                  </button>
                </footer>
              </div>
            </div>
          </section>
        )}
      </div>
    </ModalStyle>
  );
};
export default Modal;
