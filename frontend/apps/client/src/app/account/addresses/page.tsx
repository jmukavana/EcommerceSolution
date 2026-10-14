"use client";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { MapPin, Plus, Pencil, Trash2, CheckCircle2 } from "lucide-react";
import {
  useAddresses,
  useCreateAddress,
  useUpdateAddress,
  useDeleteAddress,
  useSetDefaultAddress,
} from "@/services/address.service";
import { useAppSelector } from "@/store/hooks";
import { toast } from "sonner";
import type { Address } from "@/types";

const schema = z.object({
  label: z.string().min(1),
  street: z.string().min(1),
  city: z.string().min(1),
  state: z.string().min(1),
  country: z.string().min(1),
  postalCode: z.string().min(1),
  phone: z.string().min(7),
});
type AddressForm = z.infer<typeof schema>;

export default function AddressesPage() {
  const { accessToken } = useAppSelector((s) => s.auth);
  const { data: addresses = [] } = useAddresses(!!accessToken);
  const createAddr = useCreateAddress();
  const updateAddr = useUpdateAddress();
  const deleteAddr = useDeleteAddress();
  const setDefault = useSetDefaultAddress();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [editing, setEditing] = useState<Address | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<AddressForm>({ resolver: zodResolver(schema) });

  const openCreate = () => {
    setEditing(null);
    reset({});
    setDialogOpen(true);
  };
  const openEdit = (addr: Address) => {
    setEditing(addr);
    reset(addr);
    setDialogOpen(true);
  };

  const onSubmit = async (data: AddressForm) => {
    try {
      if (editing) {
        await updateAddr.mutateAsync({ id: editing.id, ...data });
        toast.success("Address updated");
      } else {
        await createAddr.mutateAsync(data);
        toast.success("Address added");
      }
      setDialogOpen(false);
    } catch {
      toast.error("Failed to save address");
    }
  };

  const handleDelete = async (id: string) => {
    try {
      await deleteAddr.mutateAsync(id);
      toast.success("Address deleted");
    } catch {
      toast.error("Failed to delete");
    }
  };

  const handleSetDefault = async (id: string) => {
    try {
      await setDefault.mutateAsync(id);
      toast.success("Default address updated");
    } catch {
      toast.error("Failed to update");
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold">Saved Addresses</h1>
        <Button onClick={openCreate} size="sm">
          <Plus className="w-4 h-4 mr-1" />
          Add Address
        </Button>
      </div>

      {addresses.length === 0 ? (
        <div className="text-center py-12 border rounded-xl text-gray-400">
          <MapPin className="w-10 h-10 mx-auto mb-2" />
          <p>No addresses saved</p>
          <Button variant="outline" className="mt-3" onClick={openCreate}>
            Add your first address
          </Button>
        </div>
      ) : (
        <div className="grid md:grid-cols-2 gap-4">
          {addresses.map((addr) => (
            <div
              key={addr.id}
              className={`border rounded-xl p-4 relative ${addr.isDefault ? "border-blue-300 bg-blue-50/50" : ""}`}
            >
              <div className="flex items-start justify-between mb-2">
                <div className="flex items-center gap-2">
                  <MapPin className="w-4 h-4 text-gray-400" />
                  <span className="font-semibold">{addr.label}</span>
                  {addr.isDefault && (
                    <Badge className="text-xs bg-blue-600">Default</Badge>
                  )}
                </div>
                <div className="flex gap-1">
                  <button
                    onClick={() => openEdit(addr)}
                    className="p-1.5 rounded hover:bg-gray-100"
                  >
                    <Pencil className="w-3.5 h-3.5" />
                  </button>
                  <AlertDialog>
                    <AlertDialogTrigger className="p-1.5 rounded hover:bg-red-50 text-red-400">
                      <Trash2 className="w-3.5 h-3.5" />
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>Delete address?</AlertDialogTitle>
                        <AlertDialogDescription>
                          This cannot be undone.
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <AlertDialogAction
                          onClick={() => handleDelete(addr.id)}
                          className="bg-red-600 hover:bg-red-700"
                        >
                          Delete
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                </div>
              </div>
              <p className="text-sm text-gray-600">{addr.street}</p>
              <p className="text-sm text-gray-600">
                {addr.city}, {addr.state} {addr.postalCode}
              </p>
              <p className="text-sm text-gray-600">
                {addr.country} · {addr.phone}
              </p>
              {!addr.isDefault && (
                <button
                  onClick={() => handleSetDefault(addr.id)}
                  className="mt-3 flex items-center gap-1 text-xs text-blue-600 hover:underline"
                >
                  <CheckCircle2 className="w-3 h-3" /> Set as default
                </button>
              )}
            </div>
          ))}
        </div>
      )}

      <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>
              {editing ? "Edit Address" : "Add Address"}
            </DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-3">
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label>Label</Label>
                <Input placeholder="Home" {...register("label")} />
                {errors.label && (
                  <p className="text-xs text-red-500">{errors.label.message}</p>
                )}
              </div>
              <div>
                <Label>Phone</Label>
                <Input {...register("phone")} />
                {errors.phone && (
                  <p className="text-xs text-red-500">{errors.phone.message}</p>
                )}
              </div>
            </div>
            <div>
              <Label>Street</Label>
              <Input {...register("street")} />
              {errors.street && (
                <p className="text-xs text-red-500">{errors.street.message}</p>
              )}
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label>City</Label>
                <Input {...register("city")} />
              </div>
              <div>
                <Label>State</Label>
                <Input {...register("state")} />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <Label>Country</Label>
                <Input {...register("country")} />
              </div>
              <div>
                <Label>Postal Code</Label>
                <Input {...register("postalCode")} />
              </div>
            </div>
            <div className="flex justify-end gap-2">
              <Button
                type="button"
                variant="outline"
                onClick={() => setDialogOpen(false)}
              >
                Cancel
              </Button>
              <Button
                type="submit"
                disabled={createAddr.isPending || updateAddr.isPending}
              >
                Save
              </Button>
            </div>
          </form>
        </DialogContent>
      </Dialog>
    </div>
  );
}
