# OCR 문장 파싱 기능 테스트 (v1.0.0)

버전 : v1.0.0

작성자 :  TigerP-MS

작성일 : 2025-02-05

테스트 대상 : `textToParse(String text)`

관련 파일 : `TextService.java`

---

# 1. 테스트 개요

OCR을 통해 입력된 텍스트를 메시지 형식으로 변환하는 기능을 검증하는 테스트입니다.
해당 기능은 `[시간] 사용자명: 메시지` 형식의 문장을 파싱하여 리스트 형태로 변환해야 합니다.

---

# 2. 테스트 목표

- OCR에서 감지된 문장을 올바르게 파싱하여 `List<TextEntry.Message>` 객체로 변환하는지 검증
- 잘못된 입력이 들어왔을 때 예외 없이 안전하게 처리되는지 확인
- 입력값이 많거나 빈 문장이 포함된 경우에도 정상적으로 동작하는지 검증

---

# 3. 테스트 환경

### 테스트 도구

- `JUnit 5`
- `Spring Boot Test`

### 소프트웨어 환경

- `Java 23`
- `Spring Boot 3.4.1`
- `Gradle`

### 테스트 데이터

| 테스트 케이스 ID | 입력 텍스트 | 예상 출력 | 비고 |
| --- | --- | --- | --- |
| TC-01 | `[12:30] Alice: Hello` | 1개의 메세지 객체 반환 | 정상 입력 |
| TC-02 | `[12:30] Alice: Hello\n` | 1개의 메시지 객체 반환 | 정상 입력 |
| TC-03 | `[12:30] Alice: Hello\n[12:31] Bob: Hi` | 2개의 메시지 객체 반환 | 여러 개의 메시지 |
| TC-04 | `Hello this is not formatted text` | 빈 리스트 반환 | 잘못된 형식 |
| TC-05 | `[12:30] Alice:` | 메시지 내용이 빈 채로 객체 생성 | 빈 메시지 입력 |
| TC-06 | `[12:50 Alice: No closing bracket` | 빈 리스트 | 대괄호 `]` 누락 |
| TC-07 | `12:51] Alice: Extra bracket at start` | 빈 리스트 | 대괄호 `[` 누락 |
| TC-08 | `[12:52] Alice No colon` | 빈 리스트 | `:` 누락 |
| TC-09 | `[12:53]: Message without username` | 빈 리스트 | 사용자명 없음 |

---

# 4. 테스트 케이스

## [TC-01] 단일 메세지 파싱 확인

### 입력값

```
[12:30] Alice: Hello
```

### 예상 출력

| id | time | username | message | isTranslated |
| --- | --- | --- | --- | --- |
| 1 | 12:30 | Alice | Hello | 0 |

### 실행 결과

| id | time | username | message | isTranslated |
| --- | --- | --- | --- | --- |
| 1 | 12:30 | Alice | Hello | 0 |

**테스트 성공 ✅**

---

## [TC-02] 단일 메세지 (줄바꿈 포함)

### 입력값

```
[12:30] Alice: Hello\n
```

### 예상 출력

| id | time | username | message | isTranslated |
| --- | --- | --- | --- | --- |
| 1 | 12:30 | Alice | Hello | 0 |

### 실행 결과

| id | time | username | message | isTranslated |
| --- | --- | --- | --- | --- |
| 1 | 12:30 | Alice | Hello | 0 |

**테스트 성공 ✅**

---

## [TC-03] 여러 개의 메시지 파싱 확인

### 입력값

```
[12:30] Alice: Hello\n[12:31] Bob: Hi
```

### 예상 출력

| id | time | username | message | isTranslated |
| --- | --- | --- | --- | --- |
| 1 | 12:30 | Alice | Hello | 0 |
| 2 | 12:31 | Bob | Hi | 0 |

### 실행 결과

| id | time | username | message | isTranslated |
| --- | --- | --- | --- | --- |
| 1 | 12:30 | Alice | Hello | 0 |
| 2 | 12:31 | Bob | Hi | 0 |

**테스트 성공 ✅**

---

## [TC-04] 잘못된 형식의 메시지

### 입력값

```
Hello this is not formatted text
```

### 예상 출력

빈 리스트 반환 (`[]`)

### 실행 결과

발생한 에러

```css
java.lang.StringIndexOutOfBoundsException: Range [-1, 32) out of bounds for length 32
```

### 원인 분석

