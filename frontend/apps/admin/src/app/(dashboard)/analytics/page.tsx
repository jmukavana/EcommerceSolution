"use client";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  BarChart,
  Bar,
} from "recharts";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { StatsCard } from "@/components/dashboard/StatsCard";
import { TrendingUp, ShoppingCart, Package, Users } from "lucide-react";
import {
  useDashboardStats,
  useRecentOrders,
} from "@/services/dashboard.service";

const monthlyData = [
  { month: "Jan", revenue: 12000, orders: 45 },
  { month: "Feb", revenue: 19000, orders: 72 },
  { month: "Mar", revenue: 15000, orders: 60 },
  { month: "Apr", revenue: 22000, orders: 88 },
  { month: "May", revenue: 28000, orders: 105 },
  { month: "Jun", revenue: 24000, orders: 92 },
  { month: "Jul", revenue: 31000, orders: 120 },
];

const categoryData = [
  { name: "Electronics", sales: 4000 },
  { name: "Fashion", sales: 3000 },
  { name: "Home", sales: 2000 },
  { name: "Sports", sales: 2780 },
  { name: "Books", sales: 1890 },
];

export default function AnalyticsPage() {
  const { data: stats } = useDashboardStats();
  const { data: orders = [] } = useRecentOrders();

  const totalRevenue = orders.reduce((sum, o) => sum + (o.total ?? 0), 0);

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Analytics</h1>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatsCard
          title="Total Revenue"
          value={`$${totalRevenue.toFixed(0)}`}
          change={12}
          icon={TrendingUp}
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
      </div>

      <div className="grid lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Monthly Revenue</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={250}>
              <LineChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                <YAxis
                  tick={{ fontSize: 12 }}
                  tickFormatter={(v) => `$${v / 1000}k`}
                />
                <Tooltip
                  formatter={(v) => [
                    `$${Number(v).toLocaleString()}`,
                    "Revenue",
                  ]}
                />
                <Line
                  type="monotone"
                  dataKey="revenue"
                  stroke="#6366f1"
                  strokeWidth={2}
                  dot={{ r: 4, fill: "#6366f1" }}
                />
              </LineChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Monthly Orders</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={250}>
              <BarChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis dataKey="month" tick={{ fontSize: 12 }} />
                <YAxis tick={{ fontSize: 12 }} />
                <Tooltip />
                <Bar dataKey="orders" fill="#6366f1" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Sales by Category</CardTitle>
          </CardHeader>
          <CardContent>
            <ResponsiveContainer width="100%" height={250}>
              <BarChart data={categoryData} layout="vertical">
                <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
                <XAxis type="number" tick={{ fontSize: 12 }} />
                <YAxis
                  dataKey="name"
                  type="category"
                  tick={{ fontSize: 12 }}
                  width={70}
                />
                <Tooltip />
                <Bar dataKey="sales" fill="#8b5cf6" radius={[0, 4, 4, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Recent Orders Summary</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-2 text-sm">
              {orders.slice(0, 6).map((order) => (
                <div
                  key={order.id}
                  className="flex justify-between items-center py-1 border-b last:border-0"
                >
                  <span className="text-gray-500">#{order.orderNumber}</span>
                  <span className="font-semibold">
                    ${(order.total ?? 0).toFixed(2)}
                  </span>
                </div>
              ))}
              {orders.length === 0 && (
                <p className="text-center text-gray-400 py-4">No data</p>
              )}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
