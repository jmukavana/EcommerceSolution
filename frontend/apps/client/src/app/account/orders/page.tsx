"use client";
import Link from "next/link";
import { Package, ChevronRight } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { useMyOrders } from "@/services/order.service";
import { useAppSelector } from "@/store/hooks";

const statusColors: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-800",
  CONFIRMED: "bg-blue-100 text-blue-800",
  PROCESSING: "bg-indigo-100 text-indigo-800",
  SHIPPED: "bg-purple-100 text-purple-800",
  DELIVERED: "bg-green-100 text-green-800",
  CANCELLED: "bg-red-100 text-red-800",
};

export default function OrdersPage() {
  const { accessToken } = useAppSelector((s) => s.auth);
  const { data: orders = [], isLoading } = useMyOrders(!!accessToken);

  if (isLoading)
    return (
      <div className="space-y-3">
        <h1 className="text-xl font-bold mb-4">My Orders</h1>
        {[1, 2, 3, 4].map((i) => (
          <Skeleton key={i} className="h-20 rounded-xl" />
        ))}
      </div>
    );

  return (
    <div className="space-y-4">
      <h1 className="text-xl font-bold">My Orders ({orders.length})</h1>
      {orders.length === 0 ? (
        <div className="text-center py-16 border rounded-xl text-gray-400">
          <Package className="w-12 h-12 mx-auto mb-3" />
          <p className="font-medium">No orders yet</p>
          <Link
            href="/shop"
            className="text-blue-600 text-sm hover:underline mt-1 inline-block"
          >
            Start shopping
          </Link>
        </div>
      ) : (
        orders.map((order) => (
          <Link key={order.id} href={`/account/orders/${order.id}`}>
            <div className="border rounded-xl p-4 hover:bg-gray-50 transition-colors">
              <div className="flex items-start justify-between">
                <div>
                  <div className="flex items-center gap-3 mb-1">
                    <p className="font-semibold">#{order.orderNumber}</p>
                    <span
                      className={`text-xs px-2 py-0.5 rounded-full font-medium ${statusColors[order.status] ?? ""}`}
                    >
                      {order.status}
                    </span>
                  </div>
                  <p className="text-sm text-gray-500">
                    {new Date(order.createdDate).toLocaleDateString()} ·{" "}
                    {order.items.length} item
                    {order.items.length !== 1 ? "s" : ""}
                  </p>
                  <div className="flex gap-2 mt-2 overflow-x-auto">
                    {order.items.slice(0, 3).map((item) => (
                      <span
                        key={item.id}
                        className="text-xs bg-gray-100 rounded px-2 py-0.5 shrink-0"
                      >
                        {item.productName}
                      </span>
                    ))}
                    {order.items.length > 3 && (
                      <span className="text-xs text-gray-400">
                        +{order.items.length - 3} more
                      </span>
                    )}
                  </div>
                </div>
                <div className="flex items-center gap-2 shrink-0">
                  <span className="font-bold">
                    ${(order.total ?? 0).toFixed(2)}
                  </span>
                  <ChevronRight className="w-4 h-4 text-gray-400" />
                </div>
              </div>
            </div>
          </Link>
        ))
      )}
    </div>
  );
}
