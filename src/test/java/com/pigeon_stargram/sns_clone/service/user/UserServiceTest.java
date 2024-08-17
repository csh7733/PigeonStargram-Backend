package com.pigeon_stargram.sns_clone.service.user;

import com.pigeon_stargram.sns_clone.domain.user.User;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseOnlineStatusDto;
import com.pigeon_stargram.sns_clone.dto.chat.response.ResponseUserChatDto;
import com.pigeon_stargram.sns_clone.dto.login.request.RequestRegisterDto;
import com.pigeon_stargram.sns_clone.exception.user.MultipleUsersFoundException;
import com.pigeon_stargram.sns_clone.exception.login.RegisterFailException;
import com.pigeon_stargram.sns_clone.exception.user.UserNotFoundException;
import com.pigeon_stargram.sns_clone.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BasicUserService basicUserService;

    private User user;

    @Mock
    private RequestRegisterDto requestRegisterDto;

    @BeforeEach
    public void setUp() {
        userService = basicUserService;

        user = User.builder()
                .id(1L)
                .name("John Doe")
                .company("TechCorp")
                .workEmail("john.doe@techcorp.com")
                .personalEmail("john.doe@gmail.com")
                .workPhone("+123456789")
                .personalPhone("+987654321")
                .location("New York, USA")
                .avatar("http://example.com/avatar.jpg")
                .birthdayText("1985-12-15")
                .onlineStatus("Online")
                .password("1q2w3e4r")
                .build();
    }

    @Test
    @DisplayName("ID로 User 찾기 - 성공")
    public void testFindByIdSuccess(){
        //given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        //when
        User findUser = userService.findById(1L);

        //then
        assertThat(user).isEqualTo(findUser);
    }

    @Test
    @DisplayName("ID로 User 찾기 - 유저 없음")
    public void testFindByIdUserNotFound(){
        //given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        //when

        //then
        assertThatThrownBy(() -> {
            userService.findById(1L);
        }).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("WorkEmail과 Password로 User 찾기 - 성공")
    public void testFindByWorkEmailAndPasswordSuccess(){
        //given
        when(userRepository.findByWorkEmailAndPassword(anyString(), anyString()))
                .thenReturn(List.of(user));

        //when
        User findUser = userService.findByWorkEmailAndPassword("test@gmail.com", "test_password");

        //then
        assertThat(user).isEqualTo(findUser);
    }

    @Test
    @DisplayName("WorkEmail과 Password로 User 찾기 - 유저 없음")
    public void testFindByWorkEmailAndPasswordUserNotFound(){
        //given
        when(userRepository.findByWorkEmailAndPassword(anyString(), anyString()))
                .thenReturn(List.of());

        //when

        //then
        assertThatThrownBy(() -> {
            userService.findByWorkEmailAndPassword("test@gmail.com", "test_password");
        }).isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("WorkEmail과 Password로 User 찾기 - 여러 유저")
    public void testFindByWorkEmailAndPasswordMultipleUsersFound(){
        //given
        when(userRepository.findByWorkEmailAndPassword(anyString(), anyString()))
                .thenReturn(List.of(user, user));

        //when

        //then
        assertThatThrownBy(() -> {
            userService.findByWorkEmailAndPassword("test@gmail.com", "test_password");
        }).isInstanceOf(MultipleUsersFoundException.class);
    }

    @Test
    @DisplayName("RequestRegisterDto로 User 저장 - 성공")
    public void testSaveSuccess() {
        //given
        when(requestRegisterDto.toEntity()).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);

        //when
        User saveUser = userService.save(requestRegisterDto);

        //then
        assertThat(user).isEqualTo(saveUser);
    }

    @Test
    @DisplayName("RequestRegisterDto로 User 저장 - 중복된 이메일")
    public void testSave() {
        //given
        when(requestRegisterDto.toEntity()).thenReturn(user);
        when(userRepository.save(any(User.class)))
                .thenThrow(DataIntegrityViolationException.class);

        //when

        //then
        assertThatThrownBy(() -> {
            userService.save(requestRegisterDto);
        }).isInstanceOf(RegisterFailException.class);
    }

    @Test
    @DisplayName("User ID로 User 찾아서 ResponseUserChatDto로 변환")
    public void testFindUserChatById() {
        //given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        //when
        ResponseUserChatDto findResponseUserChatDto = userService.findUserChatById(1L);

        //then
        assertThat(findResponseUserChatDto.getId()).isEqualTo(user.getId());
        assertThat(findResponseUserChatDto.getName()).isEqualTo(user.getName());
        assertThat(findResponseUserChatDto.getCompany()).isEqualTo(user.getCompany());
        assertThat(findResponseUserChatDto.getWorkEmail()).isEqualTo(user.getWorkEmail());
        assertThat(findResponseUserChatDto.getPersonalEmail()).isEqualTo(user.getPersonalEmail());
        assertThat(findResponseUserChatDto.getWorkPhone()).isEqualTo(user.getWorkPhone());
        assertThat(findResponseUserChatDto.getPersonalPhone()).isEqualTo(user.getPersonalPhone());
        assertThat(findResponseUserChatDto.getLocation()).isEqualTo(user.getLocation());
        assertThat(findResponseUserChatDto.getAvatar()).isEqualTo(user.getAvatar());
        assertThat(findResponseUserChatDto.getStatus()).isEqualTo("채팅 기록 없음");
        assertThat(findResponseUserChatDto.getLastMessage()).isEqualTo("2h ago");
        assertThat(findResponseUserChatDto.getBirthdayText()).isEqualTo(user.getBirthdayText());
        assertThat(findResponseUserChatDto.getUnReadChatCount()).isEqualTo(0);
        assertThat(findResponseUserChatDto.getOnlineStatus()).isEqualTo(user.getOnlineStatus());
    }

    @Test
    @DisplayName("유저의 온라인상태 변경")
    public void testUpdateOnlineStatus() {
        //given

        //when
        userService.updateOnlineStatus(user.getId(), "test-online");

        //then
        assertThat(user.getOnlineStatus()).isEqualTo("test-online");
    }

    @Test
    @DisplayName("유저의 비밀번호 변경")
    public void testUpdatePassword() {
        //given

        //when
        userService.updatePassword(user.getId(), "test-password");

        //then
        assertThat(user.getPassword()).isEqualTo("test-password");
    }

    @Test
    @DisplayName("유저의 온라인상태 확인")
    public void testGetOnlineStatus() {
        //given
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        //when
        ResponseOnlineStatusDto responseOnlineStatusDto = userService.getOnlineStatus(1L);

        //then
        assertThat(user.getOnlineStatus()).isEqualTo(responseOnlineStatusDto.getOnlineStatus());
    }
}
