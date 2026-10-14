import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { User } from "@/types";

export interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  pendingEmail: string | null;
}

const initialState: AuthState = {
  user: null,
  accessToken: null,
  refreshToken: null,
  pendingEmail: null,
};

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setTokens(
      state,
      action: PayloadAction<{ accessToken: string; refreshToken: string }>,
    ) {
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      if (typeof window !== "undefined") {
        localStorage.setItem("accessToken", action.payload.accessToken);
        localStorage.setItem("refreshToken", action.payload.refreshToken);
      }
    },
    setUser(state, action: PayloadAction<User>) {
      state.user = action.payload;
    },
    setPendingEmail(state, action: PayloadAction<string>) {
      state.pendingEmail = action.payload;
    },
    logout(state) {
      state.user = null;
      state.accessToken = null;
      state.refreshToken = null;
      state.pendingEmail = null;
      if (typeof window !== "undefined") {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
      }
    },
    hydrate(state) {
      if (typeof window !== "undefined") {
        state.accessToken = localStorage.getItem("accessToken");
        state.refreshToken = localStorage.getItem("refreshToken");
      }
    },
  },
});

export const { setTokens, setUser, setPendingEmail, logout, hydrate } =
  authSlice.actions;
export default authSlice.reducer;
