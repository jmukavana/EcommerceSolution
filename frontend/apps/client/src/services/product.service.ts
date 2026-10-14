import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api";
import type { Product, PageResponse } from "@/types";

interface ProductFilters {
  page?: number;
  size?: number;
  categoryId?: string;
  brandId?: string;
  search?: string;
  featured?: boolean;
  sort?: string;
  status?: string;
}

export const useProducts = (filters: ProductFilters = {}) =>
  useQuery({
    queryKey: ["products", filters],
    queryFn: () =>
      api
        .get("/products", {
          params: {
            page: filters.page ?? 0,
            size: filters.size ?? 12,
            status: "PUBLISHED",
            ...filters,
          },
        })
        .then((r) => r.data.data as PageResponse<Product>),
  });

export const useProduct = (id: string) =>
  useQuery({
    queryKey: ["product", id],
    queryFn: () =>
      api.get(`/products/${id}`).then((r) => r.data.data as Product),
    enabled: !!id,
  });

export const useFeaturedProducts = () =>
  useProducts({ featured: true, size: 8 });
export const useNewArrivals = () =>
  useProducts({ sort: "createdDate,desc", size: 8 });

export const useBrands = () =>
  useQuery({
    queryKey: ["brands"],
    queryFn: () => api.get("/brands").then((r) => r.data.data),
  });
