from flask import Flask, request, Response
from flask_cors import CORS
from naverNews.news import index
import json

app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False  # JSON 응답에서 ASCII를 사용하지 않음
CORS(app)

@app.route('/')
def home():
    return 'Hello, Flask!!'

@app.route('/news', methods=['GET', 'POST'])
def news():
    return index(request)  # news.py의 index 함수 호출

if __name__ == "__main__":
    app.run(debug=True)