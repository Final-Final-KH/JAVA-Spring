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
      <h1>Welcome to the News Page!</h1>
      <NewsList />
    </div>
  );
}
export default News;
