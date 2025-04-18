import React, { useState } from "react";
import {
  Form,
  Input,
  Button,
  Checkbox,
  Typography,
  message,
  Row,
  Col,
} from "antd";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { useTheme } from "@/context/theme-context";
import { register } from "@/services/auth/index";
import axios from "axios";

const { Title, Text, Link } = Typography;

interface SignupProps {
  toggleMode: () => void; // Prop to toggle between login and signup
}

interface SignupFormValues {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  terms: boolean;
}

export function Signup({ toggleMode }: SignupProps) {
  const [loading, setLoading] = useState<boolean>(false);

  const { darkMode } = useTheme();
  const navigate = useNavigate();

  const handleSubmit = async (values: SignupFormValues) => {
    setLoading(true);
    try {
      await register({
        firstName: values.firstName,
        lastName: values.lastName,
        email: values.email,
        password: values.password,
      });
      navigate("/home");
    } catch (error) {
      const errorMessage = getErrorMessage(error);
      console.error("Signup failed:", errorMessage);
      message.error(errorMessage || "Signup failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  // Extract error messages from the server response
  function getErrorMessage(error: unknown): string {
    if (axios.isAxiosError(error) && error.response?.data?.message) {
      return error.response.data.message;
    }
    return String(error);
  }

  // Dynamic styling for dark and light mode
  const containerBackgroundColor = darkMode ? "#121212" : "#fff";
  const inputBackgroundColor = darkMode ? "#1E1E1E" : "#fff";
  const inputBorderColor = darkMode ? "#444" : "#d9d9d9";
  const inputTextColor = darkMode ? "#f5f5f5" : "#000";
  const textColor = darkMode ? "#e0e0e0" : "#000";

  return (
      <div
        style={{
          width: "100%",
          maxWidth: "600px", // Adjusted for responsiveness
          padding: "40px",
          borderRadius: "12px",
          boxShadow: "0 8px 24px rgba(0, 0, 0, 0.2)",
          backgroundColor: containerBackgroundColor,
        }}
      >
        <Title
          level={2}
          style={{
            textAlign: "center",
            marginBottom: "30px",
            color: textColor,
          }}
        >
          Sign Up
        </Title>

        <Form name="signup-form" layout="vertical" onFinish={handleSubmit}>
          {/* Row for First Name and Last Name */}
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12}>
              <Form.Item
                label={<Text style={{ color: textColor }}>First Name</Text>}
                name="firstName"
                rules={[
                  { required: true, message: "Please input your first name!" },
                  { min: 2, message: "First name must be at least 2 characters!" },
                ]}
              >
                <Input
                  placeholder="Enter your first name"
                  style={{
                    backgroundColor: inputBackgroundColor,
                    color: inputTextColor,
                    borderColor: inputBorderColor,
                    borderRadius: "8px",
                  }}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item
                label={<Text style={{ color: textColor }}>Last Name</Text>}
                name="lastName"
                rules={[
                  { required: true, message: "Please input your last name!" },
                  { min: 2, message: "Last name must be at least 2 characters!" },
                ]}
              >
                <Input
                  placeholder="Enter your last name"
                  style={{
                    backgroundColor: inputBackgroundColor,
                    color: inputTextColor,
                    borderColor: inputBorderColor,
                    borderRadius: "8px",
                  }}
                />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            label={<Text style={{ color: textColor }}>Email</Text>}
            name="email"
            rules={[
              { required: true, message: "Please input your email!" },
              { type: "email", message: "The input is not a valid email!" },
            ]}
          >
            <Input
              placeholder="Enter your email"
              style={{
                backgroundColor: inputBackgroundColor,
                color: inputTextColor,
                borderColor: inputBorderColor,
                borderRadius: "8px",
              }}
            />
          </Form.Item>

          {/* Row for Password and Confirm Password */}
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={12}>
              <Form.Item
                label={<Text style={{ color: textColor }}>Password</Text>}
                name="password"
                rules={[
                  { required: true, message: "Please input your password!" },
                  { min: 8, message: "Password must be at least 8 characters!" },
                ]}
              >
                <Input.Password
                  placeholder="Enter your password"
                  style={{
                    backgroundColor: inputBackgroundColor,
                    color: inputTextColor,
                    borderColor: inputBorderColor,
                    borderRadius: "8px",
                  }}
                />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item
                label={<Text style={{ color: textColor }}>Confirm Password</Text>}
                name="confirmPassword"
                dependencies={["password"]}
                rules={[
                  { required: true, message: "Please confirm your password!" },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue("password") === value) {
                        return Promise.resolve();
                      }
                      return Promise.reject(
                        new Error("The two passwords do not match!")
                      );
                    },
                  }),
                ]}
              >
                <Input.Password
                  placeholder="Confirm your password"
                  style={{
                    backgroundColor: inputBackgroundColor,
                    color: inputTextColor,
                    borderColor: inputBorderColor,
                    borderRadius: "8px",
                  }}
                />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item
            name="terms"
            valuePropName="checked"
            rules={[
              {
                required: true,
                message: "You must accept the terms and conditions!",
              },
            ]}
          >
            <Checkbox style={{ color: textColor }}>
              I accept the <Link href="/terms">Terms of Service</Link> and{" "}
              <Link href="/privacy">Privacy Policy</Link>
            </Checkbox>
          </Form.Item>

          <Form.Item>
            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
                style={{ width: "100%", height: "45px", borderRadius: "8px" }}
              >
                Sign Up
              </Button>
            </motion.div>
          </Form.Item>
        </Form>

        <div style={{ textAlign: "center", marginTop: "16px" }}>
          <Text style={{ color: textColor }}>
            Already have an account?{" "}
            <Link onClick={toggleMode} style={{ color: "#1890ff" }}>
              Log in
            </Link>
          </Text>
        </div>
      </div>
  );
}