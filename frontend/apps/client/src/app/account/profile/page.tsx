"use client";
import { useEffect } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Loader2 } from "lucide-react";
import { useAppSelector } from "@/store/hooks";
import api from "@/lib/api";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

const schema = z.object({
  firstName: z.string().min(1, "Required"),
  lastName: z.string().min(1, "Required"),
  phoneNumber: z.string().min(7, "Invalid phone"),
});
type ProfileForm = z.infer<typeof schema>;

export default function ProfilePage() {
  const { user } = useAppSelector((s) => s.auth);
  const qc = useQueryClient();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ProfileForm>({
    resolver: zodResolver(schema),
  });

  useEffect(() => {
    if (user)
      reset({
        firstName: user.firstName,
        lastName: user.lastName,
        phoneNumber: user.phoneNumber,
      });
  }, [user, reset]);

  const updateProfile = useMutation({
    mutationFn: (data: ProfileForm) =>
      api.put("/users/me", data).then((r) => r.data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["me"] });
      toast.success("Profile updated!");
    },
    onError: () => toast.error("Failed to update profile"),
  });

  return (
    <div className="space-y-6 max-w-lg">
      <h1 className="text-xl font-bold">My Profile</h1>
      <Card>
        <CardHeader>
          <div className="flex items-center gap-4">
            <Avatar className="w-16 h-16">
              <AvatarFallback className="text-xl bg-blue-100 text-blue-600">
                {user?.firstName?.[0]}
                {user?.lastName?.[0]}
              </AvatarFallback>
            </Avatar>
            <div>
              <p className="font-semibold">
                {user?.firstName} {user?.lastName}
              </p>
              <p className="text-sm text-gray-500">{user?.email}</p>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <form
            onSubmit={handleSubmit((d) => updateProfile.mutate(d))}
            className="space-y-4"
          >
            <div className="grid grid-cols-2 gap-3">
              <div className="space-y-1.5">
                <Label>First Name</Label>
                <Input {...register("firstName")} />
                {errors.firstName && (
                  <p className="text-xs text-red-500">
                    {errors.firstName.message}
                  </p>
                )}
              </div>
              <div className="space-y-1.5">
                <Label>Last Name</Label>
                <Input {...register("lastName")} />
                {errors.lastName && (
                  <p className="text-xs text-red-500">
                    {errors.lastName.message}
                  </p>
                )}
              </div>
            </div>
            <div className="space-y-1.5">
              <Label>
                Email{" "}
                <span className="text-gray-400 text-xs">(cannot change)</span>
              </Label>
              <Input
                value={user?.email ?? ""}
                disabled
                className="bg-gray-50"
              />
            </div>
            <div className="space-y-1.5">
              <Label>Phone Number</Label>
              <Input type="tel" {...register("phoneNumber")} />
              {errors.phoneNumber && (
                <p className="text-xs text-red-500">
                  {errors.phoneNumber.message}
                </p>
              )}
            </div>
            <Button type="submit" disabled={updateProfile.isPending}>
              {updateProfile.isPending ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Saving…
                </>
              ) : (
                "Save Changes"
              )}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
