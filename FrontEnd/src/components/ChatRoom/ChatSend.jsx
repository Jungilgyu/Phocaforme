import React, { useState } from "react";
import { useDispatch } from "react-redux";

import { useTheme } from "@mui/material/styles";
import { sendChat } from "../../store2/chat.js";
import getCurrentTime from "../../utils/currentTime";

import { TextField, InputAdornment, Popover } from "@mui/material";
import { ArrowCircleUp, Add, Image } from "@mui/icons-material";

const ChatSend = ({ roomId, loginUser, updateMessages }) => {
  const theme = useTheme();
  const dispatch = useDispatch();

  // 메시지 전송
  const [value, setValue] = useState("");

  const handleChange = (event) => {
    setValue(event.target.value);
  };

  const handleSendClick = () => {
    if (value.trim() === "") return;

    const newMessage = {
      chatId: 1,
      chatRoomId: roomId,
      sender: loginUser,
      message: value,
      imgCode: "",
      sendTime: getCurrentTime(),
      isPay: false,
    };

    updateMessages(newMessage);
    setValue("");
  };

  // 엔터 키를 눌렀을 때도 send
  const handleSendEnter = (e) => {
    if (e.key === "Enter") {
      handleSendClick();
    }
  };

  // 사진첨부 팝업
  const [anchorEl, setAnchorEl] = React.useState(null);

  const handleClick = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const open = Boolean(anchorEl);

  return (
    <div
      sx={{
        width: 500,
        maxWidth: "100%",
      }}
    >
      <Popover
        open={open}
        anchorEl={anchorEl}
        onClose={handleClose}
        anchorOrigin={{
          vertical: "top",
          horizontal: "center",
        }}
        transformOrigin={{
          vertical: "bottom",
          horizontal: "left",
        }}
      >
        <div id="add-popover-container">
          <div id="image-icon-container">
            <div id="image-icon-background">
              <Image id="image-icon" />
            </div>
            <p>사진</p>
          </div>
        </div>
      </Popover>
      <TextField
        fullWidth
        id="fullWidth"
        placeholder="메시지를 입력하세요"
        value={value}
        onChange={handleChange}
        onKeyDown={handleSendEnter}
        InputProps={{
          startAdornment: (
            <InputAdornment>
              <Add onClick={handleClick} />
            </InputAdornment>
          ),
          endAdornment: (
            <InputAdornment>
              <ArrowCircleUp onClick={handleSendClick} fontSize="large" />
            </InputAdornment>
          ),
        }}
      ></TextField>
    </div>
  );
};

export default ChatSend;
