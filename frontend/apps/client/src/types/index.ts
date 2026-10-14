export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  enabled: boolean;
  roles: Role[];
}

export interface Role {
  id: string;
  name: string;
  permissions: Permission[];
}

export interface Permission {
  id: string;
  name: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  phoneNumber: string;
}

export interface VerifyOtpRequest {
  email: string;
  otp: string;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
  description: string;
  imageUrl: string;
  parentId: string | null;
  children: Category[];
}

export interface Brand {
  id: string;
  name: string;
  slug: string;
  logoUrl: string;
}

export interface ProductImage {
  id: string;
  url: string;
  alt: string;
  primary: boolean;
}

export interface ProductAttributeValue {
  id: string;
  attribute: { name: string };
  value: string;
}

export interface ProductVariant {
  id: string;
  sku: string;
  name: string;
  price: number;
  compareAtPrice: number | null;
  costPrice: number;
  isDefault: boolean;
  active: boolean;
  imageUrl: string | null;
  attributeValues: ProductAttributeValue[];
}

export interface Product {
  id: string;
  name: string;
  slug: string;
  shortDescription: string;
  description: string;
  status: "DRAFT" | "PUBLISHED" | "ARCHIVED";
  type: "SIMPLE" | "VARIABLE";
  brand: Brand | null;
  categories: Category[];
  images: ProductImage[];
  variants: ProductVariant[];
  averageRating: number;
  totalReviews: number;
  featured: boolean;
  tags: string[];
}

export interface CartItem {
  id: string;
  variant: ProductVariant & { product: Product };
  quantity: number;
  price: number;
}

export interface Cart {
  id: string;
  items: CartItem[];
  subtotal: number;
}

export interface Address {
  id: string;
  label: string;
  street: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  phone: string;
  isDefault: boolean;
}

export interface OrderItem {
  id: string;
  productName: string;
  variantName: string;
  quantity: number;
  price: number;
  imageUrl: string;
}

export interface Order {
  id: string;
  orderNumber: string;
  status:
    | "PENDING"
    | "CONFIRMED"
    | "PROCESSING"
    | "SHIPPED"
    | "DELIVERED"
    | "CANCELLED";
  items: OrderItem[];
  subtotal: number;
  discountAmount: number;
  shippingAmount: number;
  taxAmount: number;
  total: number;
  createdDate: string;
  shippingAddress: Address;
}

export interface Review {
  id: string;
  user: { firstName: string; lastName: string };
  rating: number;
  comment: string;
  createdDate: string;
}

export interface WishlistItem {
  id: string;
  product: Product;
}

export interface Wishlist {
  id: string;
  items: WishlistItem[];
  shareToken: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ApiResponse<T> {
  data: T;
  message: string;
  status: number;
}
