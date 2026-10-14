"use client";
import { use, useEffect } from "react";
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
import { Badge } from "@/components/ui/badge";
import { Skeleton } from "@/components/ui/skeleton";
import { ArrowLeft, Loader2 } from "lucide-react";
import Link from "next/link";
import { useProduct, useUpdateProduct } from "@/services/product.service";
import { toast } from "sonner";
import { cn } from "@/lib/utils";

interface Props {
  params: Promise<{ id: string }>;
}

const schema = z.object({
  name: z.string().min(1),
  slug: z.string().min(1),
  shortDescription: z.string().optional(),
  description: z.string().optional(),
  status: z.enum(["DRAFT", "PUBLISHED", "ARCHIVED"]),
  featured: z.boolean().optional(),
  metaTitle: z.string().optional(),
  metaDescription: z.string().optional(),
});
type ProductForm = z.infer<typeof schema>;

const statusColors: Record<string, string> = {
  PUBLISHED: "bg-green-100 text-green-800",
  DRAFT: "bg-gray-100 text-gray-600",
  ARCHIVED: "bg-red-100 text-red-700",
};

export default function EditProductPage({ params }: Props) {
  const { id } = use(params);
  const router = useRouter();
  const { data: product, isLoading } = useProduct(id);
  const updateProduct = useUpdateProduct();

  const {
    register,
    handleSubmit,
    reset,
    setValue,
    formState: { errors },
  } = useForm<ProductForm>({
    resolver: zodResolver(schema),
  });

  useEffect(() => {
    if (product)
      reset({
        name: product.name,
        slug: product.slug,
        shortDescription: product.shortDescription,
        description: product.description,
        status: product.status,
        featured: product.featured,
        metaTitle: product.metaTitle ?? "",
        metaDescription: product.metaDescription ?? "",
      });
  }, [product, reset]);

  const onSubmit = async (data: ProductForm) => {
    try {
      await updateProduct.mutateAsync({ id, data });
      toast.success("Product updated!");
    } catch {
      toast.error("Failed to update product");
    }
  };

  if (isLoading)
    return (
      <div className="space-y-4 max-w-3xl">
        <Skeleton className="h-8 w-48" />
        {[1, 2, 3].map((i) => (
          <Skeleton key={i} className="h-48 rounded-xl" />
        ))}
      </div>
    );
  if (!product) return null;

  return (
    <div className="space-y-6 max-w-3xl">
      <div className="flex items-center gap-3">
        <Button variant="ghost" size="sm" asChild>
          <Link href="/products">
            <ArrowLeft className="w-4 h-4 mr-1" />
            Products
          </Link>
        </Button>
        <h1 className="text-xl font-bold truncate">{product.name}</h1>
        <span
          className={cn(
            "text-xs px-2 py-0.5 rounded-full font-medium",
            statusColors[product.status] ?? "",
          )}
        >
          {product.status}
        </span>
      </div>

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <Card>
          <CardHeader>
            <CardTitle className="text-base">Basic Information</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-1.5">
              <Label>Name *</Label>
              <Input {...register("name")} />
              {errors.name && (
                <p className="text-xs text-red-500">{errors.name.message}</p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label>Slug *</Label>
              <Input {...register("slug")} />
            </div>
            <div className="space-y-1.5">
              <Label>Short Description</Label>
              <Input {...register("shortDescription")} />
            </div>
            <div className="space-y-1.5">
              <Label>Description</Label>
              <Textarea {...register("description")} rows={4} />
            </div>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle className="text-base">Settings</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-1.5">
                <Label>Status</Label>
                <Select
                  defaultValue={product.status}
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
              <div className="flex items-end gap-2 pb-1">
                <Switch
                  defaultChecked={product.featured}
                  onCheckedChange={(v) => setValue("featured", v)}
                />
                <Label>Featured</Label>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Variants */}
        {product.variants.length > 0 && (
          <Card>
            <CardHeader>
              <CardTitle className="text-base">
                Variants ({product.variants.length})
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                {product.variants.map((v) => (
                  <div
                    key={v.id}
                    className="flex items-center justify-between p-3 border rounded-lg"
                  >
                    <div>
                      <p className="font-medium text-sm">{v.name}</p>
                      <p className="text-xs text-gray-400">SKU: {v.sku}</p>
                    </div>
                    <div className="flex items-center gap-3">
                      {v.isDefault && (
                        <Badge variant="secondary" className="text-xs">
                          Default
                        </Badge>
                      )}
                      <span className="font-semibold">
                        ${v.price.toFixed(2)}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}

        {/* Images */}
        {product.images.length > 0 && (
          <Card>
            <CardHeader>
              <CardTitle className="text-base">
                Images ({product.images.length})
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="flex gap-2 flex-wrap">
                {product.images.map((img) => (
                  <div
                    key={img.id}
                    className="w-20 h-20 rounded-lg border overflow-hidden"
                  >
                    <img
                      src={img.url}
                      alt=""
                      className="w-full h-full object-cover"
                    />
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}

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
            disabled={updateProduct.isPending}
          >
            {updateProduct.isPending ? (
              <>
                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                Saving…
              </>
            ) : (
              "Save Changes"
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
