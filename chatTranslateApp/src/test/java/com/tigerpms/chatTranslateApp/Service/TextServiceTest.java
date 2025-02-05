package com.tigerpms.chatTranslateApp.Service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TextServiceTest {

    @Autowired
    private TextService textService;

    /** ✅ TC-01 : 단일 메시지 파싱 확인 */
    @Test
    @DisplayName("TC-01 : 단일 메시지 파싱 확인")
    public void test_TC_01() {
        // Given
        String inputText = "[12:30] Alice: Hello";

        // When
        List<TextEntry.Message> parsedMessages = textService.textToParse(inputText);

        // Then
        assertNotNull(parsedMessages, "결과 리스트가 null이 아니어야 함");
        assertEquals(1, parsedMessages.size(), "메시지 개수가 1개여야 함");
        assertEquals("12:30", parsedMessages.getFirst().getTime(), "시간 값이 올바른지 확인");
        assertEquals("Alice", parsedMessages.getFirst().getUsername(), "사용자명이 올바른지 확인");
        assertEquals("Hello", parsedMessages.getFirst().getMessage(), "메시지 내용이 올바른지 확인");
    }

    /** ✅ TC-02 : 단일 메시지 (줄바꿈 포함) */
    @Test
    @DisplayName("TC-02 : 단일 메시지 (줄바꿈 포함)")
    public void test_TC_02() {
        // Given
        String inputText = "[12:30] Alice: Hello\n";

        // When
        List<TextEntry.Message> parsedMessages = textService.textToParse(inputText);

        // Then
        assertNotNull(parsedMessages);
        assertEquals(1, parsedMessages.size());
        assertEquals("12:30", parsedMessages.getFirst().getTime());
        assertEquals("Alice", parsedMessages.getFirst().getUsername());
        assertEquals("Hello", parsedMessages.getFirst().getMessage());
    }

    /** ✅ TC-03 : 여러 개의 메시지 파싱 확인 */
    @Test
    @DisplayName("TC-03 : 여러 개의 메시지 파싱 확인")
    public void test_TC_03() {
        // Given
        String inputText = "[12:30] Alice: Hello\n[12:31] Bob: Hi";

        // When
        List<TextEntry.Message> parsedMessages = textService.textToParse(inputText);

        // Then
        assertNotNull(parsedMessages);
        assertEquals(2, parsedMessages.size());

        assertEquals("12:30", parsedMessages.getFirst().getTime());
        assertEquals("Alice", parsedMessages.getFirst().getUsername());
        assertEquals("Hello", parsedMessages.getFirst().getMessage());

        assertEquals("12:31", parsedMessages.get(1).getTime());
        assertEquals("Bob", parsedMessages.get(1).getUsername());
        assertEquals("Hi", parsedMessages.get(1).getMessage());
    }

    /** ✅ TC-04 : 잘못된 형식의 메시지 */
    @Test
    @DisplayName("TC-04 : 잘못된 형식의 메시지")
    public void test_TC_04() {
        // Given
        String inputText = "Hello this is not formatted text";

        // When
        List<TextEntry.Message> parsedMessages = textService.textToParse(inputText);

        // Then
        assertNotNull(parsedMessages);
        assertEquals(0, parsedMessages.size(), "잘못된 형식의 입력은 빈 리스트여야 함");
    }

    /** ✅ TC-05 : 빈 메시지 */
    @Test
    @DisplayName("TC-05 : 빈 메시지")
    public void test_TC_05() {
        // Given
        String inputText = "[12:30] Alice:";

        // When
        List<TextEntry.Message> parsedMessages = textService.textToParse(inputText);

        // Then
        assertNotNull(parsedMessages);
        assertEquals(1, parsedMessages.size());
        assertEquals("", parsedMessages.getFirst().getMessage(), "메시지 내용이 빈 문자열이어야 함");
    }

    /** ✅ TC-06 : 닫는 대괄호 `]` 누락 */
    @Test
    @DisplayName("TC-06 : 닫는 대괄호 `]` 누락")
    public void test_TC_06() {
        // Given
        String inputText = "[12:50 Alice: No closing bracket";

        // When
        List<TextEntry.Message> parsedMessages = textService.textToParse(inputText);

        // Then
        assertNotNull(parsedMessages);
        assertEquals(0, parsedMessages.size(), "잘못된 형식의 입력은 빈 리스트여야 함");
    }

    /** ✅ TC-07 : 여는 대괄호 `[` 누락 */
    @Test
    @DisplayName("TC-07 : 여는 대괄호 `[` 누락")
    public void test_TC_07() {
        // Given
        String inputText = "12:51] Alice: Extra bracket at start";

        // When
        List<TextEntry.Message> parsedMessages = textService.textToParse(inputText);

        // Then
        assertNotNull(parsedMessages);
        assertEquals(0, parsedMessages.size(), "잘못된 형식의 입력은 빈 리스트여야 함");
    }

    /** ✅ TC-08 : 콜론 `:` 누락 */
    @Test
    @DisplayName("TC-08 : 콜론 `:` 누락")
    public void test_TC_08() {
        // Given
        String inputText = "[12:52] Alice No colon";

        // When
        List<TextEntry.Message> parsedMessages = textService.textToParse(inputText);

        // Then
        assertNotNull(parsedMessages);
        assertEquals(0, parsedMessages.size(), "잘못된 형식의 입력은 빈 리스트여야 함");
    }

    /** ✅ TC-09 : 사용자명 없음 */
    @Test
    @DisplayName("TC-09 : 사용자명 없음")
    public void test_TC_09() {
        // Given
        String inputText = "[12:53]: Message without username";

        // When
        List<TextEntry.Message> parsedMessages = textService.textToParse(inputText);

        // Then
        assertNotNull(parsedMessages);
        assertEquals(0, parsedMessages.size(), "잘못된 형식의 입력은 빈 리스트여야 함");
    }
}
