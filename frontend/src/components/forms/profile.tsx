import React, { useEffect, useState } from "react";
import {
  Form,
  Input,
  Button,
  Upload,
  message,
  Typography,
  UploadFile,
} from "antd";
import { PlusOutlined } from "@ant-design/icons";
import { useAuth } from "@/context/auth-context";
import { useTheme } from "@/context/theme-context";
import { useNavigate } from "react-router-dom";
import {
  ProfileData,
  getUserProfile,
  updateUserProfile,
} from "@/services/user";
import { uploadFiles } from "@/services/files/firebaseService";
import type { RcFile } from "antd/es/upload";

const { Title } = Typography;

export function Profile() {
  const navigate = useNavigate();
  const { darkMode } = useTheme();
  const { getUser } = useAuth();
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [fileList, setFileList] = useState<UploadFile[]>([]);
  const [profile, setProfile] = useState<ProfileData>({
    email: "",
    profileImageURL: "",
  });

  const handleUploadChange = ({ fileList: newFileList }: { fileList: UploadFile[] }) => {
    setFileList(newFileList);
  };

  const beforeUpload = (file: RcFile): boolean => {
    const isJpgOrPng = file.type === "image/jpeg" || file.type === "image/png";
    if (!isJpgOrPng) {
      message.error("You can only upload JPG/PNG files!");
      return false;
    }

    const isSizeValid = file.size / 1024 / 1024 < 2; // Limit to 2MB
    if (!isSizeValid) {
      message.error("Image must be smaller than 2MB!");
      return false;
    }

    return true;
  };

  const handleFormSubmit = async (values: ProfileData) => {
    setLoading(true);
    try {
      let updatedImageUrl = profile.profileImageURL;

      if (fileList.length > 0 && fileList[0].originFileObj) {
        const uploadedUrls = await uploadFiles(
          [{ originFileObj: fileList[0].originFileObj as RcFile, name: fileList[0].name }],
          "ProfileImages"
        );
        updatedImageUrl = uploadedUrls[0];
      }

      await updateUserProfile(values.email, {
        firstName: values.firstName,
        lastName: values.lastName,
        profileImageURL: updatedImageUrl,
      });

      message.success("Profile updated successfully!");
      setProfile((prev) => ({ ...prev, profileImageURL: updatedImageUrl }));
    } catch {
      message.error("Profile update failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const fetchProfile = async () => {
    const user = getUser();
    if (user?.email) {
      try {
        const userProfile = await getUserProfile(user.email);
        setProfile(userProfile);
        form.setFieldsValue(userProfile);

        if (userProfile.profileImageURL) {
          setFileList([
            {
              uid: "-1",
              name: "Profile Picture",
              status: "done",
              url: userProfile.profileImageURL,
            },
          ]);
        }
      } catch {
        message.error("Failed to fetch profile information.");
      }
    }
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const containerBackgroundColor = darkMode ? "#000000" : "#fff";
  const inputBackgroundColor = darkMode ? "#1f1f1f" : "#fff";
  const inputTextColor = darkMode ? "#fff" : "#000";

  return (
    <div
      style={{
        width: "100%",
        maxWidth: "500px",
        margin: "50px auto",
        padding: "30px",
        borderRadius: "10px",
        backgroundColor: containerBackgroundColor,
        boxShadow: darkMode
          ? "0px 4px 12px rgba(255, 255, 255, 0.1)"
          : "0px 4px 12px rgba(0, 0, 0, 0.1)",
        textAlign: "center",
      }}
    >
      <Title
        level={2}
        style={{
          textAlign: "center",
          marginBottom: "20px",
          color: inputTextColor,
        }}
      >
        Your Profile
      </Title>

      <div style={{ display: "flex", justifyContent: "center", marginBottom: "20px" }}>
        <Upload
          listType="picture-circle"
          fileList={fileList}
          onChange={handleUploadChange}
          beforeUpload={beforeUpload}
          showUploadList={{
            showRemoveIcon: true,
            showPreviewIcon: false,
          }}
          maxCount={1}
        >
          {fileList.length < 1 && (
            <div>
              <PlusOutlined />
              <div style={{ marginTop: 8 }}>Upload</div>
            </div>
          )}
        </Upload>
      </div>

      <Form
        layout="vertical"
        form={form}
        onFinish={handleFormSubmit}
        style={{ marginTop: "20px" }}
      >
        <Form.Item
          label={<span style={{ color: inputTextColor }}>First Name</span>}
          name="firstName"
          rules={[{ required: true, message: "First Name is required" }]}
        >
          <Input
            placeholder="Enter your First Name"
            style={{
              backgroundColor: inputBackgroundColor,
              color: inputTextColor,
            }}
          />
        </Form.Item>
        <Form.Item
          label={<span style={{ color: inputTextColor }}>Last Name</span>}
          name="lastName"
          rules={[{ required: true, message: "Last Name is required" }]}
        >
          <Input
            placeholder="Enter your Last Name"
            style={{
              backgroundColor: inputBackgroundColor,
              color: inputTextColor,
            }}
          />
        </Form.Item>

        <Form.Item
        label={<span style={{ color: inputTextColor }}>Email</span>}
        name="email"
      >
        <Input
          placeholder="Enter your email"
          style={{
            backgroundColor: darkMode ? "#1f1f1f" : "#f5f5f5", // Light gray to indicate disabled
            color: inputTextColor,
            border: "none",
            cursor: "not-allowed", // Show that it is not editable
          }}
          disabled
        />
      </Form.Item>

        <Form.Item>
          <Button
            type="primary"
            htmlType="submit"
            loading={loading}
            block
            style={{
              height: "40px",
              marginTop: "10px",
              backgroundColor: darkMode ? "#1890ff" : "#007bff",
              color: "#fff",
              borderColor: "transparent",
            }}
          >
            Update Profile
          </Button>

          <Form.Item>
          <Button
            type="primary"
            onClick={() => navigate('/update-password')} 
            block
            style={{
              height: "40px",
              marginTop: "10px",
              backgroundColor: "#007bff",
              color: "#fff",
              borderColor: "transparent",
            }}
          >
            Update Password
          </Button>
        </Form.Item>

        </Form.Item>
      </Form>
    </div>
  );
}