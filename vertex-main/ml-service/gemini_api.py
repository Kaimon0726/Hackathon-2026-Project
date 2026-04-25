import os
import io
from google import genai
from dotenv import load_dotenv
from pathlib import Path
from PIL import Image # 画像処理用に追加

# .envの読み込み
env_path = Path(__file__).parent / ".env"
load_dotenv(dotenv_path=env_path)

# 設定の取得
USE_VERTEX = os.getenv("USE_VERTEX", "False").lower() == "true"
PROJECT_ID = os.getenv("GOOGLE_CLOUD_PROJECT")
LOCATION = os.getenv("GOOGLE_CLOUD_LOCATION")
API_KEY = os.getenv("GEMINI_API_KEY")

# --- クライアントの初期化をスイッチ ---
if USE_VERTEX:
    client = genai.Client(
        vertexai=True,
        project=PROJECT_ID,
        location=LOCATION
    )
    print("🚀 Running on Vertex AI mode")
else:
    client = genai.Client(api_key=API_KEY)
    print("🛠️ Running on API Key mode")

def get_recommend_reason(item_name, skin_type, moisture_level):
    prompt = f"""
    (中身はさっきの科学的プロンプトと同じ)
    """
    model_id = 'gemini-2.5-flash' if not USE_VERTEX else 'gemini-2.0-flash'
    
    response = client.models.generate_content(
        model=model_id, 
        contents=prompt
    )
    return response.text

# 👇 ここから新規追加する画像解析機能 👇
def analyze_skin_image(image_bytes: bytes):
    """
    受け取った画像データから肌状態を解析する関数
    """
    try:
        # バイトデータをPIL画像に変換してGeminiが読み込める形式にする
        image = Image.open(io.BytesIO(image_bytes))
    except Exception as e:
        return f"画像読み込みエラーｼﾞｪﾐ: {e}"

    # AIへの指示（システムプロンプト）
    # 薬機法に抵触しうる効能の断言（「シミが消える」「治る」など）は絶対に行わないように指示 
    prompt = """
    あなたは優秀な皮膚科学のアシスタントです。
    ユーザーから提供された肌の画像から、目視できる範囲で肌の状態（シワ・シミ・毛穴など）の特徴を客観的に抽出してください。
    また、その特徴から推測される肌質（乾燥肌、脂性肌など）を簡潔に回答してください。
    
    【重要】
    ・これは医療診断ではありません。「〜という病気です」「〜で治ります」といった断言や、医療行為に該当する表現は避け、あくまで「傾向」として出力してください。
    ・成分レコメンドシステムへの入力補助として使われるため、テキストは簡潔にまとめてください。
    """

    model_id = 'gemini-2.5-flash' if not USE_VERTEX else 'gemini-2.0-flash'
    
    try:
        # 画像(image)とテキスト(prompt)をリストにして渡すことでマルチモーダル解析を実行！
        response = client.models.generate_content(
            model=model_id, 
            contents=[prompt, image]
        )
        return response.text
    except Exception as e:
        return f"APIリクエストエラーｼﾞｪﾐ: {e}"