"use client";
import Link from "next/link";
import { DollarSign, ShoppingCart, Package, Users } from "lucide-react";
import { StatsCard } from "@/components/dashboard/StatsCard";
import { RevenueChart } from "@/components/dashboard/RevenueChart";
import { OrderStatusChart } from "@/components/dashboard/OrderStatusChart";
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  useRecentOrders,
  useDashboardStats,
} from "@/services/dashboard.service";
import { cn } from "@/lib/utils";

const statusColors: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-800",
  CONFIRMED: "bg-blue-100 text-blue-800",
  PROCESSING: "bg-indigo-100 text-indigo-800",
  SHIPPED: "bg-purple-100 text-purple-800",
  DELIVERED: "bg-green-100 text-green-800",
  CANCELLED: "bg-red-100 text-red-800",
};

export default function DashboardPage() {
  const { data: stats, isLoading: statsLoading } = useDashboardStats();
  const { data: recentOrders = [], isLoading: ordersLoading } =
    useRecentOrders();

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Dashboard</h1>

      {/* Stats */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {statsLoading ? (
          [1, 2, 3, 4].map((i) => (
            <Skeleton key={i} className="h-24 rounded-xl" />
          ))
        ) : (
          <>
            <StatsCard
              title="Total Revenue"
              value={`$${(stats?.totalRevenue ?? 0).toLocaleString()}`}
              change={12}
              icon={DollarSign}
              iconColor="text-green-600"
              iconBg="bg-green-100"
            />
            <StatsCard
              title="Total Orders"
              value={stats?.totalOrders ?? 0}
              change={8}
              icon={ShoppingCart}
              iconColor="text-blue-600"
              iconBg="bg-blue-100"
            />
            <StatsCard
              title="Products"
              value={stats?.totalProducts ?? 0}
              change={3}
              icon={Package}
              iconColor="text-purple-600"
              iconBg="bg-purple-100"
            />
            <StatsCard
              title="Users"
              value={stats?.totalUsers ?? 0}
              change={15}
              icon={Users}
              iconColor="text-orange-600"
              iconBg="bg-orange-100"
            />
          </>
        )}
      </div>

      {/* Charts */}
      <div className="grid lg:grid-cols-2 gap-4">
        <RevenueChart />
        <OrderStatusChart orders={recentOrders} />
      </div>

      {/* Recent Orders */}
      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle className="text-base">Recent Orders</CardTitle>
          <Link
            href="/orders"
            className="text-sm text-indigo-600 hover:underline"
          >
            View all
          </Link>
        </CardHeader>
        <CardContent>
          {ordersLoading ? (
            <div className="space-y-2">
              {[1, 2, 3].map((i) => (
                <Skeleton key={i} className="h-10 rounded" />
              ))}
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="text-left text-gray-500 border-b">
                    <th className="pb-2 font-medium">Order</th>
                    <th className="pb-2 font-medium">Customer</th>
                    <th className="pb-2 font-medium">Status</th>
                    <th className="pb-2 font-medium text-right">Amount</th>
                  </tr>
                </thead>
                <tbody className="divide-y">
                  {recentOrders.map((order) => (
                    <tr key={order.id} className="hover:bg-gray-50">
                      <td className="py-2.5">
                        <Link
                          href={`/orders/${order.id}`}
                          className="font-medium text-indigo-600 hover:underline"
                        >
                          #{order.orderNumber}
                        </Link>
                      </td>
                      <td className="py-2.5 text-gray-600">
                        {order.user.firstName} {order.user.lastName}
                      </td>
                      <td className="py-2.5">
                        <span
                          className={cn(
                            "text-xs px-2 py-0.5 rounded-full font-medium",
                            statusColors[order.status] ?? "",
                          )}
                        >
                          {order.status}
                        </span>
                      </td>
                      <td className="py-2.5 text-right font-semibold">
                        ${(order.total ?? 0).toFixed(2)}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {recentOrders.length === 0 && (
                <p className="text-center text-gray-400 py-8">No orders yet</p>
              )}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
