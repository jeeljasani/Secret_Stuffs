import React from "react"; // Import React to avoid the error
import { MainLayout } from "@/layout/main-layout";
import UpdatePassword from "@/components/forms/update-password";

const UpdatePasswordLayout = () => {
  return (
    <MainLayout>
      <UpdatePassword />
    </MainLayout>
  );
};

export default UpdatePasswordLayout;
