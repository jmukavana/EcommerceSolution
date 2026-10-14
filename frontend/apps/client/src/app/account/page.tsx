"use client";
import Link from "next/link";
import { Package, MapPin, Heart, ShoppingBag } from "lucide-react";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { useMyOrders } from "@/services/order.service";
import { useAddresses } from "@/services/address.service";
import { useWishlist } from "@/services/wishlist.service";
import { useAppSelector } from "@/store/hooks";

const statusColors: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-800",
  CONFIRMED: "bg-blue-100 text-blue-800",
  PROCESSING: "bg-indigo-100 text-indigo-800",
  SHIPPED: "bg-purple-100 text-purple-800",
  DELIVERED: "bg-green-100 text-green-800",
  CANCELLED: "bg-red-100 text-red-800",
};

export default function AccountDashboardPage() {
  const { accessToken, user } = useAppSelector((s) => s.auth);
  const { data: orders = [], isLoading: ordersLoading } =
    useMyOrders(!!accessToken);
  const { data: addresses = [] } = useAddresses(!!accessToken);
  const { data: wishlist } = useWishlist(!!accessToken);

  const recentOrders = orders.slice(0, 5);
  const totalSpent = orders
    .filter((o) => o.status !== "CANCELLED")
    .reduce((sum, o) => sum + (o.total ?? 0), 0);

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">
        Welcome back, {user?.firstName ?? "there"} 👋
      </h1>

      {/* Stats */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
        {[
          {
            icon: Package,
            label: "Total Orders",
            value: orders.length,
            href: "/account/orders",
            color: "text-blue-600 bg-blue-100",
          },
          {
            icon: ShoppingBag,
            label: "Total Spent",
            value: `$${totalSpent.toFixed(2)}`,
            href: "/account/orders",
            color: "text-green-600 bg-green-100",
          },
          {
            icon: Heart,
            label: "Wishlist",
            value: wishlist?.items?.length ?? 0,
            href: "/account/wishlist",
            color: "text-red-600 bg-red-100",
          },
          {
            icon: MapPin,
            label: "Addresses",
            value: addresses.length,
            href: "/account/addresses",
            color: "text-purple-600 bg-purple-100",
          },
        ].map(({ icon: Icon, label, value, href, color }) => (
          <Link key={label} href={href}>
            <Card className="hover:shadow-md transition-shadow cursor-pointer">
              <CardContent className="p-4 flex items-center gap-3">
                <div
                  className={`w-10 h-10 rounded-full ${color} flex items-center justify-center`}
                >
                  <Icon className="w-5 h-5" />
                </div>
                <div>
                  <p className="text-2xl font-bold">{value}</p>
                  <p className="text-xs text-gray-500">{label}</p>
                </div>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>

      {/* Recent Orders */}
      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="font-semibold text-lg">Recent Orders</h2>
          <Link
            href="/account/orders"
            className="text-sm text-blue-600 hover:underline"
          >
            View all
          </Link>
        </div>
        {ordersLoading ? (
          <div className="space-y-2">
            {[1, 2, 3].map((i) => (
              <Skeleton key={i} className="h-14 rounded-xl" />
            ))}
          </div>
        ) : recentOrders.length === 0 ? (
          <div className="text-center py-8 text-gray-400 border rounded-xl">
            <Package className="w-10 h-10 mx-auto mb-2" />
            <p>No orders yet</p>
          </div>
        ) : (
          <div className="space-y-2">
            {recentOrders.map((order) => (
              <Link key={order.id} href={`/account/orders/${order.id}`}>
                <div className="flex items-center justify-between p-4 border rounded-xl hover:bg-gray-50 transition-colors">
                  <div>
                    <p className="font-medium text-sm">#{order.orderNumber}</p>
                    <p className="text-xs text-gray-500">
                      {new Date(order.createdDate).toLocaleDateString()} ·{" "}
                      {order.items.length} items
                    </p>
                  </div>
                  <div className="flex items-center gap-3">
                    <span
                      className={`text-xs px-2 py-0.5 rounded-full font-medium ${statusColors[order.status] ?? ""}`}
                    >
                      {order.status}
                    </span>
                    <span className="font-semibold text-sm">
                      ${(order.total ?? 0).toFixed(2)}
                    </span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
