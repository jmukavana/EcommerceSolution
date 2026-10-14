"use client";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Input } from "@/components/ui/input";
import type { Category, Brand } from "@/types";

export interface FilterState {
  categoryId?: string;
  brandId?: string;
  minPrice?: string;
  maxPrice?: string;
  search?: string;
}

interface ProductFiltersProps {
  categories: Category[];
  brands: Brand[];
  filters: FilterState;
  onFilterChange: (f: FilterState) => void;
}

export function ProductFilters({
  categories,
  brands,
  filters,
  onFilterChange,
}: ProductFiltersProps) {
  const update = (patch: Partial<FilterState>) =>
    onFilterChange({ ...filters, ...patch });
  const clear = () => onFilterChange({});

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h3 className="font-semibold text-lg">Filters</h3>
        <Button
          variant="ghost"
          size="sm"
          onClick={clear}
          className="text-blue-600 h-auto p-0"
        >
          Clear all
        </Button>
      </div>

      <Separator />

      {/* Categories */}
      {categories.length > 0 && (
        <div className="space-y-3">
          <h4 className="font-medium">Category</h4>
          <RadioGroup
            value={filters.categoryId ?? ""}
            onValueChange={(v) => update({ categoryId: v || undefined })}
          >
            <div className="flex items-center space-x-2">
              <RadioGroupItem value="" id="cat-all" />
              <Label htmlFor="cat-all" className="font-normal cursor-pointer">
                All Categories
              </Label>
            </div>
            {categories.map((cat) => (
              <div key={cat.id} className="flex items-center space-x-2">
                <RadioGroupItem value={cat.id} id={`cat-${cat.id}`} />
                <Label
                  htmlFor={`cat-${cat.id}`}
                  className="font-normal cursor-pointer"
                >
                  {cat.name}
                </Label>
              </div>
            ))}
          </RadioGroup>
        </div>
      )}

      <Separator />

      {/* Brands */}
      {brands.length > 0 && (
        <div className="space-y-3">
          <h4 className="font-medium">Brand</h4>
          <RadioGroup
            value={filters.brandId ?? ""}
            onValueChange={(v) => update({ brandId: v || undefined })}
          >
            <div className="flex items-center space-x-2">
              <RadioGroupItem value="" id="brand-all" />
              <Label htmlFor="brand-all" className="font-normal cursor-pointer">
                All Brands
              </Label>
            </div>
            {brands.map((brand) => (
              <div key={brand.id} className="flex items-center space-x-2">
                <RadioGroupItem value={brand.id} id={`brand-${brand.id}`} />
                <Label
                  htmlFor={`brand-${brand.id}`}
                  className="font-normal cursor-pointer"
                >
                  {brand.name}
                </Label>
              </div>
            ))}
          </RadioGroup>
        </div>
      )}

      <Separator />

      {/* Price Range */}
      <div className="space-y-3">
        <h4 className="font-medium">Price Range</h4>
        <div className="flex gap-2 items-center">
          <Input
            type="number"
            placeholder="Min"
            value={filters.minPrice ?? ""}
            onChange={(e) => update({ minPrice: e.target.value || undefined })}
            className="h-8"
          />
          <span className="text-gray-400">–</span>
          <Input
            type="number"
            placeholder="Max"
            value={filters.maxPrice ?? ""}
            onChange={(e) => update({ maxPrice: e.target.value || undefined })}
            className="h-8"
          />
        </div>
      </div>
    </div>
  );
}
