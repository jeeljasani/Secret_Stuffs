import React from "react"; // Add this import
import { MainLayout } from "@/layout/main-layout";
import { CreateDonationForm } from "@/components/forms/create-post";

export function CreateDonationLayout() {
  return (
    <MainLayout>
      <CreateDonationForm />
    </MainLayout>
  );
}