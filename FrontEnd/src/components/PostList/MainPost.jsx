import React, { useState, useEffect } from 'react';
import PropTypes from "prop-types";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import { Container } from "@mui/material";
import Card from '../../components/UI/Card';
import { useSelector, useDispatch } from 'react-redux';

function CustomTabPanel(props) {
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
}

CustomTabPanel.propTypes = {
  children: PropTypes.node,
  index: PropTypes.number.isRequired,
  value: PropTypes.number.isRequired,
};

function a11yProps(index) {
  return {
    id: `simple-tab-${index}`,
    "aria-controls": `simple-tabpanel-${index}`,
  };
}

const PAGE_SIZE = 4;// 페이지당 표시할 카드 수

export default function BasicTabs() {
  const [value, setValue] = useState(0);
  const [visibleCards, setVisibleCards] = useState(PAGE_SIZE);
  const posts = useSelector((state) => state.post ? state.post.posts : []);
  const dispatch = useDispatch();

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  const handleScroll = () => {
    const { scrollTop, scrollHeight, clientHeight } = document.documentElement;

    if (scrollTop + clientHeight >= scrollHeight - 20) {
      setVisibleCards((prev) => prev + PAGE_SIZE);
    }
  };

  useEffect(() => {
    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, [handleScroll]);

  return (
    <Container sx={{ width: "100%" }}>
      
      <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
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
			  <div style={{ display: "flex", flexWrap: "wrap", flexDirection: 'row' }}>
          {/* <Card style={{ width: "calc(50% - 8px)", marginRight: "16px", marginBottom: "16px" }} /> */} 
					{/* 이 자리에 생성한 게시글 뜨게 만들기 */}
					{/* 무한스크롤 여따가 해야함 */}
          {posts.map((post, index) => (
            <Card 
              key={index} 
              style={{ width: "calc(50% - 8px)", marginRight: "16px", marginBottom: "16px" }}
              title={post.title}
              content={post.content}  
            >
              {/* 게시물 내용을 여기에 렌더링 */}
              
          
              
            </Card>
          ))}

          
        </div>
      </CustomTabPanel>
      <CustomTabPanel value={value} index={1}>
				
      </CustomTabPanel>
    </Container>
  );
}
