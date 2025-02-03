import React, { useState, useEffect } from "react";
import styled from "styled-components";
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Bold from "@tiptap/extension-bold";
import Italic from "@tiptap/extension-italic";
import Underline from "@tiptap/extension-underline";
import Link from "@tiptap/extension-link";
import { ToastContainer, toast } from "react-toastify";

// ✅ 모달 스타일 정의
const ModalWrapper = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

const ModalContent = styled.div`
  background-color: #ffffff;
  border-radius: 10px;
  padding: 20px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
`;

const Toolbar = styled.div`
  display: flex;
  justify-content: center;
  margin-bottom: 10px;
  gap: 5px;

  button {
    padding: 5px 10px;
    font-size: 14px;
    border: 1px solid #ddd;
    background: white;
    cursor: pointer;

    &:hover {
      background: #f0f0f0;
    }
  }
`;

const LinkInputWrapper = styled.div`
  display: flex;
  gap: 5px;
  margin-top: 10px;

  input {
    flex: 1;
    padding: 5px;
    font-size: 14px;
    border: 1px solid #ddd;
  }

  button {
    padding: 5px 10px;
    font-size: 14px;
    background-color: #007bff;
    color: white;
    border: none;
    cursor: pointer;

    &:hover {
      background-color: #0056b3;
    }
  }
`;

const ModalActions = styled.div`
  margin-top: 20px;
  display: flex;
  justify-content: space-between;

  button {
    padding: 10px 20px;
    font-size: 14px;
    border-radius: 5px;
    cursor: pointer;

    &:nth-child(1) {
      background-color: #007bff;
      color: white;

      &:hover {
        background-color: #0056b3;
      }
    }

    &:nth-child(2) {
      background-color: #d9534f;
      color: white;

      &:hover {
        background-color: #c9302c;
      }
    }
  }
`;

const ConfirmationModal = ({
  isOpen,
  type,
  content,
  message,
  onConfirm,
  onCancel,
}) => {
  const [inputValue, setInputValue] = useState(""); // ✅ 댓글, 신고 사유 입력 필드
  const [linkInput, setLinkInput] = useState(""); // ✅ 링크 입력 필드
  const [isAddingLink, setIsAddingLink] = useState(false); // ✅ 링크 입력창 표시 여부

  // ✅ Tiptap 에디터 설정 (편집 및 링크 추가 가능)
  const editor = useEditor({
    extensions: [StarterKit, Bold, Italic, Underline, Link],
    content: "",
    onUpdate: ({ editor }) => {
      setInputValue(editor.getHTML());
    },
  });

  // ✅ 모달이 열릴 때 상태 초기화
  useEffect(() => {
    if (!isOpen) return;
    setIsAddingLink(false); // 링크 입력창 닫기
    setLinkInput(""); // 링크 입력 필드 초기화

    if (type === "editPostContent" || type === "editComment") {
      editor?.commands.setContent(content || "");
      setInputValue(content || "");
    } else if (
      type === "reportComment" ||
      type === "reportPost" ||
      type === "deletePost" ||
      type === "deleteComment"
    ) {
      editor?.commands.clearContent();
      setInputValue("");
    }
  }, [isOpen, type, content, editor]);

  // ✅ 메시지 매핑
  const messages = {
    editPostTitle: "게시글 제목 수정:",
    editPostContent: "게시글 내용 수정:",
    editComment: "댓글 수정:",
    reportComment: "이 댓글을 신고하시겠습니까?",
    reportPost: "이 게시글을 신고하시겠습니까?",
    deletePost: "게시글을 삭제하시겠습니까?",
    deleteComment: "댓글을 삭제하시겠습니까?",
    addLink: "링크를 입력하세요:", // ✅ 링크 추가 시 메시지 표시
  };

  const dynamicMessage = messages[type] || message || "진행 하시겠습니까?";

  if (!isOpen) return null;

  return (
    <ModalWrapper>
      <ModalContent>
        <h3>{dynamicMessage}</h3>

        {/* ✅ 링크 추가 시 별도 입력 필드 표시 */}
        {type === "addLink" && (
          <input
            type="text"
            value={linkInput}
            placeholder="https://example.com"
            onChange={(e) => setLinkInput(e.target.value)}
          />
        )}

        {/* ✅ 댓글 수정 및 게시글 수정 시 Tiptap 에디터 사용 */}
        {(type === "editPostContent" || type === "editComment") && (
          <>
            <Toolbar>
              <button onClick={() => editor.chain().focus().toggleBold().run()}>
                B
              </button>
              <button
                onClick={() => editor.chain().focus().toggleItalic().run()}
              >
                I
              </button>
              <button
                onClick={() => editor.chain().focus().toggleUnderline().run()}
              >
                U
              </button>
              <button onClick={() => setIsAddingLink(!isAddingLink)}>
                Link
              </button>
              <button onClick={() => editor.chain().focus().unsetLink().run()}>
                Unlink
              </button>
            </Toolbar>
            <EditorContent editor={editor} />
          </>
        )}

        {/* ✅ 링크 입력창 (댓글 추가 및 댓글 수정에서 사용) */}
        {isAddingLink && (
          <LinkInputWrapper>
            <input
              type="text"
              value={linkInput}
              placeholder="https://example.com"
              onChange={(e) => setLinkInput(e.target.value)}
            />
            <button
              onClick={() => {
                if (!linkInput.trim()) {
                  return toast.warning("URL을 입력해주세요.");
                }

                const formattedUrl =
                  linkInput.startsWith("http://") ||
                  linkInput.startsWith("https://")
                    ? linkInput
                    : `https://${linkInput}`;

                editor
                  .chain()
                  .focus()
                  .extendMarkRange("link")
                  .setLink({ href: formattedUrl })
                  .run();

                toast.success("링크가 추가되었습니다.");
                setIsAddingLink(false); // 입력창 닫기
                setLinkInput(""); // 입력 필드 초기화
              }}
            >
              확인
            </button>
          </LinkInputWrapper>
        )}

        {/* ✅ 확인 및 취소 버튼 */}
        <ModalActions>
          <button
            onClick={() => {
              if (type === "addLink") {
                if (!linkInput.trim()) {
                  return toast.warning("URL을 입력해주세요.");
                }
                const finalUrl =
                  linkInput.startsWith("http://") ||
                  linkInput.startsWith("https://")
                    ? linkInput.trim()
                    : `https://${linkInput.trim()}`;

                onConfirm(finalUrl);
              } else {
                onConfirm(inputValue.trim());
              }
            }}
          >
            Confirm
          </button>

          <button onClick={onCancel}>Cancel</button>
        </ModalActions>
      </ModalContent>
    </ModalWrapper>
  );
};

export default ConfirmationModal;
