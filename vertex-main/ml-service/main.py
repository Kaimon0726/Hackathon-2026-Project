from fastapi import FastAPI, UploadFile, File
from gemini_api import get_recommend_reason, analyze_skin_image # 画像解析関数を追加でインポート
from pydantic import BaseModel

app = FastAPI()

# データの受け取り形式を定義
class Item(BaseModel):
    item_name: str
    skin_type: str  
    moisture_level: int  

@app.post("/recommend")
def recommend(item: Item):
    reason = get_recommend_reason(
        item.item_name, 
        item.skin_type, 
        item.moisture_level
    )
    return {
        "item_name": item.item_name,
        "skin_type": item.skin_type,         
        "moisture_level": item.moisture_level, 
        "recommend_reason": reason
    }

# 👇 ここから新規追加する画像受け取りエンドポイント 👇
@app.post("/analyze-skin")
async def analyze_skin(file: UploadFile = File(...)):
    """
    カメラ画像をアップロードして、Geminiで解析するAPI
    """
    # アップロードされた画像データを非同期で読み込む
    image_bytes = await file.read()
    
    # gemini_api.pyで作った関数に投げて解析結果をもらう
    analysis_result = analyze_skin_image(image_bytes)
    
    return {
        "filename": file.filename,
        "status": "success",
        "analysis_result": analysis_result
    }