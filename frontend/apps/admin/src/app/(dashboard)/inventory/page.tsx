"use client";
import { useState } from "react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Skeleton } from "@/components/ui/skeleton";
import { ArrowDownToLine, ArrowUpDown } from "lucide-react";
import {
  useInventory,
  useLowStockItems,
  useReceiveStock,
  useAdjustStock,
} from "@/services/inventory.service";
import { toast } from "sonner";
import { cn } from "@/lib/utils";
import type { InventoryItem } from "@/types";

export default function InventoryPage() {
  const { data: inventory = [], isLoading } = useInventory();
  const { data: lowStock = [] } = useLowStockItems();
  const receiveStock = useReceiveStock();
  const adjustStock = useAdjustStock();

  const [receiveOpen, setReceiveOpen] = useState(false);
  const [adjustOpen, setAdjustOpen] = useState(false);
  const [selectedItem, setSelectedItem] = useState<InventoryItem | null>(null);

  const [receiveForm, setReceiveForm] = useState({
    variantId: "",
    warehouseId: "",
    quantity: "",
    notes: "",
  });
  const [adjustForm, setAdjustForm] = useState({ quantity: "", reason: "" });

  const handleReceive = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await receiveStock.mutateAsync({
        ...receiveForm,
        quantity: Number(receiveForm.quantity),
      });
      toast.success("Stock received");
      setReceiveOpen(false);
    } catch {
      toast.error("Failed to receive stock");
    }
  };

  const handleAdjust = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedItem) return;
    try {
      await adjustStock.mutateAsync({
        id: selectedItem.id,
        quantity: Number(adjustForm.quantity),
        reason: adjustForm.reason,
      });
      toast.success("Stock adjusted");
      setAdjustOpen(false);
    } catch {
      toast.error("Failed to adjust stock");
    }
  };

  const InventoryTable = ({ items }: { items: InventoryItem[] }) => (
    <div className="border rounded-xl overflow-hidden bg-white">
      <table className="w-full text-sm">
        <thead className="bg-gray-50 border-b">
          <tr>
            <th className="text-left px-4 py-3 font-medium text-gray-500">
              Product / Variant
            </th>
            <th className="text-left px-4 py-3 font-medium text-gray-500">
              Warehouse
            </th>
            <th className="text-left px-4 py-3 font-medium text-gray-500">
              Available
            </th>
            <th className="text-left px-4 py-3 font-medium text-gray-500">
              Reserved
            </th>
            <th className="text-left px-4 py-3 font-medium text-gray-500">
              Reorder at
            </th>
            <th className="text-left px-4 py-3 font-medium text-gray-500">
              Status
            </th>
            <th className="px-4 py-3" />
          </tr>
        </thead>
        <tbody className="divide-y">
          {isLoading
            ? [1, 2, 3].map((i) => (
                <tr key={i}>
                  <td colSpan={7} className="px-4 py-3">
                    <Skeleton className="h-5" />
                  </td>
                </tr>
              ))
            : items.map((item) => {
                const isLow = item.availableQuantity <= item.reorderPoint;
                return (
                  <tr
                    key={item.id}
                    className={cn("hover:bg-gray-50", isLow && "bg-red-50/30")}
                  >
                    <td className="px-4 py-3">
                      <p className="font-medium">
                        {item.variant?.product?.name}
                      </p>
                      <p className="text-xs text-gray-400">
                        {item.variant?.name} · {item.variant?.sku}
                      </p>
                    </td>
                    <td className="px-4 py-3 text-gray-500">
                      {item.warehouse?.name}
                    </td>
                    <td className="px-4 py-3 font-bold">
                      {item.availableQuantity}
                    </td>
                    <td className="px-4 py-3 text-gray-500">
                      {item.reservedQuantity}
                    </td>
                    <td className="px-4 py-3 text-gray-500">
                      {item.reorderPoint}
                    </td>
                    <td className="px-4 py-3">
                      <Badge
                        className={cn(
                          "text-xs",
                          isLow
                            ? "bg-red-100 text-red-700"
                            : "bg-green-100 text-green-700",
                        )}
                      >
                        {isLow ? "Low Stock" : "In Stock"}
                      </Badge>
                    </td>
                    <td className="px-4 py-3">
                      <Button
                        variant="ghost"
                        size="sm"
                        onClick={() => {
                          setSelectedItem(item);
                          setAdjustForm({ quantity: "", reason: "" });
                          setAdjustOpen(true);
                        }}
                      >
                        <ArrowUpDown className="w-3.5 h-3.5 mr-1" />
                        Adjust
                      </Button>
                    </td>
                  </tr>
                );
              })}
        </tbody>
      </table>
      {!isLoading && items.length === 0 && (
        <p className="text-center text-gray-400 py-12">No inventory items</p>
      )}
    </div>
  );

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">Inventory</h1>
        <Button
          className="bg-indigo-600 hover:bg-indigo-700"
          onClick={() => setReceiveOpen(true)}
        >
          <ArrowDownToLine className="w-4 h-4 mr-2" />
          Receive Stock
        </Button>
      </div>

      <Tabs defaultValue="all">
        <TabsList>
          <TabsTrigger value="all">All Stock</TabsTrigger>
          <TabsTrigger value="low">
            Low Stock{" "}
            {lowStock.length > 0 && (
              <Badge className="ml-1.5 bg-red-500 text-white text-xs">
                {lowStock.length}
              </Badge>
            )}
          </TabsTrigger>
        </TabsList>
        <TabsContent value="all" className="mt-4">
          <InventoryTable items={inventory} />
        </TabsContent>
        <TabsContent value="low" className="mt-4">
          <InventoryTable items={lowStock} />
        </TabsContent>
      </Tabs>

      {/* Receive Stock Dialog */}
      <Dialog open={receiveOpen} onOpenChange={setReceiveOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Receive Stock</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleReceive} className="space-y-3">
            <div>
              <Label>Variant ID *</Label>
              <Input
                value={receiveForm.variantId}
                onChange={(e) =>
                  setReceiveForm((f) => ({ ...f, variantId: e.target.value }))
                }
                required
              />
            </div>
            <div>
              <Label>Warehouse ID *</Label>
              <Input
                value={receiveForm.warehouseId}
                onChange={(e) =>
                  setReceiveForm((f) => ({ ...f, warehouseId: e.target.value }))
                }
                required
              />
            </div>
            <div>
              <Label>Quantity *</Label>
              <Input
                type="number"
                min="1"
                value={receiveForm.quantity}
                onChange={(e) =>
                  setReceiveForm((f) => ({ ...f, quantity: e.target.value }))
                }
                required
              />
            </div>
            <div>
              <Label>Notes</Label>
              <Textarea
                value={receiveForm.notes}
                onChange={(e) =>
                  setReceiveForm((f) => ({ ...f, notes: e.target.value }))
                }
                rows={2}
              />
            </div>
            <div className="flex justify-end gap-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => setReceiveOpen(false)}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={receiveStock.isPending}>
                Receive
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>

      {/* Adjust Stock Dialog */}
      <Dialog open={adjustOpen} onOpenChange={setAdjustOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              Adjust Stock — {selectedItem?.variant?.product?.name}
            </DialogTitle>
          </DialogHeader>
          <form onSubmit={handleAdjust} className="space-y-3">
            <p className="text-sm text-gray-500">
              Current available:{" "}
              <span className="font-bold">
                {selectedItem?.availableQuantity}
              </span>
            </p>
            <div>
              <Label>New Quantity *</Label>
              <Input
                type="number"
                min="0"
                value={adjustForm.quantity}
                onChange={(e) =>
                  setAdjustForm((f) => ({ ...f, quantity: e.target.value }))
                }
                required
              />
            </div>
            <div>
              <Label>Reason *</Label>
              <Textarea
                value={adjustForm.reason}
                onChange={(e) =>
                  setAdjustForm((f) => ({ ...f, reason: e.target.value }))
                }
                rows={2}
                required
              />
            </div>
            <div className="flex justify-end gap-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => setAdjustOpen(false)}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={adjustStock.isPending}>
                Save Adjustment
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
