import React, { useEffect } from "react";
import { MainLayout } from "@/layout/main-layout";
import { Button, message } from "antd";
import { useParams, useNavigate } from "react-router-dom";
import { verifyEmail } from "@/services/auth";

export function VerifyEmail() {
  const { token } = useParams<{ token: string }>();
  const navigate = useNavigate();

  useEffect(() => {
    const verifyUserEmail = async () => {
      try {
        if (token) {
          await verifyEmail(token);
          message.success("Email verified successfully!");
          navigate("/home");
        }
      } catch (error) {
        console.error('Email verification failed:', error);
      }
    };

    verifyUserEmail();
  }, [token]);

  return (
    <MainLayout>
      <div>VerifyEmail</div>
      <Button onClick={() => navigate("/home")}>Back to Home</Button>
    </MainLayout>
  );
}
