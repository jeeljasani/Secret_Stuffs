import React, { useState, useEffect, useCallback } from "react";
import { MessageOutlined, HeartOutlined } from "@ant-design/icons";
import { Typography, Button, Input, Row, Col, Space, Divider, Tag, Spin, Alert, message as antdMessage } from "antd";
import { useParams, useNavigate } from "react-router-dom";
import { getItemById } from "@/services/item/post-service";
import { useQuery } from "@tanstack/react-query";
import { useAuth } from "@/context/auth-context";
import chatService from "@/services/chat/chat-service";
import { useTheme } from "@/context/theme-context";
import { Donation, DonationService, User } from "@/services/item/donation";

const { Title, Text } = Typography;
const { TextArea } = Input;

const ItemDetails: React.FC = () => {
  const { id: itemId } = useParams<{ id: string }>();
  const { user } = useAuth();
  const { darkMode } = useTheme();
  const navigate = useNavigate();
  const [message, setMessage] = useState("");
  const [sending, setSending] = useState(false);
  const [chatExists, setChatExists] = useState(false);
  const [interested, setInterested] = useState(false);
  const [interestLoading, setInterestLoading] = useState(false);
  const [alreadyInterested, setAlreadyInterested] = useState(false);
  const [itemStatus, setItemStatus] = useState("AVAILABLE"); // Default status

  const { data: item, isLoading, error } = useQuery({
    queryKey: ["item", itemId],
    queryFn: () => getItemById(Number(itemId)),
    enabled: !!itemId,
  });

  function determineUserItemStatus(interestedUsers: Donation[], currentUserId: number): string {
    // Check if any user has the status "ACCEPTED"
    const isItemAccepted = interestedUsers.some((user) => user.status === "ACCEPTED");
  
    if (isItemAccepted) {
      // If item is accepted, mark all other users as "REJECTED"
      interestedUsers = interestedUsers.map((user) => ({
        ...user,
        status: user.status === "ACCEPTED" ? "ACCEPTED" : "REJECTED",
      }));
    }
  
    // Find the status for the current user
    const currentUserStatus = interestedUsers.find((user) => user.user.id === currentUserId)?.status;
  
    // Return the current user's status or default to "AVAILABLE" if no match
    return currentUserStatus || (isItemAccepted ? "REJECTED" : "AVAILABLE");
  }
  
  const fetchInterestedUsers = useCallback(async () => {
    try {
      if (itemId && user?.id) {
        const response = await DonationService.getInterestUsers(Number(itemId));
        const interestedUsers = response.data;
  
        // Check if the user is already interested
        const isAlreadyInterested = interestedUsers.some(
          (donation) => donation.user.id === user.id
        );
        setAlreadyInterested(isAlreadyInterested);
        setInterested(isAlreadyInterested); // Sync interested state
  
        // Determine the item status for the current user
        const status = determineUserItemStatus(interestedUsers, user.id);
        setItemStatus(status); // Store the computed status
      }
    } catch (error) {
      console.error("Error fetching interested users:", error);
    }
  }, [itemId, user?.id]);

  useEffect(() => {
    chatService.connect(() => {
      console.log("Connected to WebSocket server");
      chatService.subscribe("/user/queue/messages", (msg) => {
        console.log("Received message:", msg);
      });
    });

    return () => {
      chatService.disconnect();
    };
  }, []);

  useEffect(() => {
    const checkChatExistence = async () => {
      if (user?.email && item?.email && user.email !== item.email) {
        const exists = await chatService.checkChatExists(user.email, item.email);
        setChatExists(exists);
      }
    };
    checkChatExistence();
    fetchInterestedUsers();
  }, [user?.email, item?.email, fetchInterestedUsers]);

  const handleSendMessage = async () => {
    if (message.trim() && user?.email && item?.email) {
      const chatMessage = {
        id: Date.now(),
        senderId: user.email,
        recipientId: item.email,
        content: message,
        timestamp: new Date().toISOString(),
      };

      setSending(true);
      try {
        await chatService.sendMessage("/app/chat", chatMessage);
        setMessage("");
        antdMessage.success("Message sent successfully!");
      } catch (error) {
        console.error("Error sending message:", error);
        antdMessage.error("Failed to send message.");
      } finally {
        setSending(false);
        navigate(`/chats`);
      }
    }
  };

  const handleGoToChats = () => {
    navigate("/chats");
  };

  const handleViewAllPosts = () => {
    navigate("/my-posts");
  };

  const handleInterestedClick = async () => {
    if (!user?.id || !itemId) return;

    setInterestLoading(true);
    try {
      await DonationService.createDonation(itemId, user.id.toString());
      antdMessage.success("Added to Interested User List!");
      setInterested(true);
      setAlreadyInterested(true);
    } catch (error) {
      console.error("Error adding to interested list:", error);
      antdMessage.error("Failed to add to Interested User List.");
    } finally {
      setInterestLoading(false);
    }
  };

  if (isLoading) return <Spin />;
  if (error) return <Alert message="Error loading item details" type="error" showIcon />;

  const isOwner = user?.email === item?.email;

  return (
    <div
      style={{
        padding: "24px",
        backgroundColor: darkMode ? "#000" : "#fff",
        color: darkMode ? "#fff" : "#000",
        minHeight: "100vh",
      }}
    >
      {/* Main Content */}
      <Row gutter={[24, 24]} style={{ margin: 0, paddingTop: "24px" }}>
        <Col xs={24} md={8}>
          <div
            style={{
              aspectRatio: "1",
              width: "100%",
              borderRadius: "8px",
              overflow: "hidden",
              boxShadow: darkMode
                ? "0px 4px 12px rgba(255, 255, 255, 0.1)"
                : "0px 4px 12px rgba(0, 0, 0, 0.1)",
            }}
          >
            <img
              style={{ width: "100%", height: "100%", objectFit: "cover" }}
              src={item?.itemPostImageUrl || "https://via.placeholder.com/300"}
              alt={item?.title || "Item Image"}
            />
          </div>
        </Col>
        <Col xs={24} md={16}>
          <div style={{ padding: "8px" }}>
            <Title
              level={2}
              style={{ color: darkMode ? "#ffffff" : "#000000", marginBottom: "12px" }}
            >
              {item?.title || "Untitled Item"}
            </Title>
            <Space direction="vertical" size="middle" style={{ display: "flex", width: "100%" }}>
              <div>
                <Tag color="blue" style={{ marginBottom: "8px" }}>
                  {item?.category || "Uncategorized"}
                </Tag>
                <Tag color="green">{item?.condition || "Condition Unknown"}</Tag>
              </div>
              <Text
                style={{
                  color: darkMode ? "#aaaaaa" : "#333333",
                  fontSize: "16px",
                  lineHeight: "1.6",
                }}
              >
                {item?.description || "No description provided for this item."}
              </Text>
              {/* Status Tag */}
              <Tag
                color={
                  itemStatus === "AVAILABLE"
                    ? "green"
                    : itemStatus === "PENDING"
                    ? "orange"
                    : "red"
                }
                style={{
                  fontSize: "14px",
                  fontWeight: "bold",
                  marginTop: "8px",
                }}
              >
                {itemStatus === "AVAILABLE"
                  ? "Currently Available"
                  : itemStatus === "PENDING"
                  ? "Interest Received from Users"
                  : "Already Donated"}
              </Tag>
            </Space>
          </div>
        </Col>
      </Row>

      <Divider style={{ borderColor: darkMode ? "#555" : "#d9d9d9" }} />

      {/* Owner Actions */}
      {isOwner ? (
        <div
          style={{
            padding: "24px",
            backgroundColor: darkMode ? "#1c1c1c" : "#f9f9f9",
            borderRadius: "12px",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            maxWidth: "100%",
            margin: "24px auto",
          }}
        >
          <Text
            style={{
              color: darkMode ? "#bbbbbb" : "#333333",
              fontSize: "16px",
              lineHeight: "1.6",
            }}
          >
            You are viewing your own item listing. Interested users can message you about this item. To manage or edit your listings, click the button below.
          </Text>
          <Button
            type="primary"
            size="large"
            onClick={handleViewAllPosts}
            style={{ padding: "0 24px", marginLeft: "16px" }}
          >
            Manage Your Listings
          </Button>
        </div>
      ) : (
        <div
        style={{
          padding: "24px",
          backgroundColor: darkMode ? "#1c1c1c" : "#f9f9f9",
          borderRadius: "12px",
          display: "flex",
          flexDirection: "column",
          gap: "16px",
        }}
      >
        {/* Chat Section */}
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <Title
            level={4}
            style={{
              color: darkMode ? "#fff" : "#000",
              margin: 0,
            }}
          >
            {/* Conditional Text Based on Status */}
            {itemStatus === "ACCEPTED" && alreadyInterested
              ? "Congratulations! You got the item. Continue chatting with the owner."
              : itemStatus === "ACCEPTED"
              ? "Sorry, this item has been assigned to another user."
              : itemStatus === "AVAILABLE"
              ? interested
                ? "Your interest has been noted. You can chat with the owner."
                : "This item is currently available. Express your interest!"
              : itemStatus === "PENDING"
              ? "You can continue chatting with the owner."
              : "This item is no longer available for chat."}
          </Title>

          {/* Chat Button */}
          {chatExists ? (
            <Button
              type="primary"
              onClick={handleGoToChats}
              style={{
                padding: "0 24px",
              }}
            >
              Click Here
            </Button>
          ) : (
            <></> // If no existing chat, text input and send button will appear below
          )}
        </div>

        {/* First-Time Chat */}
        {!chatExists && (itemStatus === "AVAILABLE" || itemStatus === "PENDING") && (
          <>
            <TextArea
              rows={3}
              placeholder="Ask a question about this item..."
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              style={{
                backgroundColor: darkMode ? "#333" : "#fff",
                color: darkMode ? "#eee" : "#000",
                borderColor: darkMode ? "#555" : "#d9d9d9",
              }}
            />
            <Button
              type="primary"
              icon={<MessageOutlined />}
              onClick={handleSendMessage}
              loading={sending}
              disabled={sending || !message.trim()}
              style={{
                alignSelf: "flex-end",
                padding: "0 24px",
              }}
            >
              Send Message
            </Button>
          </>
        )}

        {/* Interested Button */}
        {!isOwner &&
          !alreadyInterested &&
          (itemStatus === "AVAILABLE" || itemStatus === "PENDING") && (
            <Button
              type="default"
              icon={<HeartOutlined />}
              onClick={handleInterestedClick}
              loading={interestLoading}
              disabled={interested || interestLoading}
              style={{
                alignSelf: "flex-start",
                marginTop: "16px",
                padding: "0 24px",
              }}
            >
              I'm Interested
            </Button>
          )}

        {/* Status for Already Interested Users */}
        {alreadyInterested && itemStatus !== "ACCEPTED" && (
          <Text
            type="warning"
            style={{
              alignSelf: "flex-start",
              marginTop: "16px",
              color: darkMode ? "#ffdd57" : "#faad14",
            }}
          >
            {itemStatus === "AVAILABLE"
              ? "Wait for the owner's confirmation."
              : itemStatus === "PENDING"
              ? "You can continue chatting with the owner."
              : "Sorry, Look For Other Items In The TimeLine."}
          </Text>
        )}
      </div>
      )}
    </div>
  );
};

export default ItemDetails;