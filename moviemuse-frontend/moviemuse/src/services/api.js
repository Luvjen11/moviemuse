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

// Update the createMovie function to handle FormData
export const createMovie = async (movieData) => {
    try {
        // Check if movieData is FormData
        const isFormData = movieData instanceof FormData;
        
        if (isFormData) {
            // Use the multipart endpoint
            const response = await api.post("/upload", movieData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
            });
            return response;
        } else {
            // If it's already a JSON object, send as is
            const response = await api.post("", movieData, {
                headers: {
                    "Content-Type": "application/json",
                },
            });
            return response;
        }
    } catch (error) {
        console.error("Create movie error:", error);
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
