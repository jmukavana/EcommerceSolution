import { createSlice } from "@reduxjs/toolkit";

interface UiState {
  sidebarCollapsed: boolean;
}

const initialState: UiState = {
  sidebarCollapsed: false,
};

export const uiSlice = createSlice({
  name: "ui",
  initialState,
  reducers: {
    toggleSidebar(state) {
      state.sidebarCollapsed = !state.sidebarCollapsed;
    },
    collapseSidebar(state) {
      state.sidebarCollapsed = true;
    },
    expandSidebar(state) {
      state.sidebarCollapsed = false;
    },
  },
});

export const { toggleSidebar, collapseSidebar, expandSidebar } =
  uiSlice.actions;
export default uiSlice.reducer;
