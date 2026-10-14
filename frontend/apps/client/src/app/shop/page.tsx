"use client";
import { Suspense, useState, useEffect } from "react";
import { useSearchParams, useRouter } from "next/navigation";
import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";
import { ProductGrid } from "@/components/product/ProductGrid";
import { ProductFilters, FilterState } from "@/components/shop/ProductFilters";
import { Button } from "@/components/ui/button";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  Sheet,
  SheetContent,
  SheetTrigger,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import {
  Breadcrumb,
  BreadcrumbItem,
  BreadcrumbLink,
  BreadcrumbList,
  BreadcrumbPage,
  BreadcrumbSeparator,
} from "@/components/ui/breadcrumb";
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { SlidersHorizontal } from "lucide-react";
import { useProducts } from "@/services/product.service";
import { useRootCategories } from "@/services/category.service";
import { useBrands } from "@/services/product.service";

function ShopContent() {
  const searchParams = useSearchParams();
  const router = useRouter();
  const [page, setPage] = useState(0);
  const [sort, setSort] = useState("createdDate,desc");

  const [filters, setFilters] = useState<FilterState>({
    categoryId: searchParams.get("categoryId") ?? undefined,
    brandId: searchParams.get("brandId") ?? undefined,
    search: searchParams.get("search") ?? undefined,
  });

  useEffect(() => {
    setPage(0);
  }, [filters, sort]);

  const { data: productsPage, isLoading } = useProducts({
    page,
    size: 12,
    sort,
    categoryId: filters.categoryId,
    brandId: filters.brandId,
    search: filters.search,
  });

  const { data: categories = [] } = useRootCategories();
  const { data: brands = [] } = useBrands();

  const total = productsPage?.totalElements ?? 0;
  const totalPages = productsPage?.totalPages ?? 1;

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-1 max-w-7xl mx-auto w-full px-4 py-8">
        <Breadcrumb className="mb-6">
          <BreadcrumbList>
            <BreadcrumbItem>
              <BreadcrumbLink href="/">Home</BreadcrumbLink>
            </BreadcrumbItem>
            <BreadcrumbSeparator />
            <BreadcrumbItem>
              <BreadcrumbPage>Shop</BreadcrumbPage>
            </BreadcrumbItem>
          </BreadcrumbList>
        </Breadcrumb>

        <div className="flex gap-8">
          {/* Desktop sidebar */}
          <aside className="hidden lg:block w-64 shrink-0">
            <div className="sticky top-4 border rounded-xl p-5 bg-white">
              <ProductFilters
                categories={categories}
                brands={brands}
                filters={filters}
                onFilterChange={setFilters}
              />
            </div>
          </aside>

          <div className="flex-1 min-w-0">
            {/* Toolbar */}
            <div className="flex items-center justify-between mb-6 gap-4">
              <div className="flex items-center gap-3">
                {/* Mobile filter button */}
                <Sheet>
                  <SheetTrigger>
                    <Button variant="outline" size="sm" className="lg:hidden">
                      <SlidersHorizontal className="w-4 h-4 mr-2" /> Filters
                    </Button>
                  </SheetTrigger>
                  <SheetContent side="left" className="w-80">
                    <SheetHeader>
                      <SheetTitle>Filters</SheetTitle>
                    </SheetHeader>
                    <div className="mt-4">
                      <ProductFilters
                        categories={categories}
                        brands={brands}
                        filters={filters}
                        onFilterChange={setFilters}
                      />
                    </div>
                  </SheetContent>
                </Sheet>
                <span className="text-sm text-gray-500">
                  {total} products found
                </span>
              </div>
              <Select value={sort} onValueChange={(v) => v && setSort(v)}>
                <SelectTrigger className="w-48">
                  <SelectValue placeholder="Sort by" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="createdDate,desc">Newest First</SelectItem>
                  <SelectItem value="price,asc">Price: Low to High</SelectItem>
                  <SelectItem value="price,desc">Price: High to Low</SelectItem>
                  <SelectItem value="averageRating,desc">Best Rated</SelectItem>
                </SelectContent>
              </Select>
            </div>

            <ProductGrid
              products={productsPage?.content}
              isLoading={isLoading}
            />

            {totalPages > 1 && (
              <Pagination className="mt-8">
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
                      onClick={() =>
                        setPage((p) => Math.min(totalPages - 1, p + 1))
                      }
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
        </div>
      </main>
      <Footer />
    </div>
  );
}

export default function ShopPage() {
  return (
    <Suspense>
      <ShopContent />
    </Suspense>
  );
}
