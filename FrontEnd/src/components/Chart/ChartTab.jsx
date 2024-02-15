import React, { useState, useEffect } from "react";

import PropTypes from "prop-types";

import axios from "axios";

import { Container, Tabs, Tab, Typography, Box } from "@mui/material";

import ChartBoy from "./ChartBoy";
import ChartGirl from "./ChartGirl";

const CustomTabPanel = (props) => {
  const { children, value, index, ...other } = props;

  const now = new Date();
  now.setDate(now.getDate() - 1);

  const formattedDate = `${now.getFullYear()}-${(now.getMonth() + 1)
    .toString()
    .padStart(2, "0")}-${now.getDate().toString().padStart(2, "0")}`;

  return (
    <div>
      <div
        role="tabpanel"
        hidden={value !== index}
        id={`tabpanel-${index}`}
        {...other}
      >
        <p id="chart-time">{formattedDate.toLocaleString()} 기준</p>
        {value === index && (
          <Box sx={{ p: 1 }}>
            <Typography>{children}</Typography>
          </Box>
        )}
      </div>
    </div>
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

const ChartTab = () => {
  const [value, setValue] = React.useState(0);

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  // 6시간에 한 번 랭킹 가져오기
  const [currentDate, setCurrentDate] = useState(new Date());

  // const isoDateFormat = (date) => {
  //   const options = {
  //     year: "numeric",
  //     month: "2-digit",
  //     day: "2-digit",
  //     hour: "2-digit",
  //     minute: "2-digit",
  //     second: "2-digit",
  //     timeZoneName: "short",
  //   };

  //   return new Intl.DateTimeFormat("en-US", options).format(date);
  // };

  useEffect(() => {
    const intervalId = setInterval(() => {
      const newDate = new Date();

      // 1분마다 API 호출 (테스트용)
      if (newDate.getMinutes() !== currentDate.getMinutes()) {
        setCurrentDate(newDate);
        // const formattedDate = newDate.toISOString();
        const formattedDate = newDate.toISOString().slice(0, -1);

        axios
          .get(
            process.env.REACT_APP_API_URL + `idol/rank?date=${formattedDate}`,
            {
              withCredentials: true,
            }
          )
          .then((response) => {
            console.log("rank loaded:", response.data);

            // 남돌 반영
            setRankBoy();

            // 여돌 반영
            setRankGirl();
          })
          .catch((error) => {
            console.error("Error get rank:", error);
          });
      }
    }, 1000); // 1분마다 시간 체크 및 API 호출
    return () => clearInterval(intervalId);
  }, [currentDate]);

  const [rankBoy, setRankBoy] = useState([]);
  const [rankGirl, setRankGirl] = useState([]);

  return (
    <Container sx={{ width: "100%" }}>
      <h2 className="main-title">오늘의 포포차트 🥇</h2>
      <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
        <Tabs value={value} onChange={handleChange}>
          <Tab
            label="남자아이돌"
            {...a11yProps(0)}
            sx={{ fontWeight: value === 0 ? 600 : 400 }}
          />
          <Tab
            label="여자아이돌"
            {...a11yProps(1)}
            sx={{ fontWeight: value === 1 ? 600 : 400 }}
          />
        </Tabs>
      </Box>
      <CustomTabPanel value={value} index={0}>
        <ChartBoy data={rankBoy} />
      </CustomTabPanel>
      <CustomTabPanel value={value} index={1}>
        <ChartGirl data={rankGirl} />
      </CustomTabPanel>
    </Container>
  );
};
export default ChartTab;
