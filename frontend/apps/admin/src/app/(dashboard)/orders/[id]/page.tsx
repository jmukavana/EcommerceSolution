"use client";
import { use } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Separator } from "@/components/ui/separator";
import { Skeleton } from "@/components/ui/skeleton";
import { ArrowLeft, MapPin, CreditCard } from "lucide-react";
import { useOrder, useUpdateOrderStatus } from "@/services/order.service";
import { toast } from "sonner";
import { cn } from "@/lib/utils";

interface Props {
  params: Promise<{ id: string }>;
}

const statusColors: Record<string, string> = {
  PENDING: "text-yellow-600 bg-yellow-50 border-yellow-200",
  CONFIRMED: "text-blue-600 bg-blue-50 border-blue-200",
  PROCESSING: "text-indigo-600 bg-indigo-50 border-indigo-200",
  SHIPPED: "text-purple-600 bg-purple-50 border-purple-200",
  DELIVERED: "text-green-600 bg-green-50 border-green-200",
  CANCELLED: "text-red-600 bg-red-50 border-red-200",
};
const VALID_TRANSITIONS: Record<string, string[]> = {
  PENDING: ["CONFIRMED", "CANCELLED"],
  CONFIRMED: ["PROCESSING", "CANCELLED"],
  PROCESSING: ["SHIPPED"],
  SHIPPED: ["DELIVERED"],
  DELIVERED: [],
  CANCELLED: [],
};

export default function OrderDetailPage({ params }: Props) {
  const { id } = use(params);
  const { data: order, isLoading } = useOrder(id);
  const updateStatus = useUpdateOrderStatus();

  const handleStatusChange = async (status: string) => {
    try {
      await updateStatus.mutateAsync({ id, status });
      toast.success(`Order status updated to ${status}`);
    } catch {
      toast.error("Failed to update status");
    }
  };

  if (isLoading)
    return (
      <div className="space-y-4 max-w-4xl">
        <Skeleton className="h-8 w-48" />
        {[1, 2, 3].map((i) => (
          <Skeleton key={i} className="h-40 rounded-xl" />
        ))}
      </div>
    );
  if (!order) return null;

  const nextStatuses = VALID_TRANSITIONS[order.status] ?? [];

  return (
    <div className="space-y-6 max-w-4xl">
      <div className="flex items-center gap-3 flex-wrap">
        <Button variant="ghost" size="sm" asChild>
          <Link href="/orders">
            <ArrowLeft className="w-4 h-4 mr-1" />
            Orders
          </Link>
        </Button>
        <h1 className="text-xl font-bold">Order #{order.orderNumber}</h1>
        <span
          className={cn(
            "text-xs px-3 py-1 rounded-full border font-medium",
            statusColors[order.status] ?? "",
          )}
        >
          {order.status}
        </span>

        {nextStatuses.length > 0 && (
          <div className="flex items-center gap-2 ml-auto">
            <span className="text-sm text-gray-500">Update status:</span>
            <Select
              onValueChange={(v: string | null) => {
                if (v) handleStatusChange(v);
              }}
            >
              <SelectTrigger className="w-40 h-8">
                <SelectValue placeholder="Change status" />
              </SelectTrigger>
              <SelectContent>
                {nextStatuses.map((s) => (
                  <SelectItem key={s} value={s}>
                    {s}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        )}
      </div>

      <div className="grid md:grid-cols-3 gap-4">
        {/* Customer */}
        <Card>
          <CardHeader>
            <CardTitle className="text-sm text-gray-500">Customer</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="font-semibold">
              {order.user.firstName} {order.user.lastName}
            </p>
            <p className="text-sm text-gray-500">{order.user.email}</p>
          </CardContent>
        </Card>

        {/* Shipping */}
        {order.shippingAddress && (
          <Card>
            <CardHeader>
              <CardTitle className="text-sm text-gray-500 flex items-center gap-1">
                <MapPin className="w-3 h-3" />
                Shipping
              </CardTitle>
            </CardHeader>
            <CardContent className="text-sm text-gray-600 space-y-0.5">
              <p>{order.shippingAddress.street}</p>
              <p>
                {order.shippingAddress.city}, {order.shippingAddress.state}
              </p>
              <p>
                {order.shippingAddress.country}{" "}
                {order.shippingAddress.postalCode}
              </p>
            </CardContent>
          </Card>
        )}

        {/* Payment */}
        <Card>
          <CardHeader>
            <CardTitle className="text-sm text-gray-500 flex items-center gap-1">
              <CreditCard className="w-3 h-3" />
              Payment
            </CardTitle>
          </CardHeader>
          <CardContent className="text-sm">
            <p className="font-medium">
              {order.paymentMethod?.replace(/_/g, " ")}
            </p>
            <p className="text-gray-500 text-xs mt-1">
              {new Date(order.createdDate).toLocaleString()}
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Items */}
      <Card>
        <CardHeader>
          <CardTitle className="text-base">Order Items</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="space-y-3">
            {order.items.map((item) => (
              <div key={item.id} className="flex justify-between items-start">
                <div>
                  <p className="font-medium">{item.productName}</p>
                  <p className="text-sm text-gray-500">
                    {item.variantName} · Qty: {item.quantity}
                  </p>
                </div>
                <p className="font-semibold">
                  ${(item.price * item.quantity).toFixed(2)}
                </p>
              </div>
            ))}
          </div>
          <Separator className="my-4" />
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
    </div>
  );
}
