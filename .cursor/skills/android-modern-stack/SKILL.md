# Modern Android Development Skill (Jetpack Compose & Room)

## Description

Esta skill orienta o Cursor Agent na geração, refatoração e revisão de código para a stack moderna do Android, aplicando Jetpack Compose para UI, Room (SQLite) com KSP para persistência local, Kotlin Coroutines/Flow para assincronismo, seguindo rigorosamente a arquitetura MVVM/Clean e padrões oficiais do Google.

## Rules & Architecture Standards

### 1. Camada de Dados (Room ORM)

- **KSP:** Use o processador de anotações KSP em vez de KAPT para o Room.
- **Entidades:** Sempre defina `@Entity` com chaves primárias explícitas e nomes de tabelas padronizados (`tb_` ou snake_case).
- **DAOs:** Devem ser `interface`. Operações de escrita/leitura única devem ser funções suspensas (`suspend fun`). Operações reativas de leitura devem retornar Kotlin `Flow<List<T>>`.
- **Database:** Singleton abstrato herdando de `RoomDatabase`. Use `AndroidSQLiteDriver` quando aplicável.

### 2. Camada de UI (Jetpack Compose)

- **State Management:** Separe o Estado da UI de forma unificada utilizando um `data class` de UI State (ex: `ScreenUiState`).
- **Unidirectional Data Flow (UDF):** Componíveis devem aceitar o Estado como parâmetro e expor eventos/lambdas para ações (ex: `onAction: () -> Unit`).
- **Estabilidade:** Evite passar instâncias diretas de ViewModels para os Componíveis internos; passe apenas o estado desestruturado e callbacks de eventos.
- **Listas:** Sempre utilize `LazyColumn` ou `LazyRow` com a propriedade `key` explicitada para otimização de renderização.

### 3. Conectividade (ViewModel & Coroutines)

- Use `viewModelScope` para chamadas assíncronas do Room.
- Converta os fluxos do Room em UI State usando `.stateIn(scope, SharingStarted.WhileSubscribed(5000), InitialState)`.

## Code Templates & Examples

### Exemplo de Entidade e DAO (Room)

```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String
)

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
}
```

### Exemplo de Tela Reativa (Compose)

```kotlin
@Composable
fun UserScreen(
    uiState: UserUiState,
    onAddUserClick: () -> Unit
) {
    Scaffold { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = uiState.users, key = { it.id }) { user ->
                Text(text = "user.firstName {user.lastName}")
            }
        }
    }
}
```

## Prompt Trigger Instructions

Sempre que o usuário solicitar a criação de uma tela, persistência de novos dados, ou a criação de um fluxo de dados local completa, aplique estritamente este fluxo arquitetural, criando arquivos separados para Entity, DAO, Repository, ViewModel e Composable.

## Unit Testing Standards

### 1. Regras para Testes de Data/Room

- **In-Memory Database:** Sempre configure o Room para rodar em memória usando `Room.inMemoryDatabaseBuilder` para garantir testes isolados e rápidos.
- **Rule Coroutines:** Utilize uma regra JUnit personalizada com `StandardTestDispatcher` ou `UnconfinedTestDispatcher` para gerenciar o avanço do relógio virtual em chamadas suspensas e fluxos.
- **Fechamento do Banco:** Lembre-se sempre de fechar a instância do banco no método `@After`.

### 2. Regras para Testes de ViewModel

- **MainDispatcherRule:** Sempre configure o `Dispatchers.setMain` antes de cada teste de ViewModel para evitar falhas com loops de mensagem do Android Looper.
- **Testar StateFlow:** Para testar a emissão do `UiState`, utilize a biblioteca `Turbine` ou capture os valores coletando o fluxo dentro de um `backgroundScope.launch`.

### Exemplo de Teste Unitário (ViewModel + Flow)

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule() // Regra customizada para Coroutines

    private val userRepository = mockk<UserRepository>(relaxed = true)
    private lateinit var viewModel: UserViewModel

    @Test
    fun `loadUsers deve emitir estado de sucesso quando houver dados`() = runTest {
        val mockUsers = listOf(User(1, "John", "Doe"))
        coEvery { userRepository.getAllUsers() } returns flowOf(mockUsers)

        viewModel = UserViewModel(userRepository)

        viewModel.uiState.test {
            val item = awaitItem()
            assert(item.users == mockUsers)
        }
    }
}
```
