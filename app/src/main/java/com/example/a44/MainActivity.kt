package com.example.a44

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.a44.ui.theme._44Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
        setContent {
            _44Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding),
                        context = this
                    )
                }
            }
        }
    }
}

suspend fun allCommentsFun(nameFile: String, context: ComponentActivity): List<Comment> {
    // переключение потока, возвращает последнее действие
    return withContext(Dispatchers.IO) {
        delay(1000L)
        val jsonRepository = context.assets.open(nameFile).bufferedReader().use { it.readText() }
        Json.decodeFromString<List<Comment>>(jsonRepository)
    }
}

suspend fun allPostsFun(nameFile: String, context: ComponentActivity): List<SocialPost> {
    // переключение потока, возвращает последнее действие
    return withContext(Dispatchers.IO) {
        delay(1000L)
        val jsonRepository = context.assets.open(nameFile).bufferedReader().use { it.readText() }
        Json.decodeFromString<List<SocialPost>>(jsonRepository)
    }
}


suspend fun loadCommentsFromPost(comments: List<Comment>, idPost: Int): List<Comment> {
    delay(6300L)
    return comments.filter { it.postId == idPost}
}


suspend fun urlFromPost(url: String): String {
    delay(6300L)
    return url
}



@Composable
fun Greeting(modifier: Modifier = Modifier, context: ComponentActivity) {
    val scope = rememberCoroutineScope()
    val allPosts = remember { mutableStateOf<List<SocialPost>>(emptyList()) }
    val allComments = remember { mutableStateOf<List<Comment>>(emptyList()) }
    val postsWithComments = remember { mutableStateOf<List<SocialPostWithComments>>(emptyList()) }
    runBlocking {
        allPosts.value = allPostsFun("social_posts.json", context)
        allComments.value = allCommentsFun("comments.json", context)
    }

    allPosts.value.forEach { post ->
        scope.launch {
            val commentsDeferred = async {
                loadCommentsFromPost(allComments.value, post.id)
            }
            val avatarDeferred = async {
                urlFromPost(post.avatarUrl)
            }
            val avatarUrl = avatarDeferred.await() // без понятия, зачем так делать, но по условию нужно
            postsWithComments.value += SocialPostWithComments(post, commentsDeferred.await())
        }
    }

    Column {
        Button(
            onClick = {
               scope.cancel()
            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "Обновить")
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(postsWithComments.value.size) { index ->
                val post = postsWithComments.value[index].post
                val comments = postsWithComments.value[index].comments
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 10.dp, bottom = 8.dp)
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
                            Text(text = post.text,
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
                            comments.forEach { comment ->
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




//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    _44Theme {
//        Greeting("Android")
//    }
//}