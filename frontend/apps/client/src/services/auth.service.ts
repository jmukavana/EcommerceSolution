import { useMutation, useQuery } from "@tanstack/react-query";
import api from "@/lib/api";
import { LoginRequest, RegisterRequest, VerifyOtpRequest, User } from "@/types";

export const useRegister = () =>
  useMutation({
    mutationFn: (data: RegisterRequest) =>
      api.post("/auth/register", data).then((r) => r.data),
  });

export const useLogin = () =>
  useMutation({
    mutationFn: (data: LoginRequest) =>
      api.post("/auth/login", data).then((r) => r.data),
  });

export const useVerifyOtp = () =>
  useMutation({
    mutationFn: (data: VerifyOtpRequest) =>
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
