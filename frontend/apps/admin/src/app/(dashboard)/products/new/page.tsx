"use client";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Switch } from "@/components/ui/switch";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ArrowLeft, Loader2 } from "lucide-react";
import Link from "next/link";
import { useCreateProduct } from "@/services/product.service";
import { useCategories } from "@/services/category.service";
import { useBrands } from "@/services/brand.service";
import { toast } from "sonner";

const schema = z.object({
  name: z.string().min(1, "Required"),
  slug: z.string().min(1, "Required"),
  shortDescription: z.string().optional(),
  description: z.string().optional(),
  type: z.enum(["SIMPLE", "VARIABLE"]),
  status: z.enum(["DRAFT", "PUBLISHED", "ARCHIVED"]),
  brandId: z.string().optional(),
  categoryIds: z.string().optional(),
  featured: z.boolean().optional(),
  price: z.coerce.number().min(0).optional(),
  compareAtPrice: z.coerce.number().optional(),
  sku: z.string().optional(),
  metaTitle: z.string().optional(),
  metaDescription: z.string().optional(),
});
type ProductForm = z.infer<typeof schema>;

export default function NewProductPage() {
  const router = useRouter();
  const createProduct = useCreateProduct();
  const { data: categories = [] } = useCategories();
  const { data: brands = [] } = useBrands();

  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<ProductForm>({
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    resolver: zodResolver(schema) as any,
    defaultValues: { type: "SIMPLE", status: "DRAFT", featured: false },
  });

  const nameValue = watch("name");
  const onNameBlur = () => {
    if (nameValue)
      setValue(
        "slug",
        nameValue
          .toLowerCase()
          .replace(/\s+/g, "-")
          .replace(/[^a-z0-9-]/g, ""),
      );
  };

  const onSubmit = async (data: ProductForm) => {
    try {
      const payload: Record<string, unknown> = {
        name: data.name,
        slug: data.slug,
        shortDescription: data.shortDescription,
        description: data.description,
        type: data.type,
        status: data.status,
        featured: data.featured,
        metaTitle: data.metaTitle,
        metaDescription: data.metaDescription,
        brandId: data.brandId || undefined,
        categoryIds: data.categoryIds ? [data.categoryIds] : [],
        variants:
          data.type === "SIMPLE"
            ? [
                {
                  name: "Default",
                  sku: data.sku || `SKU-${Date.now()}`,
                  price: data.price ?? 0,
                  compareAtPrice: data.compareAtPrice || null,
                  isDefault: true,
                  active: true,
                },
              ]
            : [],
      };
      const res = await createProduct.mutateAsync(payload);
      toast.success("Product created!");
      router.push(`/products/${(res as { id?: string })?.id ?? ""}`);
    } catch {
      toast.error("Failed to create product");
    }
  };

  return (
    <div className="space-y-6 max-w-3xl">
      <div className="flex items-center gap-3">
        <Button variant="ghost" size="sm" asChild>
          <Link href="/products">
            <ArrowLeft className="w-4 h-4 mr-1" />
            Products
          </Link>
        </Button>
        <h1 className="text-2xl font-bold">New Product</h1>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        {/* Basic Info */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Basic Information</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5 col-span-2">
                <Label>Product Name *</Label>
                <Input
                  {...register("name")}
                  onBlur={onNameBlur}
                  placeholder="e.g. iPhone 16 Pro"
                />
                {errors.name && (
                  <p className="text-xs text-red-500">{errors.name.message}</p>
                )}
              </div>
              <div className="space-y-1.5 col-span-2">
                <Label>Slug *</Label>
                <Input {...register("slug")} placeholder="iphone-16-pro" />
              </div>
              <div className="space-y-1.5 col-span-2">
                <Label>Short Description</Label>
                <Input
                  {...register("shortDescription")}
                  placeholder="Brief product summary"
                />
              </div>
              <div className="space-y-1.5 col-span-2">
                <Label>Description</Label>
                <Textarea
                  {...register("description")}
                  rows={4}
                  placeholder="Full product description…"
                />
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Organization */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Organization</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <Label>Type</Label>
                <Select
                  defaultValue="SIMPLE"
                  onValueChange={(v: string | null) => {
                    if (v) setValue("type", v as "SIMPLE" | "VARIABLE");
                  }}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="SIMPLE">Simple</SelectItem>
                    <SelectItem value="VARIABLE">Variable</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1.5">
                <Label>Status</Label>
                <Select
                  defaultValue="DRAFT"
                  onValueChange={(v: string | null) => {
                    if (v)
                      setValue(
                        "status",
                        v as "DRAFT" | "PUBLISHED" | "ARCHIVED",
                      );
                  }}
                >
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="DRAFT">Draft</SelectItem>
                    <SelectItem value="PUBLISHED">Published</SelectItem>
                    <SelectItem value="ARCHIVED">Archived</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1.5">
                <Label>Category</Label>
                <Select
                  onValueChange={(v: string | null) => {
                    if (v) setValue("categoryIds", v);
                  }}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select category" />
                  </SelectTrigger>
                  <SelectContent>
                    {categories.map((c: { id: string; name: string }) => (
                      <SelectItem key={c.id} value={c.id}>
                        {c.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1.5">
                <Label>Brand</Label>
                <Select
                  onValueChange={(v: string | null) => {
                    if (v) setValue("brandId", v);
                  }}
                >
                  <SelectTrigger>
                    <SelectValue placeholder="Select brand" />
                  </SelectTrigger>
                  <SelectContent>
                    {brands.map((b: { id: string; name: string }) => (
                      <SelectItem key={b.id} value={b.id}>
                        {b.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="flex items-center gap-2">
                <Switch onCheckedChange={(v) => setValue("featured", v)} />
                <Label>Featured product</Label>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Pricing (SIMPLE) */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Pricing &amp; Inventory</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-3 gap-4">
              <div className="space-y-1.5">
                <Label>Price ($)</Label>
                <Input
                  type="number"
                  step="0.01"
                  min="0"
                  {...register("price")}
                  placeholder="0.00"
                />
              </div>
              <div className="space-y-1.5">
                <Label>Compare At ($)</Label>
                <Input
                  type="number"
                  step="0.01"
                  min="0"
                  {...register("compareAtPrice")}
                  placeholder="0.00"
                />
              </div>
              <div className="space-y-1.5">
                <Label>SKU</Label>
                <Input {...register("sku")} placeholder="SKU-001" />
              </div>
            </div>
          </CardContent>
        </Card>

        {/* SEO */}
        <Card>
          <CardHeader>
            <CardTitle className="text-base">SEO</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-1.5">
              <Label>Meta Title</Label>
              <Input {...register("metaTitle")} />
            </div>
            <div className="space-y-1.5">
              <Label>Meta Description</Label>
              <Textarea {...register("metaDescription")} rows={2} />
            </div>
          </CardContent>
        </Card>

        <div className="flex gap-3">
          <Button
            type="submit"
            className="bg-indigo-600 hover:bg-indigo-700"
            disabled={createProduct.isPending}
          >
            {createProduct.isPending ? (
              <>
                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                Creating…
              </>
            ) : (
              "Create Product"
            )}
          </Button>
          <Button type="button" variant="outline" asChild>
            <Link href="/products">Cancel</Link>
          </Button>
        </div>
      </form>
    </div>
  );
}
