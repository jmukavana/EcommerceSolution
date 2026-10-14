import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import type { Address } from "@/types";

export const useAddresses = (enabled: boolean) =>
  useQuery({
    queryKey: ["addresses"],
    queryFn: () => api.get("/addresses").then((r) => r.data.data as Address[]),
    enabled,
  });

export const useCreateAddress = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Address>) =>
      api.post("/addresses", data).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["addresses"] }),
  });
};

export const useUpdateAddress = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, ...data }: Partial<Address> & { id: string }) =>
      api.put(`/addresses/${id}`, data).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["addresses"] }),
  });
};

export const useDeleteAddress = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) =>
      api.delete(`/addresses/${id}`).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["addresses"] }),
  });
};

export const useSetDefaultAddress = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) =>
      api.post(`/addresses/${id}/default`).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["addresses"] }),
  });
};
