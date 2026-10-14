"use client";
import { use } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  ArrowLeft,
  MapPin,
  CreditCard,
  CheckCircle2,
  Clock,
  Package,
  Truck,
  Home,
  type LucideIcon,
} from "lucide-react";
import { useOrder, useCancelOrder } from "@/services/order.service";
import { cn } from "@/lib/utils";
import { toast } from "sonner";

interface Props {
  params: Promise<{ id: string }>;
}

const statusSteps = [
  "PENDING",
  "CONFIRMED",
  "PROCESSING",
  "SHIPPED",
  "DELIVERED",
];
const statusColors: Record<string, string> = {
  PENDING: "text-yellow-600 bg-yellow-50 border-yellow-200",
  CONFIRMED: "text-blue-600 bg-blue-50 border-blue-200",
  PROCESSING: "text-indigo-600 bg-indigo-50 border-indigo-200",
  SHIPPED: "text-purple-600 bg-purple-50 border-purple-200",
  DELIVERED: "text-green-600 bg-green-50 border-green-200",
  CANCELLED: "text-red-600 bg-red-50 border-red-200",
};
const stepIcons: LucideIcon[] = [Clock, CheckCircle2, Package, Truck, Home];

export default function OrderDetailPage({ params }: Props) {
  const { id } = use(params);
  const { data: order, isLoading } = useOrder(id);
  const cancelOrder = useCancelOrder();

  const handleCancel = async () => {
    if (!confirm("Cancel this order?")) return;
    try {
      await cancelOrder.mutateAsync(id);
      toast.success("Order cancelled");
    } catch {
      toast.error("Cannot cancel order");
    }
  };

  if (isLoading)
    return (
      <div className="space-y-4">
        <Skeleton className="h-8 w-48" />
        <Skeleton className="h-32 rounded-xl" />
        <Skeleton className="h-48 rounded-xl" />
      </div>
    );
  if (!order) return null;

  const currentStep =
    order.status === "CANCELLED" ? -1 : statusSteps.indexOf(order.status);

  return (
    <div className="space-y-6">
      <div className="flex items-center gap-3">
        <Link href="/account/orders">
          <Button variant="ghost" size="sm">
            <ArrowLeft className="w-4 h-4 mr-1" />
            Orders
          </Button>
        </Link>
        <h1 className="text-xl font-bold">Order #{order.orderNumber}</h1>
        <span
          className={cn(
            "text-xs px-3 py-1 rounded-full border font-medium",
            statusColors[order.status] ?? "",
          )}
        >
          {order.status}
        </span>
      </div>

      {/* Timeline */}
      {order.status !== "CANCELLED" && (
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center justify-between">
              {statusSteps.map((s, i) => {
                const Icon = stepIcons[i] as LucideIcon;
                const done = i <= currentStep;
                return (
                  <div
                    key={s}
                    className="flex flex-col items-center gap-1 flex-1"
                  >
                    <div
                      className={cn(
                        "w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold border-2 transition-colors",
                        done
                          ? "bg-blue-600 border-blue-600 text-white"
                          : "bg-white border-gray-200 text-gray-400",
                      )}
                    >
                      <Icon className="w-4 h-4" />
                    </div>
                    <span
                      className={cn(
                        "text-xs text-center",
                        done ? "text-blue-600 font-medium" : "text-gray-400",
                      )}
                    >
                      {s}
                    </span>
                    {i < statusSteps.length - 1 && (
                      <div className={cn("absolute hidden")} />
                    )}
                  </div>
                );
              })}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Items */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">
            Items ({order.items.length})
          </CardTitle>
        </CardHeader>
        <CardContent className="space-y-3">
          {order.items.map((item) => (
            <div key={item.id} className="flex justify-between items-start">
              <div>
                <p className="font-medium text-sm">{item.productName}</p>
                <p className="text-xs text-gray-500">
                  {item.variantName} · Qty: {item.quantity}
                </p>
              </div>
              <p className="font-semibold text-sm">
                ${(item.price * item.quantity).toFixed(2)}
              </p>
            </div>
          ))}
          <Separator />
          <div className="space-y-1 text-sm">
            <div className="flex justify-between text-gray-500">
              <span>Subtotal</span>
              <span>${order.subtotal.toFixed(2)}</span>
            </div>
            {order.discountAmount > 0 && (
              <div className="flex justify-between text-green-600">
                <span>Discount</span>
                <span>-${order.discountAmount.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-gray-500">
              <span>Shipping</span>
              <span>${order.shippingAmount.toFixed(2)}</span>
            </div>
            <div className="flex justify-between text-gray-500">
              <span>Tax</span>
              <span>${order.taxAmount.toFixed(2)}</span>
            </div>
            <Separator />
            <div className="flex justify-between font-bold text-base">
              <span>Total</span>
              <span>${order.total.toFixed(2)}</span>
            </div>
          </div>
        </CardContent>
      </Card>

      <div className="grid md:grid-cols-2 gap-4">
        {/* Shipping */}
        {order.shippingAddress && (
          <Card>
            <CardHeader>
              <CardTitle className="text-sm flex items-center gap-2">
                <MapPin className="w-4 h-4" />
                Shipping Address
              </CardTitle>
            </CardHeader>
            <CardContent className="text-sm text-gray-600 space-y-1">
              <p>{order.shippingAddress.street}</p>
              <p>
                {order.shippingAddress.city}, {order.shippingAddress.state}{" "}
                {order.shippingAddress.postalCode}
              </p>
              <p>{order.shippingAddress.country}</p>
            </CardContent>
          </Card>
        )}
        {/* Payment */}
        <Card>
          <CardHeader>
            <CardTitle className="text-sm flex items-center gap-2">
              <CreditCard className="w-4 h-4" />
              Order Info
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm text-gray-600 space-y-1">
            <p>Date: {new Date(order.createdDate).toLocaleString()}</p>
          </CardContent>
        </Card>
      </div>

      {order.status === "PENDING" && (
        <Button
          variant="destructive"
          onClick={handleCancel}
          disabled={cancelOrder.isPending}
        >
          {cancelOrder.isPending ? "Cancelling…" : "Cancel Order"}
        </Button>
      )}
    </div>
  );
}
