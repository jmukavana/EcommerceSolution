"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  CheckCircle2,
  Loader2,
  MapPin,
  CreditCard,
  ShoppingBag,
} from "lucide-react";
import { useCart } from "@/services/cart.service";
import { useAddresses, useCreateAddress } from "@/services/address.service";
import { usePlaceOrder } from "@/services/order.service";
import { useAppSelector } from "@/store/hooks";
import { toast } from "sonner";
import { cn } from "@/lib/utils";

const addressSchema = z.object({
  label: z.string().min(1),
  street: z.string().min(1),
  city: z.string().min(1),
  state: z.string().min(1),
  country: z.string().min(1),
  postalCode: z.string().min(1),
  phone: z.string().min(7),
});
type AddressForm = z.infer<typeof addressSchema>;

const steps = [
  { id: 1, label: "Shipping", icon: MapPin },
  { id: 2, label: "Payment", icon: CreditCard },
  { id: 3, label: "Review", icon: ShoppingBag },
];

const PAYMENT_METHODS = [
  { value: "CASH_ON_DELIVERY", label: "Cash on Delivery" },
  { value: "CREDIT_CARD", label: "Credit Card" },
  { value: "DEBIT_CARD", label: "Debit Card" },
  { value: "MOBILE_MONEY", label: "Mobile Money" },
  { value: "PAYPAL", label: "PayPal" },
];

