import axios from "axios";

const API_BASE_URL = "http://localhost:8080/moviemuse";

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
});

export const getAllMovies = async () => {
  try {
    const response = await api.get("");
    return response.data;
  } catch (error) {
    console.error("Get all Movies error:", error);
    if (error.response) {
      throw new Error(`Server Error: ${error.response.status}`);
    } else if (error.request) {
      throw new Error("Network Error");
    } else {
      throw new Error("Request configuration Error");
    }
  }
};

export const searchAnime = async (query) => {
  if (!query) return null;
  const res = await api.get("/anilist/search/anime?query=" , {
    params: {query}
  });
  return res.data;
}

export const importAnime = async (anime) => {
  if (!anime) return null;
  const res = await api.post("/anilist/import", anime);
  return res.data;
}

export const createMovie = async (movieData) => {
    try {
        // Check if movieData is FormData
        const isFormData = movieData instanceof FormData;
        
        if (isFormData) {
            // Use the native fetch API for FormData
            const response = await fetch(`${API_BASE_URL}/upload`, {
                method: 'POST',
                body: movieData,
                // Don't set any Content-Type header - browser will set it correctly
            });
            
            if (!response.ok) {
                const errorData = await response.text();
                console.error("Server response:", errorData);
                throw new Error(`Server Error: ${response.status}`);
            }
            
            return await response.json();
        } else {
            // If it's already a JSON object, send as is
            const response = await api.post("", movieData);
            return response;
        }
    } catch (error) {
        console.error("Create movie error:", error);
        if (error.response) {
            throw new Error(`Server Error: ${error.response.status}`);
        } else if (error.request) {
            throw new Error("Network Error - Please check if the backend server is running");
        } else {
            throw new Error(error.message || "Request configuration Error");
        }
    }
}

export const getMovieById = async (id) => {
  try {
    const response = await api.get(`/${id}`);
    return response;
  } catch (error) {
    console.error("Get movie by ID error:", error);
    if (error.response) {
      throw new Error(`Server Error: ${error.response.status}`);
    } else if (error.request) {
      throw new Error("Network Error");
    } else {
      throw new Error("Request configuration Error");
    }
  }
};

export const createReview = async (reviewData) => {
  try {
    const response = await api.post("/review", reviewData, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response;
  } catch (error) {
    console.error("Create review error:", error);
    if (error.response) {
      throw new Error(`Server Error: ${error.response.status}`);
    } else if (error.request) {
      throw new Error(
        "Network Error - Please check if the backend server is running"
      );
    } else {
      throw new Error("Request configuration Error");
    }
  }
};

export const updateReview = async (id, reviewData) => {
  try {
    const response = await api.put(`/review/${id}`, reviewData, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response;
  } catch (error) {
    console.error("Update review error:", error);
    if (error.response) {
      throw new Error(`Server Error: ${error.response.status}`);
    } else if (error.request) {
      throw new Error(
        "Network Error - Please check if the backend server is running"
      );
    } else {
      throw new Error("Request configuration Error");
    }
  }
};

//delete movie
export const deleteMovie = async (movieId) => {
  try {
    const response = await api.delete(`/${movieId}`);
    return response.data;
  } catch (error) {
    console.error("Delete movie error:", error);
    if (error.response) {
      throw new Error(`Server Error: ${error.response.status}`);
    } else if (error.request) {
      throw new Error("Network Error");
    } else {
      throw new Error("Request configuration Error");
    }
  }
};