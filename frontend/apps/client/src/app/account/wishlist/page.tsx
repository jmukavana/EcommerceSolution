"use client";
import Link from "next/link";
import { Heart, ShoppingCart } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  useWishlist,
  useRemoveFromWishlist,
} from "@/services/wishlist.service";
import { useAddToCart } from "@/services/cart.service";
import { useAppSelector } from "@/store/hooks";
import { toast } from "sonner";

export default function WishlistPage() {
  const { accessToken } = useAppSelector((s) => s.auth);
  const { data: wishlist, isLoading } = useWishlist(!!accessToken);
  const removeFromWishlist = useRemoveFromWishlist();
  const addToCart = useAddToCart();

  const items = wishlist?.items ?? [];

  const handleRemove = async (productId: string) => {
    if (!wishlist) return;
    try {
      await removeFromWishlist.mutateAsync({
        wishlistId: wishlist.id,
        productId,
      });
      toast.success("Removed");
    } catch {
      toast.error("Failed to remove");
    }
  };

  const handleMoveToCart = async (productId: string) => {
    const item = items.find((i) => i.product.id === productId);
    const variant =
      item?.product.variants.find((v) => v.isDefault) ??
      item?.product.variants[0];
    if (!variant) {
      toast.error("No variant available");
      return;
    }
    try {
      await addToCart.mutateAsync({ variantId: variant.id, quantity: 1 });
      await handleRemove(productId);
      toast.success("Moved to cart!");
    } catch {
      toast.error("Failed");
    }
  };

  if (isLoading)
    return (
      <div className="space-y-3">
        <h1 className="text-xl font-bold mb-4">My Wishlist</h1>
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {[1, 2, 3].map((i) => (
            <Skeleton key={i} className="h-64 rounded-xl" />
          ))}
        </div>
      </div>
    );

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">My Wishlist ({items.length})</h1>
      {items.length === 0 ? (
        <div className="text-center py-16 border rounded-xl text-gray-400">
          <Heart className="w-12 h-12 mx-auto mb-3" />
          <p className="font-medium">Your wishlist is empty</p>
          <Link
            href="/shop"
            className="text-blue-600 text-sm hover:underline mt-1 inline-block"
          >
            Browse products
          </Link>
        </div>
      ) : (
        <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {items.map(({ id, product }) => {
            const variant =
              product.variants.find((v) => v.isDefault) ?? product.variants[0];
            const image =
              product.images.find((i) => i.primary) ?? product.images[0];
            const discountPct =
              variant?.compareAtPrice && variant.compareAtPrice > variant.price
                ? Math.round(
                    ((variant.compareAtPrice - variant.price) /
                      variant.compareAtPrice) *
                      100,
                  )
                : null;

            return (
              <div key={id} className="border rounded-xl overflow-hidden group">
                <Link
                  href={`/shop/${product.id}`}
                  className="block aspect-square bg-gray-50 overflow-hidden"
                >
                  {image?.url ? (
                    <img
                      src={image.url}
                      alt={product.name}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-4xl">
                      📦
                    </div>
                  )}
                </Link>
                <div className="p-3">
                  {discountPct && (
                    <Badge className="bg-red-500 text-xs mb-1">
                      -{discountPct}%
                    </Badge>
                  )}
                  <Link
                    href={`/shop/${product.id}`}
                    className="font-medium text-sm hover:text-blue-600 line-clamp-2 block"
                  >
                    {product.name}
                  </Link>
                  <div className="flex items-center gap-2 mt-1">
                    <span className="font-bold text-blue-600">
                      ${variant?.price.toFixed(2)}
                    </span>
                    {variant?.compareAtPrice && (
                      <span className="text-xs text-gray-400 line-through">
                        ${variant.compareAtPrice.toFixed(2)}
                      </span>
                    )}
                  </div>
                  <div className="flex gap-2 mt-3">
                    <Button
                      size="sm"
                      className="flex-1 h-8 text-xs"
                      onClick={() => handleMoveToCart(product.id)}
                    >
                      <ShoppingCart className="w-3 h-3 mr-1" /> Add to Cart
                    </Button>
                    <Button
                      size="sm"
                      variant="ghost"
                      className="h-8 text-red-400 hover:text-red-600 hover:bg-red-50 px-2"
                      onClick={() => handleRemove(product.id)}
                    >
                      <Heart className="w-3 h-3 fill-current" />
                    </Button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}
