# Compose 함수 분해 기준 & 모듈 구조

이 문서는 Composable을 언제/어떻게 쪼개고, 어느 모듈에 배치할지에 대한 판단 기준을 정리한다.
새 화면을 만들거나 기존 화면을 리팩터링할 때 참고한다. 아래 코드는 패턴을 보여주기 위한 예시이며 실제 타입/함수 이름은 프로젝트마다 다르게 지으면 된다.

## 1. 아키텍처 (Architecture)
이 프로젝트는 Google의 공식 아키텍처 가이드를 따르는 최신 Android 애플리케이션입니다. 반응형(reactive) 구조 및 단일 액티비티(single-activity) 기반 앱으로, 다음 기술들을 사용합니다:

- **UI** : 전체적으로 Jetpack Compose로 구축되었으며, Material 3 컴포넌트와 다양한 화면 크기에 대응하는 적응형 레이아웃(adaptive layout)을 포함합니다.
- **상태 관리 (State Management)** : 단방향 데이터 흐름(UDF)이 Kotlin Coroutines 및 Flow를 활용하여 구현되어 있습니다. ViewModel은 상태 홀더(state holder) 역할을 하며 UI 상태를 데이터 스트림 형태로 노출합니다.
- **의존성 주입 (Dependency Injection)** : 앱 전반에 의존성 주입을 위해 Hilt를 사용하며, 의존성 관리를 단순화하고 테스트 가능성을 향상시킵니다.
- **내비게이션 (Navigation)** : Compose용 Jetpack Navigation 2를 사용해 화면 간 전환을 선언적이고 타입 안전(type-safe)한 방식으로 처리합니다.
- **데이터 (Data)** : 데이터 레이어는 repository pattern을 적용하여 구현되었습니다.
  - **로컬 데이터 (Local Data)** : 로컬 데이터 영속성을 위해 Room과 DataStore를 사용합니다.
  - **원격 데이터 (Remote Data)** : 네트워크 데이터 조회를 위해 Retrofit과 OkHttp를 사용합니다.
- **백그라운드 처리 (Background Processing)** : 지연 가능한 백그라운드 작업을 처리하기 위해 WorkManager를 사용합니다.

## 2. 모듈 구조 설계

### 2.1 모듈 종류
메인 Android 앱은 app/ 폴더에 위치합니다. 기능 모듈(feature modules)은 feature/에 위치하며, 코어 및 공유 모듈(core and shared modules)은 core/에 위치합니다.
```
app                          # 스캐폴딩: Application, MainActivity, NavHost, 최상위 네비게이션
feature:<name>               # Screen, ViewModel, UI, 실제 구현
core:designsystem            # Material3 래핑 컴포넌트 + 테마 (도메인 모델 모름)
core:ui                      # 도메인 모델을 아는 공유 Composable
core:model / data / network  # 데이터 계층
```

### 2.2 의존성 규칙 (빌드 스크립트/컨벤션 플러그인으로 강제하는 것을 권장)

- `feature:*`은 `core:ui`, `core:designsystem`에 의존한다.
- `core:*` 모듈은 `feature:*`나 `app`에 의존하지 않는다(역방향 금지). `core` 모듈끼리는 의존 가능하지만,
`core:designsystem` 모듈이 최하단이고 공유 UI 모듈(`core:ui`)이 그 위에 있다.
- `app`은 모든 `feature`와 필요한 `core` 모듈에 의존하는 최상단.

이 규칙은 곧 "3.5 승격 기준"과 정확히 대응한다 — 어떤 Composable을 어느 모듈로 옮길지 고민될 때는
"이 컴포넌트를 쓰는 쪽에서 어떤 의존성을 새로 추가해야 하는가"로 되짚어보면 답이 나온다.

### 2.3 새 화면/컴포넌트를 만들 때의 배치 체크리스트

1. 이 UI가 특정 피처 화면에서만 쓰이는가? → 해당 `feature:*` 내부에 둔다.
2. 두 개 이상의 피처가 같은 도메인 데이터를 그려야 하는가? → 공유 UI 모듈(`core:ui`)로.
3. 도메인 지식이 전혀 없는 범용 UI 조각(버튼, 칩, 로딩, 스크롤바)인가? → 디자인시스템 모듈(`core:designsystem`)로.

## 3. Composable을 쪼개는 기준

### 3.1 Stateful/Stateless 분리 (Route ↔ Screen)

모든 화면은 두 개의 오버로드로 나눈다.

- **public 오버로드** — `hiltViewModel()`(또는 DI 컨테이너)로 `ViewModel`을 받고,
  `collectAsStateWithLifecycle()`로 State를 구독한다. 외부(NavHost)에서 호출하는 진입점이며 로직이 없다.
- **internal/private 오버로드** (동일 이름) — State와 콜백만 파라미터로 받는 순수 stateless 함수.
   Preview와 UI 테스트는 이 오버로드를 직접 호출한다.

