import pandas as pd
import re
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

class AnimeRecommender:
    def __init__(self, json_path):
        self.json_path = json_path
        self.df = None
        self.vectorizer = None
        self.tfidf_matrix = None
        self.similarity_matrix = None

        self.load_and_prepare()

    def load_and_prepare(self):
        # 1. load json
        self.df = pd.read_json(self.json_path)
        # 2. clean title
        self.df["title"] = self.df["title"].apply(
            lambda t: t["english"] if isinstance(t, dict) and t.get("english") else t.get("romaji", "")
        )
        # 3. clean description
        self.df["description"] = self.df["description"].fillna("")
        self.df["description"] = self.df["description"].apply(
            lambda x: re.sub(r"<.*?>", "", x)
        )

        # 4. convert genres to text
        self.df["genres_text"] = self.df["genres"].apply(
            lambda g: " ".join(g) if isinstance(g, list) else ""
        )
        # 5. build features
        self.df["features"] = (
            self.df["title"] + " " +
            self.df["genres_text"] + " " +
            self.df["genres_text"] + " " +
            self.df["description"]
        )

        # 6. vectorize
        self.vectorizer = TfidfVectorizer()
        self.tfidf_matrix = self.vectorizer.fit_transform(self.df["features"])

        # 7. similarity matrix
        self.similarity_matrix = cosine_similarity(self.tfidf_matrix, self.tfidf_matrix)
        

    def recommend(self, title, top_n=5):
        # 1. find title
        matches = self.df[self.df["title"].str.lower() == title.lower()]

        if matches.empty:
            return f"Title '{title}' not found."

        anime_index = matches.index[0]
        # 2. get similarity scores
        similarity_scores = list(enumerate(self.similarity_matrix[anime_index]))
        # 3. sort
        sorted_similarity_scores = sorted(similarity_scores, key=lambda x: x[1], reverse=True)
        # 4. skip itself
        top_results = sorted_similarity_scores[1:top_n+1]
        # 5. return structured results
        recommended_titles = [(self.df.iloc[i[0]]["title"], i[1]) for i in top_results]

        return recommended_titles
        


rec = AnimeRecommender("anilist_5000_with_image.json")
# print(rec.df[["title", "genres", "genres_text", "features"]].head())
# print(rec.tfidf_matrix.shape)
# print(rec.similarity_matrix.shape)
print(rec.recommend("Naruto", top_n=5))
print(rec.recommend("Attack on Titan", top_n=5))
print(rec.recommend("Death Note", top_n=5))
print(rec.recommend("Jujutsu Kaisen", top_n=5))
print(rec.recommend("My Hero Academia", top_n=5))
# print(rec.recommend("Naruto: Shippuden", top_n=5))
# print(rec.recommend("Boruto: Naruto Next Generations", top_n=5))
# print(rec.recommend("Boruto: Naruto the Movie - The Day Naruto Became Hokage", top_n=5))
# print(rec.recommend("ROAD OF NARUTO", top_n=5))
# print(rec.recommend("Naruto: The Lost Story - Mission: Protect the Waterfall Village", top_n=5))
