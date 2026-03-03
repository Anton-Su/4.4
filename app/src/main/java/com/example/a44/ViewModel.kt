package com.example.a44

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class SocialPostWithComments(
    val post: SocialPost,
    val comments: List<Comment>?
)

data class PostUiState(
    val postWithComments: SocialPostWithComments,
    val state: UiState<Unit> = UiState.Loading
)

class ViewModel(private val getAllPosts: GetAllPostsUseCase, private val getAllComments: GetAllCommentsUseCase, private val getCommentFromId: GetCommentFromPostUseCase): ViewModel() {
//    private val _uiState = MutableStateFlow<UiState<SocialPost>>(UiState.Loading)
//    val uiState: StateFlow<UiState<SocialPost>> = _uiState
    var jobs: Job? = null
    private val _postsState = MutableStateFlow<List<PostUiState>>(emptyList())
    val postsState: StateFlow<List<PostUiState>> = _postsState
    init {
        fetchData()
    }
    private fun fetchData() {
        jobs = viewModelScope.launch {
            val postsFromRepo = getAllPosts() // загружаем весь JSON
            getAllComments() // загружаем все комментарии
            postsFromRepo.forEach { post ->
                // Сначала добавляем пост в Loading
                val currentList = _postsState.value.toMutableList()
                currentList.add(PostUiState(SocialPostWithComments(post, null), UiState.Loading))
                _postsState.value = currentList
                try {
                    delay(1000)
                    _postsState.value = _postsState.value.map { p ->
                        if (p.postWithComments.post.id  == post.id) p.copy(state = UiState.Success(Unit)) else p
                    }
                    // Получаем комментарии для конкретного поста
                    delay (600)
                    val commentsDeffered = async { getCommentFromId(post.id)} // загружаем комментарии для конкретного поста
                    _postsState.value = _postsState.value.map { p ->
                        if (p.postWithComments.post.id == post.id) {
                            p.copy(
                                postWithComments = p.postWithComments.copy(
                                    comments = commentsDeffered.await()
                                ),
                                state = UiState.Success(Unit)
                            )
                        } else {
                            p
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ViewModel", "Error loading comments for post ${post.id}: ${e.message}")
                    _postsState.value = _postsState.value.map { p ->
                        if (p.postWithComments.post.id  == post.id) p.copy(state = UiState.Error(e.message ?: "Unknown Error"))
                        else p
                    }
                }
            }
        }
    }
    fun stop() {
        jobs?.cancel()
    }
}


//class ViewModel(private val getAll: GetAllUseCase, private val getAllWithPrefix: SearchByNameUseCase): ViewModel() {
//    private val _repos = MutableStateFlow<List<RepositoryItem>>(emptyList())
//    val repos = _repos.asStateFlow()
//    private val _isLoading = MutableStateFlow(true)
//    val isLoading = _isLoading.asStateFlow()
//    private val debouncedSearch = viewModelScope.debounce<String>(1000L) { query ->
//        _isLoading.value = false
//        _repos.value = getAllWithPrefix(query)
//        _isLoading.value = true
//    }
//    init {
//        loadAll()
//    }
//    private fun loadAll() {
//        viewModelScope.launch {
//            _isLoading.value = false
//            _repos.value = getAll()
//            _isLoading.value = true
//        }
//    }
//
//    fun searchByPrefix(prefix: String) {
//        viewModelScope.launch {
//            debouncedSearch(prefix)
//        }
//    }
//
//}