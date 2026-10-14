import Link from "next/link";
import { Button } from "@/components/ui/button";
import { ProductGrid } from "@/components/product/ProductGrid";
import { ArrowRight } from "lucide-react";
import type { Product } from "@/types";

interface ProductSectionProps {
  title: string;
  subtitle?: string;
  viewAllHref?: string;
  products?: Product[];
  isLoading?: boolean;
  columns?: number;
}

export function ProductSection({
  title,
  subtitle,
  viewAllHref,
  products,
  isLoading,
  columns = 4,
}: ProductSectionProps) {
  return (
    <section>
      <div className="flex items-end justify-between mb-6">
        <div>
          <h2 className="text-[22px] font-bold tracking-tight text-gray-900">
            {title}
          </h2>
          {subtitle && <p className="text-sm text-gray-500 mt-1">{subtitle}</p>}
        </div>
        {viewAllHref && (
          <Button
            variant="ghost"
            asChild
            className="text-blue-600 hover:text-blue-700 hover:bg-blue-50 font-semibold"
          >
            <Link href={viewAllHref} className="flex items-center gap-1">
              View All <ArrowRight className="h-4 w-4" />
            </Link>
          </Button>
        )}
      </div>
      <ProductGrid
        products={products}
        isLoading={isLoading}
        columns={columns}
      />
    </section>
  );
}
