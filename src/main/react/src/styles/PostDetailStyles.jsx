import styled from "styled-components";
import { createGlobalStyle } from "styled-components";

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
  display: flex;
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
    border: 2px solid #ff9900;
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
    border: 2px solid #747474;
    border-radius: 5px;
    background-color: #d3d3d3;
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
  display: flex;
  flex-direction: column;
  gap: 10px;

  .toolbar {
    display: flex;
    gap: 10px;

    button {
      padding: 8px 12px;
      font-size: 14px;
      border: 1px solid #ddd;
      border-radius: 5px;
      background: #fff;
      cursor: pointer;

      &.active {
        background: #007bff;
        color: #fff;
      }

      &:hover {
        background: #f0f0f0;
      }

      strong,
      em,
      u {
        font-size: 16px;
      }
    }
  }

  .editor {
    border: 1px solid #ddd;
    border-radius: 5px;
    min-height: 110px;
    padding: 10px;
    font-size: 14px;
    background-color: white;
  }

  button {
    align-self: flex-end;
    padding: 10px 20px;
    font-size: 14px;
    background-color: white;
    color: #007bff;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    transition: 0.3s;

    &:hover {
      background-color: #0056b3;
      color: white;
    }
  }
`;

export const HiddenCommentNotice = styled.p`
  color: #d9534f;
  font-weight: bold;
  background-color: #f8d7da;
  border: 1px solid #f5c2c7;
  padding: 8px;
  border-radius: 4px;
  text-align: center;
`;

/**
 * [FIX] EditButton should use "cursor: pointer" if it is actually clickable.
 *  If you truly want it disabled, use DisabledEditButton below.
 */
export const EditButton = styled.button`
  padding: 5px;
  font-size: 20px;
  border: none;
  background-color: transparent;
  color: #007bff;
  cursor: pointer; /* was not-allowed before, changed to pointer */
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
  cursor: not-allowed;
  transition: color 0.3s ease;

  &:focus {
    outline: none;
  }
`;

export const InlineBlockContainer = styled.div`
  /* display: flex; */
  align-items: baseline;
  gap: 0.5rem;
  margin-bottom: 5px;
`;

export const AdminEditIndicator = styled.span`
  color: #ff9900;
  font-weight: bold;
  font-size: 12px;
  margin-left: 10px;
  text-transform: uppercase;
  display: inline;
  vertical-align: baseline;
`;

export const ReportCountText = styled.span`
  margin-left: 5px;
  font-weight: bold;
  color: #555;

  report-button:hover & {
    color: white;
  }
`;

/**
 * (선택) QuotedReply, QuotedSection 등은 아직 남겨놓음.
 *  But the main logic is in .reply-quote below
 */
export const QuotedReply = styled.div`
  background-color: #f9f9f9;
  border-left: 4px solid #007bff;
  padding: 10px;
  margin-bottom: 10px;
  font-size: 14px;
  font-style: italic;
  color: #555;
  border-radius: 5px;

  p {
    margin: 0;
  }

  strong {
    font-weight: bold;
    color: #333;
  }
`;

export const QuotedSection = styled.div`
  background-color: #f5f5f5;
  padding: 10px;
  border-left: 4px solid #007bff;
  margin-bottom: 10px;
  font-size: 13px;

  p {
    margin: 0;
    font-style: italic;

    strong {
      font-weight: bold;
    }
  }
`;

/**
 * 🔻 최종적으로 <blockquote class="reply-quote">에 적용될 스타일 (Ragezone 유사)
 */
export const ReplyQuoteGlobalStyle = createGlobalStyle`
  .reply-quote {
    display: block;
    width: 100%;
    box-sizing: border-box;
    border-left: 4px solid #c00;
    background-color: #e9e9e9;
    margin: 8px 0;
    padding: 0.5rem 1rem;
    color: #333;
    border-radius: 4px;
    font-size: 0.9rem;
  }

  /* 인용 내부 <strong> 태그 굵게 */
  .reply-quote strong {
    font-weight: bold;
    color: #000; 
  }

  /* 인용 블록의 첫째 줄만 두껍게 */
  .reply-quote p:first-of-type {
    font-weight: bold;
    margin-bottom: 4px;
  }

  /* 기타 .quote-header / .quote-body 클래스를 쓸 경우 */
  .reply-quote .quote-header {
    font-weight: bold;
    margin-bottom: 4px;
    color: #c00;
  }
  .reply-quote .quote-body {
    font-size: 0.9rem;
    margin-left: 4px;
  }

  /* editor에서 작성 중인 blockquote 스타일 */
  .editor blockquote {
    border-left: 4px solid #c00;
    padding: 0.5rem 1rem;
    margin: 8px 0;
    background: #e9e9e9;
    color: #333;
  }
  .editor blockquote p {
    margin: 0.5rem 0;
    line-height: 1.4;
    font-size: 0.95rem;
  }

  /* "원본↑" 링크 (내부 앵커) */
  .reply-quote .jump-to-original {
    margin-left: 8px;
    color: #777;
    font-size: 0.8rem;
    text-decoration: none;
  }
  .reply-quote .jump-to-original:hover {
    color: #000;
  }

  /* 보조: .reply-quote a 전체에 pointer-events 허용 */
  .reply-quote a {
    pointer-events: auto; 
    color: #007bff;
    text-decoration: underline;
  }
  .reply-quote a:hover {
    color: #0056b3;
    text-decoration: underline;
  }
`;
