import React, { useState } from "react";
import {
  Card,
  Avatar,
  Tag,
  Button,
  Typography,
  Space,
  Row,
  Col,
  Divider,
  Modal,
  Dropdown,
  Menu,
  message,
} from "antd";
import {
  LikeOutlined,
  LikeFilled,
  ShareAltOutlined,
  EnvironmentOutlined,
  MailOutlined,
  FacebookOutlined,
  TwitterOutlined,
  WhatsAppOutlined,
  CopyOutlined,
  EditOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined,
  EllipsisOutlined,
} from "@ant-design/icons";
import { useTheme } from "@/context/theme-context";
import EditPostPage from "@/components/forms/EditPostPage";
import modal from "antd/es/modal";

const { Text, Title } = Typography;

interface DonationPostProps {
  id: number;
  title: string;
  description: string;
  category: string;
  condition: string;
  address: string;
  email: string;
  itemImageUrl: string;
  userImageUrl: string;
  userName: string;
  isEditable?: boolean;
  onClick?: () => void; // Triggers when the card is clicked
  onDelete?: () => void;
  onUpdate?: () => void;
  customFooter?: React.ReactNode;
}

const DonationPost: React.FC<DonationPostProps> = ({
  id,
  title,
  description,
  category,
  condition,
  address,
  email,
  itemImageUrl,
  userImageUrl,
  userName,
  isEditable = false,
  onClick,
  onDelete,
  onUpdate,
  customFooter,
}) => {
  const { darkMode } = useTheme();
  const [imageLoaded, setImageLoaded] = useState(false);
  const [liked, setLiked] = useState(false);
  const [shareVisible, setShareVisible] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);

  const handleLikeClick = (e: React.MouseEvent) => {
    e.stopPropagation(); // Prevent triggering the card's onClick
    setLiked(!liked);
  };

  const handleShareClick = (e: React.MouseEvent) => {
    e.stopPropagation(); // Prevent triggering the card's onClick
    setShareVisible(true);
  };

  const handleShareClose = () => {
    setShareVisible(false);
  };

  const handleCopyLink = () => {
    const postUrl = `${window.location.origin}/item/${id}`;
    navigator.clipboard.writeText(postUrl);
    message.success("Link copied to clipboard!");
  };

  const handleEditOpen = () => {
    setEditModalOpen(true);
  };

  const handleEditClose = () => {
    setEditModalOpen(false);
    if (onUpdate) onUpdate();
  };

  const confirmDelete = () => {
    modal.confirm({
      title: 'Confirm',
      icon: <ExclamationCircleOutlined />,
      content: 'Are you sure you want to delete this post?',
      okText: 'Delete',
      cancelText: 'Cancel',
      onOk: () => {
        onDelete?.();
      }
    });
  };
  const menu = (
    <Menu>
      <Menu.Item key="edit" icon={<EditOutlined />} onClick={handleEditOpen}>
        Update
      </Menu.Item>
      <Menu.Item
        key="delete"
        icon={<DeleteOutlined style={{ color: "#ff4d4f" }} />}
        onClick={confirmDelete}
      >
        Delete
      </Menu.Item>
    </Menu>
  );

  const styles = {
    card: {
      backgroundColor: darkMode ? "#1f1f1f" : "#ffffff",
      color: darkMode ? "#e0e0e0" : "#000000",
      width: "100%",
      maxWidth: 600,
      margin: "16px auto",
      borderRadius: "12px",
      overflow: "hidden",
      border: darkMode ? "1px solid #333" : "1px solid #e0e0e0",
      boxShadow: darkMode
        ? "0 4px 12px rgba(255, 255, 255, 0.05), 0 2px 4px rgba(0, 0, 0, 0.8)"
        : "0 4px 12px rgba(0, 0, 0, 0.1)",
      cursor: "pointer", // Add pointer cursor for clickable cards
    },
    divider: {
      margin: "8px 0",
      borderColor: darkMode ? "#444444" : "#e0e0e0",
    },
    buttonRow: {
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
      marginTop: "12px",
    },
    tags: {
      category: darkMode ? "#5a9ef8" : "#007acc",
      condition: darkMode ? "#58d68d" : "#28a745",
    },
    imageContainer: {
      position: "relative" as const,
      width: "100%",
      height: "180px",
      overflow: "hidden",
      borderRadius: "8px",
    },
    image: {
      width: "100%",
      height: "100%",
      objectFit: "cover" as React.CSSProperties["objectFit"],
      opacity: imageLoaded ? 1 : 0,
      transition: "opacity 0.5s ease-in-out",
      border: darkMode ? "1px solid #333" : "#ddd",
    },
  };

  return (
    <>
      <Card
        style={styles.card}
        hoverable
        onClick={onClick} // Attach onClick handler to the card
      >
        {isEditable && (
          <Dropdown overlay={menu} placement="topRight" trigger={["click"]}>
            <Button
              type="text"
              icon={<EllipsisOutlined />}
              style={{ float: "right", fontSize: "18px" }}
            />
          </Dropdown>
        )}

        <Row align="middle" style={{ marginBottom: 12 }}>
          <Col flex="48px">
            <Avatar
              src={userImageUrl || "https://via.placeholder.com/40"}
              size={48}
              alt={`${userName}'s profile picture`}
            />
          </Col>
          <Col flex="auto" style={{ paddingLeft: 12 }}>
            <Text strong style={{ fontSize: "16px" }}>
              {userName || "Anonymous"}
            </Text>
            <br />
            <Text type="secondary">
              <MailOutlined style={{ marginRight: 8 }} />
              {email || "Email not available"}
            </Text>
          </Col>
        </Row>

        <Divider style={styles.divider} />

        <Row gutter={16}>
          <Col xs={24} sm={10}>
            <div style={styles.imageContainer}>
              <img
                alt={title || "Donation item"}
                src={itemImageUrl || "https://via.placeholder.com/150"}
                style={styles.image}
                onLoad={() => setImageLoaded(true)}
              />
            </div>
          </Col>
          <Col xs={24} sm={14}>
            <Space direction="vertical" size="small" style={{ width: "100%" }}>
              <Title
                level={4}
                style={{
                  margin: 0,
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                }}
              >
                {title || "Untitled"}
              </Title>
              <Text
                type="secondary"
                style={{
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                  display: "-webkit-box",
                  WebkitLineClamp: 2,
                  WebkitBoxOrient: "vertical",
                }}
              >
                {description || "No description available"}
              </Text>
              <Space>
                <Tag color={styles.tags.category}>
                  {category || "Miscellaneous"}
                </Tag>
                <Tag color={styles.tags.condition}>
                  {condition || "Unknown"}
                </Tag>
              </Space>
              <Text
                type="secondary"
                style={{
                  whiteSpace: "nowrap",
                  overflow: "hidden",
                  textOverflow: "ellipsis",
                }}
              >
                <EnvironmentOutlined style={{ marginRight: 8 }} />
                {address || "Address not provided"}
              </Text>
            </Space>
          </Col>
        </Row>

        <Divider style={styles.divider} />

        <Row style={styles.buttonRow}>
          <Button
            type="text"
            icon={liked ? <LikeFilled /> : <LikeOutlined />}
            onClick={handleLikeClick}
          >
            {liked ? "Liked" : "Like"}
          </Button>
          {customFooter && (
            <div style={{ textAlign: "center", flexGrow: 1 }}>
              {customFooter}
            </div>
          )}
          <Button
            type="text"
            icon={<ShareAltOutlined />}
            onClick={handleShareClick}
          >
            Share
          </Button>
        </Row>
      </Card>

      <Modal
        visible={shareVisible}
        title="Share this Post"
        onCancel={handleShareClose}
        footer={null}
      >
        <Space direction="vertical" size="middle" style={{ width: "100%" }}>
          <Button icon={<FacebookOutlined />} block>
            <a
              href={`https://www.facebook.com/sharer/sharer.php?u=${encodeURIComponent(
                `${window.location.origin}/item/${id}`
              )}`}
              target="_blank"
              rel="noopener noreferrer"
            >
              Share on Facebook
            </a>
          </Button>
          <Button icon={<TwitterOutlined />} block>
            <a
              href={`https://twitter.com/intent/tweet?url=${encodeURIComponent(
                `${window.location.origin}/item/${id}`
              )}`}
              target="_blank"
              rel="noopener noreferrer"
            >
              Share on Twitter
            </a>
          </Button>
          <Button icon={<WhatsAppOutlined />} block>
            <a
              href={`https://wa.me/?text=${encodeURIComponent(
                `${window.location.origin}/item/${id}`
              )}`}
              target="_blank"
              rel="noopener noreferrer"
            >
              Share on WhatsApp
            </a>
          </Button>
          <Button icon={<CopyOutlined />} block onClick={handleCopyLink}>
            Copy Link
          </Button>
        </Space>
      </Modal>

      <Modal
        open={editModalOpen}
        title="Edit Post"
        footer={null}
        onCancel={handleEditClose}
        destroyOnClose
      >
        <EditPostPage id={id} onClose={handleEditClose} />
      </Modal>
    </>
  );
};

export default DonationPost;