package com.example.a44

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a44.RepositoryIml.CommentsRepImpl
import com.example.a44.RepositoryIml.PostsRepImpl
import com.example.a44.repository.CommentsRep
import com.example.a44.repository.PostsRep
import com.example.a44.ui.theme._44Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val jsonDataSource = JsonDataSource(this)
        val repositoryPosts = PostsRepImpl(jsonDataSource)
        val repositoryComments = CommentsRepImpl(jsonDataSource)
        val getAllCommentsUseCase = GetAllCommentsUseCase(repositoryComments)
        val getCommentUseCase = GetCommentFromPostUseCase(repositoryComments)
        val getAllPostsUseCase = GetAllPostsUseCase(repositoryPosts)
        val viewModel = ViewModel(getAllPostsUseCase, getAllCommentsUseCase, getCommentUseCase)
        setContent {
            _44Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        viewModel
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting(modifier: Modifier = Modifier, viewModel: ViewModel) {
    val postStates = viewModel.postsState.collectAsState()
    Column {
        Button(
            onClick = {
                viewModel.stop()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Обновить")
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(postStates.value.size) { index ->
                val post = postStates.value[index].postWithComments.post
                val comments = postStates.value[index].postWithComments.comments
                when (postStates.value[index].state) {
                    is UiState.Loading -> Text(
                        text = "Загрузка...",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    is UiState.Error -> Text(
                        text = "Ошибка загрузки данных",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    else -> {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        ) {
                            Column(
                                modifier = Modifier.padding(
                                    start = 4.dp,
                                    end = 4.dp,
                                    top = 10.dp,
                                    bottom = 8.dp
                                )
                            ) {
                                Text(
                                    text = post.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(post.avatarUrl)
                                            .crossfade(true)
                                            .build(),
                                        placeholder = painterResource(R.drawable.testphoto),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(90.dp),
                                    )
                                    Spacer(modifier = Modifier.width(18.dp))
                                    Text(
                                        text = post.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Комментарии: ",
                                    style = MaterialTheme.typography.titleSmall,
                                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
                                )
                                Column(modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)) {
                                    comments?.forEach { comment ->
                                        Text(
                                            text = comment.name + ": " + comment.text,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}




//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    _44Theme {
//        Greeting("Android")
//    }
//}