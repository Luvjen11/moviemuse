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

export const createMovie = async (movieData) => {
    try {
        const response = await api.post("", movieData, {
            headers: {
                "Content-Type": "application/json",
              },
        });
        return response;
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
