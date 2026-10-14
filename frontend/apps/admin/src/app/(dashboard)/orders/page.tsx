"use client";
import { useState } from "react";
import Link from "next/link";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { ChevronRight } from "lucide-react";
import { useAdminOrders } from "@/services/order.service";
import { cn } from "@/lib/utils";

const statuses = [
  "ALL",
  "PENDING",
  "CONFIRMED",
  "PROCESSING",
  "SHIPPED",
  "DELIVERED",
  "CANCELLED",
];

const statusColors: Record<string, string> = {
  PENDING: "bg-yellow-100 text-yellow-800",
  CONFIRMED: "bg-blue-100 text-blue-800",
  PROCESSING: "bg-indigo-100 text-indigo-800",
  SHIPPED: "bg-purple-100 text-purple-800",
  DELIVERED: "bg-green-100 text-green-800",
  CANCELLED: "bg-red-100 text-red-800",
};

export default function OrdersPage() {
  const [page, setPage] = useState(0);
  const [status, setStatus] = useState("ALL");

  const { data: ordersPage, isLoading } = useAdminOrders(
    page,
    15,
    status === "ALL" ? undefined : status,
  );
  const orders = ordersPage?.content ?? [];
  const totalPages = ordersPage?.totalPages ?? 1;

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Orders</h1>

      <Tabs
        value={status}
        onValueChange={(v) => {
          setStatus(v);
          setPage(0);
        }}
      >
        <TabsList className="flex-wrap h-auto gap-1">
          {statuses.map((s) => (
            <TabsTrigger key={s} value={s} className="text-xs">
              {s}
            </TabsTrigger>
          ))}
        </TabsList>
      </Tabs>

      <div className="border rounded-xl overflow-hidden bg-white">
        <table className="w-full text-sm">
          <thead className="bg-gray-50 border-b">
            <tr>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                Order
              </th>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                Customer
              </th>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                Status
              </th>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                Payment
              </th>
              <th className="text-left px-4 py-3 font-medium text-gray-500">
                Date
              </th>
              <th className="text-right px-4 py-3 font-medium text-gray-500">
                Total
              </th>
              <th className="px-4 py-3" />
            </tr>
          </thead>
          <tbody className="divide-y">
            {isLoading
              ? [1, 2, 3, 4, 5].map((i) => (
                  <tr key={i}>
                    <td colSpan={7} className="px-4 py-3">
                      <Skeleton className="h-5" />
                    </td>
                  </tr>
                ))
              : orders.map((order) => (
                  <tr key={order.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 font-medium text-indigo-600">
                      <Link
                        href={`/orders/${order.id}`}
                        className="hover:underline"
                      >
                        #{order.orderNumber}
                      </Link>
                    </td>
                    <td className="px-4 py-3">
                      <p className="font-medium">
                        {order.user.firstName} {order.user.lastName}
                      </p>
                      <p className="text-xs text-gray-400">
                        {order.user.email}
                      </p>
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={cn(
                          "text-xs px-2 py-0.5 rounded-full font-medium",
                          statusColors[order.status] ?? "",
                        )}
                      >
                        {order.status}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-gray-500 text-xs">
                      {order.paymentMethod?.replace(/_/g, " ")}
                    </td>
                    <td className="px-4 py-3 text-gray-500 text-xs">
                      {new Date(order.createdDate).toLocaleDateString()}
                    </td>
                    <td className="px-4 py-3 text-right font-bold">
                      ${(order.total ?? 0).toFixed(2)}
                    </td>
                    <td className="px-4 py-3">
                      <Link href={`/orders/${order.id}`}>
                        <ChevronRight className="w-4 h-4 text-gray-400" />
                      </Link>
                    </td>
                  </tr>
                ))}
          </tbody>
        </table>
        {!isLoading && orders.length === 0 && (
          <p className="text-center text-gray-400 py-12">No orders found</p>
        )}
      </div>

      {totalPages > 1 && (
        <Pagination>
          <PaginationContent>
            <PaginationItem>
              <PaginationPrevious
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                className={
                  page === 0
                    ? "pointer-events-none opacity-50"
                    : "cursor-pointer"
                }
              />
            </PaginationItem>
            <PaginationItem>
              <span className="px-4 py-2 text-sm">
                Page {page + 1} of {totalPages}
              </span>
            </PaginationItem>
            <PaginationItem>
              <PaginationNext
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                className={
                  page >= totalPages - 1
                    ? "pointer-events-none opacity-50"
                    : "cursor-pointer"
                }
              />
            </PaginationItem>
          </PaginationContent>
        </Pagination>
      )}
    </div>
  );
}
