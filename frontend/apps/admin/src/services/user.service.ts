import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import { User, PageResponse } from "@/types";

export const useAdminUsers = (page = 0, size = 10) =>
  useQuery<PageResponse<User>>({
    queryKey: ["admin", "users", page, size],
    queryFn: () =>
      api.get("/users", { params: { page, size } }).then((r) => r.data.data),
  });

export const useUser = (id: string) =>
  useQuery<User>({
    queryKey: ["admin", "users", id],
    queryFn: () => api.get(`/users/${id}`).then((r) => r.data.data),
    enabled: !!id,
  });

export const useEnableUser = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) =>
      api.patch(`/users/${id}/enable`).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["admin", "users"] }),
  });
};

export const useDisableUser = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) =>
      api.patch(`/users/${id}/disable`).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["admin", "users"] }),
  });
};

export const useAssignRoles = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, roleIds }: { id: string; roleIds: string[] }) =>
      api.post(`/users/${id}/roles`, { roleIds }).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["admin", "users"] }),
  });
};
