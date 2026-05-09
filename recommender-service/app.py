from pathlib import Path

from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

from model import AnimeRecommender


DATA_PATH = Path(__file__).resolve().parent.parent / "notebook" / "anilist_5000_with_image.json"


class RecommendRequest(BaseModel):
    title: str
    topN: int = Field(default=10, ge=1, le=50)
    excludeTitle: bool = True


class RecommendationItem(BaseModel):
    title: str
    score: float
    imageUrl: str | None = None
    anilistId: int | None = None


class RecommendResponse(BaseModel):
    queryTitle: str
    model: str
    recommendations: list[RecommendationItem]


app = FastAPI()
rec = AnimeRecommender(str(DATA_PATH))


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/recommend", response_model=RecommendResponse)
def recommend(body: RecommendRequest):
    items = rec.recommend(
        body.title,
        top_n=body.topN,
        exclude_title=body.excludeTitle,
    )
    if items is None:
        raise HTTPException(status_code=404, detail=f"Title not found: {body.title}")

    return RecommendResponse(
        queryTitle=body.title,
        model="tfidf-v1",
        recommendations=items,
    )