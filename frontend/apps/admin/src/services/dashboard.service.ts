import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api";
import { Order } from "@/types";

export const useDashboardStats = () =>
  useQuery({
    queryKey: ["dashboard", "stats"],
    queryFn: async () => {
      const [orders] = await Promise.all([
        api.get("/orders", { params: { page: 0, size: 1 } }),
      ]);
      return {
        totalOrders: orders.data.data?.totalElements ?? 0,
        totalRevenue: 0,
        totalProducts: 0,
        totalUsers: 0,
        revenueChange: 0,
        ordersChange: 0,
      };
    },
  });

export const useRecentOrders = () =>
  useQuery<Order[]>({
    queryKey: ["orders", "recent"],
    queryFn: () =>
      api
        .get("/orders", {
          params: { page: 0, size: 5, sort: "createdDate,desc" },
        })
        .then((r) => r.data.data?.content ?? []),
  });
