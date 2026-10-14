"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";
import { HeroBanner } from "@/components/home/HeroBanner";
import { CategorySection } from "@/components/home/CategorySection";
import { ProductSection } from "@/components/home/ProductSection";
import { PromoSection } from "@/components/home/PromoSection";
import {
  useFeaturedProducts,
  useNewArrivals,
} from "@/services/product.service";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Star, ArrowRight } from "lucide-react";
import { toast } from "sonner";
import { cn } from "@/lib/utils";
import type { Product } from "@/types";

/* ─── Best Sellers ─────────────────────────────────────── */

function BestSellersSection({ products }: { products?: Product[] }) {
  const items = products?.slice(0, 5) ?? [];

  return (
    <section className="py-8">
      <div className="max-w-[1280px] mx-auto px-6">
        <div className="grid grid-cols-1 lg:grid-cols-[300px_1fr] gap-8">
          {/* Ranked list */}
          <div>
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-[22px] font-bold tracking-tight text-gray-900">
                Best Sellers
              </h2>
              <Link
                href="/shop?featured=true"
                className="text-sm text-blue-600 font-semibold flex items-center gap-1 px-3 py-1.5 rounded-lg hover:bg-blue-50 transition-colors"
              >
                View All <ArrowRight className="h-3.5 w-3.5" />
              </Link>
            </div>
            <div className="flex flex-col gap-3" role="list">
              {items.length === 0
                ? Array.from({ length: 5 }).map((_, i) => (
                    <div
                      key={i}
                      className="flex items-center gap-3 p-3 border-[1.5px] border-gray-200 rounded-xl bg-white animate-pulse"
                    >
                      <div className="w-5 h-4 bg-gray-100 rounded" />
                      <div className="w-12 h-12 bg-gray-100 rounded-lg" />
                      <div className="flex-1 space-y-2">
                        <div className="h-3 bg-gray-100 rounded w-3/4" />
                        <div className="h-3 bg-gray-100 rounded w-1/3" />
                      </div>
                    </div>
                  ))
                : items.map((product, i) => {
                    const variant =
                      product.variants.find((v) => v.isDefault) ??
                      product.variants[0];
                    return (
                      <Link
                        key={product.id}
                        href={`/shop/${product.id}`}
                        role="listitem"
                        className="flex items-center gap-3 p-3 border-[1.5px] border-gray-200 rounded-xl bg-white hover:border-blue-200 transition-colors"
                      >
                        <span
                          className={cn(
                            "w-5 text-xs font-bold text-center shrink-0",
                            i < 3 ? "text-amber-500" : "text-gray-400",
                          )}
                        >
                          {i + 1}
                        </span>
                        <div className="w-12 h-12 bg-gray-50 rounded-lg flex items-center justify-center shrink-0 overflow-hidden">
                          {product.images[0]?.url ? (
                            <img
                              src={product.images[0].url}
                              alt={product.name}
                              className="w-full h-full object-cover"
                            />
                          ) : (
                            <span className="text-blue-200 text-xl">📦</span>
                          )}
                        </div>
                        <div className="flex-1 min-w-0">
                          <p className="text-sm font-medium text-gray-900 truncate">
                            {product.name}
                          </p>
                          <div className="flex items-center gap-2 mt-0.5">
                            {variant && (
                              <span className="text-sm font-bold text-gray-900">
                                ${variant.price.toFixed(2)}
                              </span>
                            )}
                            {variant?.compareAtPrice &&
                              variant.compareAtPrice > variant.price && (
                                <span className="text-xs text-gray-400 line-through">
                                  ${variant.compareAtPrice.toFixed(2)}
                                </span>
                              )}
                          </div>
                        </div>
                      </Link>
                    );
                  })}
            </div>
          </div>

          {/* Countdown */}
          <CountdownSection />
        </div>
      </div>
    </section>
  );
}

/* ─── Countdown ──────────────────────────────────────────── */

