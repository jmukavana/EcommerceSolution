import axios from "axios";

const api = axios.create({ baseURL: "http://localhost:8050/api/v1" });

api.interceptors.request.use((config) => {
  if (typeof window !== "undefined") {
    const token = localStorage.getItem("accessToken");
    if (token) config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let isRefreshing = false;
let failedQueue: {
  resolve: (token: string) => void;
  reject: (err: unknown) => void;
}[] = [];

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then((token) => {
          original.headers.Authorization = `Bearer ${token}`;
          return api(original);
        });
      }
      original._retry = true;
      isRefreshing = true;
      const refreshToken =
        typeof window !== "undefined"
          ? localStorage.getItem("refreshToken")
          : null;
      if (!refreshToken) {
        if (typeof window !== "undefined") window.location.href = "/auth/login";
        return Promise.reject(error);
      }
      try {
        const { data } = await axios.post(
          "http://localhost:8050/api/v1/auth/refresh",
          { refreshToken },
        );
        const newToken = data.data.accessToken;
        if (typeof window !== "undefined")
          localStorage.setItem("accessToken", newToken);
        failedQueue.forEach((p) => p.resolve(newToken));
        failedQueue = [];
        original.headers.Authorization = `Bearer ${newToken}`;
        return api(original);
      } catch {
        failedQueue.forEach((p) => p.reject(error));
        failedQueue = [];
        if (typeof window !== "undefined") {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          window.location.href = "/auth/login";
        }
        return Promise.reject(error);
      } finally {
        isRefreshing = false;
      }
    }
    return Promise.reject(error);
  },
);

export default api;
