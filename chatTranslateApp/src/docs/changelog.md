# CHANGELOG

## v1.0.0 (2025-02-05)

- JUnit 5 + Spring Boot Test 기반 단위 테스트 9개(TC-01 ~ TC-09) 작성

---
## v1.0.1 (2025-02-06)

### 잘못된 입력값 처리 로직 개선 (textToParse(String text))

- 추출된 텍스트에 `[`가 존재하지 않을때 예외처리 추가로 StringIndexOutOfBoundsException 방지
- 사용자명이 없을 경우 메시지 객체가 생성되지 않도록 수정

### 테스트 케이스 수정 및 업데이트

- TC-04 (잘못된 형식의 메시지): 예외 발생 (StringIndexOutOfBoundsException) → 빈 리스트 반환하도록 수정 ✅
- TC-07 (여는 대괄호 [ 누락): 예외 발생 (StringIndexOutOfBoundsException) → 빈 리스트 반환하도록 수정 ✅
- TC-09 (사용자명 없음): 메시지 객체가 생성됨 → 빈 리스트 반환하도록 수정 ✅ 

### ID 부여 로직 수정

- id = 1부터 시작하도록 수정하여 실제 데이터와 일관성을 유지

---
