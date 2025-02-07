import NewsList from "../component/NewsList";

// News.js
function News() {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        flexDirection: "column",
        padding: "20px",
        marginTop: "70px",
      }}
    >
      <h1>IT 뉴스 페이지에 오신 것을 환영합니다!</h1>
      <NewsList />
    </div>
  );
}
export default News;
