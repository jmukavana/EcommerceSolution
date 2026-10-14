import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import { InventoryItem } from "@/types";

export const useInventory = () =>
  useQuery<InventoryItem[]>({
    queryKey: ["inventory"],
    queryFn: () => api.get("/inventory").then((r) => r.data.data),
  });

export const useLowStockItems = () =>
  useQuery<InventoryItem[]>({
    queryKey: ["inventory", "low-stock"],
    queryFn: () => api.get("/inventory/low-stock").then((r) => r.data.data),
  });

export const useReceiveStock = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: {
      variantId: string;
      warehouseId: string;
      quantity: number;
      notes?: string;
    }) => api.post("/inventory/receive", data).then((r) => r.data.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["inventory"] }),
  });
};

export const useAdjustStock = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      id,
      quantity,
      reason,
    }: {
      id: string;
      quantity: number;
      reason: string;
    }) =>
      api
        .post(`/inventory/${id}/adjust`, { quantity, reason })
        .then((r) => r.data.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["inventory"] }),
  });
};
