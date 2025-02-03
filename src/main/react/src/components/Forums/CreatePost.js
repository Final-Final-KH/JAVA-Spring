import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getUserInfo } from "../../axios/AxiosInstanse";
import ForumApi from "../../api/ForumApi";
import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { storage } from "../../utils/FirebaseConfig";
import {
  CreatePostContainer,
  CreatePostTitle,
  CreatePostForm,
  FormGroup,
  CreatePostButton,
  EditorToolbar,
} from "../../styles/CreatePostStyles";

// 🔽 Tiptap Editor 관련 라이브러리
import { useEditor, EditorContent } from "@tiptap/react";
import StarterKit from "@tiptap/starter-kit";
import Bold from "@tiptap/extension-bold";
import Italic from "@tiptap/extension-italic";
import Underline from "@tiptap/extension-underline";
import Link from "@tiptap/extension-link";
import TextStyle from "@tiptap/extension-text-style";
import Blockquote from "@tiptap/extension-blockquote";

import ConfirmationModal from "./ConfirmationModal"; // 추가된 모달

const CreatePost = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: "",
    categoryId: "",
    content: "",
  });
  const [categories, setCategories] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [memberId, setMemberId] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [linkInput, setLinkInput] = useState("");

  // 📝 Tiptap Editor 설정
  const editor = useEditor({
    extensions: [
      StarterKit,
      Bold,
      Italic,
      Underline,
      Link.configure({ openOnClick: false }),
      TextStyle,
      Blockquote,
    ],
    content: "",
    onUpdate: ({ editor }) => {
      setFormData((prev) => ({ ...prev, content: editor.getHTML() }));
    },
  });

  /**
   * ✅ 사용자 정보 및 카테고리 리스트 불러오기
   */
  useEffect(() => {
    const fetchData = async () => {
      try {
        const userInfo = await getUserInfo();
        if (userInfo && userInfo.memberId) {
          setMemberId(userInfo.memberId);
        } else {
          alert("로그인이 필요합니다.");
          navigate("/login");
          return;
        }

        const categoryData = await ForumApi.fetchCategories();
        setCategories(categoryData);
      } catch (error) {
        console.error("데이터 로딩 오류:", error);
        alert("데이터를 불러오는 중 오류가 발생했습니다.");
      }
    };

    fetchData();
  }, [navigate]);

  /**
   * 🔄 폼 데이터 변경 처리
   */
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  /**
   * 📂 파일 선택 처리
   */
  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  /**
   * 📝 게시글 생성 처리
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setUploading(true);

    let fileUrl = null;

    try {
      if (!memberId) {
        alert("로그인이 필요합니다.");
        navigate("/login");
        return;
      }

      if (selectedFile) {
        const storageRef = ref(storage, `forum_files/${selectedFile.name}`);
        await uploadBytes(storageRef, selectedFile);
        fileUrl = await getDownloadURL(storageRef);
      }

      const postData = {
        ...formData,
        memberId,
        fileUrls: fileUrl ? [fileUrl] : [],
      };

      const response = await ForumApi.createPost(postData);

      alert("게시글이 성공적으로 생성되었습니다!");
      navigate(`/forum/post/${response.id}`);
    } catch (error) {
      console.error("게시글 생성 중 오류:", error);
      alert("게시글 생성에 실패했습니다.");
    } finally {
      setUploading(false);
    }
  };

  /**
   * 🔗 링크 추가 모달 열기
   */
  const openLinkModal = (e) => {
    e.preventDefault(); // 🚀 **이걸 추가하면 버튼 클릭 시 form이 제출되지 않음**
    setIsModalOpen(true);
  };

  /**
   * 🔗 링크 추가 확인 처리
   */
  const handleAddLink = (url) => {
    if (!url) return;
    editor.chain().focus().extendMarkRange("link").setLink({ href: url }).run();
    setIsModalOpen(false);
  };

  return (
    <CreatePostContainer>
      <CreatePostTitle>게시글 생성</CreatePostTitle>
      <CreatePostForm onSubmit={handleSubmit}>
        <FormGroup>
          <label htmlFor="title">제목</label>
          <input
            type="text"
            id="title"
            name="title"
            value={formData.title}
            onChange={handleChange}
            required
          />
        </FormGroup>

        <FormGroup>
          <label htmlFor="categoryId">카테고리</label>
          <select
            id="categoryId"
            name="categoryId"
            value={formData.categoryId}
            onChange={handleChange}
            required
          >
            <option value="">카테고리를 선택하세요</option>
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {category.title}
              </option>
            ))}
          </select>
        </FormGroup>

        {/* 📝 Tiptap 에디터 및 툴바 추가 */}
        <FormGroup>
          <label>내용</label>
          <EditorToolbar>
            <button
              type="button"
              onClick={() => editor.chain().focus().toggleBold().run()}
            >
              B
            </button>
            <button
              type="button"
              onClick={() => editor.chain().focus().toggleItalic().run()}
            >
              I
            </button>
            <button
              type="button"
              onClick={() => editor.chain().focus().toggleUnderline().run()}
            >
              U
            </button>
            <button type="button" onClick={openLinkModal}>
              Link
            </button>
            <button
              type="button"
              onClick={() => editor.chain().focus().unsetLink().run()}
            >
              Remove Link
            </button>
          </EditorToolbar>
          <EditorContent editor={editor} className="editor" />
        </FormGroup>

        <FormGroup>
          <label htmlFor="file">파일 첨부 (선택 사항)</label>
          <input type="file" id="file" onChange={handleFileChange} />
        </FormGroup>

        <CreatePostButton type="submit" disabled={uploading}>
          {uploading ? "업로드 중..." : "게시글 생성"}
        </CreatePostButton>
      </CreatePostForm>

      {/* 🔗 링크 추가 모달 */}
      <ConfirmationModal
        isOpen={isModalOpen}
        type="addLink"
        message="추가할 링크를 입력하세요:"
        onConfirm={handleAddLink}
        onCancel={() => setIsModalOpen(false)}
      />
    </CreatePostContainer>
  );
};

export default CreatePost;
