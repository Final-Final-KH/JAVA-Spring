import React, { useState } from "react";
import imgLogo1 from "../../images/DeveloperMark.jpg";
import imgLogo2 from "../../images/EditButton.webp";
import imgLogo3 from "../../images/SaveButton.png";

const EditableField = ({
  label,
  value,
  content,
  isEditable,
  onSave,
  placeholder,
}) => {
  const [editing, setEditing] = useState(false);
  const [fieldValue, setFieldValue] = useState(
    value || "내용이 비어 있습니다! 스스로를 채워 보세요!"
  ); // 초기값 설정

  const handleSave = () => {
    onSave(fieldValue);
    setEditing(false); // 저장 후 수정 상태 해제
  };

  const handleEditToggle = () => {
    if (editing) {
      handleSave();
    }
    setEditing(!editing); // isEditable이 true일 때만 수정 가능
  };

  const containerName = `profile-${content}-container`;

  return label ? (
    <div className="profile-content-container">
      <h3>{label}</h3>
      {editing ? (
        <textarea
          value={fieldValue}
          onChange={(e) => setFieldValue(e.target.value)}
          rows={5}
          className="edit-input"
          placeholder={placeholder}
        />
      ) : (
        <p>{fieldValue}</p>
      )}
      <button className="edit-button" onClick={handleEditToggle}>
        {editing ? (
          <img
            src={imgLogo3}
            alt="저장"
            style={{ width: "20px", height: "20px" }}
          />
        ) : (
          <img
            src={imgLogo2}
            alt="수정"
            style={{ width: "20px", height: "20px" }}
          />
        )}
      </button>
    </div>
  ) : (
    <div className={containerName}>
      <h2>
        {editing ? (
          <input
            type="text"
            value={fieldValue}
            onChange={(e) => setFieldValue(e.target.value)}
            className="edit-input"
          />
        ) : (
          fieldValue
        )}
        {isEditable && (
          <button className="edit-button" onClick={handleEditToggle}>
            {editing ? (
              <img
                src={imgLogo3}
                alt="저장"
                style={{ width: "20px", height: "20px" }}
              />
            ) : (
              <img
                src={imgLogo2}
                alt="수정"
                style={{ width: "20px", height: "20px" }}
              />
            )}
          </button>
        )}
      </h2>
    </div>
  );
};

export default EditableField;
