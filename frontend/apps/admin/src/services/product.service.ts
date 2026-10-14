import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import { Product, PageResponse } from "@/types";

export const useAdminProducts = (
  page = 0,
  size = 10,
  search?: string,
  status?: string,
) =>
  useQuery<PageResponse<Product>>({
    queryKey: ["admin", "products", page, size, search, status],
    queryFn: () =>
      api
        .get("/products", {
          params: {
            page,
            size,
            ...(search ? { search } : {}),
            ...(status ? { status } : {}),
          },
        })
        .then((r) => r.data.data),
  });

export const useProduct = (id: string) =>
  useQuery<Product>({
    queryKey: ["admin", "products", id],
    queryFn: () => api.get(`/products/${id}`).then((r) => r.data.data),
    enabled: !!id,
  });

export const useCreateProduct = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: Partial<Product>) =>
      api.post("/products", data).then((r) => r.data.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["admin", "products"] }),
  });
};

export const useUpdateProduct = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: Partial<Product> }) =>
      api.put(`/products/${id}`, data).then((r) => r.data.data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({ queryKey: ["admin", "products"] });
      qc.invalidateQueries({ queryKey: ["admin", "products", variables.id] });
    },
  });
};

export const useDeleteProduct = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) =>
      api.delete(`/products/${id}`).then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["admin", "products"] }),
  });
};

export const useCreateVariant = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      productId,
      data,
    }: {
      productId: string;
      data: Record<string, unknown>;
    }) =>
      api
        .post(`/products/${productId}/variants`, data)
        .then((r) => r.data.data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({
        queryKey: ["admin", "products", variables.productId],
      });
    },
  });
};

export const useAddProductImage = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      productId,
      data,
    }: {
      productId: string;
      data: Record<string, unknown>;
    }) =>
      api.post(`/products/${productId}/images`, data).then((r) => r.data.data),
    onSuccess: (_data, variables) => {
      qc.invalidateQueries({
        queryKey: ["admin", "products", variables.productId],
      });
    },
  });
};
