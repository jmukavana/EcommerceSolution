import { useMutation, useQuery } from "@tanstack/react-query";
import api from "@/lib/api";
import { User } from "@/types";

export const useLogin = () =>
  useMutation({
    mutationFn: (data: { email: string; password: string }) =>
      api.post("/auth/login", data).then((r) => r.data),
  });

export const useVerifyOtp = () =>
  useMutation({
    mutationFn: (data: { email: string; otp: string }) =>
      api.post("/auth/verify-otp", data).then((r) => r.data),
  });

export const useLogout = () =>
  useMutation({
    mutationFn: () => api.post("/auth/logout").then((r) => r.data),
  });

export const useCurrentUser = (enabled: boolean) =>
  useQuery<User>({
    queryKey: ["me"],
    queryFn: () => api.get("/users/me").then((r) => r.data.data),
    enabled,
  });
