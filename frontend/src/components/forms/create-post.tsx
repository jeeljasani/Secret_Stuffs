import React, { useEffect, useState } from "react";
import {
  Button,
  Upload,
  Form,
  Input,
  Select,
  Typography,
  message,
  Row,
  Col,
  UploadProps,
} from "antd";
import { UploadOutlined } from "@ant-design/icons";
import { useTheme } from "@/context/theme-context";
import { uploadFiles } from "@/services/files/firebaseService";
import {
  createPost,
  Post,
  fetchCategories,
  fetchConditions,
} from "@/services/item/post-service";
import { useAuth } from "@/context/auth-context";
import { RcFile, UploadFile as AntUploadFile } from "antd/es/upload/interface";

const { Option } = Select;
const { Title } = Typography;

export function CreateDonationForm() {
  const { darkMode } = useTheme();
  const { user } = useAuth();
  const [form] = Form.useForm<Post>();
  const [fileList, setFileList] = useState<AntUploadFile<RcFile>[]>([]);
  const [uploading, setUploading] = useState(false);
  const [categories, setCategories] = useState<string[]>([]);
  const [conditions, setConditions] = useState<string[]>([]);

  useEffect(() => {
    const loadEnums = async () => {
      try {
        const [fetchedCategories, fetchedConditions] = await Promise.all([
          fetchCategories(),
          fetchConditions(),
        ]);
        setCategories(fetchedCategories);
        setConditions(fetchedConditions);
      } catch (error) {
        message.error("Failed to load categories or conditions.");
        console.error(error);
      }
    };
    loadEnums();
  }, []);

  const handleFinish = async (values: Post) => {
    setUploading(true);
    try {
      const validFiles = fileList.map((file: AntUploadFile<RcFile>) => ({
        name: file.name,
        originFileObj: file.originFileObj as File,
      }));

      const imageUrls = await uploadFiles(validFiles, "PostImages");

      const postData: Post = {
        ...values,
        email: user?.email || "no-email@provided.com",
        itemPostImageUrl: imageUrls[0] || "",
      };

      await createPost(postData);
      message.success("Donation post created successfully!");
      form.resetFields();
      setFileList([]);
    } catch (error) {
      console.error("Error creating post:", error);
      message.error("Failed to create post. Please try again.");
    } finally {
      setUploading(false);
    }
  };

  const handleChange: UploadProps["onChange"] = (info) => {
    const newFileList = info.fileList.slice(-5).map((file) => ({
      ...file,
      status: "done" as const,
    }));
    setFileList(newFileList);
  };

  const beforeUpload: UploadProps["beforeUpload"] = (file) => {
    const isImage = file.type.startsWith("image/");
    if (!isImage) {
      message.error(`${file.name} is not a valid image file.`);
    }
    const isLessThan2MB = file.size / 1024 / 1024 < 2;
    if (!isLessThan2MB) {
      message.error(`${file.name} exceeds the 2MB limit.`);
    }
    return isImage && isLessThan2MB;
  };

  const containerStyle = {
    width: "90%",
    maxWidth: "800px",
    margin: "50px auto",
    padding: "32px",
    borderRadius: "12px",
    backgroundColor: darkMode ? "#1c1c1e" : "#ffffff",
    border: darkMode ? "1px solid #2c2c2e" : "1px solid #ddd",
    boxShadow: darkMode
      ? "0px 4px 8px rgba(0, 0, 0, 0.6)"
      : "0px 4px 8px rgba(0, 0, 0, 0.1)",
  };

  const inputStyle = {
    backgroundColor: darkMode ? "#bbbbbb" : "#ffffff",
    color: darkMode ? "#ffffff" : "#000000",
    borderColor: darkMode ? "#444" : "#ccc",
    borderRadius: "4px",
    "::placeholder": {
      color: darkMode ? "#bbbbbb" : "#888888",
    },
  };

  const selectDropdownStyle = {
    backgroundColor: darkMode ? "#3a3a3c" : "#ffffff",
    color: darkMode ? "#ffffff" : "#000000",
    border: darkMode ? "1px solid #444" : "1px solid #ddd",
    borderRadius: "6px",
    boxShadow: darkMode
      ? "0 4px 8px rgba(0, 0, 0, 0.7)"
      : "0 4px 8px rgba(0, 0, 0, 0.15)",
  };

  const titleColor = darkMode ? "#ffffff" : "#000000";

  return (
    <div style={containerStyle}>
      <Title
        level={2}
        style={{ textAlign: "center", marginBottom: "24px", color: titleColor }}
      >
        Create Donation Post
      </Title>

      <Form form={form} layout="vertical" onFinish={handleFinish}>
        <Row gutter={16}>
          <Col xs={24} sm={12}>
            <Form.Item
              name="title"
              rules={[{ required: true, message: "Please enter the title" }]}
            >
              <Input
                placeholder="Enter the title"
                style={inputStyle}
                allowClear
              />
            </Form.Item>
          </Col>
          <Col xs={24} sm={12}>
            <Form.Item
              name="address"
              rules={[{ required: true, message: "Please enter the address" }]}
            >
              <Input
                placeholder="Enter the address"
                style={inputStyle}
                allowClear
              />
            </Form.Item>
          </Col>
        </Row>

        <Row gutter={16}>
          <Col xs={24} sm={12}>
            <Form.Item
              name="condition"
              rules={[
                { required: true, message: "Please select the condition" },
              ]}
            >
              <Select
                placeholder="Select condition"
                dropdownStyle={selectDropdownStyle}
                style={inputStyle}
              >
                {conditions.map((condition) => (
                  <Option key={condition} value={condition}>
                    {condition}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
          <Col xs={24} sm={12}>
            <Form.Item
              name="category"
              rules={[{ required: true, message: "Please select a category" }]}
            >
              <Select
                placeholder="Select category"
                dropdownStyle={selectDropdownStyle}
                style={inputStyle}
              >
                {categories.map((category) => (
                  <Option key={category} value={category}>
                    {category}
                  </Option>
                ))}
              </Select>
            </Form.Item>
          </Col>
        </Row>

        <Form.Item
          name="description"
          rules={[{ required: true, message: "Please enter a description" }]}
        >
          <Input.TextArea
            rows={4}
            placeholder="Enter description"
            style={inputStyle}
            allowClear
          />
        </Form.Item>

        <Form.Item>
          <Upload
            listType="picture-card"
            fileList={fileList}
            onChange={handleChange}
            beforeUpload={beforeUpload}
          >
            {fileList.length < 5 && (
              <div style={{ textAlign: "center", padding: "8px" }}>
                <UploadOutlined />
                <div>Upload</div>
              </div>
            )}
          </Upload>
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" block loading={uploading}>
            {uploading ? "Creating Post..." : "Create Donation Post"}
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
}