```kotlin
// 1) 진입점: ViewModel을 알고 있는 얇은 wrapper
@Composable
fun FeatureScreen(
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeatureViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FeatureScreen(
        uiState = uiState,
        onItemClick = onItemClick,
        onItemChecked = viewModel::updateItemChecked,
        modifier = modifier,
    )
}

// 2) 실제 렌더링: State만으로 동작, Preview/테스트에서 직접 호출
@Composable
internal fun FeatureScreen(
    uiState: FeatureUiState,
    onItemClick: (String) -> Unit,
    onItemChecked: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) { /* ... */ }
```

기준: **ViewModel을 직접 참조하는 함수와, State만으로 렌더링 가능한 함수는 항상 분리한다.**
Preview 작성이 불가능하거나 `hiltViewModel()` 목업이 필요해지면 분리가 안 된 것이다.

### 3.2 리스트 섹션은 `LazyListScope`/`LazyGridScope` 확장 함수로 뺀다

`LazyColumn`/`LazyVerticalGrid` 본문에 아이템 블록이 여러 개 섞이기 시작하면,
각 섹션을 스코프의 `private fun` 확장 함수로 추출한다. `@Composable`이 아니어도 되며,
`item {}`/`items {}` 호출만 감싼다.

```kotlin
LazyColumn {
    header(uiState.headerState)
    itemList(items = uiState.items, onItemClick = onItemClick)
}

private fun LazyListScope.header(headerState: HeaderUiState) {
    if (headerState !is HeaderUiState.Shown) return
    item(contentType = "header") { HeaderSection(headerState) }
}

// 두 개 이상의 화면이 같은 섹션을 그린다면 모듈 공유 컴포넌트로 승격한다 (3.5 참고)
fun LazyListScope.itemList(
    items: List<Item>,
    onItemClick: (String) -> Unit,
) {
    items(items = items, key = { it.id }) { item -> ItemRow(item, onItemClick) }
}
```

기준: **`LazyColumn` 빌더 블록 안의 로직이 한 화면 넘게 스크롤해야 파악되면 분리한다.**
여러 화면에서 같은 섹션을 그리면 공유 UI 모듈로 승격한다(3.5 참고).

### 3.3 반복되거나 독립적으로 의미를 갖는 UI 블록은 private Composable로 추출

기준은 "이름을 붙였을 때 읽는 사람이 더 쉽게 이해하는가"이다.

- 하나의 화면에서만 쓰이고, 다른 곳에서 재사용될 계획이 없다 → 같은 파일에 `private fun`으로 추출한다.

  ```kotlin
  @Composable
  private fun SelectableChipRow(
      items: List<SelectableItem>,
      onToggle: (id: String, selected: Boolean) -> Unit,
      modifier: Modifier = Modifier,
  ) { /* ... */ }
  ```

- 함수 하나가 State 읽기 + 레이아웃 + 이벤트 처리를 동시에 하면, 레이아웃 단위(하나의 개념적 UI 요소)로 나눈다.
파라미터 개수가 자연스럽게 늘어나는 것은 문제가 아니다 — 
Compose는 파라미터를 통한 명시적 데이터 흐름을 선호한다(숨은 상태보다 낫다).
- 매직 넘버가 있는 레이아웃 계산(`layout { }` 커스텀 모디파이어, 오프셋 계산 등)은 별도
  `private fun`으로 이름을 붙여 의도를 드러낸다.

이름이 안 붙는 게 나은 경우(추출하지 않아도 되는 경우): 한 번만 쓰이는 5줄 이하의
`Row`/`Column` 래핑, 단순 `Spacer` 삽입 등.

### 3.4 부수효과(Side effect)는 전용 Composable로 감싼다

`LaunchedEffect`가 여러 개 화면 본문에 흩어지면, 
각각을 이름이 있는 private `@Composable` 함수로 감싸서 화면 본문에서는 호출만 남긴다.

```kotlin
@Composable
private fun NotificationPermissionEffect() {
    // Preview에서는 Context가 Activity가 아니므로 안전하게 스킵한다
    if (LocalInspectionMode.current) return
    val permissionState = rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    LaunchedEffect(permissionState) {
        if (permissionState.status.shouldRequest) permissionState.launchPermissionRequest()
    }
}

@Composable
private fun DeepLinkEffect(deepLinkedId: String?, onDeepLinkOpened: (String) -> Unit) {
    LaunchedEffect(deepLinkedId) {
        if (deepLinkedId != null) onDeepLinkOpened(deepLinkedId)
    }
}

// 화면 본문에서는 호출만 남아 "무엇을 하는지"가 순서대로 읽힌다
NotificationPermissionEffect()
DeepLinkEffect(uiState.deepLinkedId, onDeepLinkOpened)
```

기준: **부수효과 블록에 이름을 붙였을 때 화면 본문이 "무엇을 하는지"를 순서대로 읽을 수 있어야 한다.** 
`LocalInspectionMode.current` 체크로 Preview에서 안전하게 no-op 되도록 만드는 것도 이 레벨에서 처리한다.

