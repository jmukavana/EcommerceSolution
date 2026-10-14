"use client";
import Link from "next/link";
import { Skeleton } from "@/components/ui/skeleton";
import { useRootCategories } from "@/services/category.service";
import { cn } from "@/lib/utils";
import {
  Smartphone,
  Laptop,
  Headphones,
  Camera,
  Watch,
  Tv,
  Gamepad2,
  Home,
  Tag,
} from "lucide-react";

const fallbackIcons = [
  Smartphone,
  Laptop,
  Headphones,
  Camera,
  Watch,
  Tv,
  Gamepad2,
  Home,
];

export function CategorySection() {
  const { data: categories, isLoading } = useRootCategories();

  return (
    <section className="py-8">
      <div className="max-w-[1280px] mx-auto px-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-[22px] font-bold tracking-tight text-gray-900">
            Browse by Category
          </h2>
        </div>
        <div className="grid grid-cols-4 sm:grid-cols-6 md:grid-cols-8 gap-3">
          {isLoading
            ? Array.from({ length: 8 }).map((_, i) => (
                <Skeleton key={i} className="h-[90px] rounded-xl" />
              ))
            : categories?.slice(0, 8).map((cat, i) => {
                const Icon = fallbackIcons[i % fallbackIcons.length] ?? Tag;
                return (
                  <Link
                    key={cat.id}
                    href={`/shop?categoryId=${cat.id}`}
                    className={cn(
                      "group flex flex-col items-center gap-2 bg-gray-50 border-[1.5px] border-gray-200",
                      "rounded-xl p-4 cursor-pointer transition-all min-h-[44px]",
                      "hover:bg-blue-50 hover:border-blue-200",
                    )}
                  >
                    <Icon className="h-6 w-6 text-gray-400 group-hover:text-blue-600 transition-colors" />
                    <span className="text-[11px] text-gray-500 text-center group-hover:text-blue-600 group-hover:font-medium leading-tight transition-colors">
                      {cat.name}
                    </span>
                  </Link>
                );
              })}
        </div>
      </div>
    </section>
  );
}
