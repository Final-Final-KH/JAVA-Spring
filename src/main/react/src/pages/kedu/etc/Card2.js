// import React from "react";
// import { Swiper, SwiperSlide } from "swiper/react";
// import "swiper/css"; // 기본 스타일 임포트
// import "swiper/css/navigation"; // 네비게이션 스타일 임포트
// import SwiperCore, { Navigation } from "swiper"; // Navigation 모듈을 불러오기

// // Swiper에 Navigation 모듈 추가
// SwiperCore.use([Navigation]);

// const Card2 = () => {
//   const cards = [
//     {
//       title: "카드 제목 1",
//       description: "간단한 설명 1",
//       imgSrc: "https://via.placeholder.com/250x200",
//     },
//     {
//       title: "카드 제목 2",
//       description: "간단한 설명 2",
//       imgSrc: "https://via.placeholder.com/250x200",
//     },
//     {
//       title: "카드 제목 3",
//       description: "간단한 설명 3",
//       imgSrc: "https://via.placeholder.com/250x200",
//     },
//     {
//       title: "카드 제목 4",
//       description: "간단한 설명 4",
//       imgSrc: "https://via.placeholder.com/250x200",
//     },
//     {
//       title: "카드 제목 5",
//       description: "간단한 설명 5",
//       imgSrc: "https://via.placeholder.com/250x200",
//     },
//   ];

//   return (
//     <div className="card2">
//       <Swiper
//         spaceBetween={10} // 카드 간의 간격
//         slidesPerView={1} // 한 번에 보여줄 카드 수 (1장만 보이도록 설정)
//         navigation={true} // 좌우 화살표 네비게이션 활성화
//         loop={true} // 끝까지 갔을 때 다시 처음으로 돌아가도록 설정
//       >
//         {cards.map((card, index) => (
//           <SwiperSlide key={index}>
//             <div className="card">
//               <img src={card.imgSrc} alt={card.title} />
//               <h3>{card.title}</h3>
//               <p>{card.description}</p>
//             </div>
//           </SwiperSlide>
//         ))}
//       </Swiper>
//     </div>
//   );
// };

// export default Card2;
