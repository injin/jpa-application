# jpa-application
[인프런] 실전! 스프링 부트와 JPA 활용1 - 웹 애플리케이션 개발

## 1. 도메인 분석설계

### 1.1 점검포인트

- 연관관계의 주인을 어디에 둘 것인가? Foreign Key가 위치하는 곳에 두자!
- @ManyToOne, @OneToOne 을 지연로딩 설정하였는 지
  `@ManyToOne(fetch = FetchType.*LAZY*)`
- 한 번에 저장하기 편하게 Cascade ALL 설정
  예) `@OneToMany(mappedBy = ”parent”, cascade = CascadeType.ALL)`
- 양방향일 경우 연관관계 편의 메서드 작성
- 엔티티 클래스에 비즈니스 로직 필요하다면 추가

## 2. 도메인 개발

### 2.1 점검 포인트

- 서비스 클래스에 `@Transactional(readOnly = true)` 기본으로 주소 insert하는 메서드에 `@Transactional` 추가해서 성능 최적화
- **repository bean 초기화 방법**
  (방법1) 서비스 클래스에 생성자로 repository bean 주입해서 컴파일 시점에 체크할 수 있게 할 것. `@Autowired` 안 붙여도 됨
  (방법2) 클래스에 롬복으로 `@RequiredArgsConstructor` 추가한 뒤 필드에 final 추가 ⇒ `private final MemberRepository memberRepository;`
- **테스트 케이스에서 insert 쿼리 확인하려면**
  (방법1) `@Rollback(value = false)` 추가
  (방법2) EntityManager 주입 받아서 em.flush() 강제 호출
- 연관관계 편의 메서드 작성
- 엔티티 내에 생성 메서드, 비즈니스 메서드, 조회 로직 작성
  생성 메서드가 아닌 new로 객체 생성하는 것을 방지하기 위해 기본 생성자 만든 후 protected로 제한 또는 롬복으로 처리 `@NoArgsConstructor(access = AccessLevel.*PROTECTED*)`
- setter없이 엔티티를 변경할 수 있는 메서드 작성. 추적 용이

## 3. 웹 계층 개발

### 3.1 점검 포인트

- 폼 객체 개발. javax validation 활용하여 유효성 체크 (+Controller Advice 심화 )
  `@NotEmpty(message = "회원 이름은 필수 입니다.")`

    ```java
    @PostMapping(value = "/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }
    }
    
    @Getter @Setter
    public class MemberForm {
        @NotEmpty(message = "회원 이름은 필수 입니다.")
        private String name;
    }
    ```

- API를 만들 때에는 엔티티를 절대 외부로 반환하지 말것. DTO 객체를 사용. (템플릿 엔진에서는 선택적으로)
- **업데이트 기능 개발 시 준영속 엔티티를 수정하는 2가지 방법**
- 변경 감지 기능 사용
- 병합(merge) 사용 ⇒ 사용하지 말 것
* 준영속 엔티티는 영속성 컨텍스트가 더이상 관리하지 않는 엔티티
* 병합은 준영속 상태의 엔티티를 영속 상태로 변경할 때 사용하는 기능

    <aside>
    📌 **변경 감지 기능을 사용하면 원하는 속성만 선택해서 변경할 수 있지만, 병합을 사용하면 모든 속성이 변경된다. 병합시 값이 없으면 null 로 업데이트 할 위험도 있다. (병합은 모든 필드를 교체한다.) ⇒ 변경 감지를 사용하자**

    </aside>

- 테스트용 데이터 사전 입력 샘플 코드

  [`InitDb`](./doc/InitDB.txt)

- application.yml 프로파일 분리
    - src/main/resources/application.yml
      `spring.profiles.active` 설정 active ⇒ 로컬에서 톰캣 돌릴 때 적용됨
    - src/test/resources/application.yml
      `spring.profiles.active` 설정 test ⇒ 테스트케이스 돌릴 때 적용됨