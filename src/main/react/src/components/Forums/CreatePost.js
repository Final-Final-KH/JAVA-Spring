import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getUserInfo } from "../../axios/AxiosInstanse"; // Fetch user info
import ForumApi from "../../api/ForumApi"; // For API actions
import { getDownloadURL, ref, uploadBytes } from "firebase/storage";
import { storage } from "../../utils/FirebaseConfig"; // Firebase 설정 가져오기
import {
  CreatePostContainer,
  CreatePostTitle,
  CreatePostForm,
  FormGroup,
  CreatePostButton,
} from "../../styles/CreatePostStyles"; // Styled Components

const CreatePost = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    title: "",
    content: "",
    categoryId: "",
  });
  const [categories, setCategories] = useState([]); // 카테고리 리스트 상태
  const [selectedFile, setSelectedFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [memberId, setMemberId] = useState(null); // Fetch member ID

  // Fetch user information and categories
  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch user information
        const userInfo = await getUserInfo();
        if (userInfo && userInfo.memberId) {
          setMemberId(userInfo.memberId);
        } else {
          alert("로그인이 필요합니다.");
          navigate("/login");
        }

        // Fetch categories
        const categoryData = await ForumApi.fetchCategories();
        setCategories(categoryData);
      } catch (error) {
        console.error("Error fetching data:", error);
        alert("데이터를 불러오는 중 오류가 발생했습니다.");
      }
    };

    fetchData();
  }, [navigate]);

  // 폼 데이터 변경 처리
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // 파일 선택 처리
  const handleFileChange = (e) => {
    setSelectedFile(e.target.files[0]);
  };

  // 게시글 생성 처리
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
          <label htmlFor="content">내용</label>
          <textarea
            id="content"
            name="content"
            value={formData.content}
            onChange={handleChange}
            required
          ></textarea>
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
        <FormGroup>
          <label htmlFor="file">파일 첨부 (선택 사항)</label>
          <input type="file" id="file" onChange={handleFileChange} />
        </FormGroup>
        <CreatePostButton type="submit" disabled={uploading}>
          {uploading ? "업로드 중..." : "게시글 생성"}
        </CreatePostButton>
      </CreatePostForm>
    </CreatePostContainer>
  );
};

export default CreatePost;
