import styled from "styled-components";

export const CreatePostContainer = styled.div`
  background-color: #f5f6f7;
  max-width: 800px;
  margin: 70px auto;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

export const CreatePostTitle = styled.h2`
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-bottom: 20px;
`;

export const CreatePostForm = styled.form`
  display: flex;
  flex-direction: column;
  gap: 15px;
`;

export const FormGroup = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;

  label {
    font-size: 14px;
    font-weight: bold;
    color: #555;
  }

  input,
  textarea,
  select {
    padding: 10px;
    font-size: 14px;
    border: 1px solid #ddd;
    border-radius: 5px;
    background-color: #ffffff;
  }

  textarea {
    resize: none;
  }
`;

export const CreatePostButton = styled.button`
  background-color: ${(props) => (props.disabled ? "#cccccc" : "#007bff")};
  color: white;
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: ${(props) => (props.disabled ? "not-allowed" : "pointer")};
  transition: background-color 0.3s;

  &:hover:enabled {
    background-color: #0056b3;
  }
`;
