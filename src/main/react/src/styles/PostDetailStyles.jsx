import styled from "styled-components";

// Post detail container
export const PostDetailContainer = styled.div`
  background-color: #f5f6f7;
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  margin-top: 90px;
  font-family: Arial, sans-serif;
`;

// Post title
export const PostTitle = styled.h1`
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
`;

// Post header container
export const PostHeader = styled.div`
  display: flex;
  background-color: #ffffff;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 15px;
  align-items: flex-start;
`;

// Author info section
export const AuthorInfo = styled.div`
  width: 25%; /* Same width for comments and posts */
  border-right: 1px solid #ddd;
  padding-right: 15px;
  margin-right: 15px;
  display: flex; /* Ensure content aligns properly */
  flex-direction: column;

  p {
    margin: 5px 0;
    font-size: 14px;
    color: #555;

    strong {
      font-weight: bold;
      color: #333;
    }
  }
`;

// Post content section
export const ContentInfo = styled.div`
  width: 75%;
  p {
    font-size: 16px;
    line-height: 1.6;
  }
`;

export const ActionButtons = styled.div`
  display: flex;
  justify-content: space-between;

  .left,
  .right {
    display: flex;
    gap: 10px;
  }
  button {
    padding: 5px 10px;
    font-size: 14px;
    border-radius: 5px;
    border: 2px solid #007bff;
    background-color: white;
    color: #007bff;
    cursor: pointer;
    transition: 0.3s;

    &:hover {
      background-color: #007bff;
      color: white;
    }
  }

  report-button {
    padding: 5px 10px;
    font-size: 14px;
    border: 2px solid #ff0000;
    border-radius: 5px;
    background-color: white;
    color: #ff0000;
    cursor: pointer;
    transition: 0.3s;

    &:hover {
      background-color: #ff0000;
      color: white;
    }
    &:hover + span {
      color: white; /* Associated text (ReportCountText) also turns white */
    }
  }

  admin-button {
    padding: 5px 10px;
    font-size: 14px;
    border: 2px solid #ff9900; /* 관리자 전용 버튼 강조 */
    border-radius: 5px;
    background-color: white;
    color: #ff9900;
    cursor: pointer;
    transition: 0.3s;

    &:hover {
      background-color: #ff9900;
      color: white;
    }
  }

  disabled-button {
    padding: 5px 10px;
    font-size: 14px;
    border: 2px solid #747474; /* 관리자 전용 버튼 강조 */
    border-radius: 5px;
    background-color: #d3d3d3; /* Light gray for disabled buttons */
    color: #747474;
    cursor: not-allowed;
    opacity: 0.6;
  }
`;

// Comment section container
export const CommentSection = styled.div`
  margin-top: 30px;

  h2 {
    font-size: 20px;
    margin-bottom: 15px;
  }
`;

// Individual comment card
export const CommentCard = styled.div`
  display: flex;
  background-color: #ffffff;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 10px;
`;

// Comment content
export const CommentContent = styled.div`
  width: 75%;

  p {
    font-size: 14px;
    margin: 5px 0;
  }
`;

// Comment input section
export const CommentInputSection = styled.div`
  margin-top: 20px;

  textarea {
    width: 100%;
    padding: 10px;
    font-size: 14px;
    border: 1px solid #ddd;
    border-radius: 5px;
    resize: none;
    margin-bottom: 10px;
  }

  button {
    background-color: #007bff;
    color: white;
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    cursor: pointer;

    &:hover {
      background-color: #0056b3;
    }
  }
`;

export const HiddenCommentNotice = styled.p`
  color: #d9534f; /* Distinct red */
  font-weight: bold;
  background-color: #f8d7da; /* Light red background */
  border: 1px solid #f5c2c7; /* Notice border */
  padding: 8px;
  border-radius: 4px;
  text-align: center;
`;

export const EditButton = styled.button`
  padding: 5px;
  font-size: 20px;
  border: none;
  background-color: transparent;
  color: #007bff;
  cursor: not-allowed;
  transition: color 0.3s ease;

  &:hover {
    color: #0056b3;
  }

  &:focus {
    outline: none;
  }
`;

export const DisabledEditButton = styled.button`
  padding: 5px;
  font-size: 20px;
  border: none;
  background-color: transparent;
  color: #747474;
  cursor: not-allowed; /* Indicates the button is disabled */
  transition: color 0.3s ease;

  &:focus {
    outline: none;
  }
`;

export const AdminEditIndicator = styled.span`
  color: #ff9900; /* Admin-specific color */
  font-weight: bold;
  font-size: 12px;
  margin-left: 10px;
  text-transform: uppercase;
`;

export const ReportCountText = styled.span`
  margin-left: 5px;
  font-weight: bold;
  color: #555;

  /* Change text color when the parent report-button is hovered */
  report-button:hover & {
    color: white;
  }
`;
