import { ProductCard } from "./ProductCard";
import { ProductCardSkeleton } from "./ProductCardSkeleton";
import { cn } from "@/lib/utils";
import type { Product } from "@/types";

interface ProductGridProps {
  products?: Product[];
  isLoading?: boolean;
  columns?: number;
}

export function ProductGrid({
  products,
  isLoading,
  columns = 4,
}: ProductGridProps) {
  const gridClass = cn(
    "grid gap-4",
    columns === 2 && "grid-cols-1 sm:grid-cols-2",
    columns === 3 && "grid-cols-1 sm:grid-cols-2 lg:grid-cols-3",
    columns === 4 && "grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4",
    columns === 5 && "grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5",
  );

  if (isLoading) {
    return (
      <div className={gridClass}>
        {Array.from({ length: columns * 2 }).map((_, i) => (
          <ProductCardSkeleton key={i} />
        ))}
      </div>
    );
  }

  if (!products || products.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-16 text-gray-500">
        <p className="text-lg font-medium">No products found</p>
        <p className="text-sm mt-1">Try adjusting your filters</p>
      </div>
    );
  }

  return (
    <div className={gridClass}>
      {products.map((product) => (
        <ProductCard key={product.id} product={product} />
      ))}
    </div>
  );
}
