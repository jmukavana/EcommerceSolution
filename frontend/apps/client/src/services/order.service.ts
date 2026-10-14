import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import type { Order } from "@/types";

export const useMyOrders = (enabled: boolean) =>
  useQuery({
    queryKey: ["orders", "my"],
    queryFn: () => api.get("/orders/my").then((r) => r.data.data as Order[]),
    enabled,
  });

export const useOrder = (id: string) =>
  useQuery({
    queryKey: ["order", id],
    queryFn: () => api.get(`/orders/${id}`).then((r) => r.data.data as Order),
    enabled: !!id,
  });

export const usePlaceOrder = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: {
      addressId: string;
      paymentMethod: string;
      couponCode?: string;
    }) => api.post("/orders", data).then((r) => r.data.data as Order),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["cart"] });
      qc.invalidateQueries({ queryKey: ["orders"] });
    },
  });
};

export const useCancelOrder = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) =>
      api.patch(`/orders/${id}/cancel`).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["orders"] }),
  });
};