### 3.5 추출한 Composable을 어디로 승격할지 결정하는 기준

같은 파일 내 `private fun`으로 둘지, 모듈 공유 컴포넌트로 올릴지는 아래 순서로 판단한다.

1. **한 피처 내부에서만 쓰인다** → 그 피처 모듈 안에 둔다(파일 분리는 자유).
2. **여러 피처가 동일한 도메인 모델(예: `Article`, `Topic`)을 그리는 컴포넌트가 필요하다**
   → 도메인 모델을 알아도 되는 "피처 비종속" 공유 UI 모듈로 승격한다
   (예: `ItemCard`, `ItemFeed`처럼 특정 데이터 타입을 파라미터로 받는 컴포넌트).
3. **도메인 모델을 전혀 모르고, 순수 디자인 토큰/Material3 래핑만 필요하다**
   (버튼, 칩, 로딩 인디케이터, 스크롤바, 아이콘 등) → 디자인시스템 모듈로 승격한다.
   이 모듈은 다른 어떤 모듈에도 의존하지 않는 최하단 UI 키트다
   (예: `AppButton`, `AppIconToggleButton`, `AppLoadingWheel`).

공유 UI 모듈 vs 디자인시스템 모듈 한 줄 구분: **도메인 모델을 import 하면 공유 UI 모듈,
안 하면 디자인시스템 모듈.**

### 3.6 추출 시 지켜야 할 것

- 추출된 모든 public/internal Composable은 `modifier: Modifier = Modifier`를
  Optional 파라미터의 첫 번째 파라미터로 받는다(디자인시스템/화면 모두 동일). 
  Optional 파라미터는 Modifier 뒤에 위치(예: color, fontSize 등) 한다.
  후행 람다는 파라미터의 가장 마지막에 위치한다.
- 상태를 새로 추출한 Composable로 옮겼다면, 그 Composable에 대응하는 `@Preview`를 반드시
  추가하거나 기존 화면 Preview가 여전히 커버하는지 확인한다. Preview 작성이 어렵다는 것은
  파라미터 설계(추상화 경계)가 잘못됐다는 신호다.
- 리스트 아이템처럼 반복 렌더링되는 Composable은 `key`를 지정하고,
  필요하면 `@Stable`/`@Immutable` 데이터 클래스로 감싸 불필요한 재구성을 막는다.

## 4. ViewModel & UiState 설계 기준

- 화면 상태는 `sealed interface`로 표현하고, 파라미터가 없는 케이스는 `data object`,
  있는 케이스는 `data class`로 나눈다.
  이렇게 하면 Screen 쪽 `when`이 exhaustive해져서 새 상태를 빠뜨리는 케이스가 컴파일 타임에 잡힌다.

  ```kotlin
  sealed interface FeedUiState {
      data object Loading : FeedUiState
      data object LoadFailed : FeedUiState
      data class Success(val items: List<Item>) : FeedUiState
  }
  ```

- 화면에 필요한 State가 서로 독립적인 축(예: 온보딩 여부 / 피드 / 동기화 여부)이면
  하나의 거대한 UiState로 합치지 않고, ViewModel에서 각각 별도의 `StateFlow`로 노출한다.
  Screen에서는 필요한 것만 `collectAsStateWithLifecycle()`로 구독한다.
  기준: **두 State가 항상 함께 바뀌지 않는다면 합치지 않는다** — 합치면 한쪽만 바뀌어도 다른 쪽을 구독하는 UI까지 재구성된다.

  ```kotlin
  val isSyncing: StateFlow<Boolean> = syncManager.isSyncing
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue = false)

  val feedState: StateFlow<FeedUiState> = repository.observeFeed()
      .map(FeedUiState::Success)
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initialValue = FeedUiState.Loading)
  ```

  모든 `StateFlow`는 위처럼 `stateIn(scope = viewModelScope, 
  started = SharingStarted.WhileSubscribed(5_000), initialValue = ...)` 패턴을 따른다.
  5초 유예는 화면 회전 등으로 구독이 잠깐 끊겨도 upstream Flow(DB/네트워크 쿼리)가 처음부터 재시작되지
  않도록 하기 위함이다. 이 값을 바꾼다면 그 이유를 주석으로 남긴다.

- ViewModel의 공개 함수는 내부에서 `viewModelScope.launch { }`로 감싸고, Screen에서는
  `viewModel::updateItemChecked`처럼 **함수 참조**로 그대로 전달한다.
  람다로 한 번 더 감싸면(`{ id, checked -> viewModel.updateItemChecked(id, checked) }`)
  리컴포지션마다 새 람다 인스턴스가 생겨 하위 Composable의 스킵이 깨질 수 있다.

- 화면을 벗어나도 유지해야 하는 상태(딥링크로 열린 리소스 id 등)는
  `savedStateHandle.getStateFlow(key, default)`로 노출한다.

## 5. 라이브러리 사용
라이브러리 버전은 stable한 최신 버전으로 사용한다.
