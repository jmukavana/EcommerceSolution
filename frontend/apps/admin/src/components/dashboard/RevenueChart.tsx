"use client";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

const data = [
  { day: "Mon", revenue: 2400 },
  { day: "Tue", revenue: 1398 },
  { day: "Wed", revenue: 5800 },
  { day: "Thu", revenue: 3908 },
  { day: "Fri", revenue: 4800 },
  { day: "Sat", revenue: 6800 },
  { day: "Sun", revenue: 4300 },
];

export function RevenueChart() {
  return (
    <Card>
      <CardHeader>
        <CardTitle className="text-base">Revenue (Last 7 Days)</CardTitle>
      </CardHeader>
      <CardContent>
        <ResponsiveContainer width="100%" height={220}>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="#f0f0f0" />
            <XAxis dataKey="day" tick={{ fontSize: 12 }} />
            <YAxis tick={{ fontSize: 12 }} tickFormatter={(v) => `$${v}`} />
            <Tooltip formatter={(v) => [`$${Number(v)}`, "Revenue"]} />
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
  );
}
