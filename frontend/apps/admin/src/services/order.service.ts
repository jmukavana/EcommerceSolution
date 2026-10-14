import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import { Order, PageResponse } from "@/types";

export const useAdminOrders = (page = 0, size = 10, status?: string) =>
  useQuery<PageResponse<Order>>({
    queryKey: ["admin", "orders", page, size, status],
    queryFn: () =>
      api
        .get("/orders", {
          params: { page, size, ...(status ? { status } : {}) },
        })
        .then((r) => r.data.data),
  });

export const useOrder = (id: string) =>
  useQuery<Order>({
    queryKey: ["admin", "orders", id],
    queryFn: () => api.get(`/orders/${id}`).then((r) => r.data.data),
    enabled: !!id,
  });

export const useUpdateOrderStatus = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, status }: { id: string; status: string }) =>
      api.patch(`/orders/${id}/status`, { status }).then((r) => r.data.data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ["admin", "orders"] });
      qc.invalidateQueries({ queryKey: ["admin", "orders", variables.id] });
    },
  });
};
