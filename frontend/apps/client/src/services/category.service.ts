import { useQuery } from "@tanstack/react-query";
import api from "@/lib/api";
import type { Category } from "@/types";

export const useCategories = () =>
  useQuery({
    queryKey: ["categories"],
    queryFn: () =>
      api.get("/categories").then((r) => r.data.data as Category[]),
  });

export const useRootCategories = () =>
  useQuery({
    queryKey: ["categories", "roots"],
    queryFn: () =>
      api.get("/categories/roots").then((r) => r.data.data as Category[]),
  });

export const useCategoryChildren = (id: string) =>
  useQuery({
    queryKey: ["categories", id, "children"],
    queryFn: () =>
      api
        .get(`/categories/${id}/children`)
        .then((r) => r.data.data as Category[]),
    enabled: !!id,
  });
