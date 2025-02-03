// import React, { useState, useEffect } from "react";
// import ForumApi from "../../api/ForumApi"; // ForumApi 모듈 가져오기

// function ForumCategories() {
//   const [categories, setCategories] = useState([]); // 카테고리 상태 변수 초기화

//   useEffect(() => {
//     const fetchCategories = async () => {
//       try {
//         // ForumApi에서 카테고리 데이터를 가져오는 함수 호출
//         const response = await ForumApi.fetchCategories();
//         console.log("Fetched Categories in ForumCategories:", response);
//         setCategories(response); // 가져온 카테고리 데이터를 상태에 저장
//       } catch (error) {
//         console.error("포럼 카테고리 가져오기 실패:", error); // 에러 로그 출력
//       }
//     };
//     fetchCategories(); // 데이터 가져오기 실행
//   }, []); // 컴포넌트가 처음 렌더링될 때 한 번만 실행

//   return (
//     <div>
//       <h1>Forum Categories</h1> {/* 포럼 카테고리 제목 */}
//       <ul>
//         {categories.map((category) => (
//           <li key={category.id}>
//             <a href={`/forum/category/${category.id}`}>{category.title}</a>{" "}
//             {/* 카테고리 제목을 링크로 출력 */}
//           </li>
//         ))}
//       </ul>
//     </div>
//   );
// }

// export default ForumCategories; // ForumCategories 컴포넌트 내보내기