function CountdownSection() {
  const target = new Date("2026-04-22T23:59:59").getTime();
  const [timeLeft, setTimeLeft] = useState({ d: 0, h: 0, m: 0, s: 0 });

  useEffect(() => {
    const calc = () => {
      const diff = Math.max(0, target - Date.now());
      setTimeLeft({
        d: Math.floor(diff / 86400000),
        h: Math.floor((diff % 86400000) / 3600000),
        m: Math.floor((diff % 3600000) / 60000),
        s: Math.floor((diff % 60000) / 1000),
      });
    };
    calc();
    const t = setInterval(calc, 1000);
    return () => clearInterval(t);
  }, [target]);

  const pad = (n: number) => String(n).padStart(2, "0");

  return (
    <div
      className="rounded-2xl p-8 overflow-hidden relative bg-[linear-gradient(135deg,#0a0a14,#1a0a2e)]"
      aria-label="Flash sale countdown"
      role="timer"
    >
      <div className="text-[10px] font-bold tracking-[2px] uppercase text-purple-400 mb-3">
        Don&apos;t Miss!!
      </div>
      <h2 className="text-[28px] font-extrabold text-white tracking-tight mb-1 leading-snug">
        Enhance Your
        <br />
        Music Experience
      </h2>
      <p className="text-xs text-white/50 mb-6" aria-live="polite">
        Flash Deal — limited quantities
      </p>

      <div className="flex items-start gap-3 mb-6">
        {[
          { n: pad(timeLeft.d), label: "Days" },
          { n: pad(timeLeft.h), label: "Hours" },
          { n: pad(timeLeft.m), label: "Mins" },
          { n: pad(timeLeft.s), label: "Secs" },
        ].map((unit, i, arr) => (
          <div key={unit.label} className="flex items-start gap-3">
            <div className="text-center">
              <span className="font-extrabold text-[28px] text-white bg-white/[0.08] px-4 py-3 rounded-lg block min-w-[60px] tabular-nums tracking-tight leading-none">
                {unit.n}
              </span>
              <span className="text-[9px] uppercase tracking-[1px] text-white/40 mt-1 block">
                {unit.label}
              </span>
            </div>
            {i < arr.length - 1 && (
              <span className="text-xl font-bold text-blue-500 opacity-70 pt-3">
                :
              </span>
            )}
          </div>
        ))}
      </div>

      <Link
        href="/shop?deals=true"
        className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold px-6 py-3 rounded-lg min-h-[44px] transition-all hover:-translate-y-px"
        aria-label="Check out the flash deal"
      >
        Check it Out!
        <ArrowRight className="h-4 w-4" />
      </Link>

      {/* Device silhouette */}
      <div className="absolute right-6 top-1/2 -translate-y-1/2 opacity-[0.07] pointer-events-none">
        <svg width="130" height="200" viewBox="0 0 130 200" fill="none">
          <rect
            x="8"
            y="8"
            width="114"
            height="184"
            rx="20"
            stroke="#4f8ef7"
            strokeWidth="2"
          />
          <rect
            x="50"
            y="14"
            width="30"
            height="7"
            rx="3.5"
            fill="#4f8ef7"
            fillOpacity=".4"
          />
          <circle
            cx="65"
            cy="180"
            r="8"
            stroke="#4f8ef7"
            strokeWidth="2"
            fillOpacity=".4"
          />
        </svg>
      </div>
    </div>
  );
}

/* ─── Testimonials ─────────────────────────────────────── */

const testimonials = [
  {
    initials: "AM",
    name: "Amina Mwangi",
    role: "Software Engineer",
    rating: 5,
    quote:
      "Got my order delivered in 2 days — packaging was immaculate, price unbeatable. First time buying online without anxiety!",
  },
  {
    initials: "JK",
    name: "James Kimani",
    role: "Entrepreneur",
    rating: 4,
    quote:
      "Smooth checkout, real-time tracking, and amazing customer support when I had a query. Will definitely shop again.",
  },
  {
    initials: "FM",
    name: "Faith Mutua",
    role: "Graphic Designer",
    rating: 5,
    quote:
      "The product quality exceeded my expectations. Secure payments and fast dispatch. ShopNow is my go-to store now.",
  },
];

