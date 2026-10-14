import { createSlice } from "@reduxjs/toolkit";

export interface UiState {
  searchOpen: boolean;
  mobileMenuOpen: boolean;
}

const initialState: UiState = {
  searchOpen: false,
  mobileMenuOpen: false,
};

export const uiSlice = createSlice({
  name: "ui",
  initialState,
  reducers: {
    openSearch(state) {
      state.searchOpen = true;
    },
    closeSearch(state) {
      state.searchOpen = false;
    },
    openMobileMenu(state) {
      state.mobileMenuOpen = true;
    },
    closeMobileMenu(state) {
      state.mobileMenuOpen = false;
    },
  },
});

export const { openSearch, closeSearch, openMobileMenu, closeMobileMenu } =
  uiSlice.actions;
export default uiSlice.reducer;
