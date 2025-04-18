import React, { useEffect, useState } from "react";
import { Col, Row, Typography, Space, Input, Select, Grid, Spin } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import { MainLayout } from "../main-layout";
import { useTheme } from "../../context/theme-context";
import DonationPost from "@/components/donation-post";
import { fetchPosts, Post, fetchCategories, fetchConditions } from "@/services/item/post-service";
import { useNavigate } from "react-router-dom";

const { Title, Text } = Typography;
const { useBreakpoint } = Grid;
const { Option } = Select;

export function HomeLayout() {
  const { darkMode } = useTheme();
  const screens = useBreakpoint();
  const navigate = useNavigate();

  const [posts, setPosts] = useState<Post[]>([]);
  const [searchText, setSearchText] = useState("");
  const [categories, setCategories] = useState<string[]>([]);
  const [conditions, setConditions] = useState<string[]>([]);
  const [filterCategory, setFilterCategory] = useState<string | null>(null);
  const [filterCondition, setFilterCondition] = useState<string | null>(null);
  const [filterRecent, setFilterRecent] = useState<string>("newest");
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const [fetchedPosts, fetchedCategories, fetchedConditions] = await Promise.all([
          fetchPosts(),
          fetchCategories(),
          fetchConditions(),
        ]);
        setPosts(fetchedPosts);
        setCategories(fetchedCategories);
        setConditions(fetchedConditions);
      } catch (error) {
        console.error("Error fetching data:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, []);

  const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchText(e.target.value);
  };

  const handleCategoryChange = (value: string | null) => setFilterCategory(value);
  const handleConditionChange = (value: string | null) => setFilterCondition(value);
  const handleRecentChange = (value: string) => setFilterRecent(value);

  const filteredPosts = posts
    .filter((post) => {
      const matchesSearch = post.title?.toLowerCase().includes(searchText.toLowerCase());
      const matchesCategory = filterCategory ? post.category === filterCategory : true;
      const matchesCondition = filterCondition ? post.condition === filterCondition : true;
      return matchesSearch && matchesCategory && matchesCondition;
    })
    .sort((a, b) => {
      const dateA = a.createdAt ? new Date(a.createdAt).getTime() : 0;
      const dateB = b.createdAt ? new Date(b.createdAt).getTime() : 0;
      return filterRecent === "newest" ? dateB - dateA : dateA - dateB;
    });

  const handlePostClick = (postId: number) => navigate(`/item/${postId}`);

  const containerStyle = {
    backgroundColor: darkMode ? "#1C1C1C" : "#F7F7F7",
    color: darkMode ? "#FFFFFF" : "#000000",
    padding: "24px",
    minHeight: "100vh",
  };

  const inputStyle = {
    backgroundColor: darkMode ? "#2C2C2C" : "#FFFFFF",
    color: darkMode ? "#FFFFFF" : "#000000",
    borderColor: darkMode ? "#444444" : "#D9D9D9",
  };

  const noPostsTextStyle = {
    color: darkMode ? "#FFFFFF" : "#000000",
    textAlign: "center" as const,
    marginTop: "24px",
  };

  return (
    <MainLayout>
      <div style={containerStyle}>
        <div
          style={{
            position: "sticky",
            top: 0,
            zIndex: 10,
            backgroundColor: darkMode ? "#1C1C1C" : "#F7F7F7",
            paddingBottom: "16px",
            marginBottom: "24px",
          }}
        >
          <Row gutter={[16, 16]} justify="space-between" align="middle">
            <Col xs={24} md={8}>
              <Title level={2} style={{ color: darkMode ? "#FFFFFF" : "#000000", margin: 0 }}>
                Timeline
              </Title>
            </Col>
            <Col xs={24} md={16}>
              <Space
                direction={screens.xs ? "vertical" : "horizontal"}
                style={{
                  width: "100%",
                  gap: 16,
                  display: "flex",
                  flexWrap: "wrap",
                }}
              >
                <Input
                  placeholder="Search posts..."
                  prefix={<SearchOutlined style={{ color: darkMode ? "#FFFFFF" : "#000000" }} />}
                  value={searchText}
                  onChange={handleSearchChange}
                  style={{ ...inputStyle, flex: 1, minWidth: "200px", maxWidth: "300px" }}
                />
                <Select
                  placeholder="Category"
                  allowClear
                  onChange={handleCategoryChange}
                  style={{ width: "150px", ...inputStyle }}
                >
                  {categories.map((category) => (
                    <Option key={category} value={category}>
                      {category}
                    </Option>
                  ))}
                </Select>
                <Select
                  placeholder="Condition"
                  allowClear
                  onChange={handleConditionChange}
                  style={{ width: "150px", ...inputStyle }}
                >
                  {conditions.map((condition) => (
                    <Option key={condition} value={condition}>
                      {condition}
                    </Option>
                  ))}
                </Select>
                <Select
                  value={filterRecent}
                  onChange={handleRecentChange}
                  style={{ width: "150px", ...inputStyle }}
                >
                  <Option value="newest">Newest</Option>
                  <Option value="oldest">Oldest</Option>
                </Select>
              </Space>
            </Col>
          </Row>
        </div>

        {loading ? (
          <div style={{ textAlign: "center", marginTop: "50px" }}>
            <Spin size="large" />
          </div>
        ) : (
          <div style={{ maxHeight: "calc(100vh - 160px)", overflowY: "auto" }}>
            <Row gutter={[16, 16]} style={{ marginLeft: 0, marginRight: 0 }}>
              {filteredPosts.length > 0 ? (
                filteredPosts.map((post) => (
                  <Col key={post.id} xs={24} sm={12} lg={8}>
                    <DonationPost
                      id = {post.id}
                      title={post.title || "Untitled"}
                      description={post.description || "No description available"}
                      category={post.category || "Miscellaneous"}
                      condition={post.condition || "Unknown"}
                      address={post.address || "Address not provided"}
                      email={post.email || "Email not available"}
                      itemImageUrl={post.itemPostImageUrl || "https://via.placeholder.com/150"}
                      userImageUrl={post.userImageUrl || "https://via.placeholder.com/40"}
                      userName={post.userName || "Anonymous"}
                      onClick={() => handlePostClick(post.id)}
                    />
                  </Col>
                ))
              ) : (
                <Col span={24}>
                  <Text style={noPostsTextStyle}>No posts found matching your criteria.</Text>
                </Col>
              )}
            </Row>
          </div>
        )}
      </div>
    </MainLayout>
  );
}

export default HomeLayout;