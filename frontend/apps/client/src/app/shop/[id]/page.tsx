"use client";
import { useState, use } from "react";
import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import { ShoppingCart, Heart, Star, Minus, Plus } from "lucide-react";
import { useProduct } from "@/services/product.service";
import { useAddToCart } from "@/services/cart.service";
import {
  useWishlist,
  useAddToWishlist,
  useRemoveFromWishlist,
} from "@/services/wishlist.service";
import { useProductReviews, useCreateReview } from "@/services/review.service";
import { useAppSelector } from "@/store/hooks";
import { toast } from "sonner";
import { cn } from "@/lib/utils";
import type { ProductVariant } from "@/types";

interface Props {
  params: Promise<{ id: string }>;
}

export default function ProductDetailPage({ params }: Props) {
  const { id } = use(params);
  const { data: product, isLoading } = useProduct(id);
  const { accessToken } = useAppSelector((s) => s.auth);
  const { data: wishlist } = useWishlist(!!accessToken);
  const { data: reviews = [] } = useProductReviews(id);
  const addToCart = useAddToCart();
  const addToWishlist = useAddToWishlist();
  const removeFromWishlist = useRemoveFromWishlist();
  const createReview = useCreateReview();

  const [selectedVariant, setSelectedVariant] = useState<ProductVariant | null>(
    null,
  );
  const [quantity, setQuantity] = useState(1);
  const [activeImage, setActiveImage] = useState(0);
  const [reviewRating, setReviewRating] = useState(5);
  const [reviewComment, setReviewComment] = useState("");

  const variant =
    selectedVariant ??
    product?.variants.find((v) => v.isDefault) ??
    product?.variants[0];
  const isWishlisted =
    wishlist?.items.some((wi) => wi.product.id === id) ?? false;

  const discountPct =
    variant?.compareAtPrice && variant.compareAtPrice > variant.price
      ? Math.round(
          ((variant.compareAtPrice - variant.price) / variant.compareAtPrice) *
            100,
        )
      : null;

  const handleAddToCart = async () => {
    if (!accessToken) {
      toast.error("Please login to add items to cart");
      return;
    }
    if (!variant) return;
    try {
      await addToCart.mutateAsync({ variantId: variant.id, quantity });
      toast.success("Added to cart!");
    } catch {
      toast.error("Failed to add to cart");
    }
  };

  const handleWishlist = async () => {
    if (!accessToken) {
      toast.error("Please login");
      return;
    }
    try {
      if (isWishlisted && wishlist) {
        await removeFromWishlist.mutateAsync({
          wishlistId: wishlist.id,
          productId: id,
        });
        toast.success("Removed from wishlist");
      } else if (wishlist) {
        await addToWishlist.mutateAsync({
          wishlistId: wishlist.id,
          productId: id,
        });
        toast.success("Added to wishlist");
      }
    } catch {
      toast.error("Failed to update wishlist");
    }
  };

  const handleReviewSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!accessToken) {
      toast.error("Please login to review");
      return;
    }
    try {
      await createReview.mutateAsync({
        productId: id,
        rating: reviewRating,
        comment: reviewComment,
      });
      toast.success("Review submitted!");
      setReviewComment("");
    } catch {
      toast.error("Failed to submit review");
    }
  };

  if (isLoading)
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <main className="flex-1 max-w-7xl mx-auto px-4 py-12">
          <div className="grid md:grid-cols-2 gap-12">
            <Skeleton className="aspect-square rounded-xl" />
            <div className="space-y-4">
              <Skeleton className="h-8 w-3/4" />
              <Skeleton className="h-6 w-1/2" />
              <Skeleton className="h-10 w-1/3" />
              <Skeleton className="h-12 w-full" />
            </div>
          </div>
        </main>
        <Footer />
      </div>
    );

  if (!product) return null;

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-1 max-w-7xl mx-auto px-4 py-8">
        <Breadcrumb className="mb-6">
          <BreadcrumbList>
            <BreadcrumbItem>
              <BreadcrumbLink href="/">Home</BreadcrumbLink>
            </BreadcrumbItem>
            <BreadcrumbSeparator />
            <BreadcrumbItem>
              <BreadcrumbLink href="/shop">Shop</BreadcrumbLink>
            </BreadcrumbItem>
            <BreadcrumbSeparator />
            <BreadcrumbItem>
              <BreadcrumbPage>{product.name}</BreadcrumbPage>
            </BreadcrumbItem>
          </BreadcrumbList>
        </Breadcrumb>

        <div className="grid md:grid-cols-2 gap-12 mb-16">
          {/* Images */}
          <div className="space-y-4">
            <div className="aspect-square relative overflow-hidden rounded-xl border bg-gray-50">
              {product.images[activeImage] ? (
                <img
                  src={product.images[activeImage].url}
                  alt={product.name}
                  className="w-full h-full object-cover"
                />
              ) : (
                <div className="w-full h-full flex items-center justify-center text-gray-300 text-6xl">
                  📦
                </div>
              )}
              {discountPct && (
                <Badge className="absolute top-4 left-4 bg-red-500">
                  -{discountPct}%
                </Badge>
              )}
            </div>
            {product.images.length > 1 && (
              <div className="flex gap-2 overflow-x-auto">
                {product.images.map((img, i) => (
                  <button
                    key={img.id}
                    onClick={() => setActiveImage(i)}
                    className={cn(
                      "w-16 h-16 rounded-lg border overflow-hidden shrink-0",
                      i === activeImage && "ring-2 ring-blue-500",
                    )}
                  >
                    <img
                      src={img.url}
                      alt=""
                      className="w-full h-full object-cover"
                    />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* Info */}
          <div className="space-y-6">
            {product.categories[0] && (
              <Badge variant="secondary">{product.categories[0].name}</Badge>
            )}
            <h1 className="text-3xl font-bold">{product.name}</h1>
            {product.brand && (
              <p className="text-gray-500">
                by{" "}
                <span className="font-medium text-gray-700">
                  {product.brand.name}
                </span>
              </p>
            )}

            {/* Rating */}
            <div className="flex items-center gap-2">
              <div className="flex">
                {[1, 2, 3, 4, 5].map((s) => (
                  <Star
                    key={s}
                    className={cn(
                      "w-4 h-4",
                      s <= Math.round(product.averageRating)
                        ? "fill-yellow-400 text-yellow-400"
                        : "text-gray-200",
                    )}
                  />
                ))}
              </div>
              <span className="text-sm text-gray-500">
                ({product.totalReviews} reviews)
              </span>
            </div>

            {/* Price */}
            <div className="flex items-center gap-3">
              <span className="text-3xl font-bold text-blue-600">
                ${variant?.price.toFixed(2)}
              </span>
              {variant?.compareAtPrice && (
                <span className="text-xl text-gray-400 line-through">
                  ${variant.compareAtPrice.toFixed(2)}
                </span>
              )}
            </div>

            {/* Variants */}
            {product.type === "VARIABLE" && product.variants.length > 1 && (
              <div className="space-y-2">
                <p className="font-medium">Options</p>
                <div className="flex flex-wrap gap-2">
                  {product.variants.map((v) => (
                    <button
                      key={v.id}
                      onClick={() => setSelectedVariant(v)}
                      className={cn(
                        "px-3 py-1.5 border rounded-lg text-sm transition-colors",
                        variant?.id === v.id
                          ? "border-blue-500 bg-blue-50 text-blue-600 font-medium"
                          : "hover:border-gray-400",
                      )}
                    >
                      {v.name}
                    </button>
                  ))}
                </div>
              </div>
            )}

            {/* Quantity */}
            <div className="space-y-2">
              <p className="font-medium">Quantity</p>
              <div className="flex items-center gap-3 w-32">
                <button
                  onClick={() => setQuantity((q) => Math.max(1, q - 1))}
                  className="w-8 h-8 rounded-lg border flex items-center justify-center hover:bg-gray-50"
                >
                  <Minus className="w-4 h-4" />
                </button>
                <span className="font-semibold w-6 text-center">
                  {quantity}
                </span>
                <button
                  onClick={() => setQuantity((q) => q + 1)}
                  className="w-8 h-8 rounded-lg border flex items-center justify-center hover:bg-gray-50"
                >
                  <Plus className="w-4 h-4" />
                </button>
              </div>
            </div>

            {/* Actions */}
            <div className="flex gap-3">
              <Button
                onClick={handleAddToCart}
                disabled={addToCart.isPending}
                size="lg"
                className="flex-1"
              >
                <ShoppingCart className="w-5 h-5 mr-2" />
                {addToCart.isPending ? "Adding..." : "Add to Cart"}
              </Button>
              <Button onClick={handleWishlist} variant="outline" size="lg">
                <Heart
                  className={cn(
                    "w-5 h-5",
                    isWishlisted && "fill-red-500 text-red-500",
                  )}
                />
              </Button>
            </div>

            {product.shortDescription && (
              <p className="text-gray-600">{product.shortDescription}</p>
            )}
          </div>
        </div>

        {/* Tabs: Description & Reviews */}
        <Tabs defaultValue="description">
          <TabsList>
            <TabsTrigger value="description">Description</TabsTrigger>
            <TabsTrigger value="reviews">
              Reviews ({product.totalReviews})
            </TabsTrigger>
          </TabsList>
          <TabsContent
            value="description"
            className="mt-6 prose max-w-none text-gray-700"
          >
            <p>
              {product.description ||
                product.shortDescription ||
                "No description available."}
            </p>
          </TabsContent>
          <TabsContent value="reviews" className="mt-6 space-y-6">
            {reviews.map((r) => (
              <div key={r.id} className="border rounded-xl p-4">
                <div className="flex items-center gap-3 mb-2">
                  <div className="w-8 h-8 rounded-full bg-blue-100 flex items-center justify-center text-sm font-bold text-blue-600">
                    {r.user.firstName[0]}
                  </div>
                  <div>
                    <p className="font-medium text-sm">
                      {r.user.firstName} {r.user.lastName}
                    </p>
                    <div className="flex">
                      {[1, 2, 3, 4, 5].map((s) => (
                        <Star
                          key={s}
                          className={cn(
                            "w-3 h-3",
                            s <= r.rating
                              ? "fill-yellow-400 text-yellow-400"
                              : "text-gray-200",
                          )}
                        />
                      ))}
                    </div>
                  </div>
                  <span className="text-xs text-gray-400 ml-auto">
                    {new Date(r.createdDate).toLocaleDateString()}
                  </span>
                </div>
                <p className="text-gray-600 text-sm">{r.comment}</p>
              </div>
            ))}

            {accessToken && (
              <div className="border rounded-xl p-5">
                <h4 className="font-semibold mb-4">Write a Review</h4>
                <form onSubmit={handleReviewSubmit} className="space-y-4">
                  <div>
                    <p className="text-sm font-medium mb-1">Rating</p>
                    <div className="flex gap-1">
                      {[1, 2, 3, 4, 5].map((s) => (
                        <button
                          key={s}
                          type="button"
                          onClick={() => setReviewRating(s)}
                        >
                          <Star
                            className={cn(
                              "w-6 h-6",
                              s <= reviewRating
                                ? "fill-yellow-400 text-yellow-400"
                                : "text-gray-300",
                            )}
                          />
                        </button>
                      ))}
                    </div>
                  </div>
                  <textarea
                    value={reviewComment}
                    onChange={(e) => setReviewComment(e.target.value)}
                    placeholder="Share your experience..."
                    required
                    className="w-full border rounded-lg p-3 text-sm resize-none h-24 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                  <Button type="submit" disabled={createReview.isPending}>
                    {createReview.isPending ? "Submitting..." : "Submit Review"}
                  </Button>
                </form>
              </div>
            )}
          </TabsContent>
        </Tabs>
      </main>
      <Footer />
    </div>
  );
}
