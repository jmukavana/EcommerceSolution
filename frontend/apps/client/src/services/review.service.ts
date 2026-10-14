import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import type { Review } from "@/types";

export const useProductReviews = (productId: string) =>
  useQuery({
    queryKey: ["reviews", productId],
    queryFn: () =>
      api
        .get("/reviews", { params: { productId } })
        .then((r) => r.data.data as Review[]),
    enabled: !!productId,
  });

export const useCreateReview = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: {
      productId: string;
      rating: number;
      comment: string;
    }) => api.post("/reviews", data).then((r) => r.data),
    onSuccess: (_, v) =>
      qc.invalidateQueries({ queryKey: ["reviews", v.productId] }),
  });
};