function TestimonialsSection() {
  return (
    <section className="py-8">
      <div className="max-w-[1280px] mx-auto px-6">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-[22px] font-bold tracking-tight text-gray-900">
            What Our Customers Say
          </h2>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {testimonials.map((t) => (
            <div
              key={t.name}
              role="article"
              className="bg-white border-[1.5px] border-gray-200 rounded-xl p-6"
            >
              <div
                className="flex gap-0.5 mb-4"
                aria-label={`${t.rating} out of 5 stars`}
              >
                {[1, 2, 3, 4, 5].map((s) => (
                  <Star
                    key={s}
                    className={cn(
                      "h-3.5 w-3.5",
                      s <= t.rating
                        ? "fill-amber-400 text-amber-400"
                        : "fill-gray-200 text-gray-200",
                    )}
                  />
                ))}
              </div>
              <p className="text-sm text-gray-600 leading-relaxed italic mb-5">
                &ldquo;{t.quote}&rdquo;
              </p>
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-full bg-blue-50 flex items-center justify-center text-xs font-bold text-blue-600 shrink-0">
                  {t.initials}
                </div>
                <div>
                  <p className="text-sm font-semibold text-gray-900">
                    {t.name}
                  </p>
                  <p className="text-[10px] text-gray-400">{t.role}</p>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}

/* ─── Newsletter ────────────────────────────────────────── */

function NewsletterSection({
  email,
  setEmail,
  onSubmit,
}: {
  email: string;
  setEmail: (v: string) => void;
  onSubmit: (e: React.FormEvent) => void;
}) {
  return (
    <section>
      <div className="max-w-[1280px] mx-auto px-6">
        <div className="bg-blue-600 rounded-2xl px-10 py-12 text-center">
          <h2 className="text-[28px] font-extrabold text-white mb-2">
            Don&apos;t Miss Out
          </h2>
          <p className="text-sm text-white/75 mb-6">
            Subscribe to get the latest trends &amp; exclusive offers.
          </p>
          <form
            onSubmit={onSubmit}
            className="flex flex-col sm:flex-row gap-3 max-w-md mx-auto"
          >
            <Input
              type="email"
              placeholder="Enter your email address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              className="flex-1 bg-white/[0.12] border-white/25 text-white placeholder:text-white/50 focus:border-white min-h-[44px]"
            />
            <Button
              type="submit"
              className="bg-white text-blue-600 hover:bg-gray-100 font-bold shrink-0 min-h-[44px] px-8"
            >
              Subscribe
            </Button>
          </form>
          <p className="text-[10px] text-white/50 mt-3">
            No spam, ever. Unsubscribe at any time.
          </p>
        </div>
      </div>
    </section>
  );
}

/* ─── Home Page ─────────────────────────────────────────── */

export default function HomePage() {
  const { data: featured, isLoading: featuredLoading } = useFeaturedProducts();
  const { data: newArrivals, isLoading: newLoading } = useNewArrivals();
  const [email, setEmail] = useState("");

  const handleSubscribe = (e: React.FormEvent) => {
    e.preventDefault();
    if (email) {
      toast.success("Subscribed successfully!");
      setEmail("");
    }
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-1">
        <HeroBanner />
        <CategorySection />

        <section className="py-8">
          <div className="max-w-[1280px] mx-auto px-6">
            <ProductSection
              title="New Arrivals"
              viewAllHref="/shop?sort=createdDate,desc"
              products={newArrivals?.content}
              isLoading={newLoading}
            />
          </div>
        </section>

        <PromoSection />

        <BestSellersSection products={featured?.content} />

        <section className="py-8">
          <div className="max-w-[1280px] mx-auto px-6">
            <ProductSection
              title="Featured Products"
              subtitle="Handpicked for you"
              viewAllHref="/shop?featured=true"
              products={featured?.content}
              isLoading={featuredLoading}
            />
          </div>
        </section>

        <div className="py-8">
          <NewsletterSection
            email={email}
            setEmail={setEmail}
            onSubmit={handleSubscribe}
          />
        </div>

        <TestimonialsSection />
      </main>
      <Footer />
    </div>
  );
}
