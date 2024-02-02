package com.phofor.phocaforme.chat.controller;

import com.phofor.phocaforme.chat.dto.response.ChatRoomResponseDto;
import com.phofor.phocaforme.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    // 나랑 상대방이랑 게시물에 대한 채팅방이 있는지 검색이 하나
    // 없으면 나랑 상대방이랑 게시물에 대한 채팅방을 만들고 반환해 주는 것이 하나
    //
    //@PostMapping으로 채팅방 만들어주기
    // 나의 모든 채팅방 리스트 반환해주기

    // 나의 모든 채팅방 리스트 반환
    @GetMapping("/chatRoom/{userId}")
    public ResponseEntity<List<ChatRoomResponseDto>> getChatRoomAll(@PathVariable String userId){
        return ResponseEntity.ok().body(chatRoomService.getAllByOwnerIdOrVisiterId(userId));
    }

//    @PostMapping
//    public ResponseEntity<?> registChatRoom(@PathVariable Integer boardId){
//        Integer chatRoomId = chatRoomService.registChatRoom(boardId);
//        return new ResponseEntity<Integer>(chatRoomId,HttpStatus.OK);
//    }
    @PostMapping("/chatRoom/{boardId}/{userId}")
    public ResponseEntity<ChatRoomResponseDto> getChatRoom(@PathVariable Integer boardId, @PathVariable String userId) {
        return ResponseEntity.ok().body(chatRoomService.getChatRoomByBoardIdAndVisiterId(boardId, userId));
    }
}
