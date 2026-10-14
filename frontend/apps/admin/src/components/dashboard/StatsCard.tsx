import { Card, CardContent } from "@/components/ui/card";
import { TrendingUp, TrendingDown } from "lucide-react";
import { cn } from "@/lib/utils";
import type { LucideIcon } from "lucide-react";

interface StatsCardProps {
  title: string;
  value: string | number;
  change?: number;
  icon: LucideIcon;
  iconColor: string;
  iconBg: string;
}

export function StatsCard({
  title,
  value,
  change,
  icon: Icon,
  iconColor,
  iconBg,
}: StatsCardProps) {
  return (
    <Card>
      <CardContent className="p-5 flex items-start gap-4">
        <div
          className={cn(
            "w-12 h-12 rounded-xl flex items-center justify-center shrink-0",
            iconBg,
          )}
        >
          <Icon className={cn("w-6 h-6", iconColor)} />
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-sm text-gray-500">{title}</p>
          <p className="text-2xl font-bold mt-0.5">{value}</p>
          {change !== undefined && (
            <div
              className={cn(
                "flex items-center gap-1 text-xs mt-1",
                change >= 0 ? "text-green-600" : "text-red-500",
              )}
            >
              {change >= 0 ? (
                <TrendingUp className="w-3 h-3" />
              ) : (
                <TrendingDown className="w-3 h-3" />
              )}
              <span>{Math.abs(change)}% vs last month</span>
            </div>
          )}
        </div>
      </CardContent>
    </Card>
  );
}
