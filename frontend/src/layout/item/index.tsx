import React from "react"; // Add this import
import { MainLayout } from "@/layout/main-layout";
import ItemDetails from "@/components/item-details";

export function ItemLayout() {
  return (
    <MainLayout>
      <ItemDetails />
    </MainLayout>
  );
}