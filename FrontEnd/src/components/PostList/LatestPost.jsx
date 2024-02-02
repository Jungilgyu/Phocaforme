import * as React from "react";
import { useNavigate } from "react-router-dom";
import PropTypes from "prop-types";

import {
  Tabs,
  Tab,
  Typography,
  Box,
  Container,
  ImageList,
  ImageListItem,
} from "@mui/material";

import Card from "../../components/UI/Card";

const CustomTabPanel = (props) => {
  const { children, value, index, ...other } = props;

  return (
    <>
      <div
        role="tabpanel"
        hidden={value !== index}
        id={`tabpanel-${index}`}
        {...other}
      >
        {value === index && (
          <Box sx={{ p: 1 }}>
            <Typography>{children}</Typography>
          </Box>
        )}
      </div>
    </>
  );
};

CustomTabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

const a11yProps = (index) => {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
};

const LatestPost = () => {
  const navigate = useNavigate();

  const [value, setValue] = React.useState(0);
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  // 게시물로 이동 핸들러
  const handleClick = (id) => {
    navigate(`/post/${id}`);
  };

  // 게시글 정보 (임의)
  // 브라우저에 최근 본 게시글 담는다
  // 5개 넘어가면 맨 앞 객체 빼고 담는다
  const LatestPostList = [
    {
      id: 1,
      writerId: "1234",
      writerNickname: "제노예요",
      title: "[교환1] 질주 미공포 교환해요",
      images: ["/assets/images/photocard/도영.jpg"],
      group: "NCT",
      ownMembers: [{ value: "도영", label: "도영" }],
      targetMembers: [{ value: "제노", label: "제노" }],
      content: `받자마자 탑로더에 보관해서 상태 좋습니다.\n그리고 도영이가 정말 귀여워요\n귀여운 도영이\n데려가세요`,
      cardType: "미공포",
      type: "교환",
      isBartered: true,
    },
    {
      id: 2,
      writerId: "1234",
      writerNickname: "제노예요",
      title: "[교환2] 질주 미공포 교환해요",
      images: [
        "/assets/images/photocard/도영.jpg",
        "/assets/images/photocard/도영.jpg",
      ],
      group: "NCT",
      ownMembers: [{ value: "도영", label: "도영" }],
      targetMembers: [{ value: "제노", label: "제노" }],
      content: `받자마자 탑로더에 보관해서 상태 좋습니다.\n그리고 도영이가 정말 귀여워요\n귀여운 도영이\n데려가세요`,
      cardType: "미공포",
      type: "교환",
      isBartered: false,
    },
    {
      id: 3,
      writerId: "1234",
      writerNickname: "제노예요",
      title: "[교환3] 질주 미공포 교환해요",
      images: [
        "/assets/images/photocard/도영.jpg",
        "/assets/images/photocard/도영.jpg",
      ],
      group: "NCT",
      ownMembers: [{ value: "도영", label: "도영" }],
      targetMembers: [{ value: "제노", label: "제노" }],
      content: `받자마자 탑로더에 보관해서 상태 좋습니다.\n그리고 도영이가 정말 귀여워요\n귀여운 도영이\n데려가세요`,
      cardType: "미공포",
      type: "교환",
      isBartered: false,
    },
    {
      id: 4,
      writerId: "1234",
      writerNickname: "제노예요",
      title: "[교환4] 질주 미공포 교환해요",
      images: [
        "/assets/images/photocard/도영.jpg",
        "/assets/images/photocard/도영.jpg",
      ],
      group: "NCT",
      ownMembers: [{ value: "도영", label: "도영" }],
      targetMembers: [{ value: "제노", label: "제노" }],
      content: `받자마자 탑로더에 보관해서 상태 좋습니다.\n그리고 도영이가 정말 귀여워요\n귀여운 도영이\n데려가세요`,
      cardType: "미공포",
      type: "교환",
      isBartered: false,
    },
    {
      id: 5,
      writerId: "1234",
      writerNickname: "제노예요",
      title: "[교환5] 질주 미공포 교환해요",
      images: [
        "/assets/images/photocard/도영.jpg",
        "/assets/images/photocard/도영.jpg",
      ],
      group: "NCT",
      ownMembers: [{ value: "도영", label: "도영" }],
      targetMembers: [{ value: "제노", label: "제노" }],
      content: `받자마자 탑로더에 보관해서 상태 좋습니다.\n그리고 도영이가 정말 귀여워요\n귀여운 도영이\n데려가세요`,
      cardType: "미공포",
      type: "교환",
      isBartered: false,
    },
    {
      id: 6,
      writerId: "1234",
      writerNickname: "제노예요",
      title: "[판매1] 질주 미공포 교환해요",
      images: [
        "/assets/images/photocard/도영.jpg",
        "/assets/images/photocard/도영.jpg",
      ],
      group: "NCT",
      ownMembers: [{ value: "도영", label: "도영" }],
      content: `받자마자 탑로더에 보관해서 상태 좋습니다.\n그리고 도영이가 정말 귀여워요\n귀여운 도영이\n데려가세요`,
      cardType: "미공포",
      type: "판매",
      isSold: true,
    },
  ];

  return (
    <Container>
      <h2 className="profile-title">최근 본 게시글</h2>
      <div>
        <Box sx={{ width: "70vw", borderBottom: 1, borderColor: "divider" }}>
          <Tabs value={value} onChange={handleChange}>
            <Tab
              label="교환"
              {...a11yProps(0)}
              sx={{ fontWeight: value === 0 ? 600 : 400 }}
            />
            <Tab
              label="판매"
              {...a11yProps(1)}
              sx={{ fontWeight: value === 1 ? 600 : 400 }}
            />
          </Tabs>
        </Box>
        <CustomTabPanel value={value} index={0}>
          <ImageList
            id="card-list"
            sx={{ display: "flex", width: "100%" }}
            rowHeight={200}
          >
            {LatestPostList &&
              LatestPostList.filter((post) => post.type === "교환")
                .slice(-5)
                .reverse()
                .map((post, index) => (
                  <div
                    className="cards-container"
                    key={index}
                    onClick={() => handleClick(post.id)}
                  >
                    <Card
                      key={post.id}
                      style={{
                        objectFit: "contain",
                        margin: "0 16px 16px 0",
                        cursor: "pointer",
                      }}
                      id={post.id}
                      title={post.title}
                      images={post.images}
                      ownMembers={post.ownMembers}
                      targetMembers={post.targetMembers}
                      content={post.content}
                      type={post.type}
                      isBartered={post.isBartered}
                    ></Card>
                  </div>
                ))}
          </ImageList>
        </CustomTabPanel>
        <CustomTabPanel value={value} index={1}>
          <ImageList
            id="card-list"
            sx={{ display: "flex", width: "100%" }}
            rowHeight={200}
          >
            {LatestPostList &&
              LatestPostList.filter((post) => post.type === "판매")
                .slice(-5)
                .reverse()
                .map((post, index) => (
                  <div
                    className="cards-container"
                    key={index}
                    onClick={() => handleClick(post.id)}
                  >
                    <Card
                      key={post.id}
                      style={{
                        objectFit: "contain",
                        margin: "0 16px 16px 0",
                        cursor: "pointer",
                      }}
                      id={post.id}
                      title={post.title}
                      images={post.images}
                      ownMembers={post.ownMembers}
                      content={post.content}
                      type={post.type}
                      isSold={post.isSold}
                    ></Card>
                  </div>
                ))}
          </ImageList>
        </CustomTabPanel>
      </div>
    </Container>
  );
};

export default LatestPost;
