import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Cart } from "@/types";

export interface CartState {
  cart: Cart | null;
  isOpen: boolean;
  itemCount: number;
}

const initialState: CartState = {
  cart: null,
  isOpen: false,
  itemCount: 0,
};

export const cartSlice = createSlice({
  name: "cart",
  initialState,
  reducers: {
    setCart(state, action: PayloadAction<Cart>) {
      state.cart = action.payload;
      state.itemCount = action.payload.items.reduce(
        (sum, item) => sum + item.quantity,
        0,
      );
    },
    clearCart(state) {
      state.cart = null;
      state.itemCount = 0;
    },
    openCart(state) {
      state.isOpen = true;
    },
    closeCart(state) {
      state.isOpen = false;
    },
    toggleCart(state) {
      state.isOpen = !state.isOpen;
    },
  },
});

export const { setCart, clearCart, openCart, closeCart, toggleCart } =
  cartSlice.actions;
export default cartSlice.reducer;
