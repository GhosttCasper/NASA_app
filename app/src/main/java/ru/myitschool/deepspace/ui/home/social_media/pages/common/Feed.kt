package ru.myitschool.deepspace.ui.home.social_media.pages.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import kotlinx.coroutines.flow.Flow
import ru.myitschool.deepspace.R
import ru.myitschool.deepspace.data.model.*
import ru.myitschool.deepspace.ui.home.components.ErrorMessage
import ru.myitschool.deepspace.utils.Resource
import ru.myitschool.deepspace.utils.Status

/*
 * @author Samuil Nalisin
 */
@Composable
fun <T> Feed(
    listResource: Resource<List<LiveData<ContentWithLikesAndComments<T>>>>,
    currentUser: UserModel?,
    onRetryButtonClick: () -> Unit,
    itemContent: @Composable (T) -> Unit,
    headerContent: @Composable LazyItemScope.() -> Unit = { Spacer(Modifier) },
    onLikeButtonClick: (ContentWithLikesAndComments<T>) -> LiveData<Resource<Unit>>,
    onLikeInCommentClick: (ContentWithLikesAndComments<T>, Comment) -> LiveData<Resource<Unit>>,
    onDeleteComment: (Comment, ContentWithLikesAndComments<T>) -> Unit,
    onItemClick: (LiveData<ContentWithLikesAndComments<T>>) -> Unit
) {
    Box {
        if (listResource.data != null) {
            LazyColumn(Modifier.fillMaxSize()) {
                item {
                    headerContent()
                }
                items(listResource.data) { item ->
                    val content by item.observeAsState()
                    if (content != null)
                        ItemWithLikesAndComments(
                            item = content!!,
                            itemContent = itemContent,
                            currentUser = currentUser,
                            onLikeButtonClick = { onLikeButtonClick(content!!) },
                            onCommentButtonClick = { onItemClick(item) },
                            onLikeInCommentClick = { onLikeInCommentClick(content!!, it) },
                            onClick = { onItemClick(item) },
                            onDeleteComment = { onDeleteComment(it, content!!) }
                        )
                }
            }
        }
        if (listResource.status == Status.LOADING)
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        if (listResource.status == Status.ERROR)
            ErrorMessage(
                onClick = onRetryButtonClick,
                modifier = Modifier.align(Alignment.Center)
            )
    }
}

@Composable
fun <T> FeedWithPager(
    pagerFlow: Flow<PagingData<LiveData<ContentWithLikesAndComments<T>>>>,
    currentUser: UserModel?,
    itemContent: @Composable (T) -> Unit,
    headerContent: @Composable
    LazyItemScope.(LazyPagingItems<LiveData<ContentWithLikesAndComments<T>>>)
    -> Unit = { Spacer(Modifier) },
    onLikeButtonClick: (ContentWithLikesAndComments<T>) -> LiveData<Resource<Unit>>,
    onLikeInCommentClick: (ContentWithLikesAndComments<T>, Comment) -> LiveData<Resource<Unit>>,
    onDeleteComment: (Comment, ContentWithLikesAndComments<T>) -> Unit,
    onItemClick: (LiveData<ContentWithLikesAndComments<T>>) -> Unit
) {
    val lazyItems = pagerFlow.collectAsLazyPagingItems()
    Box {
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                headerContent(lazyItems)
            }
            items(lazyItems) { item ->
                if (item != null) {
                    val content by item.observeAsState()
                    if (content != null)
                        ItemWithLikesAndComments(
                            item = content!!,
                            itemContent = itemContent,
                            currentUser = currentUser,
                            onLikeButtonClick = { onLikeButtonClick(content!!) },
                            onCommentButtonClick = { onItemClick(item) },
                            onLikeInCommentClick = { onLikeInCommentClick(content!!, it) },
                            onClick = { onItemClick(item) },
                            onDeleteComment = { onDeleteComment(it, content!!) }
                        )
                }
            }
            lazyItems.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxSize()) {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                            }
                        }
                    }
                    loadState.refresh is LoadState.Error -> {
                        item {
                            Box(modifier = Modifier.fillMaxSize()) {
                                ErrorMessage(
                                    onClick = { retry() }, modifier = Modifier
                                        .padding(16.dp)
                                        .align(
                                            Alignment.Center
                                        )
                                )
                            }
                        }
                    }
                    loadState.append is LoadState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .align(
                                            Alignment.Center
                                        )
                                )
                            }
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        item {
                            ErrorMessage(
                                onClick = { retry() }, modifier = Modifier
                                    .padding(16.dp)
                                    .align(
                                        Alignment.Center
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun <T> ItemWithLikesAndComments(
    item: ContentWithLikesAndComments<T>,
    currentUser: UserModel?,
    itemContent: @Composable (T) -> Unit,
    onLikeInCommentClick: (Comment) -> LiveData<Resource<Unit>>,
    onLikeButtonClick: () -> LiveData<Resource<Unit>>,
    onCommentButtonClick: () -> Unit,
    onDeleteComment: (Comment) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            itemContent(item.content)
            Divider(modifier = Modifier.padding(8.dp))
            val bestComment =
                item.comments.maxByOrNull { it.likes.size }
            if (bestComment != null)
                CommentItem(
                    comment = bestComment,
                    currentUser = currentUser,
                    onLikeClick = { onLikeInCommentClick(bestComment) },
                    onCommentClick = { onClick() },
                    maxLines = 5,
                    showSubComments = false,
                    onDeleteComment = { onDeleteComment(it) }
                )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onCommentButtonClick) {
                        Icon(
                            painter = painterResource(R.drawable.ic_chat),
                            contentDescription = "comments"
                        )
                    }
                    var numberOfComments: Int = item.comments.size
                    item.comments.forEach { numberOfComments += it.subComments.size }
                    Text(text = numberOfComments.toString())
                    LikeButton(
                        list = item.likes,
                        currentUser = currentUser,
                        onClick = onLikeButtonClick
                    )
                }
            }
        }
    }
}
