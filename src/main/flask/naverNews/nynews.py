import os
import json
import urllib.request
import urllib.parse  # URL 인코딩
from flask import Flask, request, jsonify
from elasticsearch import Elasticsearch
from flask_cors import CORS

app = Flask(__name__)  # Flask 애플리케이션 객체 생성
CORS(app)


@app.route("/ny_news", methods=["GET"])
def ny_news():
    search_query = request.args.get("search")  # 검색어 가져오기 (없으면 None)
    num_pages = 100  # 최대 100페이지 (한 페이지당 10개 기사, 최대 1000개)

    # Elasticsearch 연결
    es = Elasticsearch("http://localhost:9200")

    # NYT API 호출 (IT 뉴스 필터 적용)
    client_secret = "y9tKSafaEQe4E4AmwP3VrzZJKXzOGDqD"
    fq_filter = 'news_desk:("Technology")'  # IT 관련 뉴스 필터

    all_articles = []  # 전체 기사 저장 리스트

    for page in range(num_pages):
        if search_query:
            url = f"https://api.nytimes.com/svc/search/v2/articlesearch.json?q={urllib.parse.quote(search_query)}&fq={urllib.parse.quote(fq_filter)}&sort=newest&api-key={client_secret}&page={page}"
        else:
            url = f"https://api.nytimes.com/svc/search/v2/articlesearch.json?fq={urllib.parse.quote(fq_filter)}&sort=newest&api-key={client_secret}&page={page}"

        try:
            req = urllib.request.Request(url)
            with urllib.request.urlopen(req) as response:
                if response.getcode() == 200:
                    data = json.loads(response.read().decode("utf-8"))

                    articles = data.get("response", {}).get("docs", [])

                    # `pub_date` 기준으로 최신순 정렬 (내림차순)
                    sorted_articles = sorted(articles, key=lambda x: x.get("pub_date", ""), reverse=True)

                    # Elasticsearch에 저장
                    for article in sorted_articles:
                        article_id = article["_id"]
                        article_data = {k: v for k, v in article.items() if k != "_id"}

                        # 최신 기사 업데이트 (upsert)
                        es.index(index="ny_news", id=article_id, document=article_data)

                    all_articles.extend(sorted_articles)

                else:
                    return jsonify({"error": f"API Error: {response.getcode()}"}), response.getcode()

        except Exception as e:
            return jsonify({"error": str(e)}), 500

    # ✅ **최대 10000개 기사만 유지** (이전 데이터 삭제)
    keep_latest_n = 10000
    try:
        es.delete_by_query(
            index="ny_news",
            body={
                "query": {
                    "range": {
                        "pub_date": {
                            "lt": all_articles[min(len(all_articles), keep_latest_n) - 1]["pub_date"]
                        }
                    }
                }
            }
        )
    except Exception as e:
        print(f"Elasticsearch 데이터 삭제 오류: {e}")

    return jsonify({"message": "News articles stored successfully!", "total_articles": len(all_articles)})


@app.route("/search_ny_news", methods=["GET"])
def search_ny_news():
    es = Elasticsearch("http://localhost:9200")
    search_query = request.args.get("search", "it")  # 기본 검색어 'it'
    max_results = 10000  # 최대 10000개 기사 반환

    query = {
        "query": {
            "bool": {
                "should": [
                    {"match": {"headline.main": search_query}},
                    {"match": {"abstract": search_query}},
                    {"match": {"lead_paragraph": search_query}}
                ]
            }
        },
        "sort": [
            {"pub_date": {"order": "desc"}}  # 최신 기사부터 정렬
        ]
    }

    try:
        response = es.search(index="ny_news", body=query, size=max_results)
        hits = response.get("hits", {}).get("hits", [])  # 검색 결과 가져오기
        results = {}
        for hit in hits:
            article_id = hit["_id"]  # `_id` 필드를 기준으로 중복 제거

            if article_id not in results:
                results[article_id] = {
                    "headline": {
                        "main": hit["_source"]["headline"]["main"]
                    },
                    "abstract": hit["_source"].get("abstract", "No abstract available"),
                    "web_url": hit["_source"].get("web_url", "#"),  # URL 필드 수정
                    "pub_date": hit["_source"].get("pub_date", "Unknown Date"),
                    "snippet": hit["_source"].get("snippet", "No summary available")
                }

        # 최종 결과 리스트
        final_results = list(results.values())

        return jsonify(final_results)

    except Exception as e:
        return jsonify({"error": str(e)}), 500


# Flask 실행
if __name__ == "__main__":
    app.run(debug=True)
