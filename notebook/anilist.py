import requests
import json
import time 

url = "https://graphql.anilist.co"

query = """
query ($page: Int, $perPage: Int) {
  Page(page: $page, perPage: $perPage) {
    media(type: ANIME, sort: POPULARITY_DESC) {
      id
      title {
        english
        romaji
      }
      genres
      description
    }
  }
}
"""
# save 5000 animes 
all_anime = []
perPage = 50
pages_needed = 100

for page in range(1, pages_needed + 1): 
    variables = {
        "page": page,
        "perPage" : perPage
    }

    response = requests.post(
        url,
        json={
            "query": query,
            "variables": variables
        }
    )

    data = response.json()
    media = data["data"]["Page"]["media"]
    all_anime.extend(media)
    print(f"Fetched page {page}, total so far: {len(all_anime)}")
    time.sleep(0.5)
  

with open("anilist_5000.json", "w", encoding="utf-8") as f:
    json.dump(all_anime, f, ensure_ascii=False, indent=2)

print(f"Saved {len(all_anime)} anime to anilist_1000.json")