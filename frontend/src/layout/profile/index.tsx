import React from "react"; // Import React to avoid the error
import { Profile } from "@/components/forms/profile"; // Ensure the import path is correct
import { MainLayout } from "@/layout/main-layout";

const ProfileLayout = () => {
  return (
    <MainLayout>
      <Profile />
    </MainLayout>
  );
};

export default ProfileLayout;
