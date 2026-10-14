"use client";
import { useState } from "react";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Minus,
  Plus,
  Trash2,
  ShoppingBag,
  ArrowRight,
  Tag,
} from "lucide-react";
import {
  useCart,
  useUpdateCartItem,
  useRemoveCartItem,
  useClearCart,
} from "@/services/cart.service";
import { useAppSelector, useAppDispatch } from "@/store/hooks";
import { setCart } from "@/store/slices/cartSlice";
import { toast } from "sonner";
import api from "@/lib/api";

export default function CartPage() {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const { accessToken } = useAppSelector((s) => s.auth);
  const { data: cartData, isLoading } = useCart(!!accessToken);
  const updateItem = useUpdateCartItem();
  const removeItem = useRemoveCartItem();
  const clearCart = useClearCart();
  const [coupon, setCoupon] = useState("");
  const [couponApplied, setCouponApplied] = useState(false);

  const items = cartData?.items ?? [];
  const subtotal = cartData?.subtotal ?? 0;
  const shipping = subtotal > 50 ? 0 : 10;
  const tax = subtotal * 0.1;
  const total = subtotal + shipping + tax;

  const handleQuantity = async (itemId: string, qty: number) => {
    if (qty < 1) return;
    try {
      await updateItem.mutateAsync({ id: itemId, quantity: qty });
    } catch {
      toast.error("Failed to update quantity");
    }
  };

  const handleRemove = async (itemId: string) => {
    try {
      await removeItem.mutateAsync(itemId);
      toast.success("Item removed");
    } catch {
      toast.error("Failed to remove item");
    }
  };

  const handleValidateCoupon = async () => {
    if (!coupon.trim()) return;
    try {
      await api.get(`/discounts/coupons/validate?code=${coupon}`);
      setCouponApplied(true);
      toast.success("Coupon applied!");
    } catch {
      toast.error("Invalid coupon code");
    }
  };

  if (!accessToken)
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <main className="flex-1 flex items-center justify-center">
          <div className="text-center space-y-4">
            <ShoppingBag className="w-16 h-16 mx-auto text-gray-300" />
            <h2 className="text-xl font-semibold">
              Please sign in to view your cart
            </h2>
            <Button asChild>
              <Link href="/login">Sign In</Link>
            </Button>
          </div>
        </main>
        <Footer />
      </div>
    );

  if (isLoading)
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <main className="flex-1 max-w-7xl mx-auto px-4 py-12">
          <div className="grid lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-4">
              {[1, 2, 3].map((i) => (
                <Skeleton key={i} className="h-24 rounded-xl" />
              ))}
            </div>
            <Skeleton className="h-64 rounded-xl" />
          </div>
        </main>
        <Footer />
      </div>
    );

  if (items.length === 0)
    return (
      <div className="flex flex-col min-h-screen">
        <Header />
        <main className="flex-1 flex items-center justify-center">
          <div className="text-center space-y-4">
            <ShoppingBag className="w-16 h-16 mx-auto text-gray-300" />
            <h2 className="text-xl font-semibold">Your cart is empty</h2>
            <p className="text-gray-500">
              Looks like you haven&apos;t added anything yet.
            </p>
            <Button asChild>
              <Link href="/shop">Start Shopping</Link>
            </Button>
          </div>
        </main>
        <Footer />
      </div>
    );

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-1 max-w-7xl mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold mb-6">
          Shopping Cart ({items.length} items)
        </h1>
        <div className="grid lg:grid-cols-3 gap-8">
          {/* Items */}
          <div className="lg:col-span-2 space-y-4">
            {items.map((item) => (
              <div
                key={item.id}
                className="flex gap-4 p-4 border rounded-xl bg-white"
              >
                <div className="w-20 h-20 rounded-lg border bg-gray-50 overflow-hidden shrink-0">
                  {item.variant?.product?.images?.[0]?.url ? (
                    <img
                      src={item.variant.product.images[0].url}
                      alt=""
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-2xl">
                      📦
                    </div>
                  )}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-semibold truncate">
                    {item.variant?.product?.name}
                  </p>
                  <p className="text-sm text-gray-500">{item.variant?.name}</p>
                  <p className="font-bold text-blue-600 mt-1">
                    ${item.price?.toFixed(2)}
                  </p>
                </div>
                <div className="flex flex-col items-end gap-2">
                  <button
                    onClick={() => handleRemove(item.id)}
                    className="text-gray-400 hover:text-red-500"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => handleQuantity(item.id, item.quantity - 1)}
                      className="w-7 h-7 rounded border flex items-center justify-center hover:bg-gray-50"
                    >
                      <Minus className="w-3 h-3" />
                    </button>
                    <span className="w-6 text-center text-sm font-medium">
                      {item.quantity}
                    </span>
                    <button
                      onClick={() => handleQuantity(item.id, item.quantity + 1)}
                      className="w-7 h-7 rounded border flex items-center justify-center hover:bg-gray-50"
                    >
                      <Plus className="w-3 h-3" />
                    </button>
                  </div>
                  <p className="text-sm font-semibold">
                    ${((item.price ?? 0) * item.quantity).toFixed(2)}
                  </p>
                </div>
              </div>
            ))}
            <div className="flex justify-between pt-2">
              <Button variant="outline" asChild>
                <Link href="/shop">Continue Shopping</Link>
              </Button>
              <Button
                variant="ghost"
                onClick={() => clearCart.mutateAsync()}
                className="text-red-500 hover:text-red-700"
              >
                Clear Cart
              </Button>
            </div>
          </div>

          {/* Summary */}
          <div className="space-y-4">
            <div className="border rounded-xl p-5 space-y-4 bg-white">
              <h2 className="font-semibold text-lg">Order Summary</h2>
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-500">Subtotal</span>
                  <span>${subtotal.toFixed(2)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Shipping</span>
                  <span>
                    {shipping === 0 ? (
                      <Badge variant="secondary">FREE</Badge>
                    ) : (
                      `$${shipping.toFixed(2)}`
                    )}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-500">Tax (10%)</span>
                  <span>${tax.toFixed(2)}</span>
                </div>
                <Separator />
                <div className="flex justify-between font-bold text-base">
                  <span>Total</span>
                  <span>${total.toFixed(2)}</span>
                </div>
              </div>

              {/* Coupon */}
              <div className="space-y-2 pt-2">
                <p className="text-sm font-medium flex items-center gap-1">
                  <Tag className="w-4 h-4" /> Coupon Code
                </p>
                <div className="flex gap-2">
                  <Input
                    placeholder="SAVE10"
                    value={coupon}
                    onChange={(e) => setCoupon(e.target.value)}
                    disabled={couponApplied}
                    className="h-9"
                  />
                  <Button
                    size="sm"
                    variant="outline"
                    onClick={handleValidateCoupon}
                    disabled={couponApplied || !coupon}
                  >
                    {couponApplied ? "✓" : "Apply"}
                  </Button>
                </div>
              </div>

              <Button
                className="w-full"
                size="lg"
                onClick={() => router.push("/checkout")}
              >
                Checkout <ArrowRight className="w-4 h-4 ml-2" />
              </Button>
              {shipping > 0 && (
                <p className="text-xs text-center text-gray-500">
                  Add ${(50 - subtotal).toFixed(2)} more for free shipping
                </p>
              )}
            </div>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
