// OAuth2Button.js

const OAuth2Button = ({ onClick, href, children, style = {} }) => {
  const defaultStyle = {
    display: "inline-block",
    width: "100%",
    padding: "10px",
    color: "#fff",
    textAlign: "center",
    border: "none",
    borderRadius: "4px",
    cursor: "pointer",
    textDecoration: "none",
    marginTop: "10px",
    boxSizing: "border-box",
    transition: "background-color 0.3s, opacity 0.3s",
  };

  const hoverStyle = {
    opacity: 0.8,
  };

  const combinedStyle = {
    ...defaultStyle,
    ...(style ? style : {}),
  };

  // 링크와 버튼을 구분하여 반환
  if (href) {
    return (
      <a
        href={href}
        style={combinedStyle}
        onMouseEnter={(e) => (e.target.style.opacity = 0.7)}
        onMouseLeave={(e) => (e.target.style.opacity = 1)}
      >
        {children}
      </a>
    );
  }

  return (
    <button
      onClick={onClick}
      style={combinedStyle}
      onMouseEnter={(e) => (e.target.style.opacity = 0.7)}
      onMouseLeave={(e) => (e.target.style.opacity = 1)}
    >
      {children}
    </button>
  );
};

export default OAuth2Button;
