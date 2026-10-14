import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import { Brand } from "@/types";

export const useBrands = () =>
  useQuery<Brand[]>({
    queryKey: ["brands"],
    queryFn: () => api.get("/brands").then((r) => r.data.data),
  });

export const useCreateBrand = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Brand>) =>
      api.post("/brands", data).then((r) => r.data.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["brands"] }),
  });
};

export const useUpdateBrand = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Brand> }) =>
      api.put(`/brands/${id}`, data).then((r) => r.data.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["brands"] }),
  });
};

export const useDeleteBrand = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => api.delete(`/brands/${id}`).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["brands"] }),
  });
};
