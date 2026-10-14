import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import api from "@/lib/api";
import type { Wishlist } from "@/types";

export const useWishlist = (enabled: boolean) =>
  useQuery({
    queryKey: ["wishlist"],
    queryFn: () =>
      api.get("/wishlists/my").then((r) => r.data.data as Wishlist),
    enabled,
  });

export const useAddToWishlist = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      wishlistId,
      productId,
    }: {
      wishlistId: string;
      productId: string;
    }) =>
      api
        .post(`/wishlists/${wishlistId}/items`, { productId })
        .then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["wishlist"] }),
  });
};

export const useRemoveFromWishlist = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: ({
      wishlistId,
      productId,
    }: {
      wishlistId: string;
      productId: string;
    }) =>
      api
        .delete(`/wishlists/${wishlistId}/items/${productId}`)
        .then((r) => r.data),
    onSuccess: () => qc.invalidateQueries({ queryKey: ["wishlist"] }),
  });
};