```java
lines = text.substring(text.indexOf("[")).split("\\[");
```

- `text.indexOf("[")` 의 값이 -1이 되어 `substring()` 실행 시 오류 발생
- `split` 시에 `indexOf()` 값이 `-1`일 경우, 해당 메시지를 건너뛰도록 예외 처리 추가

**테스트 실패 ❌**

---

## [TC-05] 빈 메시지

### 입력값

```
[12:30] Alice:
```

### 예상 출력

| id | time | username | message | isTranslated |
| --- | --- | --- | --- | --- |
| 1 | 12:30 | Alice | (빈 문자열) | 0 |

### 실행 결과

| id | time | username | message | isTranslated |
| --- | --- | --- | --- | --- |
| 1 | 12:30 | Alice | (빈 문자열) | 0 |

**테스트 성공 ✅**

---

## [TC-06] 닫는 대괄호 `]` 누락

### 입력값

```
[12:50 Alice: No closing bracket
```

### 예상 출력

빈 리스트 반환 (`[]`)

### 실행 결과

빈 리스트 반환 (`[]`)

**테스트 성공 ✅**

---

## [TC-07] 여는 대괄호 `[` 누락

### 입력값

```
12:51] Alice: Extra bracket at start
```

### 예상 출력

빈 리스트 반환 (`[]`)

### 실행 결과

발생한 에러

```css
java.lang.StringIndexOutOfBoundsException: Range [-1, 36) out of bounds for length 36
```

### 원인 분석

```java
lines = text.substring(text.indexOf("[")).split("\\[");
```

- `text.indexOf("[")` 의 값이 -1이 되어 `substring()` 실행 시 오류 발생
- `split` 시에 `indexOf()` 값이 `-1`일 경우, 해당 메시지를 건너뛰도록 예외 처리 추가

**테스트 실패 ❌**

---

## [TC-08] 콜론 `:` 누락"

### 입력값

```
[12:52] Alice No colon
```

### 예상 출력

빈 리스트 반환 (`[]`)

### 실행 결과

빈 리스트 반환 (`[]`)

**테스트 성공 ✅**

---

## [TC-08] 사용자명 없음"

### 입력값

```
[12:53]: Message without username
```

### 예상 출력

빈 리스트 반환 (`[]`)

### 실행 결과

| id | time | username | message | isTranslated |
| --- | --- | --- | --- | --- |
| 1 | 12:53 | (빈 문자열) | Message without username | 0 |

### 실패 메세지

```css
org.opentest4j.AssertionFailedError: 잘못된 형식의 입력은 빈 리스트여야 함 ==> 
필요   :0
실제   :1
```

### 원인 분석

- `username`이 빈 값이어도 메시지 객체가 생성됨

**테스트 실패 ❌**

---

## 5. 테스트 결과 요약

| 테스트 케이스 ID | 입력값 | 예상 출력 | 실행 결과 | 결과 |
| --- | --- | --- | --- | --- |
| **TC-01** | `[12:30] Alice: Hello` | 1개의 메시지 객체 반환 | 1개의 메시지 객체 반환 | ✅ 성공 |
| **TC-02** | `[12:30] Alice: Hello\n` | 1개의 메시지 객체 반환 | 1개의 메시지 객체 반환 | ✅ 성공 |
| **TC-03** | `[12:30] Alice: Hello\n[12:31] Bob: Hi` | 2개의 메시지 객체 반환 | 2개의 메시지 객체 반환 | ✅ 성공 |
| **TC-04** | `Hello this is not formatted text` | 빈 리스트 반환 | `StringIndexOutOfBoundsException` 발생 | ❌ 실패 |
| **TC-05** | `[12:30] Alice:` | 메시지 내용이 빈 채로 객체 생성 | 메시지 내용이 빈 채로 객체 생성 | ✅ 성공 |
| **TC-06** | `[12:50 Alice: No closing bracket` | 빈 리스트 반환 | 빈 리스트 반환 | ✅ 성공 |
| **TC-07** | `12:51] Alice: Extra bracket at start` | 빈 리스트 반환 | `StringIndexOutOfBoundsException` 발생 | ❌ 실패 |
| **TC-08** | `[12:52] Alice No colon` | 빈 리스트 반환 | 빈 리스트 반환 | ✅ 성공 |
| **TC-09** | `[12:53]: Message without username` | 빈 리스트 반환 | 1개의 메시지 객체 반환 (오류) | ❌ 실패 |