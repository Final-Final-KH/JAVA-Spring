import React, { useState, useEffect, useRef } from "react";
import imgLogo1 from "../images/RefreshButton.png";
import imgLogo2 from "../images/DeveloperMark.jpg";
import imgLogo3 from "../images/PictureButton.png";

const generatePosts = (page) => {
  const posts = [];
  for (let i = 1; i <= 10; i++) {
    posts.push({
      id: (page - 1) * 10 + i,
      title: `게시글 ${(page - 1) * 10 + i}`,
      content: `여기는 게시글 ${(page - 1) * 10 + i}의 내용입니다.`,
      author: {
        name: `User ${(page - 1) * 10 + i}`,
        role: "직책 예시",
        image: imgLogo2,
      },
      createdAt: new Date().toLocaleString(), // 초 단위까지 포함된 생성일
    });
  }
  return posts;
};

function Feed() {
  useEffect(() => {
    // 페이지 로드 시 body의 배경 색상을 설정
    document.body.style.backgroundColor = "#f5f6f7";
  });
  const [posts, setPosts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [page, setPage] = useState(1); // 페이지 상태
  const [newFeed, setNewFeed] = useState(""); // 새 피드 입력 값
  const [image, setImage] = useState(null); // 업로드된 이미지
  const observer = useRef(); // IntersectionObserver 참조
  const [refreshing, setRefreshing] = useState(false); // 새로고침 상태 관리

  const [friendList, setFriendList] = useState([
    { id: 1, name: "홍길동", isFriend: false, requestSent: false },
    { id: 2, name: "김철수", isFriend: false, requestSent: false },
    { id: 3, name: "이영희", isFriend: false, requestSent: false },
  ]);

  const handleSendRequest = (id) => {
    setFriendList((prevList) =>
      prevList.map((friend) =>
        friend.id === id ? { ...friend, requestSent: true } : friend
      )
    );
  };

  const handleCancelRequest = (id) => {
    setFriendList((prevList) =>
      prevList.map((friend) =>
        friend.id === id ? { ...friend, requestSent: false } : friend
      )
    );
  };

  // 게시글 데이터 가져오는 함수
  const fetchPosts = () => {
    setLoading(true);
    const newPosts = generatePosts(page); // 더미 데이터 생성
    setPosts((prevPosts) => [...prevPosts, ...newPosts]); // 이전 데이터에 새 데이터 추가
    setLoading(false);
  };

  useEffect(() => {
    fetchPosts();
  }, [page]);

  const lastPostElementRef = (node) => {
    if (loading) return;

    if (observer.current) observer.current.disconnect();
    observer.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting) {
        setPage((prevPage) => prevPage + 1);
      }
    });

    if (node) observer.current.observe(node);
  };

  // 새 피드 작성 함수(초단위)
  const handleCreateFeed = () => {
    if (newFeed.trim() || image) {
      const newPost = {
        id: Date.now(),
        title: "새 게시글",
        content: newFeed,
        image: image,
        author: {
          name: "새로운 사용자",
          role: "직책 예시",
          image: imgLogo2,
        },
        createdAt: new Date().toLocaleString(), // 초 단위까지 포함된 생성일
      };
      setPosts([newPost, ...posts]);
      setNewFeed("");
      setImage(null);
    }
  };

  // 새로고침 버튼 클릭 시 새로운 피드 로드(초단위)
  const handleRefreshFeeds = () => {
    setRefreshing(true); // 새로고침 상태 활성화
    setPage(1); // 페이지를 1로 설정하여 처음부터 새로운 피드를 로드
    const newPost = {
      id: Date.now(),
      title: "새 게시글",
      content: "새로운 게시글이 추가되었습니다!",
      author: {
        name: "새로운 사용자",
        role: "직책 예시",
        image: imgLogo2,
      },
      createdAt: new Date().toLocaleString(),
    };
    setPosts([newPost, ...posts]); // 새 게시글을 맨 위에 추가

    // 0.3초 후 새로고침 상태를 false로 설정하여 애니메이션을 종료
    setTimeout(() => setRefreshing(false), 300); // 빠른 회전 효과
  };

  // 이미지 업로드 핸들러
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setImage(reader.result); // 이미지 데이터 저장
      };
      reader.readAsDataURL(file); // 이미지 파일을 base64 형식으로 변환
    }
  };

  return (
    <div className="layout-container">
      {/* 왼쪽 섹션 */}
      <div className="profile-section">
        <img src={imgLogo2} alt="프로필 이미지" className="profile-image" />
        <h2>User Name</h2>
        <p>user@email.com</p>
        <p>User Age</p>
        <p>User Location</p>
      </div>

      {/* 가운데 섹션 */}
      <div className="feed-container">
        {/* Create a Feed 고정 영역 */}
        <div className="create-feed">
          <div className="textarea-container">
            <textarea
              value={newFeed}
              onChange={(e) => setNewFeed(e.target.value)}
              placeholder="새 피드를 작성하세요..."
              rows="3"
            />
            {/* 이미지 업로드 버튼 */}
            <input
              id="image-upload"
              type="file"
              accept="image/*"
              onChange={handleImageChange}
              style={{ display: "none" }}
            />
            <label htmlFor="image-upload" className="upload-icon-label">
              <img
                src={imgLogo3}
                alt="이미지 업로드"
                className="upload-icon"
                style={{ width: "40px", height: "40px", cursor: "pointer" }}
              />
            </label>
          </div>
          {image && (
            <img src={image} alt="업로드된 이미지" className="uploaded-image" />
          )}
          <button onClick={handleCreateFeed}>피드 작성</button>
        </div>

        {/* 새로고침 버튼 */}
        <button className="refresh-button" onClick={handleRefreshFeeds}>
          <img
            src={imgLogo1}
            alt="새로고침"
            className={`refresh-icon ${refreshing ? "refreshing" : ""}`}
            style={{ width: "50px", height: "50px" }}
          />
        </button>

        {/* 게시글 목록 */}
        <div className="posts">
          {posts.map((post, index) => (
            <div
              key={post.id}
              ref={index === posts.length - 1 ? lastPostElementRef : null}
              className="post"
            >
              {/* 게시글 상단 (회원 정보) */}
              <div className="post-header">
                <img
                  src={post.author.image}
                  alt="회원 이미지"
                  className="author-image"
                />
                <div className="author-details">
                  <span className="author-name">{post.author.name}</span>
                  <span className="author-role">{post.author.role}</span>
                  <span className="post-date">{post.createdAt}</span>
                </div>
              </div>
              <h2>{post.title}</h2>
              <p>{post.content}</p>
              {/* 이미지 표시 */}
              {post.image && (
                <img
                  src={post.image}
                  alt="게시글 이미지"
                  className="post-image"
                />
              )}
            </div>
          ))}
        </div>
        {loading && <p className="loading-text">로딩 중...</p>}
      </div>

      {/* 오른쪽 섹션 */}
      <div className="friends-section">
        <h2>친구 추천</h2>
        <ul className="friend-list">
          {friendList.map((friend) => (
            <li key={friend.id} className="friend-item">
              <div className="friend-info">
                <img
                  src={imgLogo2}
                  alt="친구 이미지"
                  className="friend-image"
                />
                <div className="friend-details">
                  <span>{friend.name}</span>
                  <span className="friend-role">미친구</span>
                </div>
              </div>

              {/* 친구 요청 버튼과 메시지 버튼 */}
              <div className="friend-actions">
                {!friend.isFriend && !friend.requestSent ? (
                  <button
                    className="friend-request-button"
                    onClick={() => handleSendRequest(friend.id)}
                  >
                    친구 요청
                  </button>
                ) : friend.requestSent ? (
                  <button
                    className="friend-request-button"
                    onClick={() => handleCancelRequest(friend.id)}
                  >
                    요청 취소
                  </button>
                ) : (
                  <button className="friend-button disabled" disabled>
                    친구
                  </button>
                )}
                <button className="message-button">메시지</button>
              </div>
            </li>
          ))}
        </ul>
      </div>

      {/* 스타일 */}
      <style jsx>{`
        .layout-container {
          margin-top: 70px;
          display: grid;
          grid-template-columns: 1fr 2fr 1fr;
          gap: 20px;
          padding: 20px;
          background-color: #f5f6f7;
          min-height: 100vh;
          font-family: Arial, sans-serif;
        }

        .profile-section,
        .friends-section {
          position: sticky;
          top: 90px; /* 네비게이션 바 높이만큼 내려오게 설정 */
          align-self: start;
          max-height: calc(
            100vh - 90px
          ); /* 뷰포트 높이에서 네비게이션 바 높이 제외 */
          overflow-y: auto;
          background: white;
          padding: 20px;
          border-radius: 8px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .profile-section {
          text-align: center;
        }

        .profile-image {
          width: 150px;
          height: 150px;
          border-radius: 50%;
          margin-bottom: 15px;
        }

        .feed-container {
          background: white;
          padding: 20px;
          border-radius: 8px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .create-feed textarea {
          width: 100%;
          padding: 10px;
          border: 1px solid #ccc;
          border-radius: 5px;
          resize: none;
          margin-bottom: 10px;
        }

        .create-feed button {
          width: 100%;
          padding: 10px;
          background-color: #4caf50;
          color: white;
          border: none;
          border-radius: 5px;
          cursor: pointer;
        }

        .create-feed button:hover {
          background-color: #45a049;
        }

        .textarea-container {
          position: relative;
          width: 100%;
        }

        textarea {
          width: 100%;
          padding: 10px;
          padding-right: 50px; /* 오른쪽 공간 확보 */
          border: 1px solid #ccc;
          border-radius: 5px;
          resize: none;
          margin-bottom: 10px;
          box-sizing: border-box;
        }

        .upload-icon-label {
          position: absolute;
          right: 20px; /* 텍스트 박스 내부 오른쪽 여백 */
          top: 50%;
          transform: translateY(-50%);
          cursor: pointer;
        }

        .upload-icon {
          width: 30px;
          height: 30px;
          transition: transform 0.2s ease;
        }

        .upload-icon:hover {
          transform: scale(1.1); /* 아이콘 확대 효과 */
        }

        .posts {
          margin-top: 20px;
        }

        .post {
          background: white;
          padding: 15px;
          margin-bottom: 10px;
          border: 1px solid #ccc;
          border-radius: 8px;
        }

        .post-header {
          display: flex;
          align-items: center;
          margin-bottom: 15px;
        }

        .author-image {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          margin-right: 10px;
        }

        .author-details {
          display: flex;
          flex-direction: column;
        }

        .author-name {
          font-weight: bold;
        }

        .author-role {
          color: #888;
        }

        .post-date {
          font-size: 12px;
          color: #888;
        }

        .uploaded-image,
        .post-image {
          display: block;
          max-width: 100%; /* 부모 요소에 맞게 조정 */
          max-height: 300px; /* 최대 높이 제한 */
          width: auto;
          height: auto;
          margin-top: 10px;
          border-radius: 8px;
        }

        .loading-text {
          text-align: center;
          color: #888;
        }

        .refresh-button {
          background: transparent;
          border: none;
          cursor: pointer;
          margin-top: 20px;
          display: block;
          margin-left: auto;
          margin-right: auto;
          transition: transform 0.2s ease; /* 부드러운 애니메이션 추가 */
        }

        .refresh-icon {
          transition: transform 0.2s ease; /* 회전 애니메이션 */
        }

        .refreshing {
          transform: rotate(360deg); /* 회전 애니메이션 */
        }
        .refresh-button:hover {
          transform: translateY(-2px); /* 마우스를 올리면 버튼이 위로 떠 보임 */
        }

        .friend-list {
          list-style: none;
          padding: 0;
        }

        .friend-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 15px;
          padding: 10px;
          border-radius: 8px;
          background-color: #f9f9f9;
          box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
        }

        .friend-info {
          display: flex;
          align-items: center;
        }

        .friend-image {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          margin-right: 10px;
        }

        .friend-details {
          display: flex;
          flex-direction: column;
        }

        .friend-role {
          font-size: 12px;
          color: #888;
        }

        .friend-actions button {
          padding: 5px 10px;
          margin: 5px;
          font-size: 14px;
          cursor: pointer;
        }

        .friend-request-button {
          background-color: #4caf50;
          color: white;
          border: none;
          border-radius: 5px;
        }

        .friend-request-button:hover {
          background-color: #45a049;
        }

        .friend-button.disabled {
          background-color: #ccc;
          color: white;
        }

        .message-button {
          background-color: #f0f0f0;
          color: #555;
          border: none;
          border-radius: 5px;
        }

        .message-button:hover {
          background-color: #e0e0e0;
        }
      `}</style>
    </div>
  );
}

export default Feed;
