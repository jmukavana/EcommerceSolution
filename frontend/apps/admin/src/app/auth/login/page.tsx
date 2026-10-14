"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  InputOTP,
  InputOTPGroup,
  InputOTPSlot,
} from "@/components/ui/input-otp";
import { ShieldCheck, Loader2 } from "lucide-react";
import { useLogin, useVerifyOtp } from "@/services/auth.service";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { setTokens, setPendingEmail } from "@/store/slices/authSlice";
import { toast } from "sonner";

const loginSchema = z.object({
  email: z.string().email(),
  password: z.string().min(6),
});
type LoginForm = z.infer<typeof loginSchema>;

export default function AdminLoginPage() {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const pendingEmail = useAppSelector((s) => s.auth.pendingEmail);
  const [step, setStep] = useState<"credentials" | "otp">(
    pendingEmail ? "otp" : "credentials",
  );
  const [otp, setOtp] = useState("");
  const login = useLogin();
  const verifyOtp = useVerifyOtp();

  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors },
  } = useForm<LoginForm>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmitCredentials = async (data: LoginForm) => {
    try {
      await login.mutateAsync(data);
      dispatch(setPendingEmail(data.email));
      setStep("otp");
      toast.success("OTP sent to your email!");
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { message?: string } } })
        ?.response?.data?.message;
      toast.error(msg ?? "Login failed. Check credentials.");
    }
  };

  const onSubmitOtp = async () => {
    const email = pendingEmail ?? getValues("email");
    if (otp.length < 6) {
      toast.error("Enter 6-digit OTP");
      return;
    }
    try {
      const res = await verifyOtp.mutateAsync({ email, otp });
      const tokens = res.data ?? res;
      dispatch(
        setTokens({
          accessToken: tokens.accessToken,
          refreshToken: tokens.refreshToken,
        }),
      );
      toast.success("Welcome to Admin Panel!");
      router.push("/");
    } catch {
      toast.error("Invalid or expired OTP");
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-950 px-4">
      <Card className="w-full max-w-md border-slate-800 bg-slate-900 text-white">
        <CardHeader className="text-center">
          <div className="flex justify-center mb-4">
            <div className="w-14 h-14 bg-indigo-600 rounded-2xl flex items-center justify-center">
              <ShieldCheck className="w-7 h-7 text-white" />
            </div>
          </div>
          <CardTitle className="text-2xl text-white">Admin Portal</CardTitle>
          <CardDescription className="text-slate-400">
            {step === "credentials"
              ? "Sign in to your admin account"
              : "Enter the OTP sent to your email"}
          </CardDescription>
        </CardHeader>
        <CardContent>
          {step === "credentials" ? (
            <form
              onSubmit={handleSubmit(onSubmitCredentials)}
              className="space-y-4"
            >
              <div className="space-y-1.5">
                <Label className="text-slate-300">Email</Label>
                <Input
                  type="email"
                  placeholder="admin@example.com"
                  {...register("email")}
                  className="bg-slate-800 border-slate-700 text-white placeholder:text-slate-500"
                />
                {errors.email && (
                  <p className="text-xs text-red-400">{errors.email.message}</p>
                )}
              </div>
              <div className="space-y-1.5">
                <Label className="text-slate-300">Password</Label>
                <Input
                  type="password"
                  placeholder="••••••••"
                  {...register("password")}
                  className="bg-slate-800 border-slate-700 text-white placeholder:text-slate-500"
                />
                {errors.password && (
                  <p className="text-xs text-red-400">
                    {errors.password.message}
                  </p>
                )}
              </div>
              <Button
                type="submit"
                className="w-full bg-indigo-600 hover:bg-indigo-700"
                disabled={login.isPending}
              >
                {login.isPending ? (
                  <>
                    <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                    Signing in…
                  </>
                ) : (
                  "Sign In"
                )}
              </Button>
            </form>
          ) : (
            <div className="space-y-6">
              <p className="text-sm text-slate-400 text-center">
                Code sent to{" "}
                <span className="font-medium text-white">
                  {pendingEmail ?? getValues("email")}
                </span>
              </p>
              <div className="flex justify-center">
                <InputOTP maxLength={6} value={otp} onChange={setOtp}>
                  <InputOTPGroup>
                    {[0, 1, 2, 3, 4, 5].map((i) => (
                      <InputOTPSlot
                        key={i}
                        index={i}
                        className="bg-slate-800 border-slate-700 text-white"
                      />
                    ))}
                  </InputOTPGroup>
                </InputOTP>
              </div>
              <Button
                className="w-full bg-indigo-600 hover:bg-indigo-700"
                onClick={onSubmitOtp}
                disabled={verifyOtp.isPending || otp.length < 6}
              >
                {verifyOtp.isPending ? (
                  <>
                    <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                    Verifying…
                  </>
                ) : (
                  "Verify OTP"
                )}
              </Button>
              <Button
                variant="ghost"
                className="w-full text-slate-400 hover:text-white"
                onClick={() => setStep("credentials")}
              >
                ← Back
              </Button>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
