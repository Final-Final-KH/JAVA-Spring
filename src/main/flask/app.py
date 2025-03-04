from flask import Flask
from flask_cors import CORS

from naverNews.nynews import ny_news, search_ny_news
from pay.pay import predict_salary
from trend_keyword.keyword import get_trend_keyword
from naverNews.news import get_news
from stock.stock import get_stock, get_KOREA_sise, get_foreign_stock_price  # ✅ stock API 추가

app = Flask(__name__)
app.config['JSON_AS_ASCII'] = False  # JSON 응답에서 ASCII를 사용하지 않음
CORS(app)

@app.route('/')
def home():
    return 'Hello, Flask!!'

app.add_url_rule('/news', 'get_news', get_news, methods=['GET', 'POST'])
app.add_url_rule('/trend_keyword', 'get_trend_keyword', get_trend_keyword, methods=['GET'] )
app.add_url_rule('/get_stock', 'get_stock', get_stock, methods=['GET'])  #
app.add_url_rule('/ny_news', 'ny_news', ny_news, methods=['GET'])  #
app.add_url_rule('/search_ny_news', 'search_ny_news', search_ny_news, methods=['GET'])  #
app.add_url_rule('/predict_salary', 'predict_salary', predict_salary, methods=['GET'])  #

if __name__ == "__main__":
    app.run(debug=True)
