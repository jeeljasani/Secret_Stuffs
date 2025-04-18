import React, { useEffect, useState } from "react";
import { Form, Input, Button, Select, message, Spin } from "antd";
import { getItemById, updatePost, fetchCategories, fetchConditions } from "@/services/item/post-service";

const { TextArea } = Input;

interface EditPostPageProps {
  id: number;
  onClose: () => void;
}

interface EditPostFormValues {
  title: string;
  description: string;
  category: string;
  condition: string;
  address?: string;
}

const EditPostPage: React.FC<EditPostPageProps> = ({ id, onClose }) => {
  const [form] = Form.useForm<EditPostFormValues>();
  const [loading, setLoading] = useState<boolean>(true);
  const [categories, setCategories] = useState<string[]>([]);
  const [conditions, setConditions] = useState<string[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const fetchedPost = await getItemById(id);
        if (!fetchedPost) {
          throw new Error("Post not found");
        }
        form.setFieldsValue(fetchedPost);
        const [fetchedCategories, fetchedConditions] = await Promise.all([
          fetchCategories(),
          fetchConditions(),
        ]);

        setCategories(fetchedCategories);
        setConditions(fetchedConditions);
      } catch {
        message.error("Failed to load data.");
        onClose();
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id, form, onClose]);

  const onFinish = async (values: EditPostFormValues) => {
    try {
      setLoading(true);
      await updatePost(id, values);
      message.success("Post updated successfully!");
      onClose();
    } catch {
      message.error("Failed to update the post.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      {loading ? (
        <Spin size="large" />
      ) : (
        <Form form={form} layout="vertical" onFinish={onFinish}>
          <Form.Item
            name="title"
            label="Title"
            rules={[{ required: true, message: "Please enter the title" }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="description"
            label="Description"
            rules={[{ required: true, message: "Please enter the description" }]}
          >
            <TextArea rows={4} />
          </Form.Item>
          <Form.Item
            name="category"
            label="Category"
            rules={[{ required: true, message: "Please select a category" }]}
          >
            <Select>
              {categories.map((category) => (
                <Select.Option key={category} value={category}>
                  {category}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item
            name="condition"
            label="Condition"
            rules={[{ required: true, message: "Please select the condition" }]}
          >
            <Select>
              {conditions.map((condition) => (
                <Select.Option key={condition} value={condition}>
                  {condition}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>
          <Form.Item name="address" label="Address">
            <Input />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={loading}>
              Update Post
            </Button>
          </Form.Item>
        </Form>
      )}
    </>
  );
};

export default EditPostPage;
