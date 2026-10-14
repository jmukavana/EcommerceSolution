"use client";
import Link from "next/link";
import { Heart, Star, Plus, ShoppingCart } from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import { useAddToCart } from "@/services/cart.service";
import {
  useWishlist,
  useAddToWishlist,
  useRemoveFromWishlist,
} from "@/services/wishlist.service";
import { useAppSelector } from "@/store/hooks";
import { cn } from "@/lib/utils";
import { toast } from "sonner";
import type { Product } from "@/types";

interface ProductCardProps {
  product: Product;
}

export function ProductCard({ product }: ProductCardProps) {
  const { accessToken } = useAppSelector((s) => s.auth);
  const addToCart = useAddToCart();
  const { data: wishlist } = useWishlist(!!accessToken);
  const addToWishlist = useAddToWishlist();
  const removeFromWishlist = useRemoveFromWishlist();

  const defaultVariant =
    product.variants.find((v) => v.isDefault) ?? product.variants[0];
  const primaryImage =
    product.images.find((i) => i.primary) ?? product.images[0];

  const discountPct =
    defaultVariant?.compareAtPrice &&
    defaultVariant.compareAtPrice > defaultVariant.price
      ? Math.round(
          ((defaultVariant.compareAtPrice - defaultVariant.price) /
            defaultVariant.compareAtPrice) *
            100,
        )
      : null;

  const isWishlisted =
    wishlist?.items.some((wi) => wi.product.id === product.id) ?? false;

  const handleAddToCart = async () => {
    if (!accessToken) {
      toast.error("Please login to add items to cart");
      return;
    }
    if (!defaultVariant) {
      toast.error("No variant available");
      return;
    }
    try {
      await addToCart.mutateAsync({
        variantId: defaultVariant.id,
        quantity: 1,
      });
      toast.success(`${product.name} added to cart`);
    } catch {
      toast.error("Failed to add to cart");
    }
  };

  const handleWishlistToggle = async () => {
    if (!accessToken) {
      toast.error("Please login to manage wishlist");
      return;
    }
    if (!wishlist) {
      toast.error("Wishlist not available");
      return;
    }
    try {
      if (isWishlisted) {
        await removeFromWishlist.mutateAsync({
          wishlistId: wishlist.id,
          productId: product.id,
        });
        toast.success("Removed from wishlist");
      } else {
        await addToWishlist.mutateAsync({
          wishlistId: wishlist.id,
          productId: product.id,
        });
        toast.success("Added to wishlist");
      }
    } catch {
      toast.error("Failed to update wishlist");
    }
  };

  return (
    <article className="group bg-white border-[1.5px] border-gray-200 rounded-xl overflow-hidden transition-all hover:border-blue-200 hover:shadow-md hover:-translate-y-0.5 relative">
      {/* Image */}
      <div className="aspect-[4/3] bg-gray-50 flex items-center justify-center relative overflow-hidden">
        {primaryImage?.url ? (
          <img
            src={primaryImage.url}
            alt={primaryImage.alt || product.name}
            className="w-full h-full object-cover"
          />
        ) : (
          <ShoppingCart className="h-12 w-12 text-gray-200" />
        )}

        {discountPct && (
          <span className="absolute top-3 left-3 bg-red-500 text-white text-[10px] font-bold px-2 py-0.5 rounded z-10">
            -{discountPct}%
          </span>
        )}

        <button
          onClick={handleWishlistToggle}
          className={cn(
            "absolute top-3 right-3 w-8 h-8 rounded-full bg-white border flex items-center justify-center transition-all z-10",
            isWishlisted
              ? "border-red-300 text-red-500"
              : "border-gray-200 text-gray-300 hover:bg-red-50 hover:border-red-400 hover:text-red-500",
          )}
          aria-label={isWishlisted ? "Remove from wishlist" : "Add to wishlist"}
        >
          <Heart
            className={cn("h-3.5 w-3.5", isWishlisted && "fill-current")}
          />
        </button>
      </div>

      {/* Info */}
      <div className="p-4">
        {product.categories[0] && (
          <div className="text-[10px] text-gray-400 tracking-[1px] uppercase font-medium mb-1">
            {product.categories[0].name}
          </div>
        )}

        <Link
          href={`/shop/${product.id}`}
          className="block text-sm font-medium text-gray-900 hover:text-blue-600 line-clamp-2 leading-snug mb-2 transition-colors"
        >
          {product.name}
        </Link>

        {/* Stock indicator */}
        <div className="flex items-center gap-1 text-[10px] font-medium text-green-700 mb-2">
          <span className="w-1.5 h-1.5 rounded-full bg-green-500 inline-block" />
          In Stock
        </div>

        {/* Rating */}
        {product.totalReviews > 0 && (
          <div className="flex items-center gap-1 mb-3">
            <div className="flex">
              {[1, 2, 3, 4, 5].map((star) => (
                <Star
                  key={star}
                  className={cn(
                    "h-3 w-3",
                    star <= Math.round(product.averageRating)
                      ? "fill-amber-400 text-amber-400"
                      : "fill-gray-200 text-gray-200",
                  )}
                />
              ))}
            </div>
            <span className="text-[10px] text-gray-400 ml-0.5">
              ({product.totalReviews})
            </span>
          </div>
        )}

        {/* Price + Add to Cart */}
        <div className="flex items-center justify-between">
          <div>
            {defaultVariant && (
              <>
                <span className="text-base font-bold text-gray-900">
                  ${defaultVariant.price.toFixed(2)}
                </span>
                {defaultVariant.compareAtPrice &&
                  defaultVariant.compareAtPrice > defaultVariant.price && (
                    <span className="text-xs text-gray-400 line-through ml-1.5">
                      ${defaultVariant.compareAtPrice.toFixed(2)}
                    </span>
                  )}
              </>
            )}
          </div>
          <button
            onClick={handleAddToCart}
            disabled={addToCart.isPending || !defaultVariant}
            className="w-9 h-9 rounded-lg bg-blue-600 hover:bg-blue-700 text-white flex items-center justify-center shrink-0 transition-all active:scale-95 disabled:opacity-50"
            aria-label={`Add ${product.name} to cart`}
          >
            <Plus className="h-4 w-4" />
          </button>
        </div>
      </div>
    </article>
  );
}