export default function CheckoutPage() {
  const router = useRouter();
  const { accessToken } = useAppSelector((s) => s.auth);
  const [step, setStep] = useState(1);
  const [selectedAddressId, setSelectedAddressId] = useState<string>("");
  const [paymentMethod, setPaymentMethod] = useState("CASH_ON_DELIVERY");
  const [showNewAddress, setShowNewAddress] = useState(false);

  const { data: cart } = useCart(!!accessToken);
  const { data: addresses = [] } = useAddresses(!!accessToken);
  const createAddress = useCreateAddress();
  const placeOrder = usePlaceOrder();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<AddressForm>({ resolver: zodResolver(addressSchema) });

  const items = cart?.items ?? [];
  const subtotal = cart?.subtotal ?? 0;
  const shipping = subtotal > 50 ? 0 : 10;
  const tax = subtotal * 0.1;
  const total = subtotal + shipping + tax;

  const handleSaveAddress = async (data: AddressForm) => {
    try {
      const res = await createAddress.mutateAsync(data);
      setSelectedAddressId(res.data?.id ?? "");
      setShowNewAddress(false);
      toast.success("Address saved");
    } catch {
      toast.error("Failed to save address");
    }
  };

  const handlePlaceOrder = async () => {
    if (!selectedAddressId) {
      toast.error("Please select a shipping address");
      return;
    }
    try {
      const order = await placeOrder.mutateAsync({
        addressId: selectedAddressId,
        paymentMethod,
      });
      toast.success("Order placed successfully!");
      router.push(`/account/orders/${order.id}`);
    } catch {
      toast.error("Failed to place order");
    }
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-1 max-w-6xl mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold mb-8">Checkout</h1>

        {/* Steps */}
        <div className="flex items-center mb-10">
          {steps.map((s, i) => (
            <div key={s.id} className="flex items-center">
              <div
                className={cn(
                  "flex items-center gap-2 px-4 py-2 rounded-full text-sm font-medium transition-colors",
                  step >= s.id
                    ? "bg-blue-600 text-white"
                    : "bg-gray-100 text-gray-500",
                )}
              >
                {step > s.id ? (
                  <CheckCircle2 className="w-4 h-4" />
                ) : (
                  <s.icon className="w-4 h-4" />
                )}
                {s.label}
              </div>
              {i < steps.length - 1 && (
                <div
                  className={cn(
                    "h-px flex-1 mx-2",
                    step > s.id ? "bg-blue-500" : "bg-gray-200",
                  )}
                />
              )}
            </div>
          ))}
        </div>

        <div className="grid lg:grid-cols-3 gap-8">
          <div className="lg:col-span-2">
            {/* Step 1: Shipping */}
            {step === 1 && (
              <Card>
                <CardHeader>
                  <CardTitle>Shipping Address</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  {addresses.length > 0 && (
                    <RadioGroup
                      value={selectedAddressId}
                      onValueChange={setSelectedAddressId}
                    >
                      {addresses.map((addr) => (
                        <label
                          key={addr.id}
                          className={cn(
                            "flex items-start gap-3 p-4 border rounded-xl cursor-pointer transition-colors",
                            selectedAddressId === addr.id
                              ? "border-blue-500 bg-blue-50"
                              : "hover:border-gray-300",
                          )}
                        >
                          <RadioGroupItem value={addr.id} className="mt-1" />
                          <div>
                            <div className="flex items-center gap-2">
                              <span className="font-medium">{addr.label}</span>
                              {addr.isDefault && (
                                <Badge variant="secondary" className="text-xs">
                                  Default
                                </Badge>
                              )}
                            </div>
                            <p className="text-sm text-gray-500">
                              {addr.street}, {addr.city}, {addr.state}{" "}
                              {addr.postalCode}
                            </p>
                            <p className="text-sm text-gray-500">
                              {addr.country} · {addr.phone}
                            </p>
                          </div>
                        </label>
                      ))}
                    </RadioGroup>
                  )}

                  <Button
                    variant="outline"
                    className="w-full"
                    onClick={() => setShowNewAddress(!showNewAddress)}
                  >
                    {showNewAddress ? "Cancel" : "+ Add New Address"}
                  </Button>

                  {showNewAddress && (
                    <form
                      onSubmit={handleSubmit(handleSaveAddress)}
                      className="space-y-3 border rounded-xl p-4"
                    >
                      <div className="grid grid-cols-2 gap-3">
                        <div>
                          <Label>Label</Label>
                          <Input placeholder="Home" {...register("label")} />
                        </div>
                        <div>
                          <Label>Phone</Label>
                          <Input
                            placeholder="+1 555 0000"
                            {...register("phone")}
                          />
                        </div>
                      </div>
                      <div>
                        <Label>Street</Label>
                        <Input
                          placeholder="123 Main St"
                          {...register("street")}
                        />
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
                      <Button
                        type="submit"
                        disabled={createAddress.isPending}
                        size="sm"
                      >
                        Save Address
                      </Button>
                    </form>
                  )}

                  <Button
                    className="w-full"
                    onClick={() => {
                      if (selectedAddressId) setStep(2);
                      else toast.error("Select an address");
                    }}
                  >
                    Continue to Payment
                  </Button>
                </CardContent>
              </Card>
            )}

            {/* Step 2: Payment */}
            {step === 2 && (
              <Card>
                <CardHeader>
                  <CardTitle>Payment Method</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <RadioGroup
                    value={paymentMethod}
                    onValueChange={setPaymentMethod}
                  >
                    {PAYMENT_METHODS.map((m) => (
                      <label
                        key={m.value}
                        className={cn(
                          "flex items-center gap-3 p-4 border rounded-xl cursor-pointer",
                          paymentMethod === m.value
                            ? "border-blue-500 bg-blue-50"
                            : "hover:border-gray-300",
                        )}
                      >
                        <RadioGroupItem value={m.value} />
                        <span className="font-medium">{m.label}</span>
                      </label>
                    ))}
                  </RadioGroup>
                  <div className="flex gap-3">
                    <Button variant="outline" onClick={() => setStep(1)}>
                      Back
                    </Button>
                    <Button className="flex-1" onClick={() => setStep(3)}>
                      Review Order
                    </Button>
                  </div>
                </CardContent>
              </Card>
            )}

            {/* Step 3: Review */}
            {step === 3 && (
              <Card>
                <CardHeader>
                  <CardTitle>Review &amp; Place Order</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  {items.map((item) => (
                    <div key={item.id} className="flex gap-3">
                      <div className="w-14 h-14 rounded-lg bg-gray-50 border overflow-hidden shrink-0">
                        {item.variant?.product?.images?.[0]?.url ? (
                          <img
                            src={item.variant.product.images[0].url}
                            alt=""
                            className="w-full h-full object-cover"
                          />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center">
                            📦
                          </div>
                        )}
                      </div>
                      <div className="flex-1">
                        <p className="font-medium text-sm">
                          {item.variant?.product?.name}
                        </p>
                        <p className="text-xs text-gray-500">
                          Qty: {item.quantity}
                        </p>
                      </div>
                      <p className="font-semibold text-sm">
                        ${((item.price ?? 0) * item.quantity).toFixed(2)}
                      </p>
                    </div>
                  ))}
                  <Separator />
                  <div className="text-sm space-y-1">
                    <div className="flex justify-between text-gray-500">
                      <span>Payment</span>
                      <span>{paymentMethod.replace(/_/g, " ")}</span>
                    </div>
                    <div className="flex justify-between font-bold">
                      <span>Total</span>
                      <span>${total.toFixed(2)}</span>
                    </div>
                  </div>
                  <div className="flex gap-3">
                    <Button variant="outline" onClick={() => setStep(2)}>
                      Back
                    </Button>
                    <Button
                      className="flex-1"
                      onClick={handlePlaceOrder}
                      disabled={placeOrder.isPending}
                    >
                      {placeOrder.isPending ? (
                        <>
                          <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                          Placing…
                        </>
                      ) : (
                        "Place Order"
                      )}
                    </Button>
                  </div>
                </CardContent>
              </Card>
            )}
          </div>

          {/* Order Summary sidebar */}
          <div>
            <Card>
              <CardHeader>
                <CardTitle className="text-base">Order Summary</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2 text-sm">
                <div className="flex justify-between text-gray-500">
                  <span>Subtotal ({items.length} items)</span>
                  <span>${subtotal.toFixed(2)}</span>
                </div>
                <div className="flex justify-between text-gray-500">
                  <span>Shipping</span>
                  <span>
                    {shipping === 0 ? "FREE" : `$${shipping.toFixed(2)}`}
                  </span>
                </div>
                <div className="flex justify-between text-gray-500">
                  <span>Tax</span>
                  <span>${tax.toFixed(2)}</span>
                </div>
                <Separator />
                <div className="flex justify-between font-bold text-base">
                  <span>Total</span>
                  <span>${total.toFixed(2)}</span>
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
