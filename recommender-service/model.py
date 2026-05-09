import re
from typing import Any

import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity


class AnimeRecommender:
    def __init__(self, json_path: str) -> None:
        self.json_path = json_path
        self.df: pd.DataFrame | None = None
        self.vectorizer: TfidfVectorizer | None = None
        self.tfidf_matrix = None
        self.similarity_matrix = None

        self.load_and_prepare()

    def load_and_prepare(self) -> None:
        df = pd.read_json(self.json_path)

        df["title"] = df["title"].apply(_display_title)

        df["description"] = df["description"].fillna("")
        df["description"] = df["description"].apply(
            lambda x: re.sub(r"<.*?>", "", str(x))
        )

        df["genres_text"] = df["genres"].apply(
            lambda g: " ".join(g) if isinstance(g, list) else ""
        )

        df["anilistId"] = df["id"]
        df["imageUrl"] = df["coverImage"].apply(_cover_large)

        df["features"] = (
            df["title"]
            + " "
            + df["genres_text"]
            + " "
            + df["genres_text"]
            + " "
            + df["description"]
        )

        self.vectorizer = TfidfVectorizer()
        self.tfidf_matrix = self.vectorizer.fit_transform(df["features"])
        self.similarity_matrix = cosine_similarity(
            self.tfidf_matrix,
            self.tfidf_matrix,
        )
        self.df = df

    def recommend(
        self,
        title: str,
        top_n: int = 10,
        *,
        exclude_title: bool = True,
    ) -> list[dict[str, Any]] | None:
        if self.df is None or self.similarity_matrix is None:
            return None

        t = title.strip().lower()
        matches = self.df[self.df["title"].str.strip().str.lower() == t]
        if matches.empty:
            return None

        query_idx = matches.index[0]
        row_pos = self.df.index.get_loc(query_idx)
        if isinstance(row_pos, slice):
            row_pos = row_pos.start
        row_pos = int(row_pos)

        scores_row = self.similarity_matrix[row_pos]
        ranked = sorted(enumerate(scores_row), key=lambda x: x[1], reverse=True)

        if exclude_title:
            ranked = [(i, s) for i, s in ranked if i != row_pos]

        out: list[dict[str, Any]] = []
        for i, s in ranked[:top_n]:
            row = self.df.iloc[i]
            aid = row.get("anilistId")
            img = row.get("imageUrl")
            out.append(
                {
                    "title": row["title"],
                    "score": round(float(s), 6),
                    "imageUrl": img if pd.notna(img) and img else None,
                    "anilistId": int(aid)
                    if pd.notna(aid) and aid is not None
                    else None,
                }
            )

        return out


def _display_title(t: Any) -> str:
    if isinstance(t, dict):
        if t.get("english"):
            return str(t["english"])
        return str(t.get("romaji") or "")
    return str(t)


def _cover_large(ci: Any) -> str | None:
    if isinstance(ci, dict) and ci.get("large"):
        return str(ci["large"])
    return None