import React from "react";
import "./Card.css";

const Card = ({ title, description, imgSrc }) => {
  return (
    <div className="card">
      <img src={imgSrc} alt={title} />
      <h3>{title}</h3>
      <p>{description}</p>
    </div>
  );
};

const Card1 = () => {
  const cards = [
    {
      title: "카드 제목 1",
      description: "간단한 설명 1",
      imgSrc: "https://via.placeholder.com/250x200",
    },
    {
      title: "카드 제목 2",
      description: "간단한 설명 2",
      imgSrc: "https://via.placeholder.com/250x200",
    },
    {
      title: "카드 제목 3",
      description: "간단한 설명 3",
      imgSrc: "https://via.placeholder.com/250x200",
    },
    {
      title: "카드 제목 4",
      description: "간단한 설명 4",
      imgSrc: "https://via.placeholder.com/250x200",
    },
    {
      title: "카드 제목 5",
      description: "간단한 설명 5",
      imgSrc: "https://via.placeholder.com/250x200",
    },
    {
      title: "카드 제목 6",
      description: "간단한 설명 6",
      imgSrc: "https://via.placeholder.com/250x200",
    },
  ];

  return (
    <div className="card-container">
      {cards.map((card, index) => (
        <Card
          key={index}
          title={card.title}
          description={card.description}
          imgSrc={card.imgSrc}
        />
      ))}
    </div>
  );
};

export default Card1;
