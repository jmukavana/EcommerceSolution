import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import type { Cart } from "@/types";

export const useCart = (enabled: boolean) =>
  useQuery({
    queryKey: ["cart"],
    queryFn: () => api.get("/cart").then((r) => r.data.data as Cart),
    enabled,
  });

export const useAddToCart = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: { variantId: string; quantity: number }) =>
      api.post("/cart/items", data).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["cart"] }),
  });
};

export const useUpdateCartItem = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, quantity }: { id: string; quantity: number }) =>
      api.put(`/cart/items/${id}`, { quantity }).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["cart"] }),
  });
};

export const useRemoveCartItem = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) =>
      api.delete(`/cart/items/${id}`).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["cart"] }),
  });
};

export const useClearCart = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: () => api.delete("/cart").then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["cart"] }),
  });
};
